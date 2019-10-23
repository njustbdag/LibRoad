package cn.fudan.libpecker.core;

import java.io.IOException;

public class ProcessingDirectory {
	
	public static void main(String[] args) throws IOException{
		ProcessingDirectory processingDirectory=new ProcessingDirectory();
		//System.out.println(processingDirectory.Directorycount("\\support-fragment-28.0.0.dex\\android.support.v4.app"));
		processingDirectory.InterceptunobfusDir("android.support.v7.widget.a");
		//System.out.println(JudgeObfuscatedState("asdd.sqww.weew.dwe.fwe.jccc"));
	}
	
    public int Directorycount(String Directory) {
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
		return num;
		
	}
    
    public static boolean JudgeObfuscated(String pkgname){
    	String endString=null;
    	if (pkgname.contains(".")) {
        	endString=pkgname.substring(pkgname.lastIndexOf(".")+1);
        	//System.out.println(endString);	
		}
    	if(pkgname.contains(".a.")||pkgname.contains(".b.")||pkgname.contains(".c.")||pkgname.contains(".d.")||pkgname.contains(".e.")||pkgname.contains(".f.")
    			||pkgname.contains(".f.")||pkgname.contains(".g.")||pkgname.contains(".h.")||pkgname.contains(".i.")||pkgname.contains(".j.")||pkgname.contains(".k.")||pkgname.contains(".l.")
    			||pkgname.contains(".m.")||pkgname.contains(".n.")||pkgname.contains(".o.")||pkgname.contains(".p.")||pkgname.contains(".q.")||pkgname.contains(".r.")||pkgname.contains(".s.")||pkgname.contains(".t.")
    			||pkgname.contains(".u.")||pkgname.contains(".v.")||pkgname.contains(".w.")||pkgname.contains(".x.")||pkgname.contains(".y.")||pkgname.contains(".z."))
    		return true;
    	if (endString!=null) {
			    	if(endString.equals("a")||endString.equals("b")||endString.equals("c")||endString.equals("d")||endString.equals("e")||endString.equals("f")
    			||endString.equals("g")||endString.equals("h")||endString.equals("i")||endString.equals("j")||endString.equals("k")||endString.equals("l")
    			||endString.equals("m")||endString.equals("n")||endString.equals("o")||endString.equals("p")||endString.equals("q")||endString.equals("r")
    			||endString.equals("s")||endString.equals("t")||endString.equals("u")||endString.equals("v")||endString.equals("w")||endString.equals("x")
    			||endString.equals("y")||endString.equals("z"))
    		return true;
		}

    	if(pkgname.equals("a")||pkgname.equals("b")||pkgname.equals("c")||pkgname.equals("d")||pkgname.equals("e")||pkgname.equals("f")
    			||pkgname.equals("g")||pkgname.equals("h")||pkgname.equals("i")||pkgname.equals("j")||pkgname.equals("k")||pkgname.equals("l")
    			||pkgname.equals("m")||pkgname.equals("n")||pkgname.equals("o")||pkgname.equals("p")||pkgname.equals("q")||pkgname.equals("r")
    			||pkgname.equals("s")||pkgname.equals("t")||pkgname.equals("u")||pkgname.equals("v")||pkgname.equals("w")||pkgname.equals("x")
    			||pkgname.equals("y")||pkgname.equals("z"))
    		return true;
    	if(pkgname.contains(".")){
    		String lastnameString=pkgname.substring(pkgname.lastIndexOf(".")+1);
    		//System.out.println(lastnameString);
    	if(lastnameString.equals("a")||lastnameString.equals("b")||lastnameString.equals("c")||lastnameString.equals("d")||lastnameString.equals("e")||lastnameString.equals("f")){
    		return true;
    	}
    		
    	}
    	return false;
    }
    
    public static boolean JudgeObfuscatedShortDir(String startString){
    	if (startString!=null) {
	    	if(startString.equals("a")||startString.equals("b")||startString.equals("c")||startString.equals("d")||startString.equals("e")||startString.equals("f")
		||startString.equals("g")||startString.equals("h")||startString.equals("i")||startString.equals("j")||startString.equals("k")||startString.equals("l")
		||startString.equals("m")||startString.equals("n")||startString.equals("o")||startString.equals("p")||startString.equals("q")||startString.equals("r")
		||startString.equals("s")||startString.equals("t")||startString.equals("u")||startString.equals("v")||startString.equals("w")||startString.equals("x")
		||startString.equals("y")||startString.equals("z"))
	return true;
}
		return false;
    	
    }
    
    public static int JudgeObfuscatedState(String pkgname){
    	String endString=null;
    	String startString=null;
    	if (pkgname.contains(".")) {
        	endString=pkgname.substring(pkgname.lastIndexOf(".")+1);
        	startString=pkgname.substring(0, pkgname.indexOf("."));
        	//System.out.println(startString);	
        	//System.out.println(endString);	
		}
    	if (startString!=null) {
	    	if(startString.equals("a")||startString.equals("b")||startString.equals("c")||startString.equals("d")||startString.equals("e")||startString.equals("f")
		||startString.equals("g")||startString.equals("h")||startString.equals("i")||startString.equals("j")||startString.equals("k")||startString.equals("l")
		||startString.equals("m")||startString.equals("n")||startString.equals("o")||startString.equals("p")||startString.equals("q")||startString.equals("r")
		||startString.equals("s")||startString.equals("t")||startString.equals("u")||startString.equals("v")||startString.equals("w")||startString.equals("x")
		||startString.equals("y")||startString.equals("z"))
	return 4;
}
    	if(pkgname.contains(".a.")||pkgname.contains(".b.")||pkgname.contains(".c.")||pkgname.contains(".d.")||pkgname.contains(".e.")||pkgname.contains(".f.")
    			||pkgname.contains(".f.")||pkgname.contains(".g.")||pkgname.contains(".h.")||pkgname.contains(".i.")||pkgname.contains(".j.")||pkgname.contains(".k.")||pkgname.contains(".l.")
    			||pkgname.contains(".m.")||pkgname.contains(".n.")||pkgname.contains(".o.")||pkgname.contains(".p.")||pkgname.contains(".q.")||pkgname.contains(".r.")||pkgname.contains(".s.")||pkgname.contains(".t.")
    			||pkgname.contains(".u.")||pkgname.contains(".v.")||pkgname.contains(".w.")||pkgname.contains(".x.")||pkgname.contains(".y.")||pkgname.contains(".z."))
    		return 1;
    	if (endString!=null) {
			    	if(endString.equals("a")||endString.equals("b")||endString.equals("c")||endString.equals("d")||endString.equals("e")||endString.equals("f")
    			||endString.equals("g")||endString.equals("h")||endString.equals("i")||endString.equals("j")||endString.equals("k")||endString.equals("l")
    			||endString.equals("m")||endString.equals("n")||endString.equals("o")||endString.equals("p")||endString.equals("q")||endString.equals("r")
    			||endString.equals("s")||endString.equals("t")||endString.equals("u")||endString.equals("v")||endString.equals("w")||endString.equals("x")
    			||endString.equals("y")||endString.equals("z"))
    		return 2;
		}
    	if(pkgname.equals("a")||pkgname.equals("b")||pkgname.equals("c")||pkgname.equals("d")||pkgname.equals("e")||pkgname.equals("f")
    			||pkgname.equals("g")||pkgname.equals("h")||pkgname.equals("i")||pkgname.equals("j")||pkgname.equals("k")||pkgname.equals("l")
    			||pkgname.equals("m")||pkgname.equals("n")||pkgname.equals("o")||pkgname.equals("p")||pkgname.equals("q")||pkgname.equals("r")
    			||pkgname.equals("s")||pkgname.equals("t")||pkgname.equals("u")||pkgname.equals("v")||pkgname.equals("w")||pkgname.equals("x")
    			||pkgname.equals("y")||pkgname.equals("z"))
    		return 3;

    		    	
    	return 0;
    }
    
    
    public String InterceptunobfusDir(String Directory){
    	if (JudgeObfuscatedState(Directory)==0) {
    		System.out.println("未混淆目录为："+Directory);
			return Directory;
		}
    	String unobfusDir = ".";
    	int ObfuscatedState=JudgeObfuscatedState(Directory);
    	//System.out.println("初始状态："+ObfuscatedState);
    	if (ObfuscatedState!=3) {
        	while(ObfuscatedState!=4&Directory.contains(".")){ 
        		//System.out.println(Directory.substring(0, Directory.indexOf(".")));
            		String firstDir=Directory.substring(0, Directory.indexOf("."));
            		if(!JudgeObfuscatedShortDir(firstDir)){
            			unobfusDir=unobfusDir.concat(firstDir)+".";
            			Directory=Directory.substring(firstDir.length()+1);
            			//System.out.println(unobfusDir);
            			//System.out.println(Directory);
            		}   		
            	ObfuscatedState=JudgeObfuscatedState(Directory);
            	//System.out.println(ObfuscatedState);
    	}	
		}
    	if (unobfusDir.length()>1) {
		   	unobfusDir=unobfusDir.substring(1,unobfusDir.length()-1);	
		}
    	else {
    		unobfusDir=unobfusDir.substring(1);
		}
    	System.out.println("未混淆目录为："+unobfusDir);
		return unobfusDir;   	
    }
    
}
