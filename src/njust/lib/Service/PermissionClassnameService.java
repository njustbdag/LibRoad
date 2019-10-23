package njust.lib.Service;

import njust.lib.dao.PermissionClassnameDAO;
import njust.lib.dao.PermissionClassnameDAO;
import edu.njust.bean.Permission;
import edu.njust.bean.PermissionClassname;

public class PermissionClassnameService {
	
	private static PermissionClassnameDAO  PermissionClassnameDAO = new  PermissionClassnameDAO();
	
	public static int addPermission(String CallerClass){
		if (findOneByCallerClass(CallerClass)==0) {
		     PermissionClassname Per = new PermissionClassname();	
		     Per.setCallerClass(CallerClass);
			 PermissionClassnameDAO.add(Per);
			 return Per.getId();	
		}else {
			System.out.println("数据库中已存在该Permission，插入失败！");
			return 0;
		}

		    			
	}

	public static int findOneByCallerClass(String CallerClass) {
		String hql = " from  PermissionClassname where CallerClass='"+CallerClass+"'";
		PermissionClassname  Lib =  PermissionClassnameDAO.findOneByHql(hql);
		if(Lib==null)
			return 0;
		else
		return  Lib.getId();
	}
}
