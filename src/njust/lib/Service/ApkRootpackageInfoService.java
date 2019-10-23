package njust.lib.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.regex.Pattern;

import javax.persistence.criteria.CriteriaBuilder.In;

import cn.fudan.libpecker.main.CreateBatFile;
import cn.fudan.libpecker.main.LibDetective;
import cn.fudan.libpecker.model.ApkProfile;
import edu.njust.bean.ApkInfo;
import edu.njust.bean.ApkRootpackageInfo;
import edu.njust.bean.ApkRootpackageInfo;
import edu.njust.bean.LibRootpackageInfo;
import groundtruth.CopylibToapk;
import njust.lib.dao.ApkRootpackageInfoDAO;
import njust.lib.dao.LibRootpackageInfoDAO;

public class ApkRootpackageInfoService {
	private static ApkRootpackageInfoDAO  ApkRootpackageInfoDAO = new  ApkRootpackageInfoDAO();
	private static LibRootpackageInfoDAO  LibRootpackageInfoDAO = new  LibRootpackageInfoDAO();
	
	public ApkRootpackageInfo addApkRootpackage(String ApkRootpackagename,String Apkname){
	     ApkRootpackageInfo ApkRootpackage = new ApkRootpackageInfo();
	     ApkRootpackage.setApkRootpackagename(ApkRootpackagename);
	     ApkInfoService ApkInfoService=new ApkInfoService();
	     ApkInfo ApkInfo=ApkInfoService.getOneBycontent1(Apkname);
	     ApkRootpackage.setApkId(ApkInfo.getId());
	     ApkRootpackage.setApkName(ApkInfo.getApkName());
		 ApkRootpackageInfoDAO.add(ApkRootpackage);
	    return ApkRootpackage;				
}
	public static void updatepackagestructureHash(String Apkname,String packagestructureHash){
		String hql = " from  ApkRootpackageInfo where ApkName='"+Apkname+"'";
		ApkRootpackageInfo  Apk =  ApkRootpackageInfoDAO.findOneByHql(hql);
		System.out.println(Apk.getApkName());
		Apk.setPackagestructureHash(packagestructureHash);
		ApkRootpackageInfoDAO.update(Apk);
		System.out.println("填充完成");
	}
	
	private static String findConfusedapk(int apkId){
		String hql = " from  ApkRootpackageInfo where apkId='"+apkId+"'";
		List<ApkRootpackageInfo>  Apk =  ApkRootpackageInfoDAO.findByHql(hql);
		ListIterator<ApkRootpackageInfo> apkIterator=Apk.listIterator();
		while (apkIterator.hasNext()) {
			ApkRootpackageInfo apkRootpackageInfo = (ApkRootpackageInfo) apkIterator
					.next();
			if (!ApkProfile.JudgeObfuscated(apkRootpackageInfo.getApkRootpackagename())) {
				return apkRootpackageInfo.getApkName();
			}
		}
		return null;
	}
	
	public static void updatepackage(String Apkname,String ApkRootpackagename,int subpckNum,int directorynum,String pckStructure,String packagestructureHash){
		String hql = " from  ApkRootpackageInfo where ApkName='"+Apkname+"'and ApkRootpackagename='"+ApkRootpackagename+"'";;
		ApkRootpackageInfo  Apk =  ApkRootpackageInfoDAO.findOneByHql(hql);
		Apk.setDirectorynum(directorynum);
		Apk.setPckStructure(pckStructure);
		Apk.setSubpckNum(subpckNum);
		Apk.setPackagestructureHash(packagestructureHash);
		ApkRootpackageInfoDAO.update(Apk);
		System.out.println("填充完成");
	}
	
	public static void addonepackage(String Apkname,String ApkRootpackagename,int subpckNum,int directorynum,String pckStructure,String packagestructureHash){
		String hql = " from  ApkRootpackageInfo where apkName='"+Apkname+"'and apkRootpackagename='"+ApkRootpackagename+"'";;
		ApkRootpackageInfo  Apk1 =  ApkRootpackageInfoDAO.findOneByHql(hql);
		if (Apk1==null) {
		int ApkId=ApkInfoService.findOneByApkname(Apkname);
		if (ApkId==0) {
			System.out.println("Apkinfo数据库中没有这个Apk，现在插入！");
			ApkId=ApkInfoService.addApk(Apkname);
		}
		ApkRootpackageInfo  Apk= new ApkRootpackageInfo(); 
		Apk.setDirectorynum(directorynum);
		Apk.setPckStructure(pckStructure);
		Apk.setSubpckNum(subpckNum); 
		Apk.setPackagestructureHash(packagestructureHash);
		Apk.setApkRootpackagename(ApkRootpackagename); 
		Apk.setApkId(ApkId); 
		Apk.setApkName(Apkname);
		ApkRootpackageInfoDAO.add(Apk);
		System.out.println("填充完成");
		}else {
			System.out.println("已经有了，不需要添加！");
		}
		
	}
	
	public static void fillApktype(){
		for (int id = 15; id < 6221; id++) {
		String hql = " from  ApkRootpackageInfo where id='"+id+"'";
		ApkRootpackageInfo  Apk =  ApkRootpackageInfoDAO.findOneByHql(hql);
		String ApkType= Pattern.compile("[\\d]").matcher(Apk.getApkName()).replaceAll("");
		ApkRootpackageInfoDAO.update(Apk);
		}
		System.out.println("填充完成");
}
	public String  findonebyApkname(String ApkName){
		String hql = " from  ApkRootpackageInfo where ApkName='"+ApkName+"'";
		ApkRootpackageInfo  Apk =  ApkRootpackageInfoDAO.findOneByHql(hql);
	    return Apk.getApkName();				
}
	
	public static String  findonebyApkRootpackagename(String ApkRootpackagename){
		String hql = " from  ApkRootpackageInfo where apkRootpackagename='"+ApkRootpackagename+"'";
		ApkRootpackageInfo  Apk =  ApkRootpackageInfoDAO.findOneByHql(hql);
		if (Apk!=null) {
			  return Apk.getApkName();
		}
		return null;
}
	public static List<String>  findallbyApkRootpackagename(String ApkRootpackagename){
		List<String> apkNameList=new ArrayList<>();
		String hql = " from  ApkRootpackageInfo where apkRootpackagename='"+ApkRootpackagename+"'";
		List<ApkRootpackageInfo>  Apk =  ApkRootpackageInfoDAO.findByHql(hql);
		if (Apk!=null) {
			for (ApkRootpackageInfo apkRootpackageInfo : Apk) {
				apkNameList.add(apkRootpackageInfo.getApkName());
			}
			  return apkNameList;
		}
		return null;
}
	
	public static String  findonebyApkType(String ApkType){
		String hql = " from  ApkRootpackageInfo where ApkType='"+ApkType+"'";
		ApkRootpackageInfo  Apk =  ApkRootpackageInfoDAO.findOneByHql(hql);
	    return Apk.getApkRootpackagename();				
}
	
	public static boolean  findonebyApkName(String ApkType){
		String hql = " from  ApkRootpackageInfo where apkName='"+ApkType+"'";
		ApkRootpackageInfo  Apk =  ApkRootpackageInfoDAO.findOneByHql(hql);
		if (Apk==null) {
			 return true;
		}
	    return false;				
}
	
	
	public static List<ApkRootpackageInfo>  findallbyRootandHash(String Root,String Hash){
		String hql = " from  ApkRootpackageInfo where ApkRootpackagename='"+Root+"'and packagestructureHash='"+Hash+"'";
		List<ApkRootpackageInfo>  Apk =  ApkRootpackageInfoDAO.findByHql(hql);
		if (Apk.size()!=0) {
			System.out.println("在数据库中找到包名和包Hash值匹配的Apk:");
			for (ApkRootpackageInfo ApkPackagestructure : Apk) {
				String ApkType=ApkPackagestructure.getApkName();
				System.out.println(ApkType);
			}
		}
	    return Apk;				 
}
	public static List<ApkRootpackageInfo>  findallbyunobfusDirandHash(String root, String unobfusDir,int rootDirnum, String Hash, List<String> conversedList){
		List<ApkRootpackageInfo>  ApkRootpackageInfos=new ArrayList<>();
		if (root.equals(unobfusDir)) {
			String hql2 = " from  ApkRootpackageInfo where ApkRootpackagename ='"+root+"'and packagestructureHash='"+Hash+"'";  
			List<ApkRootpackageInfo>  Apk2 =  ApkRootpackageInfoDAO.findByHql(hql2);
			String hql4 = " from  ApkRootpackageInfo where ApkRootpackagename ='"+root+"'";  
			List<ApkRootpackageInfo>  Apk4 =  ApkRootpackageInfoDAO.findByHql(hql4);
			if (Apk2.size()>0) { 
				for (ApkRootpackageInfo ApkRootpackageInfo : Apk2) {
						System.out.println("包名未混淆，在数据库中找到精确包名和Hash值匹配的Apk:"+ApkRootpackageInfo.getApkName());
						ApkRootpackageInfos.add(ApkRootpackageInfo);							  	
								}
				 return ApkRootpackageInfos;
		}if (Apk4.size()>0&Apk2.size()==0) {
			for (ApkRootpackageInfo ApkRootpackageInfo : Apk4){
				String PackagestructureList =ApkRootpackageInfo.getPckStructure();
			 	int matchnum=0;
					/**
				 * 把字符串变成List<String>检查包结构匹配度
				 */if (PackagestructureList!=null) {
					    String[] strArr =PackagestructureList.split(",");
					    //System.out.println(strArr.length); 
				        for (int i = 0; i < strArr.length; ++i){
				        	if (conversedList.contains(strArr[i])) {
								matchnum++;
							}
				        }
				        if (matchnum/conversedList.size()>0.8) {
				        	System.out.println("包名未混淆，在数据库中找到精确包名和包结构匹配的Apk:"+ApkRootpackageInfo.getApkName());
							ApkRootpackageInfos.add(ApkRootpackageInfo);	
						}else {
							System.out.println("包名未混淆，在数据库中找到精确包名但包结构不匹配的Apk:"+ApkRootpackageInfo.getApkName()+"---"+ApkRootpackageInfo.getPackagestructureHash());
							ApkRootpackageInfos.add(ApkRootpackageInfo);
						}	 
				 }				  	
			}
			 return ApkRootpackageInfos;	
		}
			else {
			String hql3 = " from  ApkRootpackageInfo ApkRootpackageInfo where ApkRootpackageInfo.ApkRootpackagename like '%"+unobfusDir+"%'and packagestructureHash='"+Hash+"'";   
			List<ApkRootpackageInfo>  Apk3 =  ApkRootpackageInfoDAO.findByHql(hql3);
			String hql5 = " from  ApkRootpackageInfo ApkRootpackageInfo where ApkRootpackageInfo.ApkRootpackagename like '%"+unobfusDir+"%'";  
			List<ApkRootpackageInfo>  Apk5 =  ApkRootpackageInfoDAO.findByHql(hql5);
			if (Apk3.size()>0) { 
				for (ApkRootpackageInfo ApkRootpackageInfo : Apk3) {
						System.out.println("包名未混淆，在数据库中找到模糊包名和Hash匹配的Apk:"+ApkRootpackageInfo.getApkName());
						ApkRootpackageInfos.add(ApkRootpackageInfo);
						
				}
				return ApkRootpackageInfos;
		}if (Apk5.size()>0&Apk3.size()==0) {
			for (ApkRootpackageInfo ApkRootpackageInfo : Apk5){
				System.out.println("包名未混淆，在数据库中找到模糊包名的Apk:"+ApkRootpackageInfo.getApkName()+"---"+ApkRootpackageInfo.getPackagestructureHash());
				ApkRootpackageInfos.add(ApkRootpackageInfo);
				  
			}
			 return ApkRootpackageInfos;	
		}
		}
		}else {
				String hql = " from  ApkRootpackageInfo where packagestructureHash='"+Hash+"'";
		List<ApkRootpackageInfo>  Apk =  ApkRootpackageInfoDAO.findByHql(hql);
		if (Apk.size()>0) { 
				for (ApkRootpackageInfo ApkRootpackageInfo : Apk) {
			if (ApkRootpackageInfo.getApkRootpackagename().contains(unobfusDir)&ApkRootpackageInfo.getDirectorynum()==rootDirnum) {
			        	System.out.println("包名被混淆，在数据库中找到包名结构和包Hash值匹配的Apk:"+ApkRootpackageInfo.getApkName());
						ApkRootpackageInfos.add(ApkRootpackageInfo);						
			}
		}	
		}else {
			String hql1 = " from  ApkRootpackageInfo ApkRootpackageInfo where ApkRootpackageInfo.ApkRootpackagename like '%"+unobfusDir+"%'";  
			//String hql1 = " from  ApkRootpackageInfo where directorynum='"+rootDirnum+"'";
			List<ApkRootpackageInfo>  Apk1 =  ApkRootpackageInfoDAO.findByHql(hql1);	
			for (ApkRootpackageInfo ApkRootpackageInfo : Apk1) {
		if (ApkRootpackageInfo.getApkRootpackagename().contains(unobfusDir)) {
			String PackagestructureList =ApkRootpackageInfo.getPckStructure();
		 	int matchnum=0;
				/**
			 * 把字符串变成List<String>检查包结构匹配度
			 */
		 	if (PackagestructureList!=null) {
			    String[] strArr =PackagestructureList.split(",");
			    //System.out.println(strArr.length); 
		        for (int i = 0; i < strArr.length; ++i){
		        	if (conversedList.contains(strArr[i])) {
						matchnum++;
					}
		        }
		        if (matchnum/conversedList.size()>0.8) {
		        	System.out.println("包名被混淆，在数据库中找到包名结构和包结构匹配的Apk:"+ApkRootpackageInfo.getApkName());
					ApkRootpackageInfos.add(ApkRootpackageInfo);	
				}	
			}

		}
	}
		}	
		}
	    return ApkRootpackageInfos;				 
}
	
	
	  public static int Directorycount(String Directory) {
	    	if (Directory.contains("\\")) {
	    	  	Directory=Directory.substring(Directory.lastIndexOf("\\"));
			}
	    	int num = 0;
	        // 循环遍历每个字符，判断是否是字符 a ，如果是，累加次数
	       for (int i=0;i<Directory.length();i++)
	       {
	           // 获取每个字符，判断是否是字符a
	           if (Directory.charAt(i)=='.') {
	               // 累加统计次数
	               num++; 
	           }
	       }
	      // System.out.println(num+"级目录");
			return num;
			
		}
	
	public static List<ApkRootpackageInfo>  findallbyRoot(String unobfusDir, int rootDirnum){
		List<ApkRootpackageInfo>  ApkRootpackageInfos=new ArrayList<>();
		String hql = " from  ApkRootpackageInfo where id>0";
		List<ApkRootpackageInfo>  Apk =  ApkRootpackageInfoDAO.findByHql(hql);
		for (ApkRootpackageInfo ApkRootpackageInfo : Apk) {
			if (ApkRootpackageInfo.getApkRootpackagename().contains(unobfusDir)) {
				ApkRootpackageInfos.add(ApkRootpackageInfo);
			}
		}
	    return Apk;				
	    
	    
}
	public static void main(String[] args) throws Exception{
		//List<String> folderList=CopylibToapk.findfolder1("G:\\libdetectiongroundtruth\\APKset\\apk2smali");
		//String ARPName="io.card.payment";
		//findapkbyARPname(ARPName);
		//System.out.println(findallbyApkRootpackagename("com.admob.android.ads"));
		for (int i = 7971; i < 10070; i++) {
			String conString=findConfusedapk(i);
			if (conString!=null&&!conString.contains("?")) {
				System.out.println(conString);
			}
		}
		/**List<String>notConfusedset=new ArrayList<>();//notConfusedset是包名没有被混淆的apk根包
		List<String>Confusedset=new ArrayList<>();//Confusedset是包名被混淆的apk根包
		Set<String> notConfusedConfusedapkName=new HashSet<>();
		Set<String> ConfusedapkName=new HashSet<>();
		Set<String> apkName=new HashSet<>();
		String hql1 = " from  ApkRootpackageInfo where id>'"+1+"'";
		List<ApkRootpackageInfo>  Apk =  ApkRootpackageInfoDAO.findByHql(hql1);
		for (ApkRootpackageInfo apkRootpackageInfo : Apk){
			apkName.add(apkRootpackageInfo.getApkName());
			if(LibDetective.JudgeObfuscated(apkRootpackageInfo.getApkRootpackagename())){
        		Confusedset.add(apkRootpackageInfo.getApkRootpackagename());
        		ConfusedapkName.add(apkRootpackageInfo.getApkName());
        	}
        	else {
        		notConfusedset.add(apkRootpackageInfo.getApkRootpackagename());
        		notConfusedConfusedapkName.add(apkRootpackageInfo.getApkName());
			}
		}
		System.out.println("Confusedset"+Confusedset.size());
		System.out.println("notConfusedset"+notConfusedset.size());
		System.out.println("ConfusedapkName"+ConfusedapkName.size());
		System.out.println("notConfusedConfusedapkName"+notConfusedConfusedapkName.size());
		System.out.println("apkName"+apkName.size());**/
	}
	private static void findapkbyARPname(String ARPName) {
		List<String> apkName=new ArrayList<>();
		List<String> apkARPName=new ArrayList<>();
		List<String> dexPath=new ArrayList<>();
		int apknamenum=0;
		int apkARPnum=0;
		String hql1 = " from  ApkRootpackageInfo where apkRootpackagename='"+ARPName+"'";
		//String hql1 = " from  LibRootpackageInfo where libRootpackagename='"+libName+"'";
		List<ApkRootpackageInfo>  Apk =  ApkRootpackageInfoDAO.findByHql(hql1);
		System.out.println("apkARPnum"+Apk.size());
		for (ApkRootpackageInfo apkRootpackageInfo : Apk) {
			if (!apkName.contains(apkRootpackageInfo.getApkName())) {
				apkName.add(apkRootpackageInfo.getApkName());
				System.out.println(apkRootpackageInfo.getApkName());
				try {
					dexPath.addAll(CopylibToapk.Oldpathparse(apkRootpackageInfo.getApkName(),ARPName));
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
		System.out.println("apknamenum"+apkName.size());

		

		
	}
	public static ApkRootpackageInfo getOneByrootpackagename(String rootpackagename,String Apkname) {
		String hql = " from  ApkRootpackageInfo where ApkRootpackagename='"+rootpackagename+"'and ApkName='"+Apkname+"'";
		ApkRootpackageInfo  Apk =  ApkRootpackageInfoDAO.findOneByHql(hql);
		return Apk;
	}
}
