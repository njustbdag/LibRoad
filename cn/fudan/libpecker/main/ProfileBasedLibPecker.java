package cn.fudan.libpecker.main;

import cn.fudan.common.util.PackageNameUtil;
import cn.fudan.libpecker.core.LibApkMapper;
import cn.fudan.libpecker.core.PackageMapEnumerator;
import cn.fudan.libpecker.core.PackagePairCandidate;
import cn.fudan.libpecker.core.ParseApkTest;
import cn.fudan.libpecker.model.*;
import cn.njust.analysis.tree.PackageNode;
import cn.njust.common.Apk;
import cn.njust.common.Lib;
import cn.njust.common.LibPeckerConfig;
import cn.njust.common.Sdk;
import groundtruth.Versionsimilarity;
import groundtruth.calpkgsim;

import java.io.IOException;
import java.util.*;

import njust.lib.Service.LibpeckerResultService;

import org.xmlpull.v1.XmlPullParserException;


/**
 * Created by yuanxzhang on 27/04/2017.
 */
public class ProfileBasedLibPecker {

    Set<String> targetSdkClassNameSet;
    LibProfile libProfile;
    List<String> libClassBasicSigList = new ArrayList<>();
    List<String> apkClassBasicSigList = new ArrayList<>();
    public Map<String, ApkPackageProfile> apkPackageProfileMap;//pkg name -> ApkPackageProfile
    public Map<String, LibPackageProfile> libPackageProfileMap;//pkg name -> LibPackageProfile
    static Map<String, Integer>set=new HashMap<>();

    public ProfileBasedLibPecker(LibProfile libProfile, ApkProfile apkProfile, Set<String> targetSdkClassNameSet) {
        this.targetSdkClassNameSet = targetSdkClassNameSet;
        this.libProfile = libProfile;
        this.apkPackageProfileMap = apkProfile.packageProfileMap;
        this.libPackageProfileMap = this.libProfile.packageProfileMap;
    }

    public double calculateMaxProbability() {
        /*
        Step 0: fail-fast to check the basic sig of library classes
        * */
        //List<String> libClassBasicSigList = new ArrayList<>();
        //List<String> apkClassBasicSigList = new ArrayList<>();
    	apkClassBasicSigList.clear();
    	libClassBasicSigList.clear();
        for (ApkPackageProfile apkPackageProfile : apkPackageProfileMap.values()) {
            for (SimpleClassProfile simpleClassProfile : apkPackageProfile.classProfileMap.values())
                apkClassBasicSigList.add(simpleClassProfile.getBasicHashStrict());
        }
        for (LibPackageProfile libPackageProfile : libPackageProfileMap.values()) {
            for (SimpleClassProfile simpleClassProfile : libPackageProfile.classProfileMap.values())
                libClassBasicSigList.add(simpleClassProfile.getBasicHashStrict());
        }
        int matchLibClassBasicHashSize = 0;
        for (String basicClassHash : libClassBasicSigList) {
            if (apkClassBasicSigList.contains(basicClassHash)) {
                matchLibClassBasicHashSize ++;
                apkClassBasicSigList.remove(basicClassHash);
            }
        }
        double classBasicHashRatioUpperBound = 1.0*matchLibClassBasicHashSize/libClassBasicSigList.size();
        if (classBasicHashRatioUpperBound < LibPeckerConfig.LIB_APK_PAIR_THRESHOLD) {
            if (LibPeckerConfig.DEBUG_LIBPECKER) {
                //System.out.println("classBasicHashRatio not exceed threshold: " + classBasicHashRatioUpperBound);
            }
            return classBasicHashRatioUpperBound;
        }

        /*
        Step 1: candidate package calculation
        for each lib package, find all apk packages that has at least 50% matched class hashes
        */
        Map<String, PackagePairCandidate> libPackagePairCandidateMap = new HashMap<>();//pkg name -> PackagePairCandidate
        for (LibPackageProfile libPkg : libPackageProfileMap.values()) {
            PackagePairCandidate candidatePackages = new PackagePairCandidate(libPkg, apkPackageProfileMap.values(),set);

            libPackagePairCandidateMap.put(libPkg.packageName, candidatePackages);
            if (LibPeckerConfig.DEBUG_LIBPECKER) {
                //System.out.println(libPkg.packageName + ", weight: " + libPkg.getPackageWeight());
                for (ApkPackageProfile apkPackageProfile : candidatePackages.getCandiApkPackages()) {
                   // System.out.println("\t"+apkPackageProfile.packageName+", " + candidatePackages.getApkPackageSimilarity(apkPackageProfile));
                }
            }
        }

        /*
        Step 2: link package
        use some rules to filter out some candidates,
        a) If a library package lp1 with pack- age name com.foo has app package candidates
        starting with the same package name, we can remove candidates with different root packages.
        b) If lp1 matches ap1 with package name a.b.c we deduce that a.b is one potential
        library root package within the app. By applying this to all pairs <lpi,apj> we receive
        a list of potential root packages.
        */
        LibApkMapper mapper = new LibApkMapper(libPackageProfileMap, apkPackageProfileMap, libProfile.rootPackageMap);
        //apply rule 1
        for (PackagePairCandidate packageCandidate : new ArrayList<>(libPackagePairCandidateMap.values())) {
            ApkPackageProfile perfectMatch = packageCandidate.perfectMatch();//找到perfectMatch的apk包就是找到与lib包名相同的apk包而已
            if (perfectMatch != null) {
                boolean paired = mapper.makePair(packageCandidate, perfectMatch);
                if (! paired) {
                    throw new RuntimeException("can not be true");
                }
                else {
                    packageCandidate.justKeepPerfectMatch();//如果能够找到lib包和某个apk包是perfectMatch的话，之前的packageCandidate都无效清零，只剩下这一个perfectMatch的apk包。
                }
            }
        }
        //rule 2, for those perfect match packages, we extract their root packages,
        // and use these root packages to filter candidate apk package in other lib packages
        // e.g com.facebook.network is perfect match in apk, then other lib packages with com.facebook as root packages
        //  should only have apk package candidates start with com.facebook
        for (String libPackageName : mapper.getExistingPackageMap().keySet()) {
            if (libPackageName.equals(PackageNode.Factory.DEFAULT_PACKAGE))
                continue;

            String parentPackageName = PackageNameUtil.getParentPackageName(libPackageName);
            String rootPackageName = libProfile.getRootPackage(libPackageName);
            System.out.println("libPackageName:"+libPackageName); 
            System.out.println(rootPackageName);
            while (parentPackageName.length() >= rootPackageName.length()) {
                for (PackagePairCandidate packageCandidate : libPackagePairCandidateMap.values()) {
                    packageCandidate.filterRootPackageName(parentPackageName);
                }

                parentPackageName = PackageNameUtil.getParentPackageName(parentPackageName);
            }
        }

        /*
        Step 2.9 optimization
        all enumeration would be quite slow, we can calculate the upper bound
        */
        double similarityUpperBound = mapper.similarityUpperBound(libPackagePairCandidateMap);
        if (similarityUpperBound < LibPeckerConfig.LIB_APK_PAIR_THRESHOLD) {
            if (LibPeckerConfig.DEBUG_LIBPECKER) {
               // System.out.println("similarityUpperBound not exceed threshold: " + similarityUpperBound);
            }
            return similarityUpperBound;
        }

        /*
        Step 3: partition package
        exhaustiveEnumerate all candidate partitions
        */
        PackageMapEnumerator packageMapEnumerator = new PackageMapEnumerator(libPackagePairCandidateMap, mapper);
        List<LibApkMapper> allPartitions = packageMapEnumerator.exhaustiveEnumerate();
        if (LibPeckerConfig.DEBUG_LIBPECKER) {
           // System.out.println();
           // System.out.println(packageMapEnumerator);
        }


        /*
        Step 4: maximum total similarity
        */
        double maxSimilarity = 0;
        for (LibApkMapper partition : allPartitions) {
            double similarity = partition.similarity(libPackagePairCandidateMap);
            if (LibPeckerConfig.DEBUG_LIBPECKER) {
                //System.out.println("similarity: " + similarity);
                //System.out.println(partition);
            }
            if (maxSimilarity < similarity) {
                maxSimilarity = similarity;
                maxPartition = partition;
            }
        }
        if (LibPeckerConfig.DEBUG_LIBPECKER) {
            System.out.println(maxPartition);
        }

        return maxSimilarity;
    }

    private LibApkMapper maxPartition = null;
    public LibApkMapper getMaxPartition(){
        if (maxPartition == null) {
            calculateMaxProbability();
        }
        return maxPartition;
    }

    private static void fail(String message) {
        System.err.println(message);
        System.exit(0);
    }

    public static double singleMain(String apkPath, String libPath) {
        Apk apk = Apk.loadFromFile(apkPath);
        if (apk == null) {
            fail("apk not parsed");
        }
        Lib lib = Lib.loadFromFile(libPath);
        if (lib == null) {
            fail("lib not parsed");
        }
        Sdk sdk = Sdk.loadDefaultSdk();
        if (sdk == null) {
            fail("default sdk not parsed");
        }
        Set<String> targetSdkClassNameSet = sdk.getTargetSdkClassNameSet();
        LibProfile libProfile = LibProfile.create(lib, targetSdkClassNameSet);

        ApkProfile apkProfile = ApkProfile.create(apk, targetSdkClassNameSet);

        ProfileBasedLibPecker pecker = new ProfileBasedLibPecker(libProfile, apkProfile, targetSdkClassNameSet);
        double similarity = pecker.calculateMaxProbability();

        return similarity;
    }
    
    public static void versionas() throws IOException{
        Versionsimilarity  versionsimilarity=new Versionsimilarity();
        List<String> version=versionsimilarity.Findversion();
		  for(String v:version){
			  System.out.println("包括"+v);
			  List<String> versionseries=versionsimilarity.Findversionseries(v); 
			  String verString=versionseries.get(0);
			  version(versionseries,verString);
		  }
    }

    
    public static void version(List<String> dexfilepath,String apkPath) throws IOException {
    	double maxsimilarity=0;
    	String maxlibpath = null;
    	Map<String,Double>libset=new HashMap<>();
        String apkname=apkPath.substring(50);
        calpkgsim pecker1 = new calpkgsim();
    	String filenameTemp=pecker1.creatdexFile(apkname);
        getdexfilepath aa=new getdexfilepath();
    	long current = System.currentTimeMillis();
        Sdk sdk = Sdk.loadDefaultSdk();
        if (sdk == null) {
            fail("default sdk not parsed");
        }
        Apk apk = Apk.loadFromFile(apkPath);
        if (apk == null) {
            fail("apk not parsed");
        }
        Set<String> targetSdkClassNameSet = sdk.getTargetSdkClassNameSet();
        ApkProfile apkProfile = ApkProfile.create(apk, targetSdkClassNameSet);
    	for(String libPath:dexfilepath){
    		System.out.println("--------------"+libPath.substring(libPath.lastIndexOf("\\"))+"\\进程开始--------------");
            Lib lib = Lib.loadFromFile(libPath);
            if (lib == null) {
                fail("lib not parsed");
            }
            LibProfile libProfile = LibProfile.create(lib, targetSdkClassNameSet);
            ProfileBasedLibPecker pecker = new ProfileBasedLibPecker(libProfile, apkProfile, targetSdkClassNameSet);
        	double similarity = pecker.calculateMaxProbability();
        		libset.put(libPath.substring(libPath.lastIndexOf("\\")).substring(1), similarity);       		
        	System.out.println("similarity: " + similarity);
        	System.out.println("--------------"+libPath.substring(libPath.lastIndexOf("\\"))+"\\进程完毕--------------");
        }
    	for(String aaa:libset.keySet()){
    		System.out.println(aaa+"：similarity: "+libset.get(aaa));
    		pecker1.writeTxtFile("："+apkname+"------"+aaa+"*****"+libset.get(aaa),filenameTemp);
    	}
        System.out.println("time: " + (System.currentTimeMillis() - current));
	}
    
    
    
    public static void main(String[] args) throws IOException, XmlPullParserException {
    	//versionas();
    	double maxsimilarity=0;
    	String maxlibpath = null;
    	Map<String,Double>libset=new HashMap<>();
    	 String apkPath = null;
	        //String libPath0 = "G:\\libdetectiongroundtruth\\lib340";
 	String libPath0 =null;
 	
	        if (args == null || args.length == 2) {
	        	apkPath = args[0];
	            libPath0 = args[1];
	        }
	        else {
	            fail("Usage: java -cp LibPecker3.jar cn.fudan.libpecker.mainProfileBasedLibPecker <apk_path> <lib_path>");
	        }
    	//LibpeckerResultService apkLibService=new LibpeckerResultService();
        //String apkPath = "E:\\LibDetect实验groundtruth\\smali2apk\\00e74c118fa3902e5c85fd8e37f3d084.apk";
        String apkname=apkPath.substring(apkPath.lastIndexOf("\\")).substring(1);
        calpkgsim pecker1 = new calpkgsim();
    	String filenameTemp=pecker1.creatdexFile(apkname);
        //G:\\LibPecker源码\\LibPecker-masteroriginal\\LibPecker-master\\test\\apk\\444.apk
        //String libPath = "C:\\Users\\ZJY\\Desktop\\test.dex";
        getdexfilepath aa=new getdexfilepath();
        List<String> dexfilepath=new ArrayList<>();
    	dexfilepath.addAll(aa.traverseFolder1(libPath0));
    	long current = System.currentTimeMillis();
    	//String pkgname=ParseApkTest.test(apkPath);
    	//System.out.println("pkgname"+pkgname);
        //set.putAll(defanaly.singleMain(apkPath,pkgname));//测试时杠掉
        System.out.println("下面是真正输出的是主模块包名以及该包与主包间的依赖值：");
        for(String key : set.keySet()){
        	System.out.println(key);
        	System.out.println(set.get(key));
        }
        Sdk sdk = Sdk.loadDefaultSdk();
        if (sdk == null) {
            fail("default sdk not parsed");
        }
        Apk apk = Apk.loadFromFile(apkPath);
        if (apk == null) {
            fail("apk not parsed");
        }
        Set<String> targetSdkClassNameSet = sdk.getTargetSdkClassNameSet();
        ApkProfile apkProfile = ApkProfile.create(apk, targetSdkClassNameSet);
    	for(String libPath:dexfilepath){
    		System.out.println("--------------"+libPath.substring(libPath.lastIndexOf("\\"))+"\\进程开始--------------");
            Lib lib = Lib.loadFromFile(libPath);
            if (lib == null) {
                fail("lib not parsed");
            }
            LibProfile libProfile = LibProfile.create(lib, targetSdkClassNameSet);
         	//Map<String,List<String>> rootPackageMap=apkProfile.rootanalysisMap(apkPath);
            ProfileBasedLibPecker pecker = new ProfileBasedLibPecker(libProfile, apkProfile, targetSdkClassNameSet);
        	double similarity = pecker.calculateMaxProbability();
        	if(similarity>maxsimilarity){
        		maxsimilarity=similarity;
        		maxlibpath=libPath;
        	}
        //	if(similarity>0.6){
        		libset.put(libPath.substring(libPath.lastIndexOf("\\")).substring(1), similarity);
        //	}
        		
        	System.out.println("similarity: " + similarity);
        	System.out.println("--------------"+libPath.substring(libPath.lastIndexOf("\\"))+"\\进程完毕--------------");
        }
    	System.out.println("最终检测结果为：");
    	System.out.println("最终检测结果有："+libset.size()+"个");
    	for(String aaa:libset.keySet()){
    		if (libset.get(aaa)>0.5) {
		System.out.println(aaa+"：similarity: "+libset.get(aaa));
    		pecker1.writeTxtFile("："+apkname+"------"+aaa+"*5",filenameTemp);
			}

    		//apkLibService.addapklibbyname(apkname,aaa);
    	}
    	//System.out.println("该apk中存在的第三方库路径是："+maxlibpath);
    	//double similarity = singleMain(apkPath, maxlibpath);
    	//System.out.println("similarity: " + similarity);
        System.out.println("time: " + (System.currentTimeMillis() - current));
    }

}
