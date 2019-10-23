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

import java.io.IOException;
import java.util.*;

import njust.lib.Service.ApkLibInfosService;
import njust.lib.Service.LibdetectionResultService;
import njust.lib.Service.LibpeckerResultService;

import org.xmlpull.v1.XmlPullParserException;

import edu.njust.bean.ApkLibInfos;
import edu.njust.bean.LibdetectionResult;
import edu.njust.bean.LibpeckerResult;


public class resultcompare {
	
	public static List<String> findLibpeckerResultbyapkname(String apkname){
		LibpeckerResultService apkLibService=new LibpeckerResultService();
		List<String> q=new ArrayList<>();
		List<LibpeckerResult> a=apkLibService.getallByapkname(apkname);
		System.out.println("Libpecker找到了"+a.size()+"个");
		for(LibpeckerResult aa:a){
			System.out.println(aa.getLibname());
			q.add(aa.getLibname());
		}
		return q;
	}
	
	public static List<String> findLibdetectionResultbyapkname(String apkname){
		LibdetectionResultService apkLibService1=new LibdetectionResultService();
		List<String> q=new ArrayList<>();
		List<LibdetectionResult> b=apkLibService1.getallByapkname(apkname);
		System.out.println("Libdetection找到了"+b.size()+"个");
		for(LibdetectionResult aa:b){
			System.out.println(aa.getLibname());
			q.add(aa.getLibname());
		}
		return q;
		
	}
	
	public static List<String> findApkLibInfosbyapkname(String apkname){
		ApkLibInfosService apkLibService1=new ApkLibInfosService();
		List<String> q=new ArrayList<>();
		List<ApkLibInfos> b=apkLibService1.getallByapkname(apkname);
		System.out.println("ApkLibInfos找到了"+b.size()+"个");
		for(ApkLibInfos aa:b){
			System.out.println(aa.getLibname());
			q.add(aa.getLibname());
		}
		return q;
		
	}
	
	public static void main(String[] args){
		String apkname="03449.apk";
		LibdetectionResultService apkLibService1=new LibdetectionResultService();
		List<String> a=findLibpeckerResultbyapkname(apkname);
		System.out.println("         ");
		List<String> b=findLibdetectionResultbyapkname(apkname);
		System.out.println("         ");
		List<String> c=findApkLibInfosbyapkname(apkname);
		System.out.println("         ");
		System.out.println("Libpecker和Libdetection跟groundtruth做比较");
		for(String q:c){
			if(!a.contains(q))
				System.out.println("Libpecker没有找到这个lib："+q);
			if(!b.contains(q))
				System.out.println("Libdetection没有找到这个lib："+q);
		}
		System.out.println("-----------比较结束-------------");
		System.out.println("Libpecker和Libdetection做比较");
		for(String w:b){
			if(!a.contains(w)){
				System.out.println("Libpecker没有找到这个lib："+w);
				System.out.println(w+"的属性值为"+apkLibService1.getOneBycontent2(apkname,w));
			}
				
				
		}
		System.out.println("-----------比较结束-------------");
		
	}
	
	
	
}
