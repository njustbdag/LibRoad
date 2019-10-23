
package njust.lib.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import edu.njust.bean.LibInfo;
import edu.njust.bean.LibRootpackageInfo;
import edu.njust.bean.LibSubpackageInfo;
import edu.njust.bean.LibSubpackageInfo;
import njust.lib.dao.LibSubpackageInfoDAO;

public class LibSubpackageInfoService {
	private static LibSubpackageInfoDAO  LibSubpackageInfoDAO = new  LibSubpackageInfoDAO();
	
	public LibSubpackageInfo addLibSubpackage(String LibSubpackagename,String Libname,String Rootpackagename){
		LibSubpackageInfo libSubpackage = new LibSubpackageInfo();
	     libSubpackage.setLibSubpackagename(LibSubpackagename);
	     LibRootpackageInfo libRootpackage=LibRootpackageInfoService.getOneByrootpackagename(Rootpackagename,Libname);
	     libSubpackage.setLibRootpackageid(libRootpackage.getId());
	     libSubpackage.setLibRootpackagename(Rootpackagename);
	     libSubpackage.setLibId(libRootpackage.getLibId());
	     libSubpackage.setLibName(Libname);
		 LibSubpackageInfoDAO.add(libSubpackage);
	    return libSubpackage;				
}
	
	public static LibSubpackageInfo getOneBySubpackagename(String Subpackagename,String libname) {
		String hql = " from  LibSubpackageInfo where libSubpackagename='"+Subpackagename+"'and libName='"+libname+"'";
		LibSubpackageInfo  Lib =  LibSubpackageInfoDAO.findOneByHql(hql);
		return Lib;
	}
	
	public static List<LibSubpackageInfo> getAllBySubpackagename(String Subpackagename) {
		String hql = " from  LibSubpackageInfo where libSubpackagename like '%"+Subpackagename+"%'"; 
		List<LibSubpackageInfo>  Lib =  LibSubpackageInfoDAO.findByHql(hql);
		System.out.println(Lib.size());
		for (LibSubpackageInfo libSubpackageInfo : Lib) {
			System.out.println(libSubpackageInfo.getLibSubpackagename());
			System.out.println(libSubpackageInfo.getLibName());
			System.out.println(libSubpackageInfo.getId());
		}
		return Lib;
	}
	/**
	 * 做版本号排除，忽略版本号，只考虑lib的种类
	 * @param Subpackagename
	 */
	public static void getallBypackagename(String Subpackagename) {
		String hql = " from  LibSubpackageInfo where libSubpackagename='"+Subpackagename+"'";
		List<LibSubpackageInfo>  Lib =  LibSubpackageInfoDAO.findByHql(hql);
		List<String> libtypeList=new ArrayList<>();
		if (!Lib.isEmpty()) {
		//System.out.println("找到包名相同的lib有"+Lib.size()+"个");
		for (LibSubpackageInfo libSubpackageInfo : Lib) {
			String LibName=libSubpackageInfo.getLibName();
			String tmp = LibName;
			String description = Pattern.compile("[\\d]").matcher(tmp).replaceAll("");
			if (!libtypeList.contains(description)) {
			System.out.println(description);
			libtypeList.add(description);
			}
		}
		System.out.println("找到包名相同的lib有"+libtypeList.size()+"个");
		}

	}
	
	public static void main(String[] args){
		LibSubpackageInfoService LibSubpackageInfoService=new LibSubpackageInfoService();
		LibSubpackageInfoService.getAllBySubpackagename("com.google");
	     /**LibInfoService libInfoService=new LibInfoService();
	     String Libname="967af7be58c4efde245efea26e544d4dabb351f09f396f41b40c78d32c2723a0.dex";
		LibInfo libInfo=libInfoService.getOneBycontent1(Libname);
		System.out.println(libInfo.getId());**/
		//LibSubpackageInfoService.addLibSubpackage("uk.co.senab.photoview.sdjk","967af7be58c4efde245efea26e544d4dabb351f09f396f41b40c78d32c2723a0.dex","uk.co.senab.photoview.log");
	}
}
