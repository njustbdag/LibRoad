package cn.fudan.libpecker.main;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import njust.lib.Service.LibClassInfoService;
import njust.lib.Service.LibPackagestructureService;
import njust.lib.Service.LibRootpackageInfoService;
import njust.lib.Service.LibSubpackageInfoService;
import njust.lib.Service.LitelibClassInfoService;
import cn.fudan.common.util.HashHelper;
import cn.fudan.libpecker.core.ParserXML;
import cn.fudan.libpecker.core.ProcessingDirectory;
import cn.fudan.libpecker.model.ApkPackageProfile;
import cn.fudan.libpecker.model.ApkProfile;
import cn.fudan.libpecker.model.ConversionPkgName;
import cn.fudan.libpecker.model.LibPackageProfile;
import cn.fudan.libpecker.model.LibProfile;
import cn.fudan.libpecker.model.SimpleClassProfile;
import cn.njust.common.Apk;
import cn.njust.common.Lib;
import cn.njust.common.Sdk;
import edu.njust.bean.LibClassInfo;
import edu.njust.bean.LibPackagestructure;
import groundtruth.FindTxtContent;

public class Integrityquickmatch {
	static ApkProfile apkProfile;
	static Map<String,String> finalMatchedrootpkg;
	static String apkPath;
	 public static Map<String, ApkPackageProfile> apkPackageProfileMapcom;//pkg name -> ApkPackageProfile
	 public static String primarymodulename;
	 public Integrityquickmatch(String apkPath,ApkProfile apkProfile,Map<String,String> finalMatchedrootpkg) {
		this.apkPath=apkPath;
		 this.apkProfile=apkProfile;
		this.finalMatchedrootpkg=finalMatchedrootpkg;
	}

    private static void fail(String message) {
        System.err.println(message);
        System.exit(0);
    }
    public static void start(Map<String, List<String>> rootPackageMap) throws Exception {
    	for (String rootString :  rootPackageMap.keySet()) {
			System.out.println("根包："+rootString);
			if (primarymodulename.equals(rootString)) {
				System.out.println("此包是该apk根包，不需要分析");
			}else {
				for (String subString :  rootPackageMap.get(rootString)) {
				System.out.println("子包："+subString);
				ApkPackageProfile apksubPackageProfile=apkPackageProfileMapcom.get(subString);
				//System.out.println("该子包的类数目："+apksubPackageProfile.includeClassNum);
				LibSubpackageInfoService.getallBypackagename(subString);
           	 /**for (String classstring : apksubPackageProfile.classProfileMap.keySet()) {
         			System.out.println("类名:"+classstring);
         			String classHash=apksubPackageProfile.classProfileMap.get(classstring).getClassHash();
         			String classHashStrict=apksubPackageProfile.classProfileMap.get(classstring).getClassHashStrict();
         			System.out.println(classHash);
         			System.out.println(classHashStrict);
         			//LitelibClassInfoService.getOneByclassinfo(classstring,classHash,classHashStrict);
         			//List<LibClassInfo>  Lib =LibClassInfoService.getallByclassinfo(classstring);
           	 }**/
			}
			}

			System.out.println();
		}
    }
	   public static void main(String[] args) throws Exception {
		   String apkpath="G:\\libdetectiongroundtruth\\APKset\\bacth2\\2018_Arabic_Mehndi_Designs_v1.0_apkpure.com.apk";
	        Apk apk = Apk.loadFromFile(apkpath);
	        String apknameString=apkpath.substring(apkpath.lastIndexOf("\\")+1);
	        String apkAndroidManifestPathString="G:\\libdetectiongroundtruth\\APKset\\apk2smali\\"+apknameString+"\\AndroidManifest.xml";
	        System.out.println(apkAndroidManifestPathString);
			File file1 = new File(apkAndroidManifestPathString);
			ParserXML parserXML=new ParserXML();
			primarymodulename=parserXML.readtxt(file1);
	        Sdk sdk = Sdk.loadDefaultSdk();
	        if (sdk == null) {
	            fail("default sdk not parsed");
	        }
	        if (apk == null) {
	            fail("apk not parsed");
	        }
	        Set<String> targetSdkClassNameSet = sdk.getTargetSdkClassNameSet();
	        ApkProfile apkProfile = ApkProfile.create(apk, targetSdkClassNameSet);
	        ApkProfile apkProfile1 = ApkProfile.create1(apk, targetSdkClassNameSet);
	        Map<String, List<String>> rootPackageMap=apkProfile.rootPackageMap;
	        apkPackageProfileMapcom=apkProfile1.packageProfileMap;
	        Map<String,ApkPackageProfile> apkPackageProfilemap=apkProfile1.packageProfileMap;
	        start(rootPackageMap);	 
	        IntegrityAnalysis();
	}

	   public static void IntegrityAnalysis() throws Exception {
		   Map<String,List<String>> Matchedrootpkg =SortResult();	
		   
	}

	@SuppressWarnings("null")
	private static Map<String,List<String>> SortResult() throws Exception {
		List<String> dexfilepath=new ArrayList<>();
		Map<String,List<String>> Matchedrootpkg = new HashMap<>();
		List<String> apkpkgname=new ArrayList<>();
		for (String dexname : finalMatchedrootpkg.keySet()) {
			String apkpkg=finalMatchedrootpkg.get(dexname);
			if (!apkpkgname.contains(apkpkg)) {
				apkpkgname.add(apkpkg);
				List<String> dexpkgname=new ArrayList<>();
				for (String astring : finalMatchedrootpkg.keySet()) {
					if (finalMatchedrootpkg.get(astring).equals(apkpkg)) {
						dexpkgname.add(astring);
					}
				}

				Matchedrootpkg.put(apkpkg, dexpkgname);
			}
		}
		for (String root : Matchedrootpkg.keySet()) {
			System.out.println(root);
			for (String typestring :Matchedrootpkg.get(root)) {
				System.out.println(typestring);
				dexfilepath.addAll(FindTxtContent.readallTxt("G:\\libdetectiongroundtruth\\dexlib\\所有dex的路径.txt",typestring));
			}
			Map<String, List<String>> rootPackageMap=apkProfile.rootPackageMap;
			List<String> subpkg=rootPackageMap.get(root);
			System.out.println("子包个数："+subpkg.size());
			if (subpkg.size()<3) {
				System.out.println("apk根包结构太简单，包结构树匹配方法无效");
			}else {
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
				/**LibPackagestructureService LibPackagestructureService=new LibPackagestructureService();
				List<LibPackagestructure>  Lib=LibPackagestructureService.findallBylibhashvalue(PackagestructureHash);	
				if (Lib.size()!=0) {
					System.out.println("在数据库中找到包结构匹配的lib:");
					for (LibPackagestructure libPackagestructure : Lib) {
						String libType=libPackagestructure.getLibName();
						System.out.println(libType);
						System.out.println(LibRootpackageInfoService.findonebylibType(libType));
					}
				}else {
					System.out.println("没有在数据库中找到包结构匹配的lib");
				}**/
    		System.out.println("************分析"+root+"根包完毕****************");
			System.out.println();
			}
		} 
		Integritymatchagain.start(apkPath, dexfilepath);
		return Matchedrootpkg; 
		
	}
	

}
