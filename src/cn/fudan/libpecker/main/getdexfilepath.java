package cn.fudan.libpecker.main;

import groundtruth.FindTxtContent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class getdexfilepath {
	public List<String> traverseFolder2(String path) {//找到以classes.jar为后缀名的所有文件
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
	            } else if(file2.getAbsolutePath().contains(".aar")||file2.getAbsolutePath().contains(".jar")){
	                //System.out.println(file2.getAbsolutePath());
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
	                } else if(file2.getAbsolutePath().contains(".aar")||file2.getAbsolutePath().contains(".jar")){
	                    //System.out.println(file2.getAbsolutePath());
	                    dexfilepath.add( file2.getAbsolutePath());
	                    fileNum++;
	                }
	            }
	        }
	    } else {
	        System.out.println("文件不存在!");
	    }
	   // System.out.println("文件夹共有:" + folderNum + ",文件共有:" + fileNum);
		return dexfilepath;

	}

public List<String> traverseFolder1(String path) {
	List<String> dexfilepath=new ArrayList<String>();
    int fileNum = 0, folderNum = 0;
    File file = new File(path);
    if (file.exists()) {
        LinkedList<File> list = new LinkedList<File>();
        File[] files = file.listFiles();
        for (File file2 : files) {
            if (file2.isDirectory()) {
                System.out.println("文件夹:" + file2.getAbsolutePath());
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
                    System.out.println("文件夹:" + file2.getAbsolutePath());
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
    System.out.println("文件夹共有:" + folderNum + ",文件共有:" + fileNum);
	return dexfilepath;

}

public int Category(String path){
	if(path.contains("GoogletplsAdvertising")){
		return 1;
	}
	if(path.contains("GoogletplsAnalytics")){
		return 2;
	}
	if(path.contains("GoogletplsAndroid")){
		return 3;
	}
	if(path.contains("GoogletplsCloud")){
		return 4;
	}
	if(path.contains("GoogletplsSocialMedia")){
		return 5;
	}
	if(path.contains("GoogletplsUtilities")){
		return 6;
	}
	return 0;
}

public List<String> Getallaarjarpath(){
	List<String> a=new ArrayList<>();
	getdexfilepath aa=new getdexfilepath();
	a.addAll(aa.traverseFolder2("E:\\GoogletplsAdvertising"));
	a.addAll(aa.traverseFolder2("E:\\GoogletplsAnalytics"));
	a.addAll(aa.traverseFolder2("E:\\GoogletplsAndroid"));
	a.addAll(aa.traverseFolder2("E:\\GoogletplsCloud"));
	a.addAll(aa.traverseFolder2("E:\\GoogletplsSocialMedia"));
	a.addAll(aa.traverseFolder2("E:\\GoogletplsUtilities"));
	return a;
}

public static boolean writeTxtFile(String newStr,String filenameTemp) throws IOException {
	// 先读取原有文件内容，然后进行写入操作
	boolean flag = false;
	String filein = newStr + "\r\n";
	String temp = "";

	FileInputStream fis = null;
	InputStreamReader isr = null;
	BufferedReader br = null;

	FileOutputStream fos = null;
	PrintWriter pw = null;
	try {
		// 文件路径
		File file = new File(filenameTemp);
		// 将文件读入输入流
		fis = new FileInputStream(file);
		isr = new InputStreamReader(fis);
		br = new BufferedReader(isr);
		StringBuffer buf = new StringBuffer();

        
        
		// 保存该文件原有的内容
		for (int j = 1; (temp = br.readLine()) != null; j++) {
			buf = buf.append(temp);
			// System.getProperty("line.separator")
			// 行与行之间的分隔符 相当于“\n”
			buf = buf.append(System.getProperty("line.separator"));
		}
		
		buf.append(filein);

		fos = new FileOutputStream(file);
		pw = new PrintWriter(fos);
		pw.write(buf.toString().toCharArray());
		pw.flush();
		flag = true;
	} catch (IOException e1) {
		// TODO 自动生成 catch 块
		throw e1;
	} finally {
		if (pw != null) {
			pw.close();
		}
		if (fos != null) {
			fos.close();
		}
		if (br != null) {
			br.close();
		}
		if (isr != null) {
			isr.close();
		}
		if (fis != null) {
			fis.close();
		}
	}
	return flag;
}


public static void main(String[] args) throws IOException{

	getdexfilepath aa=new getdexfilepath();
	/**List<String> b=aa.Getallaarjarpath();
	for(String i:b){
		if(i.contains("android-beacon-library-2.9")){
			System.out.println(i);
			System.out.println(aa.Category(i));
		}

	}**/
    List<String> dexfilepath=FindTxtContent.readallTxt("G:\\libdetectiongroundtruth\\dex库\\所有dex的路径.txt","retrofit-...dex");
	for (String string : dexfilepath) {
		System.out.println(string);
	}
    //System.out.println(a.size());
}
}