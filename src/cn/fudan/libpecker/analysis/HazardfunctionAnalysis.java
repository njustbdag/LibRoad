package cn.fudan.libpecker.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import njust.lib.Service.PermissionClassnameService;
import njust.lib.Service.PermissionService;



public class HazardfunctionAnalysis {
	static List<String> dangerouStrings=new ArrayList<>();
	
	public static void main(String[] args){
		long current = System.currentTimeMillis();
		StartThreatAnalysis("104_Job_Search_v1.11.0_apkpure.com.apk"+"\\smali\\com\\alibaba");
		System.out.println("time: " + (System.currentTimeMillis() - current));
	}
	
	
	  private static void StartPermissionAnalysis(List<String> smalipath) {
		  
		  Set<String> Threat =new HashSet<>();
		  for (String smalipathstring : smalipath) {
				Threat.addAll(PermissionAnalysis(smalipathstring));
			}
			if (Threat.size()==0) {
				System.out.println("该第三方库没有使用权限！");
			}else {
				System.out.println("该第三方库使用到的权限有：");
			}
		for (String string : Threat) {
			System.out.println(string);
		}
	}


	private static List<String> PermissionAnalysis(String smalipath) {
		 List<String> permissionThreat =new ArrayList<>();
		  List<String>result= readAPITxt(smalipath);
		  if (result.size()!=0) {
			//System.out.println(smalipath);
			for (String detailDesc : result) { 
				//System.out.println(detailDesc);
				String permission=PermissionService.getOneBydetailDesc(detailDesc);
				if (permission!=null) {
					//System.out.println(detailDesc);
					//System.out.println(permission);
					permissionThreat.add(permission);
				}else {
					//System.out.println("没找到");
				}/****/
			}
			//System.out.println();
		}
		return permissionThreat;
	}


	public static Set<String> StartThreatAnalysis(String apkpathString) {
			dangerouStrings.add("DeleteContactlnfo");
			dangerouStrings.add("AddContaetlnfo");
			dangerouStrings.add("UploadFile");
			dangerouStrings.add("GetAppList");
			dangerouStrings.add("SendIntent");
			dangerouStrings.add("GetPaekagelnfo");
			dangerouStrings.add("DowrdoadFile");
			dangerouStrings.add("GetApn");
			dangerouStrings.add("SetAvReeorder");
			dangerouStrings.add("StartlmageCapture");
			dangerouStrings.add("Ljava/lang/reflect/Method;->invoke");
			dangerouStrings.add("Ljava/lang/reflect/Constructor;->newInstance");
			dangerouStrings.add("DexClassLoader");
			//dangerouStrings.add("Activity");
			//dangerouStrings.add("Service");
			//dangerouStrings.add("Lcom/e104/entity/newresume/JobConditionInfo;->getROLE()[Ljava/lang/String;");
		  apkpathString="G:\\libdetectiongroundtruth\\APKset\\apk2smali\\"+apkpathString;
		  Set<String> allThreat =new HashSet<>();
			List<String> smalipath=traverseFolder1(apkpathString);
			for (String smalipathstring : smalipath) {
				List<String> Threat =Analysis(smalipathstring);
				if (Threat!=null) { 
					allThreat.addAll(Threat);
				}
			}if (allThreat.size()==0) {
				System.out.println("该第三方库不存在威胁！");
			}else {
				for (String string : allThreat) {
		System.out.println(string);
				}
			}
			System.out.println("开始权限分析：");
			 StartPermissionAnalysis(smalipath);
			return allThreat;
		}
	
	  private static List<String> Analysis(String smalipath) {
		  List<String> Threat =new ArrayList<>();
		  List<String>result= readTxt(smalipath);
		  if (result.size()!=0) {
			//System.out.println(smalipath);
			for (String string : result) { 
				if (string.contains("Ljava/lang/reflect/Method;->invoke")||string.contains("Ljava/lang/reflect/Constructor;->newInstance")) {
					if (!Threat.contains("该第三方库存在Java反射威胁！")) {
						Threat.add("该第三方库存在Java反射威胁！");
					}			
				}
				else if (string.contains("DexClassLoader")) {
					if (!Threat.contains("该第三方库存在动态加载威胁！")) {
						Threat.add("该第三方库存在动态加载威胁！");
					}			
				}else {
					if (!Threat.contains("该第三方库存在后门威胁！")) {
						Threat.add("该第三方库存在后门威胁！");
					}
				}
				//System.out.println(string);
			}
			//System.out.println();
		}
		return Threat;
	}


	private static   List<String> findclassfile(String path){//找到path的一级文件夹目录
			List<String> fileList=new ArrayList<>();
			System.out.println("=========指定目录下的所有文件==========");
			File file = new File(path);
			File[] aa = file.listFiles();
			for (int i = 0; i < aa.length; i++) {
				if (aa[i].isFile()) {
					if(aa[i].toString().contains("smali")){
						//if(!aa[i].toString().contains("apktool.bat")){
		            System.out.println(aa[i].toString());
					fileList.add(aa[i].toString().replaceAll("\\\\", "\\\\\\\\"));
						//}
		
					}

				}
			}
			return fileList;
			
		}
	  private static List<String> traverseFolder1(String path) {
			List<String> dexfilepath=new ArrayList<String>();
		    int fileNum = 0, folderNum = 0;
		    File file = new File(path);
		    if (file.exists()) {
		        LinkedList<File> list = new LinkedList<File>();
		        File[] files = file.listFiles();
		        for (File file2 : files) {
		            if (file2.isDirectory()) {
		               // System.out.println("文件夹:" + file2.getAbsolutePath());
		                list.add(file2);
		                folderNum++;
		            } else {
		                //System.out.println("文件:" + file2.getAbsolutePath().replaceAll("\\\\", "\\\\\\\\"));
		                dexfilepath.add( file2.getAbsolutePath().replaceAll("\\\\", "\\\\\\\\"));
		                fileNum++;
		            }
		        }
		        File temp_file;
		        while (!list.isEmpty()) {
		            temp_file = list.removeFirst();
		            files = temp_file.listFiles();
		            for (File file2 : files) {
		                if (file2.isDirectory()) {
		                    //System.out.println("文件夹:" + file2.getAbsolutePath());
		                    list.add(file2);
		                    folderNum++;
		                } else {
		                    //System.out.println("文件:" + file2.getAbsolutePath().replaceAll("\\\\", "\\\\\\\\"));
		                    dexfilepath.add( file2.getAbsolutePath().replaceAll("\\\\", "\\\\\\\\"));
		                    fileNum++;
		                }
		            }
		        }
		    } else {
		        System.out.println("文件不存在!");
		    }
		    //System.out.println("文件夹共有:" + folderNum + ",文件共有:" + fileNum);
			return dexfilepath;

		}
	  
		public static List<String> readAPITxt(String filePath) {
			List<String>result1=new ArrayList<>();
			  try {
			    File file = new File(filePath);
			    if(file.isFile() && file.exists()) {
			      InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "utf-8");
			      BufferedReader br = new BufferedReader(isr);
			      String lineTxt = null;
			      while ((lineTxt = br.readLine()) != null) { 
		                    if (lineTxt.contains(";->")) {
		                    	if (!result1.contains(lineTxt.substring(lineTxt.lastIndexOf(",")+2))) {
			                     String classname=lineTxt.substring(lineTxt.lastIndexOf(",")+3, lineTxt.indexOf(";->"));
		                    	//System.out.println(classname);
		                    	if (PermissionClassnameService.findOneByCallerClass(classname)!=0) {
			    					//System.out.println(lineTxt.substring(lineTxt.lastIndexOf(",")+2));
			    					result1.add(lineTxt.substring(lineTxt.lastIndexOf(",")+2));
								}  
								}

		    				}
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
		
	public static List<String> readTxt(String filePath) {
		List<String>result1=new ArrayList<>();
		  try {
		    File file = new File(filePath);
		    if(file.isFile() && file.exists()) {
		      InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "utf-8");
		      BufferedReader br = new BufferedReader(isr);
		      String lineTxt = null;
		      while ((lineTxt = br.readLine()) != null) {
		    	  for (String dangerouName :dangerouStrings) {
	                    if (lineTxt.contains(dangerouName)) {
	    					//System.out.println(lineTxt);
	    					result1.add(lineTxt);
	    				}
					}
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
}
