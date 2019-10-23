package cn.fudan.libpecker.main;

import cn.fudan.common.util.PackageNameUtil;
import cn.fudan.libpecker.core.LibApkMapper;
import cn.fudan.libpecker.core.PackageMapEnumerator;
import cn.fudan.libpecker.core.PackagePairCandidate;
import cn.fudan.libpecker.core.ParseApkTest;
import cn.fudan.libpecker.core.ProfileComparator;
import cn.fudan.libpecker.model.*;
import cn.njust.analysis.tree.PackageNode;
import cn.njust.common.Apk;
import cn.njust.common.Lib;
import cn.njust.common.LibPeckerConfig;
import cn.njust.common.Sdk;
import groundtruth.calpkgsim;

import java.io.File;
import java.io.IOException;
import java.util.*;

import njust.lib.Service.LibdetectionResultService;

import org.xmlpull.v1.XmlPullParserException;


/**
 * Created by yuanxzhang on 27/04/2017.
 */
public class Filter {

    Set<String> targetSdkClassNameSet;
    LibProfile libProfile;
    List<String> libClassBasicSigList = new ArrayList<>();
    List<String> apkClassBasicSigList = new ArrayList<>();
    static Map<String, String> bestmatchpair;
    static Map<String, Double> bestmatchSim;
	static Map<String, String> PkgNameMap = new HashMap<>();//apkpkgname---lib:libpkgname--apk包的最佳配对lib
	static Map<String, List<String>> LibPkgNameMap = new HashMap<>();//libname---lib:libpkgname
	static Map<String, Double> PkgSimilarity = new HashMap<>();//apkpkgname---similarity---apk包最佳配对值
	static Map<String, Double> LibpkgWeight = new HashMap<>();//lib+libpkgname---similarity
    public Map<String, ApkPackageProfile> apkPackageProfileMap;//pkg name -> ApkPackageProfile
    public Map<String, LibPackageProfile> libPackageProfileMap;//pkg name -> LibPackageProfile
    static Map<String, Integer>set=new HashMap<>();//set1是不需要对比检测的包，包括2类：1.（主模块的包，该包与主包的依赖值）；2.已经被完美匹配到的包（包名，相似度）。
    static List<String>set1=new ArrayList<>();//set1是不需要对比检测的包，包括2类：1.主模块的包；2.已经被完美匹配到的包。

    public Filter(LibProfile libProfile, ApkProfile apkProfile, Set<String> targetSdkClassNameSet,Map<String, String> bestmatchpair,Map<String, Double> bestmatchSim) {
        this.targetSdkClassNameSet = targetSdkClassNameSet;
        this.libProfile = libProfile;
        this.apkPackageProfileMap = apkProfile.packageProfileMap;
        this.libPackageProfileMap = this.libProfile.packageProfileMap;
    	this.bestmatchpair=bestmatchpair;
    	this.bestmatchSim=bestmatchSim;
    }
    

    private static double getClassMatchSimilarityThreshold(SimpleClassProfile libClassProfile) {
        int memberCount = 1 + libClassProfile.getMethodHashList().size() + libClassProfile.getFieldHashList().size();
        if (memberCount <= 5)
            return 1.0;
        if (memberCount <= 10)
            return 0.9;
        if (memberCount <= 15)
            return 0.8;
        if (memberCount <= 20)
            return 0.7;
        if (memberCount <= 25)
            return 0.6;
        else
            return 0.5;
    }
    public double desribecompare(ApkPackageProfile apkPackageProfile,LibPackageProfile libPackageProfile){
    	int samecount=0;
    	int libclassbum=0;
    	List<String>apkclasssigList=new ArrayList<>();
    	for(SimpleClassProfile apkClassProfile :apkPackageProfile.classProfileMap.values()){
    		apkclasssigList.add(apkClassProfile.getMethoddescriptorHashList());
    	}
    	for(SimpleClassProfile libClassProfile :libPackageProfile.classProfileMap.values()){   	
    		libclassbum++;
        		        if(apkclasssigList.contains(libClassProfile.getMethoddescriptorHashList())){
        		        	samecount++;           		        	  						          			
    	}

    }
		return samecount/libclassbum;
    	
    }
    public static double rawPackageSimilarity(LibPackageProfile libPackageProfile, ApkPackageProfile apkPackageProfile,
            /*as return value*/Map<String, String> classNameMap, /*as return value*/Map<String, Double> classRawSimilarity) {
List<String> ClassList = libPackageProfile.getWeightClassList();
      for (String libClassName : ClassList) {
SimpleClassProfile simpleLibClassProfile = libPackageProfile.classProfileMap.get(libClassName);
      double RAW_CLASS_SIMILARITY_THRESHOLD = getClassMatchSimilarityThreshold(simpleLibClassProfile);

SimpleClassProfile bestMatchApkClassProfile = null;
        double maxSimilarity = 0;

for (SimpleClassProfile apkClassProfile : apkPackageProfile.classProfileMap.values()) {
if (classNameMap.values().contains(apkClassProfile.getClassName()))
continue;

double similarity = ProfileComparator.rawClassSimilarity(simpleLibClassProfile, apkClassProfile);
if (similarity >= RAW_CLASS_SIMILARITY_THRESHOLD) {
if (similarity > maxSimilarity) {
maxSimilarity = similarity;
bestMatchApkClassProfile = apkClassProfile;
}
}
}

if (bestMatchApkClassProfile != null) {
classNameMap.put(libClassName, bestMatchApkClassProfile.getClassName());
classRawSimilarity.put(libClassName, maxSimilarity);
}
else {
classNameMap.put(libClassName, null);
classRawSimilarity.put(libClassName, 0.0);
}
}
    //  System.out.println("classRawSimilarity.size():"+classRawSimilarity.size());
double Similarity = 0.0;
for (String libClassName : ClassList) {
if (LibPeckerConfig.DEBUG_LIBPECKER) {
if (libPackageProfile.packageName.equals(LibPeckerConfig.DEBUG_LIBPECKER_LIB_PKG_NAME)
&& apkPackageProfile.packageName.equals(LibPeckerConfig.DEBUG_LIBPECKER_APK_PKG_NAME)) {
System.out.println("\t class name: "+libClassName);
System.out.println("\t\t class weight: "+libPackageProfile.getClassWeight(libClassName));
System.out.println("\t\t class similarity: "+classRawSimilarity.get(libClassName));
System.out.println("\t\t class match: "+classNameMap.get(libClassName));
}
}
//System.out.println("\t\t classRawSimilarity.get(libClassName): "+classRawSimilarity.get(libClassName));
//System.out.println("\t\t libPackageProfile.getClassWeight(libClassName): "+libPackageProfile.getClassWeight(libClassName));
//System.out.println("\t\t classRawSimilarity.size(): "+classRawSimilarity.size());
//Similarity += classRawSimilarity.get(libClassName)/classRawSimilarity.size();//*libPackageProfile.getClassWeight(libClassName);
Similarity += classRawSimilarity.get(libClassName)*libPackageProfile.getClassWeight(libClassName);
//System.out.println("Similarity:"+Similarity);
}
if(Double.isNaN(Similarity)){
	System.out.println("出现NANlllllllllllllllllllllllllllllllllllllllll");
	for (String libClassName : ClassList) {
		System.out.println("\t\t classRawSimilarity.get(libClassName): "+classRawSimilarity.get(libClassName));
		System.out.println("\t\t libPackageProfile.getClassWeight(libClassName): "+libPackageProfile.getClassWeight(libClassName));	
	}
	
}

return Similarity;
}
    
    public static double rawClassSimilarity(SimpleClassProfile libClassProfile, SimpleClassProfile apkClassProfile) {
        String apkClassHash = apkClassProfile.getClassHash();
        if (libClassProfile.getClassHash().equals(apkClassHash))
            return 1;
        if (libClassProfile.getClassHashStrict().equals(apkClassProfile.getClassHashStrict()))
            return 1;

        if (! libClassProfile.getBasicHash().equals(apkClassProfile.getBasicHash())
                && ! libClassProfile.getBasicHashStrict().equals(apkClassProfile.getBasicHashStrict()))
            return 0;
        else {
            List<String> apkMethodHashList = new ArrayList<>(apkClassProfile.getMethodHashList());
            List<String> apkFieldHashList = new ArrayList<>(apkClassProfile.getFieldHashList());

            double rate = 1.0*(libClassProfile.getMethodHashList().size()+libClassProfile.getFieldHashList().size())/(apkMethodHashList.size()+apkFieldHashList.size());
            if (rate > 1.0)
                rate = 1.0;

            int sameCounter = 1;
            for (int i = 0; i < libClassProfile.getMethodHashList().size(); i ++) {
                if (apkMethodHashList.contains(libClassProfile.getMethodHashList().get(i))) {
                    sameCounter ++;

                    apkMethodHashList.remove(libClassProfile.getMethodHashList().get(i));
                    continue;
                }
                if (apkMethodHashList.contains(libClassProfile.getMethodHashStrictList().get(i))) {
                    sameCounter ++;

                    apkMethodHashList.remove(libClassProfile.getMethodHashStrictList().get(i));
                    continue;
                }
            }

            for (int i = 0; i < libClassProfile.getFieldHashList().size(); i ++) {
                if (apkFieldHashList.contains(libClassProfile.getFieldHashList().get(i))) {
                    sameCounter ++;

                    apkFieldHashList.remove(libClassProfile.getFieldHashList().get(i));
                    continue;
                }
                if (apkFieldHashList.contains(libClassProfile.getFieldHashStrictList().get(i))) {
                    sameCounter ++;

                    apkFieldHashList.remove(libClassProfile.getFieldHashStrictList().get(i));
                    continue;
                }
            }

            double similarity = rate*sameCounter/(1+libClassProfile.getFieldHashList().size()+libClassProfile.getMethodHashList().size());

            return similarity;
        }
    }

 
      
    public  double calculateMaxProbabilitytest00(String libname){
    	double libsim=0;
    	//List<String> LibPkgFlag = new ArrayList<>();
    	List<ApkPackageProfile>apkPackageProfileset=new ArrayList<>();
    	List<LibPackageProfile>libPackageProfileset=new ArrayList<>();
    	List<String>apkpkgname=new ArrayList<>();
    	List<String>libpkgname=new ArrayList<>();
    	Map<String, Double> libpkgweighted= new HashMap<>();
    	double libweightsum = 0;
    	System.out.println("这里是第一步，通过匹配包名快速找到该lib是否存在于apk中：");
        for (ApkPackageProfile apkPackageProfile : apkPackageProfileMap.values()) {
        	apkpkgname.add(apkPackageProfile.packageName);
        	apkPackageProfileset.add(apkPackageProfile);
        }
        for (LibPackageProfile libPackageProfile : libPackageProfileMap.values()) {
        	double libpkgweight=libPackageProfile.getPackageWeight1();
        	libweightsum+=libpkgweight;
        	libPackageProfileset.add(libPackageProfile);
        	libpkgname.add(libPackageProfile.packageName);                      
        }
        
        int matchpkgcount=0;
        List<String>matchedLibPkgList=new ArrayList<>();
        for(String a:libpkgname){
       	 if(apkpkgname.contains(a)){
       		matchedLibPkgList.add(a);
       		 //System.out.println("存在apk包名与lib包名相同："+a);
       		 //System.out.println("两个相同包名的包相似度为："+calculatelibapkpkg,a));
       		 matchpkgcount++;
       	 }
        }
        LibPkgNameMap.put(libname, matchedLibPkgList);
        if(matchpkgcount==0){
        	System.out.println("apk中不存在包与lib包名相同-------------第一步结束-------------");
        	return 0;
        }
        
        System.out.println("该lib一共有"+libPackageProfileset.size()+"个包");
        //System.out.println("找到包名匹配的有"+matchpkgcount+++"对apkpkg---libpkg");
        //System.out.println("libweightsum"+libweightsum);
       /** if(matchpkgcount!=libpkgname.size()){
        	System.out.println("-------------第一步结束，没有在该apk中找到与lib包名相同的包,下面进入第二步仔细匹配-----------------");
        	return 0;
        }**/
        if(libpkgname.size()==1){
        	for(String a:libpkgname)
            	LibpkgWeight.put(libname+"\\"+a, (double) 1);
        	System.out.println("这是唯一的lib包名："+libpkgname);
        	LibPackageProfile libPkg = null;
        	for (LibPackageProfile libPackageProfile : libPackageProfileMap.values()){
        		libPkg=libPackageProfile;
        	}
			PackagePairCandidate packageCandidate = new PackagePairCandidate(libPkg, apkPackageProfileMap.values(),set);
        	LibApkMapper mapper = new LibApkMapper(libPackageProfileMap, apkPackageProfileMap, libProfile.rootPackageMap);
                 ApkPackageProfile perfectMatch = packageCandidate.perfectMatch();//找到perfectMatch的apk包就是找到与lib包名相同的apk包而已
                 if (perfectMatch != null) {
                     boolean paired = mapper.makePair(packageCandidate, perfectMatch);
                     if (! paired) {
                         throw new RuntimeException("can not be true");
                     }
                     else {
               			 Map<String, String> classNameMap = new HashMap<>();
              	        Map<String, Double> classRawSimilarity = new HashMap<>();
              	        double sim0=rawPackageSimilarity(libPkg,perfectMatch, classNameMap, classRawSimilarity);
          	        	if(PkgSimilarity.keySet().contains(perfectMatch.packageName)){
          	        		if(PkgSimilarity.get(perfectMatch.packageName)>sim0)
          	        			return -1;
          	        	}         	        		          	        	
              	        if(sim0>=LibPeckerConfig.LIB_APK_PAIR_THRESHOLD){
              	        	if(PkgSimilarity.keySet().contains(perfectMatch.packageName)){
              	        		if(PkgSimilarity.get(perfectMatch.packageName)<sim0){
              	        		PkgNameMap.put(perfectMatch.packageName, libname+"\\"+libPkg.packageName);
              	        	PkgSimilarity.put(perfectMatch.packageName, sim0);
              	        		}
              	        		/**if(PkgSimilarity.get(perfectMatch.packageName)>sim0){
              	        			System.out.println("该lib只有一个包，匹配相似度为"+sim0);
              	        			System.out.println("但是这个apk包已经有最佳匹配了"+perfectMatch.packageName);
              	        			return -1;
              	        		}**/
              	        			
              	        	}
              	        	if(!PkgSimilarity.keySet().contains(perfectMatch.packageName)){
                  	            PkgNameMap.put(perfectMatch.packageName, libname+"\\"+libPkg.packageName);
                  	        	PkgSimilarity.put(perfectMatch.packageName, sim0);
              	        	}

              	        System.out.println("第一步成功，该lib只有一个包，匹配相似度为"+sim0+"超过阈值，进程结束");
              	        return sim0;
              	        }
              	        else {
              	        	System.out.println("该lib只有一个包，匹配到的apk包与lib包相似度为"+sim0+"不能超过阈值，不需要进入第二步，进程结束");	
              	        	return 2;
						}
                         //packageCandidate.justKeepPerfectMatch();//如果能够找到lib包和某个apk包是perfectMatch的话，之前的packageCandidate都无效清零，只剩下这一个perfectMatch的apk包。
                     }
                 }
             
        }
        for(LibPackageProfile a:libPackageProfileset){
        	double weighted=a.getPackageWeight1()/libweightsum;
        	libpkgweighted.put(a.packageName, weighted);
       	 System.out.println("lib包名"+a.packageName+"---weighted:"+weighted);
       	LibpkgWeight.put(libname+"\\"+a.packageName, weighted);
       	 for(ApkPackageProfile b:apkPackageProfileset){
       		 if(a.packageName.equals(b.packageName)){
       			 //double sim=calculatelibapkpkgsim(b,a);
       			 Map<String, String> classNameMap = new HashMap<>();
     	        Map<String, Double> classRawSimilarity = new HashMap<>();
     	        double sim0=rawPackageSimilarity(a,b, classNameMap, classRawSimilarity);
  	        	if(PkgSimilarity.keySet().contains(b.packageName)){
  	        		if(PkgSimilarity.get(b.packageName)<sim0){
  	        	     PkgNameMap.put(b.packageName, libname+"\\"+a.packageName);
      	        	PkgSimilarity.put(b.packageName, sim0);
  	        		}


      	        	}
      	        	if(!PkgSimilarity.keySet().contains(b.packageName)){
          	            PkgNameMap.put(b.packageName, libname+"\\"+a.packageName);
          	        	PkgSimilarity.put(b.packageName, sim0);
      	        	}
  	    
     	        if(Double.isNaN(sim0)){
     	        	System.out.println("出现NAN,sim0=0");
     	        	sim0=0;
     	        }
       			 libsim+=sim0*libpkgweighted.get(b.packageName);;
       			 //System.out.println("lib包："+a.packageName+"和匹配到的apk包："+b.packageName+"相似度为sim："+sim0);
       			//LibPkgNameMap.put(libname, libpkgname);
       			System.out.println("lib包："+a.packageName+"和匹配到的apk包："+b.packageName+"相似度为sim0："+sim0);
       		 }
       		 
       	 }
        }
        if(matchpkgcount==libpkgname.size()){
        	 System.out.println("---------第一步结束，该lib所有包名都存在于该apk中，libsim："+libsim+"--------------");
        	 return libsim;
        }
        if(matchpkgcount>0&matchpkgcount<libpkgname.size()){       
        	double matchpercent=matchpkgcount*100/libpkgname.size();
         	 System.out.println("---------第一步结束，只有"+matchpercent+"%lib在apk中，不能完全匹配到包名，libsim："+libsim+"--------------");
         	 return libsim;
         }

		return libsim;
    	
    	
    	
    }
    public  double calculateMaxProbabilitytest0(String libname, ApkProfile apkProfile, Map<String, List<String>> rootPackageMap){
    	double libsim=0;
    	double libweightsum = 0;
    	List<ApkPackageProfile>apkPackageProfileset=new ArrayList<>();
    	List<LibPackageProfile>libPackageProfileset=new ArrayList<>();
    	List<String>apkpkgname=new ArrayList<>();
    	List<String>libpkgname=new ArrayList<>();
    	List<String>matchpkgnamelist=new ArrayList<>();
    	List<SimpleClassProfile>libSimpleClassProfile=new ArrayList<>();
    	List<SimpleClassProfile>apkSimpleClassProfile=new ArrayList<>();
    	Map<String, Double> classRawSimilarity0= new HashMap<>();
    	Map<String, Double> libpkgweighted= new HashMap<>();
    	Map<String, String> classNameMap0 = new HashMap<>();
    	System.out.println("                                      ");
    	System.out.println("这里是第二步，通过仔细比对包相似度确定该lib是否存在于apk中：");
        for (ApkPackageProfile apkPackageProfile : apkPackageProfileMap.values()) {
        	if(!set1.contains(apkPackageProfile.packageName)){
        		apkpkgname.add(apkPackageProfile.packageName);
            	apkPackageProfileset.add(apkPackageProfile);
                for (SimpleClassProfile simpleClassProfile : apkPackageProfile.classProfileMap.values()) {
                    apkClassBasicSigList.add(simpleClassProfile.getClassHashStrict());
                    apkSimpleClassProfile.add(simpleClassProfile);
                }
        	}       	
        }

        for (LibPackageProfile libPackageProfile : libPackageProfileMap.values()) {
        	double pkgweight=libPackageProfile.getPackageWeight1();
        	libweightsum+=pkgweight;
        	libPackageProfileset.add(libPackageProfile);
        	libpkgname.add(libPackageProfile.packageName);
            for (SimpleClassProfile simpleClassProfile : libPackageProfile.classProfileMap.values()){
            	 libClassBasicSigList.add(simpleClassProfile.getClassHashStrict());  
            	 libSimpleClassProfile.add(simpleClassProfile);
            }                      
        }
        int n=1;
        System.out.println("该lib一共有"+libPackageProfileset.size()+"个包");
        for (LibPackageProfile libPackageProfile : libPackageProfileMap.values()){   
        	//String pkgroot=libPackageProfile
        	double pkgweight=libPackageProfile.getPackageWeight1();
        	double weighted=pkgweight/libweightsum;
        	libpkgweighted.put(libPackageProfile.packageName, weighted);
        	System.out.println("正在比对lib的第"+n+"个包："+libPackageProfile.packageName+"--------weight:"+pkgweight+"---weighted:"+weighted);
        	double maxsim=0;
        	String matchpkgname = null;
        	for (ApkPackageProfile apkPackageProfile : apkPackageProfileMap.values()) {
        	      Map<String, String> classNameMap = new HashMap<>();
        	        Map<String, Double> classRawSimilarity = new HashMap<>();
        	        double similarity=rawPackageSimilarity(libPackageProfile,apkPackageProfile, classNameMap, classRawSimilarity);      	      
        	        if(similarity>=0){
        	        	if(maxsim<similarity||maxsim==similarity){
              	    	   maxsim=similarity;
              	    	   matchpkgname=apkPackageProfile.packageName;    
              	       }
         		//System.out.println("libpkg--apkpkg相似性："+libPackageProfile.packageName+"--"+apkPackageProfile.packageName+"相似值"+similarity);
        	        }
            	        
        	}
        	if(maxsim==0){
        		matchpkgname=null;
        	}
	        	
        	if(maxsim>0){
            	classRawSimilarity0.put(matchpkgname, maxsim);//matchpkgname是apk包名
            	classNameMap0.put(matchpkgname, libPackageProfile.packageName);  	        	
        	}
        	System.out.println("匹配结果------"+"lib包名："+libPackageProfile.packageName+"--------apk包名："+matchpkgname+"---------"+classRawSimilarity0.get(matchpkgname));
        	if(bestmatchpair.containsKey(matchpkgname)){
        		//String apkmatchedlibpkgString=bestmatchpair.get(matchpkgname).substring(1, endIndex);
        		if(!bestmatchpair.get(matchpkgname).contains(libname)){
            		System.out.println("该apk包的最佳匹配lib包不是它，是：");
            		System.out.println(bestmatchpair.get(matchpkgname)+"-----similar score:"+bestmatchSim.get(matchpkgname));	
            		classRawSimilarity0.put(matchpkgname,(double) 0);
        		}

        	}
        	n++;
        } 
        for(String pkgname:classRawSimilarity0.keySet()){
            matchpkgnamelist.add(pkgname);
            String libnameString=classNameMap0.get(pkgname);
        	libsim+=classRawSimilarity0.get(pkgname)*libpkgweighted.get(libnameString);
        }
       if (!JudgeRelation(matchpkgnamelist,apkProfile,rootPackageMap)) {
    	   libsim=calculateMaxProbability();
	}
        System.out.println("-------------第二步结束,该lib在apk中的可能性为"+libsim+"-----------------");

		return libsim;   	
    }
    
  
    private boolean JudgeRelation(List<String> matchpkgnamelist, ApkProfile apkProfile,Map<String, List<String>> rootPackageMap) {
    	List<String> apkpkgList=new ArrayList<>();
    	for (String apkpackageName:matchpkgnamelist) {
    		String pkgrootString=apkProfile.getRootPackage(apkpackageName,rootPackageMap);
    		if (!apkpkgList.contains(pkgrootString)) {
				apkpkgList.add(pkgrootString);
			}
		}   	
    	System.out.println(apkpkgList);
    	System.out.println("匹配到的apk包有"+apkpkgList.size()+"个根包");
    	if (apkpkgList.size()>1) {
			System.out.println("special situation:Package Relationship uncertain!");
			return false;
		}
		return true;
	}


	private static void fail(String message) {
        System.err.println(message);
        System.exit(0);
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
    
    public static boolean JudgePkgname(ApkProfile apkProfile){ 
        int Obfuscatedpkgnum=0;
        Map<String, ApkPackageProfile> apkPackageProfileMapSource=apkProfile.packageProfileMap;
    	System.out.println("下面输出apk的包信息：该apk一共有"+apkPackageProfileMapSource.size()+"个包");
        for(String pkgname:apkPackageProfileMapSource.keySet()){
        	if(JudgeObfuscated(pkgname)){
        		Obfuscatedpkgnum++;
        		System.out.print("该包被混淆处理过"); 
        	}    	
        	//System.out.println(pkgname);   	
        }
        if(Obfuscatedpkgnum!=0){
        System.out.println("其中有"+Obfuscatedpkgnum+"个包被模糊处理了");
		return true;
        }
        System.out.println("输入的apk没有被混淆处理过，只需要第一步检测");
        return false;

    }

    public static boolean JudgeObfuscated(String pkgname){
    	if(pkgname.contains(".a.")||pkgname.contains(".b.")||pkgname.contains(".c.")||pkgname.contains(".d.")||pkgname.contains(".e.")||pkgname.contains(".f."))
    		return true;
    	if(pkgname.equals("a")||pkgname.equals("b")||pkgname.equals("c")||pkgname.equals("d")||pkgname.equals("e")||pkgname.equals("f"))
    		return true;
    	if(pkgname.contains(".")){
    		String lastnameString=pkgname.substring(pkgname.lastIndexOf(".")+1);
    		//System.out.println(lastnameString);
    	if(lastnameString.equals("a")||lastnameString.equals("b")||lastnameString.equals("c")||lastnameString.equals("d")||lastnameString.equals("e")||lastnameString.equals("f")){
    		return true;
    	}
    		
    	}
    	return false;
    }
    
    
    public static void main(String[] args) throws IOException, XmlPullParserException {
		 String apkPath = "E:\\LibDetect实验groundtruth\\smali2apk\\2f111c93f115e6215fd62facc2d10ce8.apk";
    	String libPath0 ="G:\\libdetectiongroundtruth\\12";
       System.out.println(apkPath);
	        getdexfilepath aa=new getdexfilepath();
        List<String> dexfilepath=new ArrayList<>();
    	dexfilepath.addAll(aa.traverseFolder1(libPath0));//"G:\\libpecker备份\\袁倩婷\\lib340"
    	long current = System.currentTimeMillis();
        for(String key : set.keySet()){
        	System.out.println(key);
        	set1.add(key);
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
    		String libname=libPath.substring(libPath.lastIndexOf("\\")).substring(1);
            Lib lib = Lib.loadFromFile(libPath);
            if (lib == null) {
                fail("lib not parsed");
            }
            LibProfile libProfile = LibProfile.create(lib, targetSdkClassNameSet);        
            Filter pecker = new Filter(libProfile, apkProfile, targetSdkClassNameSet,bestmatchpair,bestmatchSim);
            System.out.println("--------------"+libPath.substring(libPath.lastIndexOf("\\"))+"\\进程开始--------------");
                //double fastmatchsim1 =  pecker.calculateMaxProbabilitytest0(libPath.substring(libPath.lastIndexOf("\\")), apkProfile);//这里是我的第二步
                System.out.println("--------------该候选者是通过第二步确定的--------------");
    	
        System.out.println("time: " + (System.currentTimeMillis() - current));
    }

}
}
