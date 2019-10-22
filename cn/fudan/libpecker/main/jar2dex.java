
package cn.fudan.libpecker.main;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class jar2dex {


public List<String> traverseFolder1(String path) {
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
            } else if(file2.getAbsolutePath().contains("jar")){
                System.out.println(file2.getAbsolutePath());
                dexfilepath.add(file2.getAbsolutePath());
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
                } else if(file2.getAbsolutePath().contains("jar")){
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
public static void main(String[] args){
	List<String> a=new ArrayList<>();
	jar2dex aa=new jar2dex();
	//a.addAll(aa.traverseFolder1("E:\\pachongTPLSAnalytics\\Amazon-AWS-Mobile-Analytics"));
	a.addAll(aa.traverseFolder1("E:\\备份1"));
	//E:\GoogletplsAdvertising//G:\\LibPecker源码\\LibPecker-master\\test\\lib
	 System.out.println(a.size());
}
}