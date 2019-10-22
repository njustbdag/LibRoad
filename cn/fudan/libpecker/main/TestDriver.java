package cn.fudan.libpecker.main;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestDriver {
    public static void main(String[] args){
    	 List<String> dexfilepath=new ArrayList<>();
    	 getdexfilepath aa=new getdexfilepath();
    	dexfilepath.addAll(aa.traverseFolder1("G:\\LibPecker源码\\LibPecker-master\\test\\lib"));
        for (String line : dexfilepath){
        	TestDriver a = new TestDriver();
        	a.test(line);
        }

    }
    
    
    
    
    
    
    public void test(String dexfilepath){
        try {
            List<String> lines = FileUtils.readLines(new File("G:\\LibPecker源码\\LibPecker-masteroriginal\\LibPecker-master\\test\\apk_lib_list.txt"));
            getdexfilepath aa=new getdexfilepath();
            //List<String> dexfilepath=new ArrayList<>();
        	//dexfilepath.addAll(aa.traverseFolder1("G:\\LibPecker源码\\LibPecker-master\\test\\lib\\7cc2314e50de4dc66de7c108ab9c429f3122c6056855fac016fe2a00a9e4ee08.dex"));
            //for (String line : dexfilepath) {
                //String[] parts = line.split(":");

                String apkPath = "G:\\LibPecker源码\\LibPecker-masteroriginal\\LibPecker-master\\test\\apk\\444.apk";
                String libPath = dexfilepath;
                //double actualSimilarity = Double.parseDouble(parts[2]);
                //ProfileBasedLibPecker pecker=null;
                //@SuppressWarnings("null")
				double similarity = ProfileBasedLibPecker.singleMain(apkPath, libPath);
               // if (Math.abs(actualSimilarity-similarity) < 0.0001) {
                    //System.out.println("=== pass test: "+apkPath+" "+libPath);
                    System.out.println("similarity: "+similarity);
               // }
              //  else {
                    //System.err.println("=== fail test: "+apkPath+" "+libPath);
               // }
            //}

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
