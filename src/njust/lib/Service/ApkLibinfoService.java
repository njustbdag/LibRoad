package njust.lib.Service;

import java.util.List;

import njust.lib.dao.ApkLibinfoDAO;
import edu.njust.bean.ApkLibinfo;
import njust.lib.dao.ApkInfoDAO;
import edu.njust.bean.ApkInfo;
import edu.njust.bean.LibInfo;
import njust.lib.dao.LibInfoDAO;

public class ApkLibinfoService {
private static ApkLibinfoDAO  ApkLibinfoDAO = new  ApkLibinfoDAO();
	

	
	/**public ApkLibinfo apk(String apk1,String lib1){
		ApkLibinfo  Apk0 = getOneBycontent(apk1);
		 if( Apk0 == null){
			 ApkLibinfo apklib = new  ApkLibinfo();
			 apklib.setApkName(apk1);
			 apklib.setLibName(lib1);
			 //apk.setUserId(2);
			 ApkLibinfoDAO.add(apklib);
		    return apklib;
		 }else
		**/
	
	public ApkLibinfo addapklib(int apkid,String apk1,int libid,String lib1){
		     ApkLibinfo apklib = new ApkLibinfo();	 
		     apklib.setApkId(apkid);
			 apklib.setApkName(apk1);
			 apklib.setLibId(libid);
			 apklib.setLibName(lib1);
			 //apk.setUserId(2);
			 ApkLibinfoDAO.add(apklib);
		    return apklib;				
	}
	
	public ApkLibinfo addapklibbyname(String apk1,String lib1){
	     ApkLibinfo apklib = new ApkLibinfo();		     
		 ApkInfoService apk=new ApkInfoService();
		 LibInfoService lib=new  LibInfoService();
		 ApkLibinfoService apkLibinfoService=new ApkLibinfoService();
		 if(!lib.getOneBycontent(lib1)){
			 lib.addLib(lib1);
		 }
		 if(apk.getOneBycontent1(apk1)==null){
			 apk.addapklib(apk1);
		 }
		 if(!apkLibinfoService.getOneBycontent(apk1, lib1)){
			 apklib.setApkId(apk.findOneBylibname(apk1));
			 apklib.setApkName(apk1);
			 apklib.setLibId(lib.findOneBylibname(lib1));
			 apklib.setLibName(lib1);
			 //apk.setUserId(2);
			 ApkLibinfoDAO.add(apklib);
		    return apklib;
		 }
	     	return null;			
}
	
	
	public  boolean getOneBycontent(String apk1,String lib1){	
		String hql = " from  ApkLibinfo where apkName='"+apk1+"'and libName='"+lib1+"'";
		ApkLibinfo  apkLibinfo =  ApkLibinfoDAO.findOneByHql(hql);
		if(apkLibinfo==null)
			return false;
		return  true;
	}
	
	public List<ApkLibinfo> getallByapkname(String apk1){	
		String hql = " from  ApkLibinfo where apkName='"+apk1+"'";
		List<ApkLibinfo>   apkLibinfo =  ApkLibinfoDAO.findByHql(hql);
		return  apkLibinfo;
	}
	
	public  int getOneBycontent1(String apk1,String lib1){	
		String hql = " from  ApkLibinfo where apkName='"+apk1+"'and libName='"+lib1+"'";
		ApkLibinfo  apkLibinfo =  ApkLibinfoDAO.findOneByHql(hql);
		if(apkLibinfo==null)
			return 0;
		return  apkLibinfo.getId();
	}
	
	public static void main(String[] args){
		ApkLibinfoService apkLibService=new ApkLibinfoService();
		ApkInfo apkInfo=new ApkInfo();
		LibInfo libInfo=new LibInfo();
		LibInfoService LibInfoService1=new LibInfoService();
		int lib;
		ApkInfoService ApkInfoService1=new ApkInfoService();
		lib=LibInfoService1.findOneBylibname("acra-4.6.2.dex");
		int apk;
		apk=ApkInfoService1.findOneBylibname("edu.testshared1");
		//apkLibService.addapklib(apkInfo.getId(),"edu.testshared1",lib, "acra-4.6.2.dex");
		//apkLibService.addapklibbyname("0053554adc5e2f0b25c2c7cb95826842.apk", "acra-4.6.2.dex");
		//System.out.println(apkLibService.getOneBycontent("0053554adc5e2f0b25c2c7cb95826842.apk", "acra-4.6.2.dex"));
		List<ApkLibinfo> a=apkLibService.getallByapkname("23.apk");
		for(ApkLibinfo aa:a){
			System.out.println(aa.getLibName());
		}
		System.out.println("插入数据完成");
	}
}
