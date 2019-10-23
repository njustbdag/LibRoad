package njust.lib.Service;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import njust.lib.dao.LibdetectionResultDAO;
import edu.njust.bean.ApkInfo;
import edu.njust.bean.LibdetectionResult;
import edu.njust.bean.LibInfo;

public class LibdetectionResultService {
private static LibdetectionResultDAO  LibdetectionResultDAO = new  LibdetectionResultDAO();
	

	
	/**public LibdetectionResult apk(String apk1,String lib1){
		LibdetectionResult  Apk0 = getOneBycontent(apk1);
		 if( Apk0 == null){
			 LibdetectionResult apklib = new  LibdetectionResult();
			 apklib.setApkName(apk1);
			 apklib.setLibName(lib1);
			 //apk.setUserId(2);
			 LibdetectionResultDAO.add(apklib);
		    return apklib;
		 }else
		**/
	
	public LibdetectionResult addapklib(int apkid,String apk1,int libid,String lib1){
		LibdetectionResult apklib = new LibdetectionResult();	 
		     apklib.setApkid(apkid);
			 apklib.setApkname(apk1);
			 apklib.setLibid(libid);
			 apklib.setLibname(lib1);
			 //apk.setUserId(2);
			 LibdetectionResultDAO.add(apklib);
		    return apklib;				
	}
	
	public LibdetectionResult addapklibbyname(String apk1,String lib1,int atr){
	     LibdetectionResult apklib = new LibdetectionResult();		     
		 ApkInfoService apk=new ApkInfoService();
		 LibInfoService lib=new  LibInfoService();
		 LibdetectionResultService LibdetectionResultervice=new LibdetectionResultService();
		 if(!lib.getOneBycontent(lib1)){
			 lib.addLib(lib1);
			 System.out.println("lib找不到，现在插入");
		 }
		 if(apk.getOneBycontent1(apk1)==null){
			 apk.addapklib(apk1);
			 System.out.println("apk找不到，现在插入");
		 }
		 if(!LibdetectionResultervice.getOneBycontent(apk1, lib1)){
			 System.out.println("apk-lib对找不到，现在插入");
			 apklib.setApkid(apk.findOneBylibname(apk1));
			 apklib.setApkname(apk1);
			 apklib.setLibid(lib.findOneBylibname(lib1));
			 apklib.setLibname(lib1);
			 apklib.setAttribute(atr);
			 //apk.setUserId(2);
			 LibdetectionResultDAO.add(apklib);
		    return apklib;
		 }
	     	return null;			
}
	
	
	public  boolean getOneBycontent(String apk1,String lib1){	
		String hql = " from  LibdetectionResult where apkname='"+apk1+"'and libname='"+lib1+"'";
		LibdetectionResult  LibdetectionResult =  LibdetectionResultDAO.findOneByHql(hql);
		if(LibdetectionResult==null)
			return false;
		return  true;
	}
	
	public List<LibdetectionResult> getallByapkname(String apk1){	
		String hql = " from  LibdetectionResult where apkname='"+apk1+"'";
		List<LibdetectionResult>   LibdetectionResult =  LibdetectionResultDAO.findByHql(hql);
		return  LibdetectionResult;
	}
	
	public List<LibdetectionResult> getallBylibname(String apk1){	
		String hql = " from  LibdetectionResult where libname='"+apk1+"'";
		List<LibdetectionResult>   LibdetectionResult =  LibdetectionResultDAO.findByHql(hql);
		return  LibdetectionResult;
	}
	
	public List<LibdetectionResult> deleteallByid(int id){	
		String hql = " from  LibdetectionResult where id>'"+id+"'";
		List<LibdetectionResult>   LibdetectionResult =  LibdetectionResultDAO.findByHql(hql);
		for(LibdetectionResult q:LibdetectionResult){
			LibdetectionResultDAO.delete(q);
		}
		return  LibdetectionResult;
	}
	
	public  int getOneBycontent1(String apk1,String lib1){	
		String hql = " from  LibdetectionResult where apkname='"+apk1+"'and libname='"+lib1+"'";
		LibdetectionResult  LibdetectionResult =  LibdetectionResultDAO.findOneByHql(hql);
		if(LibdetectionResult==null)
			return 0;
		return  LibdetectionResult.getId();
	}
	
	public  int getOneBycontent2(String apk1,String lib1){	
		String hql = " from  LibdetectionResult where apkname='"+apk1+"'and libname='"+lib1+"'";
		LibdetectionResult  LibdetectionResult =  LibdetectionResultDAO.findOneByHql(hql);
		if(LibdetectionResult==null)
			return 0;
		return  LibdetectionResult.getAttribute();
	}
	
	public Map<Integer,Integer> LibStatistics(){
		Map<Integer,Integer> Lib=new TreeMap<>();
		int libcount=0;
		for(int libattr=0;libattr<5;libattr++){
			libcount=getallnumByattr(libattr);
			Lib.put(libattr, libcount);
			}
		return Lib;	
	}
	public int getallnumByattr(int attr){	
		String hql = " from  LibdetectionResult where attribute='"+attr+"'";
		List<LibdetectionResult>   LibdetectionResult =  LibdetectionResultDAO.findByHql(hql);
		return  LibdetectionResult.size();
	}
	
public List<LibdetectionResult> getallByattr(int attr){	
	String hql = " from  LibdetectionResult where attribute='"+attr+"'";
	List<LibdetectionResult>   LibdetectionResult =  LibdetectionResultDAO.findByHql(hql);
	return  LibdetectionResult;
}
public void getallByid(int id){	
	String hql = " from  LibdetectionResult where id='"+id+"'";
	List<LibdetectionResult>   LibdetectionResult =  LibdetectionResultDAO.findByHql(hql);
	
}
	
	public static void main(String[] args){
		LibdetectionResultService apkLibService=new LibdetectionResultService();
		//ApkInfo apkInfo=new ApkInfo();
		//LibInfo libInfo=new LibInfo();
		//LibInfoService LibInfoService1=new LibInfoService();
		//int lib;
		//ApkInfoService ApkInfoService1=new ApkInfoService();
		//lib=LibInfoService1.findOneBylibname("acra-4.6.2.dex");
		//int apk;
		//apk=ApkInfoService1.findOneBylibname("edu.testshared1");
		//apkLibService.addapklib(apkInfo.getId(),"edu.testshared1",lib, "acra-4.6.2.dex");
		//apkLibService.addapklibbyname("09.apk", "sqqqqqfo4.8.9.dex");
		//System.out.println(apkLibService.getOneBycontent("0053554adc5e2f0b25c2c7cb95826842.apk", "acra-4.6.2.dex"));
		//List<LibdetectionResult> a=apkLibService.getallByapkname("sh.apk");
		//System.out.println(a.size());
		//for(LibdetectionResult aa:a){
		//	System.out.println(aa.getLibname());
		//}
		//apkLibService.deleteallByid(1);
		LibInfoService LibInfoService=new LibInfoService();
		List<LibdetectionResult> Results=apkLibService.getallByattr(4);
		Map<String,Integer> App=new HashMap<>();
		List<String>set=new ArrayList<>();
		List<String>set1=new ArrayList<>();
		for(LibdetectionResult i:Results){
			//if(!set.contains(i.getLibname())){
				set.add(i.getLibname());
			//}
				if(!set1.contains(i.getLibname())){
				set1.add(i.getLibname());
			}
			//System.out.println(i.getLibname());
		}
		for(String s:set1){
			int num=0;
			for(String i:set){
				if(i.equals(s)){
					num++;
				}
			}
			if(!App.containsKey(s)){
				App.put(s, num);
			}
		}
		for(String q:App.keySet()){
			//System.out.println(q);
			LibInfo  Lib =LibInfoService.getOneBycontent1(q);
			System.out.println(Lib.getLibId());
			//System.out.println(App.get(q));
		}
		
		
		//System.out.println("插入数据完成");
		//System.out.println(set.size());
		//System.out.println(set1.size());
		//System.out.println(App.size());
	}
}
