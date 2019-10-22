package cn.fudan.libpecker.model;

import cn.fudan.common.util.HashHelper;
import cn.fudan.libpecker.analysis.ClassWeightAnalysis;
import cn.fudan.libpecker.analysis.PackageSortAnalysis;
import cn.fudan.libpecker.analysis.RootPackageAnalysis;
import cn.fudan.libpecker.core.ExtractlibbyARP;
import cn.fudan.libpecker.core.ParserXML;
import cn.fudan.libpecker.core.ProcessingDirectory;
import cn.fudan.libpecker.main.defanaly;
import cn.fudan.libpecker.main.getdexfilepath;
import cn.njust.analysis.dep.DepAnalysis;
import cn.njust.analysis.profile.ClassProfile;
import cn.njust.analysis.tree.PackageNode;
import cn.njust.analysis.tree.TreeAnalysis;
import cn.njust.analysis.util.DexHelper;
import cn.njust.common.Apk;
import cn.njust.common.Lib;
import cn.njust.common.Sdk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.*;

import njust.lib.Service.ApkRootpackageInfoService;
import njust.lib.Service.LibPackagestructureService;
import njust.lib.Service.LibRootpackageInfoService;
import edu.njust.bean.LibPackagestructure;
import edu.njust.bean.LibRootpackageInfo;
import groundtruth.CopylibToapk;
import groundtruth.GetURLContent;

/**
 * Created by yuanxzhang on 19/05/2017.
 */
public class ApkProfile {
    public Map<String, ApkPackageProfile> packageProfileMap;//pkg name -> [LibPackageProfile, ...]
    public static Map<String, List<String>> rootPackageMap;//root package -> [package names, ...]
    
    
    public ApkProfile(){}

    public String getRootPackage(String packageName,Map<String, List<String>> rootPackageMap1) {
        for (String rootPackageName : rootPackageMap1.keySet()) {
            for (String pkg : rootPackageMap1.get(rootPackageName)) {
                if (pkg.equals(packageName))
                    return rootPackageName;
            }
        }
        return null;
    }
    public static boolean JudgeObfuscated(String pkgname){
    	String endString=null;
    	if (pkgname.contains(".")) {
        	endString=pkgname.substring(pkgname.lastIndexOf(".")+1);
        	//System.out.println(endString);	
		}
    	if(pkgname.contains(".a.")||pkgname.contains(".b.")||pkgname.contains(".c.")||pkgname.contains(".d.")||pkgname.contains(".e.")||pkgname.contains(".f.")
    			||pkgname.contains(".f.")||pkgname.contains(".g.")||pkgname.contains(".h.")||pkgname.contains(".i.")||pkgname.contains(".j.")||pkgname.contains(".k.")||pkgname.contains(".l.")
    			||pkgname.contains(".m.")||pkgname.contains(".n.")||pkgname.contains(".o.")||pkgname.contains(".p.")||pkgname.contains(".q.")||pkgname.contains(".r.")||pkgname.contains(".s.")||pkgname.contains(".t.")
    			||pkgname.contains(".u.")||pkgname.contains(".v.")||pkgname.contains(".w.")||pkgname.contains(".x.")||pkgname.contains(".y.")||pkgname.contains(".z."))
    		return false;
    	if (endString!=null) {
			    	if(endString.equals("a")||endString.equals("b")||endString.equals("c")||endString.equals("d")||endString.equals("e")||endString.equals("f")
    			||endString.equals("g")||endString.equals("h")||endString.equals("i")||endString.equals("j")||endString.equals("k")||endString.equals("l")
    			||endString.equals("m")||endString.equals("n")||endString.equals("o")||endString.equals("p")||endString.equals("q")||endString.equals("r")
    			||endString.equals("s")||endString.equals("t")||endString.equals("u")||endString.equals("v")||endString.equals("w")||endString.equals("x")
    			||endString.equals("y")||endString.equals("z"))
    		return false;
		}

    	if(pkgname.equals("a")||pkgname.equals("b")||pkgname.equals("c")||pkgname.equals("d")||pkgname.equals("e")||pkgname.equals("f")
    			||pkgname.equals("g")||pkgname.equals("h")||pkgname.equals("i")||pkgname.equals("j")||pkgname.equals("k")||pkgname.equals("l")
    			||pkgname.equals("m")||pkgname.equals("n")||pkgname.equals("o")||pkgname.equals("p")||pkgname.equals("q")||pkgname.equals("r")
    			||pkgname.equals("s")||pkgname.equals("t")||pkgname.equals("u")||pkgname.equals("v")||pkgname.equals("w")||pkgname.equals("x")
    			||pkgname.equals("y")||pkgname.equals("z"))
    		return false;
    	if(pkgname.contains(".")){
    		String lastnameString=pkgname.substring(pkgname.lastIndexOf(".")+1);
    		//System.out.println(lastnameString);
    	if(lastnameString.equals("a")||lastnameString.equals("b")||lastnameString.equals("c")||lastnameString.equals("d")||lastnameString.equals("e")||lastnameString.equals("f")){
    		return false;
    	}
    		
    	}
    	return true;
    }
    
    public List<String> getPackagesWithSameRoot(String packageName) {
        for (String rootPackageName : rootPackageMap.keySet()) {
            for (String pkg : rootPackageMap.get(rootPackageName)) {
                if (pkg.equals(packageName))
                    return rootPackageMap.get(rootPackageName);
            }
        }
        return null;
    }
    
    
    public static ApkProfile create(Apk apk, Set<String> targetSdkClassNameSet) {
        ApkProfile apkProfile = new ApkProfile();
        apkProfile.rootPackageMap = RootPackageAnalysis.extractRootPackages(apk);
        apkProfile.packageProfileMap = new HashMap<>();
        Set<PackageNode> apkPackages = TreeAnalysis.analyze(apk);
        List<ApkPackageProfile> apkProfiles = new ArrayList<>();
        for (PackageNode packageNode : apkPackages) {
            apkProfiles.add(new ApkPackageProfile(packageNode, targetSdkClassNameSet));
        }
        for (ApkPackageProfile profile : apkProfiles) {
            apkProfile.packageProfileMap.put(profile.packageName, profile);
        }

        apkPackages.clear();
        apkProfiles.clear();

        return apkProfile;

    }
    
    
    public static ApkProfile create1(Apk apk, Set<String> targetSdkClassNameSet) {
    	ApkProfile apkProfile = new ApkProfile();
    	apkProfile.rootPackageMap = RootPackageAnalysis.extractRootPackages(apk);

        Map<String, Integer> classBBWeightMap = ClassWeightAnalysis.getClassBBWeight(apk);
        DepAnalysis depAnalysis = new DepAnalysis(apk, (HashSet)targetSdkClassNameSet);
        Map<String, Integer> classDepWeightMap = ClassWeightAnalysis.getClassDepWeight(depAnalysis);
        Map<String, Set<SimpleClassProfile>> packageProfileMap = getClassProfilesGroupedByPackage(depAnalysis.allClassProfiles);

        apkProfile.packageProfileMap = new HashMap<>();
        for (String packageName : packageProfileMap.keySet()) {
        	apkProfile.packageProfileMap.put(packageName, new ApkPackageProfile(packageName, classBBWeightMap, classDepWeightMap, packageProfileMap.get(packageName)));
        }

        //System.out.println(classBBWeightMap);
        classBBWeightMap.clear();
        classDepWeightMap.clear();
        packageProfileMap.clear();
        return apkProfile;
    }
    
    
    private static Map<String, Set<SimpleClassProfile>> getClassProfilesGroupedByPackage(Set<ClassProfile> classProfileSet) {
        Map<String, Set<SimpleClassProfile>> packageClassProfileMap = new HashMap<>();
        for (ClassProfile classProfile : classProfileSet) {
            String packageName = DexHelper.getPackageName(classProfile.getClassName());
            if (! packageClassProfileMap.containsKey(packageName))
                packageClassProfileMap.put(packageName, new HashSet<SimpleClassProfile>());
            packageClassProfileMap.get(packageName).add(classProfile);
        }

        return packageClassProfileMap;
    }
    
    private static void fail(String message) {
        System.err.println(message);
        System.exit(0);
    }
    
    public Map<String,List<String>>  SimplerootanalysisMap(String apkpathString) {
    	Sdk sdk = Sdk.loadDefaultSdk();
        if (sdk == null) {
            fail("default sdk not parsed");
        }
        List<String> Primarymodule=new ArrayList<>();
        List<String> NonPrimarymodule=new ArrayList<>();
        String apkpath=apkpathString;
        String apknameString=apkpath.substring(apkpath.lastIndexOf("\\")+1);
        String apkAndroidManifestPathString="G:\\libdetectiongroundtruth\\APKset\\apk2smali\\"+apknameString+"\\AndroidManifest.xml";
        System.out.println(apkAndroidManifestPathString);
		File file1 = new File(apkAndroidManifestPathString);
		ParserXML parserXML=new ParserXML();
		String LibList1=parserXML.readtxt(file1);
		Primarymodule.add(LibList1);
		//System.out.println(LibList1);
        Apk apk = Apk.loadFromFile(apkpath);
        if (apk == null) {
            fail("apk not parsed");
        }
        Set<String> targetSdkClassNameSet = sdk.getTargetSdkClassNameSet();
        ApkProfile apkProfile = ApkProfile.create1(apk, targetSdkClassNameSet);
        Map<String,ApkPackageProfile> apkPackageProfilemap=apkProfile.packageProfileMap;
        //System.out.println("apkPackageProfilemap.keySet()"+apkPackageProfilemap.keySet());
    	/**for (String apkpkg:apkPackageProfilemap.keySet()) {
			System.out.println("包名:"+apkpkg);
			ApkPackageProfile apkPackageProfile=apkPackageProfilemap.get(apkpkg);
			List<String> ClassList = apkPackageProfile.getWeightClassList();
			for (String classname:ClassList) {
				System.out.println("类名:"+classname);
				System.out.println("类权重:"+apkPackageProfile.getClassWeight(classname));
				
			}
		}**/
    	int cou=rootPackageMap.size();
    	System.out.println("根包有"+cou+"个");
    	System.out.println("主模块根包："+LibList1);
    	List<String> subpkg1=rootPackageMap.get(LibList1);
    	if (subpkg1!=null) {
			System.out.println("主模块根包有"+subpkg1.size()+"个子包");
		}   	
    	else {
    		System.out.println("主模块根包有"+0+"个子包");
		}
    	rootPackageMap.remove(LibList1);
    	System.out.println("排除主模块后根包有"+rootPackageMap.size()+"个");
    	for (String root:rootPackageMap.keySet()) {
    		System.out.println(root); 
    		if (root.equals("com.google")) {
				//Dealwithgoogle(root);
			}     		
    		/**if (root.equals("android.support")) {//开始根据包之间的依赖关系进行再次分组，已实现，但是没什么用
    			List<String> subpkg2=rootPackageMap.get(root);
    			Printsubpkg(root);
    			DepAnalysis pecker2=new DepAnalysis(apk);
    			defanaly pecker = new defanaly(apkProfile, targetSdkClassNameSet,pecker2); 
    			Map<String, List<String>> groupRoot=pecker.defanalyrootpackage(pecker2,root,subpkg2);
    			groupRootpkg(groupRoot);
    			//pecker.defanalyrootpackage(pecker2,root,subpkgString);
			}**/
		}
    	System.out.println("以上根包对应的子包个数:");
    	for (String root:rootPackageMap.keySet()) {
    		if (!root.equals(".")) {
        		System.out.println("************分析"+root+"根包开始****************");
        		//ApkPackageProfile apkPackageProfile0=apkPackageProfilemap.get(root);
        		//Map<String, SimpleClassProfile> classProfileMap0=apkPackageProfile0.classProfileMap;
        		//System.out.println(classProfileMap0.keySet());
            		//System.out.println(apkPackageProfile0.includeClassNum);	
            		System.out.println(root+"包括以下子包：");
        		List<String> subpkg=rootPackageMap.get(root);
            	System.out.println(subpkg.size());
            	for (String subpkgstring : subpkg) {
            		//if (subpkgstring.contains("uk.co.senab.photoview")) {
            		System.out.println("apk包名："+subpkgstring);
            		ApkPackageProfile apkPackageProfile=apkPackageProfilemap.get(subpkgstring);
            		Map<String, SimpleClassProfile> classProfileMap=apkPackageProfile.classProfileMap;
            		System.out.println("apk包的类数目:"+apkPackageProfile.includeClassNum);	
            		//System.out.println("apk包里的类:"+classProfileMap.keySet());
            		//}
    				//System.out.println(subpkgstring);
    			}
            	System.out.println("************分析"+root+"根包，生成该根包的包树****************");
    			Map<String,Integer> levelListtest=new HashMap<>();
    			ConversionPkgName conversionPkgName=new ConversionPkgName(subpkg,levelListtest);
    			List<String> conversedList=conversionPkgName.PackageTreeGenerator();
    				StringBuilder PackagestructureHashList = new StringBuilder();
    				for (String astring : conversedList) {
    					PackagestructureHashList.append(astring);	
    			}				
    				String PackagestructureHash=HashHelper.hash(PackagestructureHashList.toString());
    				System.out.println("哈希值："+PackagestructureHash);
                    System.out.println("************分析"+root+"根包完毕****************");
			}
    	}
		return rootPackageMap; 
		
	}
    
    public static List<String>  getrootpkg(String apkpathString) {
    	Sdk sdk = Sdk.loadDefaultSdk();
        if (sdk == null) {
            fail("default sdk not parsed");
        }
        List<String> Primarymodule=new ArrayList<>();
        List<String> NonPrimarymodule=new ArrayList<>();
        String apkpath=apkpathString;
        String apknameString=apkpath.substring(apkpath.lastIndexOf("\\")+1);
        Apk apk = Apk.loadFromFile(apkpath);
        if (apk == null) {
            fail("apk not parsed");
        }
        Set<String> targetSdkClassNameSet = sdk.getTargetSdkClassNameSet();
        ApkProfile apkProfile = ApkProfile.create(apk, targetSdkClassNameSet);
    	Set<String> NonPrimarymodule1=apkProfile.rootPackageMap.keySet();
    	NonPrimarymodule.addAll(NonPrimarymodule1);
    	return NonPrimarymodule;
	}
    public Map<String,List<String>>  NotprintrootanalysisMap(String apkpathString) {
    	Sdk sdk = Sdk.loadDefaultSdk();
        if (sdk == null) {
            fail("default sdk not parsed");
        }
        List<String> Primarymodule=new ArrayList<>();
        List<String> NonPrimarymodule=new ArrayList<>();
        String apkpath=apkpathString;
        String apknameString=apkpath.substring(apkpath.lastIndexOf("\\")+1);
        String apkAndroidManifestPathString="G:\\libdetectiongroundtruth\\APKset\\apk2smali\\"+apknameString+"\\AndroidManifest.xml";
        System.out.println(apkAndroidManifestPathString);
		File file1 = new File(apkAndroidManifestPathString);
		ParserXML parserXML=new ParserXML();
		String LibList1=parserXML.readtxt(file1);
		Primarymodule.add(LibList1);
		//System.out.println(LibList1);
        Apk apk = Apk.loadFromFile(apkpath);
        if (apk == null) {
            fail("apk not parsed");
        }
        Set<String> targetSdkClassNameSet = sdk.getTargetSdkClassNameSet();
        ApkProfile apkProfile = ApkProfile.create1(apk, targetSdkClassNameSet);
        Map<String,ApkPackageProfile> apkPackageProfilemap=apkProfile.packageProfileMap;
    	int cou=rootPackageMap.size();
    	NonPrimarymodule=(List<String>) apkProfile.packageProfileMap.keySet();
    	List<String> subpkg1=rootPackageMap.get(LibList1);
    	rootPackageMap.remove(LibList1);
		return rootPackageMap; 
		
	}
    /**
     * 把apk的根包信息存到数据库ApkRootpackageInfoService
     * @param apkpathString
     * @return
     * @throws IOException 
     */
    public void  fillApkRootpackageInfoService(String apkpathString) throws IOException {
    	ProcessingDirectory processingDirectory=new ProcessingDirectory();
    	Sdk sdk = Sdk.loadDefaultSdk();
        if (sdk == null) {
            fail("default sdk not parsed");
        }
        String apkpath=apkpathString;
        String apknameString=apkpath.substring(apkpath.lastIndexOf("\\")+1);
        Apk apk = Apk.loadFromFile(apkpath);
        if (apk == null) {
            fail("apk not parsed");
        }
        Set<String> targetSdkClassNameSet = sdk.getTargetSdkClassNameSet();
        ApkProfile apkProfile = ApkProfile.create(apk, targetSdkClassNameSet);
        Map<String,ApkPackageProfile> apkPackageProfilemap=apkProfile.packageProfileMap;
    	int cou=rootPackageMap.size();
    	System.out.println("根包有"+cou+"个");
    	System.out.println("以上根包对应的子包个数:");
    	for (String root:rootPackageMap.keySet()) {
    		StringBuilder PackagestructureHashList0 = new StringBuilder();
    		if (!root.equals(".")) {
        		System.out.println("************分析"+root+"根包开始****************");
        		String unobfusDir=processingDirectory.InterceptunobfusDir(root);
        		int rootDirnum=Directorycount(root);
        		//ApkPackageProfile apkPackageProfile0=apkPackageProfilemap.get(root);
        		//Map<String, SimpleClassProfile> classProfileMap0=apkPackageProfile0.classProfileMap;
        		//System.out.println(classProfileMap0.keySet());
            		//System.out.println(apkPackageProfile0.includeClassNum);	
            		//System.out.println(root+"包括以下子包：");
        		List<String> subpkg=rootPackageMap.get(root);
        		Set<String> tempSet=new HashSet<>();
        		tempSet.addAll(subpkg);
        		int subpckNum=subpkg.size();
            	System.out.println("子包数目"+subpckNum);
            	for (String subpkgstring : subpkg) {
            		//if (subpkgstring.contains("uk.co.senab.photoview")) {
            		//System.out.println("apk包名："+subpkgstring);
            		ApkPackageProfile apkPackageProfile=apkPackageProfilemap.get(subpkgstring);
            		Map<String, SimpleClassProfile> classProfileMap=apkPackageProfile.classProfileMap;
            		//System.out.println("apk包的类数目:"+apkPackageProfile.includeClassNum);	
            		//System.out.println("apk包里的类:"+classProfileMap.keySet());
            		//}
    				//System.out.println(subpkgstring);
    			}
            	System.out.println("************分析"+root+"根包，生成该根包的包树****************");
    			Map<String,Integer> levelListtest=new HashMap<>();
    			ConversionPkgName conversionPkgName=new ConversionPkgName(subpkg,levelListtest);
    			List<String> conversedList=conversionPkgName.PackageTreeGenerator();//这是旧的转换方法
    			//PackageSortAnalysis packageSortAnalysis=new PackageSortAnalysis(tempSet);
    			//TreeSet<String> conversedList=packageSortAnalysis.sortBegin();//这是新的转换方法
    				StringBuilder PackagestructureHashList = new StringBuilder();
    				String PackagestructureList = null;
    				for (String astring : conversedList) {
    					PackagestructureList=PackagestructureList+astring+",";
    					PackagestructureHashList0.append(astring);	
    					PackagestructureHashList.append(astring);
    			}				
    				PackagestructureList=PackagestructureList.substring(4);
    				String PackagestructureHash=HashHelper.hash(PackagestructureHashList0.toString());
    				System.out.println("该根包结构："+PackagestructureList);
    				System.out.println("该根包哈希值："+PackagestructureHash);
    				String content=apknameString+"*"+root+"*"+subpckNum+"*"+rootDirnum+"*"+PackagestructureList+"*"+PackagestructureHash;
    				writeTxtFile(content,"C://Users//ZJY//Desktop//q.txt");
    				//ApkRootpackageInfoService.addonepackage(apknameString, root, subpckNum, rootDirnum, PackagestructureList,PackagestructureHash);
    				//LibPackagestructureService LibPackagestructureService=new LibPackagestructureService();
					//List<LibRootpackageInfo>  Lib=LibRootpackageInfoService.findallbyRootandHash(unobfusDir, PackagestructureHash);    					
        		System.out.println("************分析"+root+"根包完毕****************");
        		System.out.println(); 
			}
    	} 
	}
    
    public static boolean writeTxtFile(String newStr,String filenameTemp) throws IOException {
		// 先读取原有文件内容，然后进行写入操作
		boolean flag = false;
		String filein = newStr + "\r\n";
		String temp = "";
 
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
 
		FileOutputStream fos = null;
		PrintWriter pw = null;
		try {
			// 文件路径
			File file = new File(filenameTemp);
			// 将文件读入输入流
			fis = new FileInputStream(file);
			isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);
			StringBuffer buf = new StringBuffer();

            
            
			// 保存该文件原有的内容
			for (int j = 1; (temp = br.readLine()) != null; j++) {
				buf = buf.append(temp);
				// System.getProperty("line.separator")
				// 行与行之间的分隔符 相当于“\n”
				buf = buf.append(System.getProperty("line.separator"));
			}
			
			buf.append(filein);
 
			fos = new FileOutputStream(file);
			pw = new PrintWriter(fos);
			pw.write(buf.toString().toCharArray());
			pw.flush();
			flag = true;
		} catch (IOException e1) {
			// TODO 自动生成 catch 块
			throw e1;
		} finally {
			if (pw != null) {
				pw.close();
			}
			if (fos != null) {
				fos.close();
			}
			if (br != null) {
				br.close();
			}
			if (isr != null) {
				isr.close();
			}
			if (fis != null) {
				fis.close();
			}
		}
		return flag;
	}
    
    public Map<String,List<String>>  rootanalysisMap(String apkpathString) throws Exception {
    	Map<String,List<String>> matchResultMap=new HashMap<String, List<String>>();
    	ProcessingDirectory processingDirectory=new ProcessingDirectory();
    	Sdk sdk = Sdk.loadDefaultSdk();
        if (sdk == null) {
            fail("default sdk not parsed");
        }
        List<String> Primarymodule=new ArrayList<>();
        List<String> NonPrimarymodule=new ArrayList<>();
        String apkpath=apkpathString;
        String apknameString=apkpath.substring(apkpath.lastIndexOf("\\")+1).trim();
        //String apkAndroidManifestPathString="G:\\libdetectiongroundtruth\\APKset\\apk2smali\\"+apknameString+"\\AndroidManifest.xml";
        //System.out.println(apkAndroidManifestPathString);
		//File file1 = new File(apkAndroidManifestPathString);
		//ParserXML parserXML=new ParserXML();
		//String LibList1=parserXML.readtxt(file1);
		//Primarymodule.add(LibList1);
		//System.out.println(LibList1);
        Apk apk = Apk.loadFromFile(apkpath);
        if (apk == null) {
            fail("apk not parsed");
        }
        Set<String> targetSdkClassNameSet = sdk.getTargetSdkClassNameSet();
        ApkProfile apkProfile = ApkProfile.create1(apk, targetSdkClassNameSet);
        Map<String,ApkPackageProfile> apkPackageProfilemap=apkProfile.packageProfileMap;
        //System.out.println("apkPackageProfilemap.keySet()"+apkPackageProfilemap.keySet());
    	/**for (String apkpkg:apkPackageProfilemap.keySet()) {
			System.out.println("包名:"+apkpkg);
			ApkPackageProfile apkPackageProfile=apkPackageProfilemap.get(apkpkg);
			List<String> ClassList = apkPackageProfile.getWeightClassList();
			for (String classname:ClassList) {
				System.out.println("类名:"+classname);
				System.out.println("类权重:"+apkPackageProfile.getClassWeight(classname));
				
			}
		}**/
    	int cou=rootPackageMap.size();
    	System.out.println("根包有"+cou+"个");
    	//System.out.println("主模块根包："+LibList1);
    	//List<String> subpkg1=rootPackageMap.get(LibList1);
    	/**if (subpkg1!=null) {
			System.out.println("主模块根包有"+subpkg1.size()+"个子包");
		}   	
    	else {
    		System.out.println("主模块根包有"+0+"个子包");
		}
    	rootPackageMap.remove(LibList1);**/
    	System.out.println("排除主模块后根包有"+rootPackageMap.size()+"个");
    	for (String root:rootPackageMap.keySet()) {
    		System.out.println(root); 
    		if (root.equals("com.google")) {
				//Dealwithgoogle(root);
			}     		
    		/**if (root.equals("android.support")) {//开始根据包之间的依赖关系进行再次分组，已实现，但是没什么用
    			List<String> subpkg2=rootPackageMap.get(root);
    			Printsubpkg(root);
    			DepAnalysis pecker2=new DepAnalysis(apk);
    			defanaly pecker = new defanaly(apkProfile, targetSdkClassNameSet,pecker2); 
    			Map<String, List<String>> groupRoot=pecker.defanalyrootpackage(pecker2,root,subpkg2);
    			groupRootpkg(groupRoot);
    			//pecker.defanalyrootpackage(pecker2,root,subpkgString);
			}**/
		}
    	System.out.println("以上根包对应的子包个数:");
    	for (String root:rootPackageMap.keySet()) {
    		if (!root.equals(".")) {
        		System.out.println("************分析"+root+"根包开始****************");
        		String unobfusDir=processingDirectory.InterceptunobfusDir(root);
        		int rootDirnum=Directorycount(root);
        		//ApkPackageProfile apkPackageProfile0=apkPackageProfilemap.get(root);
        		//Map<String, SimpleClassProfile> classProfileMap0=apkPackageProfile0.classProfileMap;
        		//System.out.println(classProfileMap0.keySet());
            		//System.out.println(apkPackageProfile0.includeClassNum);	
            		//System.out.println(root+"包括以下子包：");
        		List<String> subpkg=rootPackageMap.get(root);
            	System.out.println("子包数目"+subpkg.size());
            	for (String subpkgstring : subpkg) {
            		//if (subpkgstring.contains("uk.co.senab.photoview")) {
            		//System.out.println("apk包名："+subpkgstring);
            		ApkPackageProfile apkPackageProfile=apkPackageProfilemap.get(subpkgstring);
            		Map<String, SimpleClassProfile> classProfileMap=apkPackageProfile.classProfileMap;
            		//System.out.println("apk包的类数目:"+apkPackageProfile.includeClassNum);	
            		//System.out.println("apk包里的类:"+classProfileMap.keySet());
            		//}
    				//System.out.println(subpkgstring);
    			}
            	System.out.println("************分析"+root+"根包，生成该根包的包树****************");
    			Map<String,Integer> levelListtest=new HashMap<>();
    			ConversionPkgName conversionPkgName=new ConversionPkgName(subpkg,levelListtest);
    			List<String> conversedList=conversionPkgName.PackageTreeGenerator();
    				StringBuilder PackagestructureHashList = new StringBuilder();
    				String PackagestructureList = null;
    				for (String astring : conversedList) {
    					//System.out.println(astring); 
    					PackagestructureHashList.append(astring);	
    			}				
    				/**
    				 * 把字符串变成List<String>
    				 */
    				 /**String[] strArr =PackagestructureList.split(",");
    				    System.out.println(strArr.length); 
    			        for (int i = 0; i < strArr.length; ++i){
    			        System.out.println(strArr[i]);
    			        }**/
    				String PackagestructureHash=HashHelper.hash(PackagestructureHashList.toString());
    				System.out.println("哈希值："+PackagestructureHash);
    				//LibPackagestructureService LibPackagestructureService=new LibPackagestructureService();
					//List<LibRootpackageInfo>  Lib=LibRootpackageInfoService.findallbyRootandHash(unobfusDir, PackagestructureHash);    					
    				List<LibRootpackageInfo>  Lib1=LibRootpackageInfoService.findallbyunobfusDirandHash(root,unobfusDir,rootDirnum, PackagestructureHash,conversedList);
    				List<String> libname=new ArrayList<>();
    				if (Lib1!=null&&Lib1.size()>300) {//匹配到的候选lib太多，每个版本保留一个进行检测
    					//System.out.println("dfhdlghueafhioojfdslfji");
    					HashSet<String> libtypeHashSet=new HashSet<>();
    					 for (LibRootpackageInfo astring : Lib1) {
    						 if (!astring.getLibName().contains(apknameString)&!astring.getLibName().equals("fromapkAnalysis")) {
    							 if (!libtypeHashSet.contains(astring.getLibType())) {
    								 libtypeHashSet.add(astring.getLibType());
       							 libname.add(astring.getLibName());
								} 
    						}					
    					}
    					 //System.out.println(Lib1.size());
    					// System.out.println(libtypeHashSet.size());
    					 //System.out.println(libname.size());
					}
    				else if (Lib1!=null) {
					 for (LibRootpackageInfo astring : Lib1) {
						 if (!astring.getLibName().contains(apknameString)&&!astring.getLibName().equals("fromapkAnalysis")) {
							libname.add(astring.getLibName());
						}			
					}	
					}
    				/**if (libname.size()<4&JudgeObfuscated(root)){
    					libname.addAll(CopylibToapk.findfirstclassfolder(root,apknameString));
    					if (libname.size()<4){
    						libname.addAll(ExtractlibbyARP.findapkbyARPname(root,apknameString)); 
    					}
					}**/
    				//System.out.println(libname.size());
    				matchResultMap.put(root, libname);
    				//System.out.println(matchResultMap.get(root).size());
    				/***if (Lib.size()!=0) {
    					System.out.println("在数据库中找到包名和包结构匹配的lib:");
    					for (LibRootpackageInfo libPackagestructure : Lib) {
    						String libType=libPackagestructure.getLibName();
    						System.out.println(libType);
    					}
    				}else if (Lib1.size()!=0) {
    					System.out.println("在数据库中找到包名和包结构匹配的lib:");
    					for (LibRootpackageInfo libPackagestructure : Lib1) {
    						String libType=libPackagestructure.getLibName();
    						System.out.println(libType);
    					}
					}else{
    					System.out.println("Not Found!");
					}**/
        		System.out.println("************分析"+root+"根包完毕****************");
        		System.out.println();
			}
    	} 
		return matchResultMap;  
	}
    
    
    public static int Directorycount(String Directory) {
    	if (Directory.contains("\\")) {
    	  	Directory=Directory.substring(Directory.lastIndexOf("\\"));
		}
    	int num = 0;
        // 循环遍历每个字符，判断是否是字符 a ，如果是，累加次数
       for (int i=0;i<Directory.length();i++)
       {
           // 获取每个字符，判断是否是字符a
           if (Directory.charAt(i)=='.') {
               // 累加统计次数
               num++; 
           }
       }
       System.out.println(num+"级目录");
		return num;
		
	}
    
    private void Dealwithgoogle(String root) {//把com.google.android.gms.dynamite用.分隔成多个字符串，并统计每一个字符串出现的次数
    	List<String> subpkg=rootPackageMap.get(root);
    	List<String> root_subpkg=new ArrayList<>();
    	List<String> Repeatroot_subpkg=new ArrayList<>();
    	Map<String, Integer> Root_Byte=new HashMap<>();
    	for (String subpkgstring : subpkg) {
    		System.out.println(subpkgstring);
    		String str = subpkgstring.replace(".", ",");
        	String[]  strs=str.split(",");
        	for(int i=0,len=strs.length;i<len;i++){
        		String RootByte=strs[i].toString();
        		if (!root_subpkg.contains(RootByte)) {
        			root_subpkg.add(RootByte);
        			Root_Byte.put(RootByte, 1);
				}else {
					int count=Root_Byte.get(RootByte);
					count++;
					Root_Byte.put(RootByte, count);
					//System.out.println(Root_Byte.get(RootByte));
				}
        	}
		}
    	for (String rootstring : root_subpkg) {
			System.out.println(rootstring);
			System.out.println(Root_Byte.get(rootstring));
		}
    	//System.out.println(root_subpkg);
	}

	private void groupRootpkg(Map<String, List<String>> groupRoot) {
    	Map<Integer, List<String>> groupRootmaxMap=new HashMap<>();
    	Map<String, List<String>> tempgroupRootMap=new HashMap<>();
    	List<String>groupRss=new ArrayList<>();
    	int groupNum=0;
    	tempgroupRootMap=groupRoot;
		for (String subpkg : groupRoot.keySet()) {
			groupRss.addAll(groupRoot.get(subpkg));
			//System.out.println(subpkg);
			//System.out.println(groupRoot.get(subpkg));						
	}
		while(groupRss.size()!=0){
			System.out.println("groupRss.size()"+groupRss.size());
			groupNum++;
			Set<String> set=new HashSet();
			for (String subpkg : groupRoot.keySet()) {
				if (set.size()==0) {
					set.addAll(groupRoot.get(subpkg));
					groupRss.removeAll(groupRoot.get(subpkg));
				}
				if (groupRoot.get(subpkg)!=null) {
					for (String depsubpkg :groupRoot.get(subpkg)) {
					if (set.contains(depsubpkg)) {
						set.addAll(groupRoot.get(subpkg));
						groupRss.removeAll(groupRoot.get(subpkg));
					}
				}	
				}
						
		}
			List<String>groupRList=new ArrayList<>();
			for (String a : set) {
				groupRList.add(a);
			}			
			groupRootmaxMap.put(groupNum, groupRList);
			System.out.println("groupRss.size()"+groupRss.size());
		}
		for (int groupNumq : groupRootmaxMap.keySet()) {
			System.out.println(groupNum);
			System.out.println(groupRootmaxMap.get(groupNumq).size()+"个");
			for (String astring : groupRootmaxMap.get(groupNumq)) {
				System.out.println(astring);	
			}
		}
	}
	
    public static void createbatFile(String path,String filename) throws IOException{
        File file=new File(path+"/"+filename);
        if(!file.exists())
            file.createNewFile();
    }

	private void Printsubpkg(String root) {    	 		        	      	
        	List<String> subpkg=rootPackageMap.get(root);
        	System.out.println("这个根包有"+subpkg.size()+"个子包");
        	//System.out.println(subpkg.size());
        	for(String sub:subpkg){
        	System.out.println(sub);        	
		}
        	System.out.println();
	}

	public static void main(String[] args) throws IOException{
		/**String apkPath = null;
	     String libPath =null;
       if (args == null || args.length == 2) {
       	 apkPath = args[0];
            libPath = args[1];
       }
       else {
           fail("Usage: java -cp LibPecker3.jar cn.fudan.libpecker.mainProfileBasedLibPecker <apk_path> <lib_path>");
       }**/
		long current = System.currentTimeMillis();
    	ApkProfile apkProfile=new ApkProfile();    	
        List<String> apkfilepath=new ArrayList<>();
        GetURLContent GetURLContent=new GetURLContent();
        //ApkRootpackageInfoService apkRootpackageInfoService=new ApkRootpackageInfoService();
        getdexfilepath aa=new getdexfilepath();
    	apkfilepath.addAll(aa.traverseFolder1("D:\\GooglePlay100"));
    	for (String apkstring : apkfilepath) {
    		String apkname=apkstring.substring(apkstring.lastIndexOf("\\")+1);
    		//if (ApkRootpackageInfoService.findonebyApkName(apkname)) {
    			System.out.println(apkname);
    			System.out.println(apkstring);
    			//apkProfile.fillApkRootpackageInfoService(apkstring);
			//}
		}
    	/**
    	 * 把获取到的信息填入数据库
    	 
    	  List<String> contentList=GetURLContent.readallTxt("C://Users//ZJY//Desktop//q.txt");
    	for (String content : contentList) {
    		String[] s = content.split("\\*");
    		int subpckNum=Integer.parseInt(s[2]);
    		int directorynum=Integer.parseInt(s[3]);
    		ApkRootpackageInfoService.addonepackage(s[0], s[1],subpckNum,directorynum,s[4],s[5]);
		}*/
        	//String apkpathString="G:\\libdetectiongroundtruth\\APKset\\bacth2\\85_Cafe_v1.0.8_apkpure.com.apk";
    		//String apkname=apkPath.substring(apkPath.lastIndexOf("\\")+1);
    	//String apkpathString="G:\\libdetectiongroundtruth\\APKset\\bacth2\\8891新車_v2.24.1_apkpure.com.apk";
    	//apkProfile.fillApkRootpackageInfoService(apkPath);
		System.out.println("time: " + (System.currentTimeMillis() - current));
    	 
     	
    }


    
    
}


