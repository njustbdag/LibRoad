package cn.fudan.libpecker.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.fudan.libpecker.model.ApkPackageProfile;
import cn.fudan.libpecker.model.ApkProfile;
import cn.fudan.libpecker.model.LibPackageProfile;
import cn.fudan.libpecker.model.LibProfile;
import cn.fudan.libpecker.model.SimpleClassProfile;
import cn.njust.common.Apk;
import cn.njust.common.Lib;
import cn.njust.common.LibPeckerConfig;
import cn.njust.common.Sdk;

public class Filtration {
	String apkPath;
List<String> candidateliblist;
Map<String, String> bestmatchpair;
Map<String, Double> bestmatchSim;
public Map<String, ApkPackageProfile> apkPackageProfileMap;//pkg name -> ApkPackageProfile
public Map<String, LibPackageProfile> libPackageProfileMap;//pkg name -> LibPackageProfile
static List<String>set1=new ArrayList<>();//set1是不需要对比检测的包，包括2类：1.主模块的包；2.已经被完美匹配到的包。

public Filtration(String apkPath,List<String> candidateliblist,Map<String, String> bestmatchpair,Map<String, Double> bestmatchSim){
	this.apkPath=apkPath;
	this.candidateliblist=candidateliblist;
	this.bestmatchpair=bestmatchpair;
	this.bestmatchSim=bestmatchSim;
}

public static void main(String[] args){
	String apkPath="E:\\LibDetect实验groundtruth\\smali2apk\\2f111c93f115e6215fd62facc2d10ce8.apk";
	List<String> candidateliblist=new ArrayList<>();
	candidateliblist.add("G:\\libdetectiongroundtruth\\lib340\\versionedparcelable-28.0.0.dex");
	candidateliblist.add("G:\\libdetectiongroundtruth\\lib340\\firebase-firestore-17.1.0.dex");
	candidateliblist.add("G:\\libdetectiongroundtruth\\lib340\\bolts-android-1.4.0.dex");
	candidateliblist.add("G:\\libdetectiongroundtruth\\lib340\\constraint-layout-1.1.3.dex");
	candidateliblist.add("G:\\libdetectiongroundtruth\\lib340\\support-v4-27.0.2.dex");
	Map<String, String> bestmatchpair=new HashMap<>();
	Map<String, Double> bestmatchSim=new HashMap<>();
	Filtration filtration=new Filtration(apkPath,candidateliblist,bestmatchpair,bestmatchSim);
	for(String libPath:candidateliblist){
		//filtration.JudgeCandidate2(apkPath, libPath);	
	}

}

/**public boolean JudgeCandidate1(Map<String,Double>libset3,String apkPath,String libPath){
	for(String aaa:libset3.keySet()){
		double NotPerfectWeight = 0;
		System.out.println(aaa+"：similarity: "+libset3.get(aaa));
		//System.out.println("这是匹配到的lib包："+LibPkgNameMap.get("\\"+aaa));
		for(String libpkg:LibPkgNameMap.get("\\"+aaa)){
			System.out.print(libpkg+"-------------weighted：");
			String libnameandlibpkg="\\"+aaa+"\\"+libpkg;
			System.out.println(LibpkgWeight.get(libnameandlibpkg));
			String perfectmatchString=PkgNameMap.get(libpkg);
			if(perfectmatchString!=null){
      		System.out.println("对应的apk包的perfectmatched：---------------"+perfectmatchString+PkgSimilarity.get(libpkg));
    		if(!perfectmatchString.subSequence(1,perfectmatchString.lastIndexOf("\\")).equals(aaa)){
  			NotPerfectWeight+=LibpkgWeight.get(libnameandlibpkg);
    		System.out.println("注意，这个lib包不是perfectmatched");	
    		}

			}

		}
		//pecker.writeTxtFile("："+apkname+"------"+aaa+"*3",filenameTemp);
		//apkLibService.addapklibbyname(apkname,aaa,1);
		System.out.println("NotPerfectWeight:"+NotPerfectWeight);
		if(NotPerfectWeight==1&libset3.get(aaa)!=1){
			MatchedLib.remove(aaa);
		}
		System.out.println("---------------------------------------------"+aaa+"分析结束---------------------------------------------");
		System.out.println("           ");
	}
}**/

public boolean JudgeCandidate2(ApkProfile apkProfile,Map<String,List<String>> rootPackageMap,String libPath){
    Sdk sdk = Sdk.loadDefaultSdk();
    if (sdk == null) {
        fail("default sdk not parsed");
    }
	Lib lib = Lib.loadFromFile(libPath);
	  if (lib == null) {
          fail("lib not parsed");
      }

      Set<String> targetSdkClassNameSet = sdk.getTargetSdkClassNameSet();
      //ApkProfile apkProfile = ApkProfile.create(apk, targetSdkClassNameSet);
      LibProfile libProfile = LibProfile.create(lib, targetSdkClassNameSet);        
      Filter pecker = new Filter(libProfile, apkProfile, targetSdkClassNameSet,bestmatchpair,bestmatchSim);
     	//Map<String,List<String>> rootPackageMap=apkProfile.rootanalysisMap(apkPath);
      System.out.println("--------------"+libPath.substring(libPath.lastIndexOf("\\"))+"\\候选者分析进程开始--------------");
      double fastmatchsim1 =  pecker.calculateMaxProbabilitytest0(libPath.substring(libPath.lastIndexOf("\\")),apkProfile,rootPackageMap);//这里是我的第二步
    	 if(fastmatchsim1<0.5){
    	      System.out.println("--------------该候选者不能通过筛选，被排除在外--------------");
    	      System.out.println("--------------"+libPath.substring(libPath.lastIndexOf("\\"))+"\\候选者分析进程完毕--------------");
    	  	return false;
    	 }
      System.out.println("--------------该候选者通过筛选--------------");
      System.out.println("--------------"+libPath.substring(libPath.lastIndexOf("\\"))+"\\候选者分析进程完毕--------------");
      System.out.println("                 ");
    
	return true;
	
}


    private static void fail(String message) {
        System.err.println(message);
        System.exit(0);
    }



}
