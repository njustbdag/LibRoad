package njust.lib.Service;

import java.util.List;

import njust.lib.dao.ApkInfoDAO;
import edu.njust.bean.ApkInfo;
import edu.njust.bean.LibInfo;

public class ApkInfoService {
private static ApkInfoDAO  ApkInfoDAO = new  ApkInfoDAO();
	

	
	public ApkInfo apk(String apk1){
		ApkInfo  Apk0 = getOneBycontent(apk1);
		 if( Apk0 == null){
			 ApkInfo apklib = new  ApkInfo();
			 apklib.setApkName(apk1);
			 //apk.setUserId(2);
			 ApkInfoDAO.add(apklib);
		    return apklib;
		 }else
		return Apk0;				
	}
	
	public ApkInfo addapklib(String apk1){
		     ApkInfo apklib = new ApkInfo();		   
			 apklib.setApkName(apk1);
			 //apk.setUserId(2);
			 ApkInfoDAO.add(apklib);
		    return apklib;				
	}
	
	public  ApkInfo getOneBycontent(String name){
		String hql = " from  ApkInfo where apkName='"+name+"'";
		ApkInfo  Apk =  ApkInfoDAO.findOneByHql(hql);
		return  Apk;
	}
	
	public  List<ApkInfo> getallBycontent(String name){
		String hql = " from  ApkInfo where apkName='"+name+"'";
		List<ApkInfo>  Apk =  ApkInfoDAO.findByHql(hql);
		return  Apk;
	}
	
	public  ApkInfo getOneBycontent1(String name){
		String hql = " from  ApkInfo where apkName='"+name+"'";
		ApkInfo  Apk =  ApkInfoDAO.findOneByHql(hql);
		if(Apk==null)
			return null;
		return  Apk;
	}
	
	
	public  int findOneBylibname(String name){
		String hql = " from  ApkInfo where apkName='"+name+"'";
		ApkInfo  apk =  ApkInfoDAO.findOneByHql(hql);
		if(apk==null)
			return 0;
		return  apk.getId() ;
	}
	
	public static void main(String[] args){
		ApkInfoService ApkInfoService=new ApkInfoService();
		//ApkInfoService.addapklib("edu.testshargfhgfcjchv");
		//int   Apk=ApkInfoService.findOneBylibname("edu.targfhgfcjchv");
		//System.out.println(ApkInfoService.getOneBycontent1("23.pk"));
		List<ApkInfo>  Apk=ApkInfoService.getallBycontent("23.apk");
		for(ApkInfo a:Apk){
			System.out.println(a.getId());
			//ApkInfoDAO.delete(a);
		}
		//ApkInfoDAO.delete(entity);
		System.out.println("插入数据完成");
	}

	public static int findOneByApkname(String apkname) {
		String hql = " from  ApkInfo where apkName='"+apkname+"'";
		ApkInfo  Apk =  ApkInfoDAO.findOneByHql(hql);
		if(Apk==null)
			return 0;
		return  Apk.getId();
	}

	public static int addApk(String apkname) {
	     ApkInfo apklib = new ApkInfo();		   
		 apklib.setApkName(apkname);
		 //apk.setUserId(2);
		 ApkInfoDAO.add(apklib);
	    return apklib.getId();	
	}
}
