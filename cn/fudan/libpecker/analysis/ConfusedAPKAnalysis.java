package cn.fudan.libpecker.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import cn.fudan.common.util.HashHelper;
import cn.fudan.libpecker.main.LibSeacher;
import cn.fudan.libpecker.model.ApkProfile;
import cn.njust.common.Apk;
import cn.njust.common.Sdk;

public class ConfusedAPKAnalysis {
	public static void main (String[] args) {
		File file=new File("C:\\Users\\ZJY\\Desktop\\test.txt");
		List<String> apkNameList=readtxt(file);
			List<String> hashList1=apkAnalysis("C:\\Users\\ZJY\\Desktop\\E\\000EE4E31CCCADDB3B9E301BD803CEED512B06F31D149C2E248545B49F4EE967.apk");
			List<String> hashList2=apkAnalysis("C:\\Users\\ZJY\\Desktop\\E\\repackage\\D73C16E1BE06B4360EA13B1E25E2933BBC99AD5CB824B31B424A6BA09B3436AC.apk");
			System.out.println("两个apk的包结构相似度："+compare2list(hashList1,hashList2));
	}
 
	private static Double compare2list(List<String> hashList1,
			List<String> hashList2) {
		int sameNUM=0;
		if (hashList1.get(hashList1.size()-1).equals(hashList2.get(hashList1.size()-1))) {
			return (double) 1;
		}
		for (String string : hashList2) {
			if (hashList1.contains(string)) {
				sameNUM++; 
			}
		}
		return (double) (sameNUM/hashList2.size());
	}

	private static List<String> apkAnalysis(String apkpathString){
		List<String> hashList=new ArrayList<>();
		StringBuilder wholeapkBuilder=new StringBuilder();
		List<String> notallASPcon=new ArrayList<>();
	    Map<String, List<String>> rootPackageMap;//root package -> [package names, ...]
		//String apkpathString="C:\\Users\\ZJY\\Desktop\\E\\000EE4E31CCCADDB3B9E301BD803CEED512B06F31D149C2E248545B49F4EE967.apk"; 	
		//System.out.println(apkpathString); 
		Sdk sdk = Sdk.loadDefaultSdk(); 
	        if (sdk == null) {
	            fail("default sdk not parsed");
	        }
	        Apk apk = Apk.loadFromFile(apkpathString);
	        if (apk == null) {
	            fail("apk not parsed");
	        }
		Set<String> targetSdkClassNameSet = sdk.getTargetSdkClassNameSet();
         rootPackageMap = RootPackageAnalysis.previousextractRootPackages(apk);
         Iterator<Entry<String, List<String>>> rootpIterator=rootPackageMap.entrySet().iterator();
         while (rootpIterator.hasNext()) {
			Map.Entry<java.lang.String, java.util.List<java.lang.String>> entry = (Map.Entry<java.lang.String, java.util.List<java.lang.String>>) rootpIterator
					.next();
			System.out.println(entry.getKey()+"构建包树开始");
			StringBuilder PackagestructureHashList0 = new StringBuilder();
			//System.out.println(entry.getValue());
			Set<String>tempList=new HashSet<>();
			tempList.addAll(entry.getValue());
			PackageSortAnalysis packageSortAnalysis=new PackageSortAnalysis(tempList);
			TreeSet<String> conversedList=packageSortAnalysis.sortBegin();
			StringBuilder PackagestructureHashList = new StringBuilder();
			String PackagestructureList = null;
			for (String astring : conversedList) { 
				PackagestructureList=PackagestructureList+astring+",";
				PackagestructureHashList0.append(astring);	
				PackagestructureHashList.append(astring);
		}				
			PackagestructureList=PackagestructureList.substring(4);
			String PackagestructureHash=HashHelper.hash(PackagestructureHashList0.toString());
			//System.out.println("该根包结构："+PackagestructureList);
			System.out.println("该根包哈希值："+PackagestructureHash);
			hashList.add(PackagestructureHash);
			wholeapkBuilder.append(PackagestructureHash);
			System.out.println();
		}
         //System.out.println(wholeapkBuilder.toString());
         System.out.println("整个apk的包结构哈希值："+HashHelper.hash(wholeapkBuilder.toString()));
         hashList.add(HashHelper.hash(wholeapkBuilder.toString()));
         return hashList;
	}

	private static void ConfusedAnalysis(String apkName) {
		List<String> notallASPcon=new ArrayList<>();
	    Map<String, List<String>> rootPackageMap;//root package -> [package names, ...]
		String apkpathString="G:\\libdetectiongroundtruth\\APKset\\bacth2\\"+apkName; 	
		//System.out.println(apkpathString); 
		Sdk sdk = Sdk.loadDefaultSdk(); 
	        if (sdk == null) {
	            fail("default sdk not parsed");
	        }
	        Apk apk = Apk.loadFromFile(apkpathString);
	        if (apk == null) {
	            fail("apk not parsed");
	        }
		Set<String> targetSdkClassNameSet = sdk.getTargetSdkClassNameSet();
         rootPackageMap = RootPackageAnalysis.previousextractRootPackages(apk);
         Iterator<Entry<String, List<String>>>pkgIterator=rootPackageMap.entrySet().iterator();
         while (pkgIterator.hasNext()) {
        	 int numOFconASP=0;
        	 int numOFunconASP=0;
			Map.Entry<java.lang.String, java.util.List<java.lang.String>> entry = (Map.Entry<java.lang.String, java.util.List<java.lang.String>>) pkgIterator
					.next();
			if (ApkProfile.JudgeObfuscated(entry.getKey())) {//如果ARP是被混淆的，就进行分析
				//System.out.println(entry.getKey()+"分析中...");
				boolean allASPconFLAG=true; 
				for (String string : entry.getValue()) {
					if (!ApkProfile.JudgeObfuscated(string)) {//如果ASP没有被混淆的，退出
						//System.out.println("apk:"+apkName+"中的ARP:"+entry.getKey()+"中的ASP被混淆："+string);
						numOFconASP++;
						allASPconFLAG=false;
					}else {
						//System.out.println("apk:"+apkName+"中的ARP:"+entry.getKey()+"中的ASP未被混淆："+string);
						numOFunconASP++;
					}
				}
				if (allASPconFLAG==false) {
					notallASPcon.add(entry.getKey());
				}
			}
			if (numOFconASP!=0) {
				System.out.println("apk:"+apkName+"中的ARP:"+entry.getKey()+"混淆情况：");
				System.out.println("未被混淆的ASP数目："+numOFunconASP);
				System.out.println("被混淆的ASP数目："+numOFconASP);
			}
		}
		
	}

	private static List<String> readtxt(File file){
        //StringBuilder result = new StringBuilder();
        List<String> resultList=new ArrayList<>();
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
            	//System.out.println(s);
            	resultList.add(s);	           
            }
            br.close();    
        }catch(Exception e){
            e.printStackTrace();
        }
        return resultList;
    } static void fail(String message) {
        System.err.println(message);
        System.exit(0);
    }
}
