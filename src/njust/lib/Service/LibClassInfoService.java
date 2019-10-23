package njust.lib.Service;

import java.util.List;

import edu.njust.bean.LibInfo;
import edu.njust.bean.LibRootpackageInfo;
import edu.njust.bean.LibClassInfo;
import edu.njust.bean.LibSubpackageInfo;
import njust.lib.dao.LibClassInfoDAO;

public class LibClassInfoService {
	private static LibClassInfoDAO  LibClassInfoDAO = new  LibClassInfoDAO();
	
	public LibClassInfo addLibClass(String LibClassname,String Libname,String subpackagename,String classHash,String classHashStrict){
	     LibClassInfo LibClass = new LibClassInfo();
	     LibClass.setLibClassname(LibClassname);
	     LibSubpackageInfo libSubpackage=LibSubpackageInfoService.getOneBySubpackagename(subpackagename,Libname);
	     LibClass.setLibPackageid(libSubpackage.getId());
	     LibClass.setLibPackagename(subpackagename);
	     LibClass.setLibId(libSubpackage.getLibId());
	     LibClass.setLibName(Libname);
	     LibClass.setClassHash(classHash);
	     LibClass.setClassHashStrict(classHashStrict);
		 LibClassInfoDAO.add(LibClass);
	    return LibClass;				
}
	
	public static LibClassInfo getOneBySubpackagename(String packagename) {
		String hql = " from  LibClassInfo where LibClassname='"+packagename+"'";
		LibClassInfo  Lib =  LibClassInfoDAO.findOneByHql(hql);
		return Lib;
	}
	
	public static void getOneByclassinfo(String classsname, String classHash, String classHashStrict) {
		String hql = " from  LibClassInfo where libClassname='"+classsname+"'and classHash='"+classHash+"'and classHashStrict='"+classHashStrict+"'";
		List<LibClassInfo>  Lib =  LibClassInfoDAO.findByHql(hql);
		if (Lib.isEmpty()) {
				System.out.println("在数据库中找不到与该apk类匹配的lib类");
			}else {
				for (LibClassInfo libClassInfo : Lib) {
				System.out.println(libClassInfo.getLibId()+":"+libClassInfo.getLibName());
			}
		}
		//return Lib;
	}
	
	public static List<LibClassInfo> getallByclassinfo(String classsname) {
		String hql = " from  LibClassInfo where libClassname='"+classsname+"'";
		List<LibClassInfo>  Lib =  LibClassInfoDAO.findByHql(hql);
			if (Lib.isEmpty()) {
 				System.out.println("在数据库中找不到与该apk类匹配的lib类");
 			}else {
 				for (LibClassInfo libClassInfo : Lib) {
					System.out.println(libClassInfo.getLibId()+":"+libClassInfo.getLibName());
				}
			}
		return Lib;
	}
	
	public static void main(String[] args){
		LibClassInfoService LibClassInfoService=new LibClassInfoService();		
	     LibInfoService libInfoService=new LibInfoService();
	     String Libname="967af7be58c4efde245efea26e544d4dabb351f09f396f41b40c78d32c2723a0.dex";
		//LibInfo libInfo=libInfoService.getOneBycontent1(Libname);
		//System.out.println(libInfo.getId());
		//LibClassInfoService.addLibClass("uk.co.senab.photoview.PhotoViewAttacher$OnPhotoTapListener","aws-android-sdk-mobileanalytics-2.1.10.dex","com.amazonaws.services.mobileanalytics","6c29711921057c3d","2ddab6ec9ab239c6");
	     String classstring="app.mycompany.a.imageviewerbyshehan.classes.PhotoViewAttacher$FlingRunnable";
	     List<LibClassInfo>  Lib =LibClassInfoService.getallByclassinfo(classstring);
	}
}
