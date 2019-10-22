package cn.fudan.libpecker.core;

import edu.njust.bean.ApkRootpackageInfo;
import groundtruth.CopylibToapk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import njust.lib.dao.ApkRootpackageInfoDAO;

public class ExtractlibbyARP {
	private static ApkRootpackageInfoDAO  ApkRootpackageInfoDAO = new  ApkRootpackageInfoDAO();
	
	public static void main(String[] args) throws Exception{
		String ARPName="com.microsoft.codepush.react";
		//findapkbyARPname(ARPName);
		List<String> aRPNameList=readTxt("C:\\Users\\ZJY\\Desktop\\ExtractlibbyARP.txt");
		for (String aRPName : aRPNameList) {
			findapkbyARPname(aRPName,"(");
		}
	}
	
	
	
	public static List<String> findapkbyARPname(String ARPName,String apknameString) throws Exception {
		//dex所在目录E:\groundtruth\createdex\dexfileset2
		//smali所在目录E:\groundtruth\createdex\smalifileset2
		List<String> apkName=new ArrayList<>();
		Set<String> dexPath=new HashSet<>();
		List<String> dexName=new ArrayList<>();
		List<String> apkARPName=new ArrayList<>();
		int apknamenum=0;
		int apkARPnum=0;
		String hql1 = " from  ApkRootpackageInfo where apkRootpackagename='"+ARPName+"'";
		//String hql1 = " from  LibRootpackageInfo where libRootpackagename='"+libName+"'";
		List<ApkRootpackageInfo>  Apk =  ApkRootpackageInfoDAO.findByHql(hql1);
		System.out.println("apkARPnum"+Apk.size());
		for (ApkRootpackageInfo apkRootpackageInfo : Apk) {
			if (!apkName.contains(apkRootpackageInfo.getApkName())&!apkRootpackageInfo.getApkName().contains(apknameString)) {
				apkName.add(apkRootpackageInfo.getApkName());
				System.out.println(apkRootpackageInfo.getApkName());
				try {
					dexPath.addAll(CopylibToapk.Oldpathparse(apkRootpackageInfo.getApkName(),ARPName));
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
		System.out.println("apknamenum"+apkName.size());
		for (String singledexPath : dexPath) {
			System.out.println(singledexPath);
			dexName.add(singledexPath.substring(singledexPath.lastIndexOf("\\")+1));
			copypath(singledexPath,"D:\\\\所有的dex集合\\\\");
		}
		return dexName;
	}
	
    public static void copypath(String oldstring, String tostring) throws Exception {
		File file = new File(oldstring);
		createDir(tostring);
		File tofile = new File(tostring);
		CopylibToapk.copy(file, tofile);
		System.out.println("从apk中提取出来的dex已复制到D:\\所有的dex集合中");
	}


	public static void createDir(String path){
        File dir=new File(path);
        if(!dir.exists())
            dir.mkdir();
    }
	
	public static List<String> readTxt(String filePath) {
		List<String>result1=new ArrayList<>();
		  try {
		    File file = new File(filePath);
		    if(file.isFile() && file.exists()) {
		      InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "utf-8");
		      BufferedReader br = new BufferedReader(isr);
		      String lineTxt = null;
		      while ((lineTxt = br.readLine()) != null) {
				        System.out.println(lineTxt);	
				        result1.add(lineTxt);
		      }
		      br.close();
		    } else {
		      System.out.println("文件不存在!");
		    }
		  } catch (Exception e) {
		    System.out.println("文件读取错误!");
		  }
		return result1;
		 
		  }
}
