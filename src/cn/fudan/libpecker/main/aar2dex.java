package cn.fudan.libpecker.main;

import groundtruth.DecompressZipp;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
 
/**
 * java批量修改指定文件夹下所有后缀名的文件为另外后缀名的代码
 * @author yangwenxue(vashon)
 *
 */
public class aar2dex {
	public static void main(String args[]){
		aar2dex aa=new aar2dex();
		String aarPathString="D:\\测试下载jar";//aar所在目录
		//aa.execute(aarPathString);//开始处理aar
		//a.addAll(aa.traverseFolder1("E:\\pachongTPLSAnalytics\\Amazon-AWS-Mobile-Analytics"));
		
		/**List<String>po=new ArrayList<>();
		List<String>real=new ArrayList<>();
		for(String aarpath:a){//把a中的文件拷贝到copydest上
			if(!po.contains(aarpath.substring(aarpath.lastIndexOf("\\"),aarpath.lastIndexOf("-"))))
				po.add(aarpath.substring(aarpath.lastIndexOf("\\"),aarpath.lastIndexOf("-")));
			String copysour=aarpath;			
			
		}
		for(String p:po){
			System.out.println(p+"找到了");			
			real.add(find(a,p));
			String copydest="G:\\LibPecker源码\\LibPecker-masteroriginal\\LibPecker-master\\bin\\lib";
			copyFile(find(a,p),copydest);
			}
		System.out.println(real.size());**/
		}
	
	public  List<String> execute(List<String> aarPathString) {
		List<String> zipPathList=trans(aarPathString);//把aar转成zip
		DecompressZipp decompressZip=new DecompressZipp(zipPathList);
		List<String> jarPathList=decompressZip.startdecompressZip();//开始解压缩zip		
		return jarPathList;
	}

	private static List<String> trans(List<String> aarPathString) {
		List<String> zipPathList=new ArrayList<>();
		//List<String> a=new ArrayList<>();
		//a.addAll(traverseFolder2(aarPathString));//
		//String dir="G:\\LibPecker源码\\LibPecker-masteroriginal\\LibPecker-master\\bin\\4.8.0"+File.separator+"aar";
		//File file=new File(dir);
		String srcSuffix="aar";
		String dstSuffix="zip";//把a中的aar文件的后缀名改为zip
		//List<String> paths=listPath(file,srcSuffix);
		for(String path : aarPathString){//把a中的aar文件的后缀名改为zip
			zipPathList.add(path.replace("aar", "zip"));
			File srcFile=new File(path);
			String name=srcFile.getName();
			int idx=name.lastIndexOf(".");
			String prefix=name.substring(0, idx);
			System.out.println(srcFile.getParent());
			File dstFile=new File(srcFile.getParent()+"/"+prefix+"."+dstSuffix);
			if(dstFile.exists()){
				srcFile.delete();
				continue;
			}
			srcFile.renameTo(dstFile);
		}
		return zipPathList;
		
	}

	public static String find(List<String> a,String p){
		String po = null;
		for(String aarpath:a){
			if(aarpath.contains(p))
				po=aarpath;
				//System.out.println(aarpath);
		}
		System.out.println(po);
		return po;
		
	}
		
	
	

public List<String> traverseFolder1(String path) {//找到以classes.jar为后缀名的所有文件
	List<String> dexfilepath=new ArrayList<String>();
    int fileNum = 0, folderNum = 0;
    File file = new File(path);
    if (file.exists()) {
        LinkedList<File> list = new LinkedList<File>();
        File[] files = file.listFiles();
        for (File file2 : files) {
            if (file2.isDirectory()) {
                //System.out.println("文件夹:" + file2.getAbsolutePath());
                list.add(file2);
                folderNum++;
            } else if(file2.getAbsolutePath().contains("aar")){
                System.out.println(file2.getAbsolutePath());
                dexfilepath.add(file2.getAbsolutePath());//.replace("\\", "\\\\")
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
                } else if(file2.getAbsolutePath().contains("apk")){
                    System.out.println(file2.getAbsolutePath());
                    dexfilepath.add( file2.getAbsolutePath());
                    fileNum++;
                }
            }
        }
    } else {
        System.out.println("文件不存在!");
    }
    System.out.println("文件夹共有:" + folderNum + ",文件共有:" + fileNum);
	return dexfilepath;

}


public static List<String> traverseFolder2(String path) {//找到以classes.jar为后缀名的所有文件
	List<String> dexfilepath=new ArrayList<String>();
    int fileNum = 0, folderNum = 0;
    File file = new File(path);
    if (file.exists()) {
        LinkedList<File> list = new LinkedList<File>();
        File[] files = file.listFiles();
        for (File file2 : files) {
            if (file2.isDirectory()) {
                //System.out.println("文件夹:" + file2.getAbsolutePath());
                list.add(file2);
                folderNum++;
            } else if(file2.getAbsolutePath().contains("aar")){
                System.out.println(file2.getAbsolutePath());
                dexfilepath.add(file2.getAbsolutePath());//.replace("\\", "\\\\")
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
                } else if(file2.getAbsolutePath().contains("aar")){
                    System.out.println(file2.getAbsolutePath());
                    dexfilepath.add( file2.getAbsolutePath());
                    fileNum++;
                }
            }
        }
    } else {
        System.out.println("文件不存在!");
    }
    System.out.println("文件夹共有:" + folderNum + ",文件共有:" + fileNum);
	return dexfilepath;

}

	/**
	 * 获取指定路径下的所有符合条件的路径
	 * @param file 路径
	 * @param srcSuffix 后缀名
	 * @return
	 */
	private static List<String> listPath(File path, String srcSuffix) {
		List<String> list=new ArrayList<String>();
		File[] files=path.listFiles();
		Arrays.sort(files);
		for(File file : files){
			if(file.isDirectory()){//如果是目录
				//关键是理解以下两步操作(递归判断下级目录)
				List<String> _list= listPath(file, srcSuffix);//递归调用
				list.addAll(_list);//将集合添加到集合中
			}else{//不是目录
				String name=file.getName();
				int idx=name.lastIndexOf(".");
				String suffix=name.substring(idx+1);
				if(suffix.equals(srcSuffix)){
					list.add(file.getAbsolutePath());//把文件的绝对路径添加到集合中
					System.out.println(file.getAbsolutePath());
				}
			}
		}
		return list;
	}
	/**
	 * 复制指定路径下的文件到另一目录
	 * @param sour 路径
	 * @param dest 后缀名
	 * @return
	 */
	private static void copyFile(String sour, String dest) {
		//获取进程
		Runtime run = Runtime.getRuntime();
		Process p = null;
		//得到目标文件名
		//File sourFile =new File(sour); 
		//String  filename = sourFile.list()[0];
		String inputname = sour;//+filename;
		String command = "cmd /c copy  "+inputname+"  "+dest;
		System.out.println(command);
		//执行doc命令
		try {
			p = run.exec(command);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}



}
