package cn.fudan.libpecker.main;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.fudan.libpecker.model.ApkPackageProfile;
import cn.fudan.libpecker.model.ApkProfile;
import cn.fudan.libpecker.model.LibProfile;
import cn.njust.common.Lib;
import cn.njust.common.Sdk;

public class SupplementDetect {
	private static Map<String, String> finalMatchedrootpkg;//<libname,rootPackage>
	private static Map<String, Double> finalMatchedLib;//<libname,simscore>
	private static List<String> retestRootPackageList;//<apkrootPackage>
	private static String newdexPath;//<lib>
    public Map<String, ApkPackageProfile> apkPackageProfileMap;//pkg name -> ApkPackageProfile
	private static ApkProfile apkProfile;

	public SupplementDetect(ApkProfile apkProfile,Map<String, String> finalMatchedrootpkg,Map<String, Double> finalMatchedLib,
			List<String> retestRootPackageList, String newdexPath) {
		this.finalMatchedrootpkg=finalMatchedrootpkg;
		this.finalMatchedLib=finalMatchedLib;
		this.retestRootPackageList=retestRootPackageList;
		this.newdexPath=newdexPath;	
		this.apkProfile=apkProfile;
		this.apkPackageProfileMap = apkProfile.packageProfileMap;
	}
	
	   public static String getFileSize(String path) {
		    String resourceSizeMb = null;
		    try {
		        // 指定路径即可
		        File f = new File(path);

		        FileInputStream fis = new FileInputStream(f);

		        DecimalFormat df = new DecimalFormat("#.##");

		        // double resourceSize = (double)((double) fis.available() / 1024);
		        // ELog.e(TAG, "resourceSize:" + resourceSize);

		        if((double)((double) fis.available() / 1024) > 1000) {
		            resourceSizeMb = df.format((double)((double) fis.available() / 1024 / 1024)) + "MB";
		        } else {
		            resourceSizeMb= df.format((double)((double) fis.available() / 1024)) + "KB";
		        }

		    } catch (Exception e) {
		        e.printStackTrace();
		        resourceSizeMb = null;
		    } 
		    return resourceSizeMb;
		}
	    
	
	public static void start() throws Exception{
		Sdk sdk = Sdk.loadDefaultSdk();
		 Set<String> targetSdkClassNameSet = sdk.getTargetSdkClassNameSet();
		List<String> needdeldexfilepath=new ArrayList<>();
        List<String> dexfilepath=new ArrayList<>();
        getdexfilepath aa=new getdexfilepath();
    	dexfilepath.addAll(aa.traverseFolder1(newdexPath));//"G:\\libpecker备份\\袁倩婷\\lib340"
		for (String dexfile : dexfilepath) {//删除文件大小为0KB的dex
			//System.out.println(dexfile);
			String resourceSizeMb=getFileSize(dexfile);
		    if (resourceSizeMb.equals("0KB")) {
				needdeldexfilepath.add(dexfile);
				System.out.println(dexfile);
		    	 System.out.println("该文件大小为0KB,异常删除！");
			}
		}
		dexfilepath.removeAll(needdeldexfilepath);
		for(String libPath:dexfilepath){
			String libname=libPath.substring(libPath.lastIndexOf("\\")).substring(1);
            Lib lib = Lib.loadFromFile(libPath);
            if (lib == null) {
                fail("lib not parsed");
            }
            LibProfile libProfile = LibProfile.create(lib, targetSdkClassNameSet); 
            LibDetective detective = new LibDetective(libProfile, apkProfile, targetSdkClassNameSet);
            System.out.println("--------------"+libPath.substring(libPath.lastIndexOf("\\"))+"\\进程开始--------------");
            double fastmatchsim=detective.fourthStep(retestRootPackageList,libPath.substring(libPath.lastIndexOf("\\")));
			 if (fastmatchsim>0.6) {
	        	//finalMatchedLib.put(libString, fastmatchsim);			    
	        	//finalMatchedrootpkg.put(libString, root);
			}
		}
		
	}

    private static void fail(String message) {
        System.err.println(message);
        System.exit(0);
    }
	
}
