package cn.fudan.libpecker.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserXML {

	public static void main(String[] args) throws IOException{
		File file1 = new File("G:\\libdetectiongroundtruth\\APKset\\apk2smali\\85_Cafe_v1.0.8_apkpure.com.apk\\AndroidManifest.xml");
		ParserXML parserXML=new ParserXML();
		String LibList1=parserXML.readtxt(file1);
		System.out.println(LibList1);
	}
	
	
	public  String readtxt(File file){
        //StringBuilder result = new StringBuilder();
        String resultList = null;
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
            	if (s.contains("package=")) {
                	//System.out.println(s);
                	resultList=s.substring(s.indexOf("package=")+9);
                	if (!resultList.contains(" ")) {
                		resultList=resultList.substring(0, resultList.indexOf(">")-1);
					}
                	else {
				     	resultList=resultList.substring(0, resultList.indexOf(" ")-1);
					}

                	
				}          
            }
            br.close();    
        }catch(Exception e){
            e.printStackTrace();
        }
        return resultList;
    }
	
}
