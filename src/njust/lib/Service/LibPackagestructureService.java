package njust.lib.Service;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cn.fudan.libpecker.main.getdexfilepath;
import edu.njust.bean.LibPackagestructure;
import njust.lib.dao.LibPackagestructureDAO;
import edu.njust.bean.LibPackagestructure;

public class LibPackagestructureService {
private static LibPackagestructureDAO  LibPackagestructureDAO = new  LibPackagestructureDAO();
	

	

	
	public static LibPackagestructure addLib(String Lib1,String Hash){
		     LibPackagestructure Lib = new LibPackagestructure();		   
			 Lib.setLibName(Lib1);
			 Lib.setPackagestructureHash(Hash);
			 LibPackagestructureDAO.add(Lib);
		    return Lib;				
	}
	
	public  int findOneBylibname(String name){
		String hql = " from  LibPackagestructure where libName='"+name+"'";
		LibPackagestructure  Lib =  LibPackagestructureDAO.findOneByHql(hql);
		if(Lib==null)
			return 0;
		else
		return  Lib.getId();
	}
	
	
	public  List<LibPackagestructure>  findallBylibhashvalue(String packagestructureHash){
		String hql = " from  LibPackagestructure where packagestructureHash='"+packagestructureHash+"'";
		List<LibPackagestructure>  Lib =  LibPackagestructureDAO.findByHql(hql);
		if(Lib==null)
			return null;
		else
		return  Lib;
	}
	
	public  boolean getOneBycontent(String name){
		String hql = " from  LibPackagestructure where libName='"+name+"'";
		LibPackagestructure  Lib =  LibPackagestructureDAO.findOneByHql(hql);
		if(Lib==null)
			return false;
		else
		return  true;
	}
	
	public  LibPackagestructure getOneBycontent1(String name){
		String hql = " from  LibPackagestructure where libName='"+name+"'";
		LibPackagestructure  Lib =  LibPackagestructureDAO.findOneByHql(hql);
		if(Lib==null){
			System.out.println("lib不存在，现在插入");
		return	addLib(name,"error");
		}
			
		else
		return Lib;
	}
	
	public  LibPackagestructure getOneByid(int id){
		String hql = " from  LibPackagestructure where id='"+id+"'";
		LibPackagestructure  Lib =  LibPackagestructureDAO.findOneByHql(hql);
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
		LibPackagestructureService LibPackagestructureService=new LibPackagestructureService();
		List<LibPackagestructure>  Lib=LibPackagestructureService.findallBylibhashvalue("4ac042f49ebf2990");	
		if (Lib.size()!=0) {
			System.out.println("在数据库中找到包结构匹配的lib:");
			System.out.println(Lib.size());
			for (LibPackagestructure libPackagestructure : Lib) {
				System.out.println(libPackagestructure.getLibName());
			}
		}System.out.println("插入数据完成");
	}
}
