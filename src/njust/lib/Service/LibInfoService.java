package njust.lib.Service;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import cn.fudan.libpecker.main.getdexfilepath;
import edu.njust.bean.LibInfo;
import edu.njust.bean.LibRootpackageInfo;
import njust.lib.dao.LibInfoDAO;
import edu.njust.bean.LibInfo;

public class LibInfoService {
private static LibInfoDAO  LibInfoDAO = new  LibInfoDAO();
	

	
public static void updatepackagestructureHash(String libname,String packagestructureHash){
	String hql = " from  LibInfo where libName='"+libname+"'";
	LibInfo  Lib =  LibInfoDAO.findOneByHql(hql);
	System.out.println(Lib.getLibName());
	Lib.setPackagestructureHash(packagestructureHash);
	LibInfoDAO.update(Lib);
	System.out.println("填充完成");
}
	
	public static int addLib(String Lib1){
		     LibInfo Lib = new LibInfo();		   
			 Lib.setLibName(Lib1);
			 //Lib.setUserId(2);
			 LibInfoDAO.add(Lib);
		    return Lib.getId();				
	}
	
	public static  int findOneBylibname(String name){
		String hql = " from  LibInfo where libName='"+name+"'";
		LibInfo  Lib =  LibInfoDAO.findOneByHql(hql);
		if(Lib==null)
			return 0;
		else
		return  Lib.getId();
	}
	
	
	public  boolean getOneBycontent(String name){
		String hql = " from  LibInfo where libName='"+name+"'";
		LibInfo  Lib =  LibInfoDAO.findOneByHql(hql);
		if(Lib==null)
			return false;
		else
		return  true;
	}
	
	public  LibInfo getOneBycontent1(String name){
		String hql = " from  LibInfo where libName='"+name+"'";
		LibInfo  Lib =  LibInfoDAO.findOneByHql(hql);
		if(Lib==null){
			System.out.println("lib不存在，现在插入");
		return	null;
		}
			
		else
		return Lib;
	}
	
	
	
	public  List<LibInfo> getallBypackagestructureHash(String  packagestructureHash){
		String hql = " from  LibInfo where packagestructureHash='"+ packagestructureHash+"'";
		List<LibInfo>  Lib =  LibInfoDAO.findByHql(hql);
		if(Lib.size()==0){
			System.out.println("在数据库中找不到lib的包结构哈希值与之相同");
		return	null;
		}			
		else
		return Lib;
	}
	public  LibInfo getOneByid(int id){
		String hql = " from  LibInfo where id='"+id+"'";
		LibInfo  Lib =  LibInfoDAO.findOneByHql(hql);
		return  Lib;
	}
	
	public  int getattr(List<String> b,String path){
		getdexfilepath aa=new getdexfilepath();
		int category=0;
		for(String i:b){
			if(i.contains(path)){
				//System.out.println(i);
				//System.out.println(aa.Category(i));
				category=aa.Category(i);
			}
		}
		return category;
	}
	
	
	public static void main(String[] args){
		LibInfoService LibInfoService=new LibInfoService();
		//LibInfoService.updatepackagestructureHash("edu.testshared1","b1bacd884319896c");
		LibInfo  Libi=LibInfoService.getOneBycontent1("glide-3.8.0.dex");
		System.out.println(Libi.getId());
		List<LibInfo>  Lib =LibInfoService.getallBypackagestructureHash("ccec7b62da5bf653");
		if (Lib!=null) {
			for (LibInfo libInfo : Lib) {
				System.out.println(libInfo.getLibName());
			}	
		}
		//int  Lib=LibInfoService.findOneBylibname("acr6.2.dex");
		
		//if(Lib==0)
		//System.out.println(LibInfoService.getOneBycontent("acra-4.6.2.dex"));
		
		/**getdexfilepath aa=new getdexfilepath();
		List<String> b=aa.Getallaarjarpath();

		for(int i=23;i<5939;i++){
			LibInfo  Lib=LibInfoService.getOneByid(i);
			String temp=Lib.getLibName();
			String poString=temp.substring(0, temp.indexOf(".dex"));
			int libId=LibInfoService.getattr(b,poString);
			//System.out.println(temp);
			System.out.println(poString);
			System.out.println(libId);
			Lib.setLibId(libId);
			 LibInfoDAO.update(Lib);			
		}**/
		//LibInfo  Lib =LibInfoService.getOneBycontent1("support-core-utils-28.0.0.dex");
		//System.out.println(Lib.getLibId());
		//System.out.println("插入数据完成");
	}
}
