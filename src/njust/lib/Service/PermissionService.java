package njust.lib.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import njust.lib.dao.PermissionDAO;
import cn.fudan.libpecker.main.getdexfilepath;
import edu.njust.bean.Permission;

public class PermissionService {
	private static PermissionDAO  PermissionDAO = new  PermissionDAO();
	

	
	public static void updatepackagestructureHash(String libname,String packagestructureHash){
		String hql = " from  Permission where libName='"+libname+"'";
		Permission  Lib =  PermissionDAO.findOneByHql(hql);
		//System.out.println(Lib.getLibName());
		//Lib.setPackagestructureHash(packagestructureHash);
		PermissionDAO.update(Lib);
		System.out.println("填充完成");
	}
		
		public static int addPermission(String CallerClass,String CallerMethod,String CallerMethodDesc,String Permission){
			String detailDesc="L"+CallerClass+";->"+CallerMethod+CallerMethodDesc;
			System.out.println(detailDesc);
			if (findOneBydetailDesc(detailDesc)==0) {
			     Permission Per = new Permission();	
			     Per.setDetailDesc(detailDesc);
			     Per.setCallerClass(CallerClass);
			     Per.setCallerMethod(CallerMethod);
			     Per.setCallerMethodDesc(CallerMethodDesc);
			     Per.setPermission(Permission);
				 PermissionDAO.add(Per);
				 return Per.getId();	
			}else {
				System.out.println("数据库中已存在该Permission，插入失败！");
				return 0;
			}

			    			
		}
		
		public static  int findOneBydetailDesc(String detailDesc){
			String hql = " from  Permission where detailDesc='"+detailDesc+"'";
			Permission  Lib =  PermissionDAO.findOneByHql(hql);
			if(Lib==null)
				return 0;
			else
			return  Lib.getId();
		}
		
		public static  String getOneBydetailDesc(String detailDesc){
			String hql = " from  Permission where detailDesc='"+detailDesc+"'";
			Permission  Lib =  PermissionDAO.findOneByHql(hql);
			if(Lib==null)
				return null;
			else
			return  Lib.getPermission();
		}
		
		public static List<String> readTxt(String filePath) {
			List<String>result1=new ArrayList<>();
			  try {
			    File file = new File(filePath);
			    if(file.isFile() && file.exists()) {
			      InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "utf-8");
			      BufferedReader br = new BufferedReader(isr);
			      String lineTxt = null;
			      while ((lineTxt = br.readLine()) != null) {
		    					System.out.println(lineTxt);
		    					result1.add(lineTxt);
		    					List<String> result = Arrays.asList(lineTxt.split(","));
		    					//System.out.println(result.get(0));
		    					//System.out.println(result.get(1));
		    					//System.out.println(result.get(2));
		    					//System.out.println(result.get(3));
		    					addPermission(result.get(0),result.get(1),result.get(2),result.get(3));
			      }
			      br.close();
			    } else {
			      System.out.println("文件不存在!");
			    }
			  } catch (Exception e) {
			    System.out.println("文件读取错误!");
			  }
			return result1;
			 
			  }
		
		
		public static void main(String[] args){
			PermissionService PermissionService=new PermissionService();
			PermissionService.addPermission("com/android/server/LocationManagerService","getProviders","(Landroid/location/Criteria;Z)Ljava/util/List;","android.permission.ACCESS_COARSE_LOCATION");
			//PermissionService.readTxt("C:\\Users\\ZJY\\Desktop\\mapping_4.4.1.csv");
			//PermissionService.readTxt("C:\\Users\\ZJY\\Desktop\\mapping_4.4.4.csv");
			//PermissionService.readTxt("C:\\Users\\ZJY\\Desktop\\mapping_5.0.2.csv");
			//System.out.println("插入数据完成");
		}
}
