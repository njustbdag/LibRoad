package njust.lib.Service;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;






import edu.njust.bean.ApkInfo;
import edu.njust.bean.ApkLib;
import edu.njust.bean.LibInfo;
import njust.lib.dao.ApkLibDAO;

public class ApkLibService {
	private static ApkLibDAO  ApkLibDAO = new  ApkLibDAO();
	

	
	public ApkLib apk(String apk1,String lib1){
		ApkLib  Apk0 = getOneBycontent(apk1);
		 if( Apk0 == null){
			 ApkLib apklib = new ApkLib();
			 apklib.setApk(apk1);
			 apklib.setLib(lib1);
			 //apk.setUserId(2);
			 ApkLibDAO.add(apklib);
		    return apklib;
		 }else
		return Apk0;				
	}
	
	public ApkLib addapklib(String apk1,String lib1){
			 ApkLib apklib = new ApkLib();		 
			 apklib.setApk(apk1);
			 apklib.setLib(lib1);
			 //apk.setUserId(2);
			 ApkLibDAO.add(apklib);
		    return apklib;				
	}
	
	public  ApkLib getOneBycontent(String name){
		String hql = " from  ApkLib where name='"+name+"'";
		ApkLib  Apk =  ApkLibDAO.findOneByHql(hql);
		return  Apk;
	}
	
	public static void main(String[] args){
		ApkLibService apkLibService=new ApkLibService();
		apkLibService.addapklib("edu.testshared1", "acra-4.6.2.dex");
		System.out.println("插入数据完成");
	}
	
	
	
	
}
