package njust.lib.Service;

import java.util.List;

import njust.lib.dao.ApkLibInfosDAO;
import edu.njust.bean.ApkInfo;
import edu.njust.bean.ApkLibInfos;
import edu.njust.bean.ApkLibInfos;
import edu.njust.bean.LibInfo;

public class ApkLibInfosService {
private static ApkLibInfosDAO  ApkLibInfosDAO = new  ApkLibInfosDAO();
	

	
	/**public ApkLibInfos apk(String apk1,String lib1){
		ApkLibInfos  Apk0 = getOneBycontent(apk1);
		 if( Apk0 == null){
			 ApkLibInfos apklib = new  ApkLibInfos();
			 apklib.setApkName(apk1);
			 apklib.setLibName(lib1);
			 //apk.setUserId(2);
			 ApkLibInfosDAO.add(apklib);
		    return apklib;
		 }else
		**/
	
	public ApkLibInfos addapklib(int apkid,String apk1,int libid,String lib1){
		ApkLibInfos apklib = new ApkLibInfos();	 
		     apklib.setApkid(apkid);
			 apklib.setApkname(apk1);
			 apklib.setLibid(libid);
			 apklib.setLibname(lib1);
			 //apk.setUserId(2);
			 ApkLibInfosDAO.add(apklib);
		    return apklib;				
	}
	
	public ApkLibInfos addapklibbyname(String apk1,String lib1){
	     ApkLibInfos apklib = new ApkLibInfos();		     
		 ApkInfoService apk=new ApkInfoService();
		 LibInfoService lib=new  LibInfoService();
		 ApkLibInfosService ApkLibInfoservice=new ApkLibInfosService();
		 if(!lib.getOneBycontent(lib1)){
			 lib.addLib(lib1);
			 System.out.println("lib找不到，现在插入");
		 }
		 if(apk.getOneBycontent1(apk1)==null){
			 apk.addapklib(apk1);
			 System.out.println("apk找不到，现在插入");
		 }
		 if(!ApkLibInfoservice.getOneBycontent(apk1, lib1)){
			 System.out.println("apk-lib对找不到，现在插入");
			 apklib.setApkid(apk.findOneBylibname(apk1));
			 apklib.setApkname(apk1);
			 apklib.setLibid(lib.findOneBylibname(lib1));
			 apklib.setLibname(lib1);
			 apklib.setAttribute(1);
			 //apk.setUserId(2);
			 ApkLibInfosDAO.add(apklib);
		    return apklib;
		 }
	     	return null;			
}
	
	public ApkLibInfos addapklibbyattr(String apk1,String lib1,int atr){
	     ApkLibInfos apklib = new ApkLibInfos();		     
		 ApkInfoService apk=new ApkInfoService();
		 LibInfoService lib=new  LibInfoService();
		 ApkLibInfosService ApkLibInfoservice=new ApkLibInfosService();
		 if(!lib.getOneBycontent(lib1)){
			 lib.addLib(lib1);
			 System.out.println("lib找不到，现在插入");
		 }
		 if(apk.getOneBycontent1(apk1)==null){
			 apk.addapklib(apk1);
			 System.out.println("apk找不到，现在插入");
		 }
		 if(!ApkLibInfoservice.getOneBycontent(apk1, lib1)){
			 System.out.println("apk-lib对找不到，现在插入");
			 apklib.setApkid(apk.findOneBylibname(apk1));
			 apklib.setApkname(apk1);
			 apklib.setLibid(lib.findOneBylibname(lib1));
			 apklib.setLibname(lib1);
			 apklib.setAttribute(atr);
			 //apk.setUserId(2);
			 ApkLibInfosDAO.add(apklib);
		    return apklib;
		 }
	     	return null;			
}
	
	
	public  boolean getOneBycontent(String apk1,String lib1){	
		String hql = " from  ApkLibInfos where apkName='"+apk1+"'and libName='"+lib1+"'";
		ApkLibInfos  ApkLibInfos =  ApkLibInfosDAO.findOneByHql(hql);
		if(ApkLibInfos==null)
			return false;
		return  true;
	}
	
	public  int  getattrBycontent(String apk1,String lib1){	
		String hql = " from  ApkLibInfos where apkName='"+apk1+"'and libName='"+lib1+"'";
		ApkLibInfos  ApkLibInfos =  ApkLibInfosDAO.findOneByHql(hql);
		if(ApkLibInfos==null)
			return 0;
		return  ApkLibInfos.getId();
	}
	
	public List<ApkLibInfos> getallByapkname(String apk1){	
		String hql = " from  ApkLibInfos where apkName='"+apk1+"'";
		List<ApkLibInfos>   ApkLibInfos =  ApkLibInfosDAO.findByHql(hql);
		return  ApkLibInfos;
	}
	
	public int getallByattr(int attr){	
		String hql = " from  ApkLibInfos where attribute='"+attr+"'";
		List<ApkLibInfos>   ApkLibInfos =  ApkLibInfosDAO.findByHql(hql);
		return  ApkLibInfos.size();
	}
	public List<ApkLibInfos> deleteByid(int id){	
		String hql = " from  ApkLibInfos where id>'"+id+"'";
		
		List<ApkLibInfos>   ApkLibInfos =  ApkLibInfosDAO.findByHql(hql);
		for(ApkLibInfos q:ApkLibInfos){
			ApkLibInfosDAO.delete(q);
		}
		return  ApkLibInfos;
	}
	
	public  int getOneBycontent1(String apk1,String lib1){	
		String hql = " from  ApkLibInfos where apkName='"+apk1+"'and libName='"+lib1+"'";
		ApkLibInfos  ApkLibInfos =  ApkLibInfosDAO.findOneByHql(hql);
		if(ApkLibInfos==null)
			return 0;
		return  ApkLibInfos.getId();
	}
	
	public static void main(String[] args){
		ApkLibInfosService apkLibService=new ApkLibInfosService();
		ApkInfo apkInfo=new ApkInfo();
		LibInfo libInfo=new LibInfo();
		LibInfoService LibInfoService1=new LibInfoService();
		int lib;
		ApkInfoService ApkInfoService1=new ApkInfoService();
		lib=LibInfoService1.findOneBylibname("acra-4.6.2.dex");
		int apk;
		apk=ApkInfoService1.findOneBylibname("edu.testshared1");
		//apkLibService.addapklib(apkInfo.getId(),"edu.testshared1",lib, "acra-4.6.2.dex");
		//apkLibService.addapklibbyname("0053554a5826842.apk", "sfddfgho4.8.9.dex");
		//System.out.println(apkLibService.getattrBycontent("3d_drawing_v2.0.1_apkpure.com.apk","support-fragment-28.0.0.dex"));
		//apkLibService.addapklibbyattr("111找工作_視上線_找_v3.7.2_apkpure.com.apk","support-core-utils-28.0.0.dex",6);
		//apkLibService.addapklibbyname("111找工作_視訊面試上線_找工作還送你1萬元大紅包_人人有機會_v3.7.2_apkpure.com.apk","coordinatorlayout-28.0.0.dex");
		//System.out.println(apkLibService.getOneBycontent("0053554adc5e2f0b25c2c7cb95826842.apk", "acra-4.6.2.dex"));
		//List<ApkLibInfos> a=apkLibService.getallByapkname("23.apk");
		//for(ApkLibInfos aa:a){
		//	System.out.println(aa.getLibname());
		//}
		//List<ApkLibInfos>   ApkLibInfos =apkLibService.deleteByid(18462);
		//System.out.println(ApkLibInfos.size());
		//System.out.println(apkLibService.getallByattr(6));
		long i=20753/7;
		System.out.println(i);
	}
}
