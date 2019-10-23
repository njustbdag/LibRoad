package njust.lib.Service;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.common.collect.Multiset.Entry;

import njust.lib.dao.LibpeckerResultDAO;
import edu.njust.bean.ApkInfo;
import edu.njust.bean.LibdetectionResult;
import edu.njust.bean.LibpeckerResult;
import edu.njust.bean.LibInfo;

public class LibpeckerResultService {
private static LibpeckerResultDAO  LibpeckerResultDAO = new  LibpeckerResultDAO();
	

	
	/**public LibpeckerResult apk(String apk1,String lib1){
		LibpeckerResult  Apk0 = getOneBycontent(apk1);
		 if( Apk0 == null){
			 LibpeckerResult apklib = new  LibpeckerResult();
			 apklib.setApkName(apk1);
			 apklib.setLibName(lib1);
			 //apk.setUserId(2);
			 LibpeckerResultDAO.add(apklib);
		    return apklib;
		 }else
		**/
	
	public LibpeckerResult addapklib(int apkid,String apk1,int libid,String lib1){
		LibpeckerResult apklib = new LibpeckerResult();	 
		     apklib.setApkid(apkid);
			 apklib.setApkname(apk1);
			 apklib.setLibid(libid);
			 apklib.setLibname(lib1);
			 //apk.setUserId(2);
			 LibpeckerResultDAO.add(apklib);
		    return apklib;				
	}
	
	public LibpeckerResult addapklibbyname(String apk1,String lib1,int attr){
	     LibpeckerResult apklib = new LibpeckerResult();		     
		 ApkInfoService apk=new ApkInfoService();
		 LibInfoService lib=new  LibInfoService();
		 LibpeckerResultService LibpeckerResultervice=new LibpeckerResultService();
		 if(!lib.getOneBycontent(lib1)){
			 lib.addLib(lib1);
			 System.out.println("lib找不到，现在插入");
		 }
		 if(apk.getOneBycontent1(apk1)==null){
			 System.out.println("apk找不到，现在插入");
			 apk.addapklib(apk1);
		 }
		 if(!LibpeckerResultervice.getOneBycontent(apk1, lib1)){
			 System.out.println("apk-lib对找不到，现在插入");
			 apklib.setApkid(apk.findOneBylibname(apk1));
			 apklib.setApkname(apk1);
			 apklib.setLibid(lib.findOneBylibname(lib1));
			 apklib.setLibname(lib1);
			 apklib.setAttribute(attr);
			 //apk.setUserId(2);
			 LibpeckerResultDAO.add(apklib);
		    return apklib;
		 }
	     	return null;			
}
	
	
	public  boolean getOneBycontent(String apk1,String lib1){	
		String hql = " from  LibpeckerResult where apkname='"+apk1+"'and libname='"+lib1+"'";
		LibpeckerResult  LibpeckerResult =  LibpeckerResultDAO.findOneByHql(hql);
		if(LibpeckerResult==null)
			return false;
		return  true;
	}
	
	public List<LibpeckerResult> getallByapkname(String apk1){	
		String hql = " from  LibpeckerResult where apkname='"+apk1+"'";
		List<LibpeckerResult>   LibpeckerResult =  LibpeckerResultDAO.findByHql(hql);
		return  LibpeckerResult;
	}
	
	public List<LibpeckerResult> deleteallByid(int id){	
		String hql = " from  LibpeckerResult where id>'"+id+"'";
		List<LibpeckerResult>   LibpeckerResult =  LibpeckerResultDAO.findByHql(hql);
		for(LibpeckerResult q:LibpeckerResult){
			LibpeckerResultDAO.delete(q);
		}
		return  LibpeckerResult;
	}
	
	public  int getOneBycontent1(String apk1,String lib1){	
		String hql = " from  LibpeckerResult where apkname='"+apk1+"'and libname='"+lib1+"'";
		LibpeckerResult  LibpeckerResult =  LibpeckerResultDAO.findOneByHql(hql);
		if(LibpeckerResult==null)
			return 0;
		return  LibpeckerResult.getId();
	}
	
	public  int findallbylibid(int libid){	
		String hql = " from  LibpeckerResult where libid='"+libid+"'";
		List<LibpeckerResult>  LibpeckerResult =  LibpeckerResultDAO.findByHql(hql);
		if(LibpeckerResult==null)
			return 0;
		return  LibpeckerResult.size();
	}
	
	public  int findallbyappid(int appid){	
		String hql = " from  LibpeckerResult where apkid='"+appid+"'";
		List<LibpeckerResult>  LibpeckerResult =  LibpeckerResultDAO.findByHql(hql);
		if(LibpeckerResult==null)
			return 0;
		return  LibpeckerResult.size();
	}
	
	public  String findnamebylibid(int libid){	
		String hql = " from  LibpeckerResult where libid='"+libid+"'";
		LibpeckerResult  LibpeckerResult =  LibpeckerResultDAO.findOneByHql(hql);
		if(LibpeckerResult==null)
			return null;
		return  LibpeckerResult.getLibname();
	}
	
	public  String findnamebyappid(int appid){	
		String hql = " from  LibpeckerResult where apkid='"+appid+"'";
		LibpeckerResult  LibpeckerResult =  LibpeckerResultDAO.findOneByHql(hql);
		if(LibpeckerResult==null)
			return null;
		return  LibpeckerResult.getApkname();
	}
	
	public Map<String,Integer> LibStatistics(){
		Map<String,Integer> Lib=new TreeMap<>();
		int libcount=0;
		for(int libid=0;libid<5939;libid++){
			String libnameString=findnamebylibid(libid);
			if(libnameString!=null){
		    libcount=findallbylibid(libid);
			Lib.put(libnameString, libcount);
			}

		}
		return Lib;
		
	}

	public Map<String,Integer> AppStatistics(){
		Map<String,Integer> App=new TreeMap<>();
		int libcount=0;
		for(int libid=0;libid<8934;libid++){
			String libnameString=findnamebyappid(libid);
			if(libnameString!=null){
		    libcount=findallbyappid(libid);
		    App.put(libnameString, libcount);
			}

		}
		return App;
		
	}
	
	public static void main(String[] args){
		LibpeckerResultService apkLibService=new LibpeckerResultService();
		ApkInfo apkInfo=new ApkInfo();
		LibInfo libInfo=new LibInfo();
		LibInfoService LibInfoService1=new LibInfoService();
		if(apkLibService.getOneBycontent("*df473e3d789c63bae99828044da74500.apk", "play-services-appinvite-9.8.0.dex"))
			System.out.println("找到了");
		//apkLibService.deleteallByid(37989);
		//int lib;
		//ApkInfoService ApkInfoService1=new ApkInfoService();
		//lib=LibInfoService1.findOneBylibname("acra-4.6.2.dex");
		//int apk;
		//apk=ApkInfoService1.findOneBylibname("edu.testshared1");
		//apkLibService.g
		//apkLibService.addapklib(apkInfo.getId(),"edu.testshared1",lib, "acra-4.6.2.dex");
		//apkLibService.addapklibbyname("0053554adc5e2f0b25c2c7cb95826842.apk", "sfddfgqdfdsgdxf4.8.9.dex");
		//System.out.println(apkLibService.getOneBycontent("0053554adc5e2f0b25c2c7cb95826842.apk", "acra-4.6.2.dex"));
		//List<LibpeckerResult> a=apkLibService.getallByapkname("sh.apk");
		//System.out.println(a.size());
		//for(LibpeckerResult aa:a){
		//System.out.println(aa.getLibname());
		//}
		//apkLibService.deleteallByid(103);
		//int test=apkLibService.findallbylibid(88);
		//String iiString=apkLibService.findnamebylibid(88);
		//System.out.println(test+iiString);
		//Map<String,Integer> Lib=apkLibService.LibStatistics();
		//String libnameString=apkLibService.findnamebyappid(8849);
		//int libcount=apkLibService.findallbyappid(8849);
		//System.out.println(libnameString+libcount);
		//Map<String,Integer> App=apkLibService.AppStatistics();
		//for(String libname:App.keySet()){
		//	System.out.println(libname);
			//System.out.println(App.get(libname));
		//}
		//for(String libname:App.keySet()){
			//System.out.println(libname);
		//	System.out.println(App.get(libname));
		//}
		System.out.println("插入数据完成");
	}
}
