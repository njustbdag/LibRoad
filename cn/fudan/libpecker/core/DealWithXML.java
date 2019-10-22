package cn.fudan.libpecker.core;

import java.io.File;   

import javax.xml.parsers.DocumentBuilder;   
import javax.xml.parsers.DocumentBuilderFactory;   
  
import org.w3c.dom.Document;   
import org.w3c.dom.NodeList;   
  
public class DealWithXML {   
public static void main(String arge[]) {   
  
  long lasting = System.currentTimeMillis();   
  getcontent("C:\\Users\\ZJY\\Desktop\\data_10k.xml");
  
} 

public static void getcontent(String xmlpath){
	  try {   
		    File f = new File(xmlpath);   
		    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();   
		    DocumentBuilder builder = factory.newDocumentBuilder();   
		    Document doc = builder.parse(f);   
		    NodeList nl = doc.getElementsByTagName("VALUE");   
		   for (int i = 0; i < nl.getLength(); i++) {   
		     System.out.print("车牌号码:"+ doc.getElementsByTagName("NO").item(i).getFirstChild().getNodeValue());   
		     System.out.println("车主地址:"+ doc.getElementsByTagName("ADDR").item(i).getFirstChild().getNodeValue());   
		    }   
		   } catch (Exception e) {   
		    e.printStackTrace();   
		   } 
}
}