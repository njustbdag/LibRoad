package njust.lib.Service;

import java.util.List;

import edu.njust.bean.LibInfo;
import edu.njust.bean.LibRootpackageInfo;
import edu.njust.bean.LitelibClassInfo;
import edu.njust.bean.LibSubpackageInfo;
import njust.lib.dao.LitelibClassInfoDAO;

public class LitelibClassInfoService {
	private static LitelibClassInfoDAO  LitelibClassInfoDAO = new  LitelibClassInfoDAO();
	
	public LitelibClassInfo addLibClass(String LibClassname,String Libname,String subpackagename,String classHash,String classHashStrict){
	     LitelibClassInfo LibClass = new LitelibClassInfo();
	     LibClass.setLibClassname(LibClassname);
	     LibSubpackageInfo libSubpackage=LibSubpackageInfoService.getOneBySubpackagename(subpackagename,Libname);
	     LibClass.setLibPackageid(libSubpackage.getId());
	     LibClass.setLibPackagename(subpackagename);
	     LibClass.setLibId(libSubpackage.getLibId());
	     LibClass.setLibName(Libname);
	     LibClass.setClassHash(classHash);
	     LibClass.setClassHashStrict(classHashStrict);
		 LitelibClassInfoDAO.add(LibClass);
	    return LibClass;				
}
	
	public static LitelibClassInfo getOneBySubpackagename(String packagename) {
		String hql = " from  LitelibClassInfo where LibClassname='"+packagename+"'";
		LitelibClassInfo  Lib =  LitelibClassInfoDAO.findOneByHql(hql);
		return Lib;
	}
	
	public static void getOneByclassinfo(String classsname, String classHash, String classHashStrict) {
		String hql = " from  LitelibClassInfo where libClassname='"+classsname+"'and classHash='"+classHash+"'and classHashStrict='"+classHashStrict+"'";
		List<LitelibClassInfo>  Lib =  LitelibClassInfoDAO.findByHql(hql);
		if (Lib.isEmpty()) {
				System.out.println("在数据库中找不到与该apk类匹配的lib类");
			}else {
				for (LitelibClassInfo LitelibClassInfo : Lib) {
				System.out.println(LitelibClassInfo.getLibId()+":"+LitelibClassInfo.getLibName());
			}
		}
		//return Lib;
	}
	
	public static List<LitelibClassInfo> getallByclassinfo(String classsname) {
		String hql = " from  LitelibClassInfo where libClassname='"+classsname+"'";
		List<LitelibClassInfo>  Lib =  LitelibClassInfoDAO.findByHql(hql);
			if (Lib.isEmpty()) {
 				System.out.println("在数据库中找不到与该apk类匹配的lib类");
 			}else {
 				for (LitelibClassInfo LitelibClassInfo : Lib) {
					System.out.println(LitelibClassInfo.getLibId()+":"+LitelibClassInfo.getLibName());
				}
			}
		return Lib;
	}
	
	public static void main(String[] args){
		LitelibClassInfoService LitelibClassInfoService=new LitelibClassInfoService();		
	     LibInfoService libInfoService=new LibInfoService();
	     String Libname="967af7be58c4efde245efea26e544d4dabb351f09f396f41b40c78d32c2723a0.dex";
		//LibInfo libInfo=libInfoService.getOneBycontent1(Libname);
		//System.out.println(libInfo.getId());
		LitelibClassInfoService.addLibClass("uk.co.senab.photoview.PhotoViewAttacher$OnPhotoTapListener","aws-android-sdk-mobileanalytics-2.1.10.dex","com.amazonaws.services.mobileanalytics","6c29711921057c3d","2ddab6ec9ab239c6");
	     //String classstring="app.mycompany.a.imageviewerbyshehan.classes.PhotoViewAttacher$FlingRunnable";
	     //List<LitelibClassInfo>  Lib =LitelibClassInfoService.getallByclassinfo(classstring);
	}
}
