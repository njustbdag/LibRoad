package cn.fudan.libpecker.main;

import java.util.Map;
import java.util.Set;

import org.jf.baksmali.main;

import cn.fudan.libpecker.model.ApkProfile;
import cn.fudan.libpecker.model.LibPackageProfile;
import cn.fudan.libpecker.model.LibProfile;
import cn.njust.common.Lib;
import cn.njust.common.Sdk;

public class LibAnalysis {
	
    private String dexPath;

	public LibAnalysis(String dexPath) {
        this.dexPath = dexPath;
    }

	public int getlibpkgnum(String libname) {
		 Map<String, LibPackageProfile> libPackageProfileMap;
		String libPath=dexPath+"\\"+libname.substring(1, libname.lastIndexOf("\\"));
		Sdk sdk = Sdk.loadDefaultSdk();
        Set<String> targetSdkClassNameSet = sdk.getTargetSdkClassNameSet();	
		Lib lib = Lib.loadFromFile(libPath);
		 LibProfile libProfile = LibProfile.create(lib, targetSdkClassNameSet); 
		// System.out.println(libProfile.getRootPackage(packageName));
		 libPackageProfileMap=libProfile.packageProfileMap;
		// for (String pkgnameString:libPackageProfileMap.keySet()) {
			//System.out.println(pkgnameString);
			//System.out.println(libPackageProfileMap.get(pkgnameString).packageName);
			 //System.out.println(libProfile.getRootPackage(pkgnameString));
		//}
		 //System.out.println("这个lib的pkg数目："+libProfile.packageProfileMap.size());
		return libProfile.packageProfileMap.size();		
	}
	
	public static void main(String[] args){
		//LibAnalysis libAnalysis=new LibAnalysis();
		//libAnalysis.getlibpkgnum("\\play-services-clearcut-9.8.0.dex\\");
		float num=(float)7/19;
		System.out.println(num);
	}
	
}
