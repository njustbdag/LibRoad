package cn.fudan.libpecker.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import cn.fudan.common.util.HashHelper;
import cn.fudan.libpecker.model.LibProfile;
import cn.njust.common.Lib;
import cn.njust.common.Sdk;

public class PackageSortAnalysis {
	static Map<Integer, TreeSet<String>> sortPkgMap=new HashMap<>();
	static Map<String, String> newPkgMap=new HashMap<>();
	static Map<String, String> obfuscatedPkgMap=new HashMap<>();
	static Map<String, String> AdjustedtreeMap=new HashMap<>();
	static Set<String> subpkg=new HashSet<>();
	static int maxlevel=0;
	public PackageSortAnalysis(Set<String> subpkg) {
		this.subpkg=subpkg;
	}
	public static void main(String[] args) {
    	Sdk sdk = Sdk.loadDefaultSdk();
    	Set<String> targetSdkClassNameSet = sdk.getTargetSdkClassNameSet();
    	String libPath="D:\\所有的dex集合\\30_Day_Fitness_Challenge_Workout_at_Home_v1.0.41_apkpure.com.apk.com.mopub.dex";//Calligraphy_Brush_v1.2.1_apkpure.com.apk.com.adobe.dex
    	String apkpathString="E:\\libdetection论文\\LibSearcher论文\\重打包样本\\2\\000EE4E31CCCADDB3B9E301BD803CEED512B06F31D149C2E248545B49F4EE967.apk"; //G:\\libdetectiongroundtruth\\APKset\\bacth2\\85_Cafe_v1.0.8_apkpure.com.apk   	
    	Lib lib = Lib.loadFromFile(libPath);
    	LibProfile libProfile = LibProfile.create(lib, targetSdkClassNameSet); 
    	Set<String> subpkg=new HashSet<>();
    	//subpkg.addAll(libProfile.packageProfileMap.keySet());
    	subpkg.add("twitter4j"); 
		PackageSortAnalysis tempAnalysis=new PackageSortAnalysis(subpkg);
		tempAnalysis.sortBegin();	
	}
	public static TreeSet<String> sortBegin() {
		maxlevel=0;
		newPkgMap.clear();
		sortPkgMap.clear();
		obfuscatedPkgMap.clear();
		AdjustedtreeMap.clear();
		TreeSet<String> subpkg1=new TreeSet<>();
		Set<String> newsubpkg=new HashSet<>();
    	subpkg1.addAll(subpkg);
    	Set<String> throwpkg=new HashSet<>();
    	//System.out.println(subpkg1);
    	for (String string : subpkg1) {  
			//System.out.println(string);
			//System.out.println(getlevel(string));
			if (maxlevel<getlevel(string)) {
				maxlevel=getlevel(string);
			}
			if (!string.contains(".")) {
				throwpkg.add(string);
			}
		}
    	subpkg1.removeAll(throwpkg);
    	//System.out.println(subpkg1.size());
    	if (subpkg1.size()==0||subpkg1.first().equals(".")) {
    		TreeSet<String> sub=new TreeSet<>();
    		sub.add("A");
			return sub;
		}
    	for (int i = 0; i <= maxlevel; i++) {
    		TreeSet<String> curTreeSet=new TreeSet<>();
        	for (String string : subpkg1) {
        		if (getlevel(string)==i) {
        			//System.out.println(string);
        			curTreeSet.add(string);		
        			}
    		}	
        	//System.out.println(curTreeSet.size());
        	if (curTreeSet.size()!=0) {
        		//System.out.println(curTreeSet);
        		//System.out.println(i);
            	sortPkgMap.put(i, curTreeSet);	
			}
		}
    	for (Integer level : sortPkgMap.keySet()) {
    		//System.out.println(level);
    		for (String sortpkg:sortPkgMap.get(level)) { 
			//System.out.println(sortpkg+":"+getsubNodeNum(sortpkg,level));
			//System.out.println(sortpkg+":all:"+getallsubNodeNum(subpkg,sortpkg,level));
			//System.out.println(	ConvertPKG(sortpkg,getsubNodeNum(sortpkg,level)));
			String newpkgNameString=ConvertPKG(sortpkg,getsubNodeNum(sortpkg,level),getallsubNodeNum(subpkg,sortpkg,level));
			newPkgMap.put(sortpkg, newpkgNameString);
			//newsubpkg.add(newpkgNameString);
			}
		} 
    	newsubpkg.addAll(refreshpkgName(subpkg)); 
    	return sortAgain(newsubpkg);
	}
	private static int getallsubNodeNum(Set<String> Subpkg, String sortpkg, Integer level) {
		int allsubNodeNum=0;
		if (level==maxlevel) {
			return allsubNodeNum;
		}
		for (String subpkg:Subpkg) {
			if (!subpkg.equals(sortpkg)&&subpkg.contains(sortpkg)) {
				allsubNodeNum++;
			}
		}
		return allsubNodeNum;
	}
	private static HashSet<String> refreshpkgName(Set<String> subpkg) {
		HashSet<String> newsubpkg=new HashSet<>();
		//System.out.println();
		/**for (String string :newPkgMap.keySet()) {
			System.out.println("old:"+string+"     new:"+newPkgMap.get(string));
		}**/
		for (String refreshstring : subpkg) { 
			String aString=refreshstring;
			//System.out.println("正在转换："+refreshstring);
			String refreshedstring=refreshstring;
			while (newPkgMap.get(refreshstring) != null) {
				refreshedstring=refreshedstring.replace(refreshstring, newPkgMap.get(refreshstring));
				//System.out.println("更新为："+refreshedstring);
				refreshstring=refreshstring.substring(0,refreshstring.lastIndexOf("."));
				//System.out.println("未更新部分为："+refreshstring);
			}
			//System.out.println("refreshedstring:"+refreshedstring);
			newsubpkg.add(refreshedstring);
			AdjustedtreeMap.put(refreshedstring,aString);
		}
		return newsubpkg;
	}
	private static TreeSet<String> refreshobpkgName(Set<String> subpkg) {
		String hashStringBuilder = null;
		TreeSet<String> newsubpkg=new TreeSet<>();
		Map<String, String> obedtreeMap=new HashMap<>();
		System.out.println();
		/**for (String string :obfuscatedPkgMap.keySet()) {
			System.out.println("old:"+string+"     new:"+obfuscatedPkgMap.get(string));
		}**/
		for (String refreshstring : subpkg) { 
			String originalString=null;
			if (AdjustedtreeMap.keySet().contains(refreshstring)) { 
				originalString=AdjustedtreeMap.get(refreshstring);
				System.out.println(AdjustedtreeMap.get(refreshstring));
			}
			//System.out.println("正在转换："+refreshstring);
			String refreshedstring=refreshstring;
			while (obfuscatedPkgMap.get(refreshstring) != null&&refreshstring.contains(".")) {
				refreshedstring=refreshedstring.replace(refreshstring, obfuscatedPkgMap.get(refreshstring));
				//System.out.println("更新为："+refreshedstring);

					refreshstring=refreshstring.substring(0,refreshstring.lastIndexOf("."));		

				//System.out.println("未更新部分为："+refreshstring);
			}
			refreshedstring=refreshedstring.replace(refreshstring, obfuscatedPkgMap.get(refreshstring));
			System.out.println("refreshedstring:"+refreshedstring);
			newsubpkg.add(refreshedstring);
			obedtreeMap.put(refreshedstring,originalString);
		}
		for (String string : newsubpkg) {
			//System.out.println(string);
			//System.out.println(obedtreeMap.get(string));
			hashStringBuilder=hashStringBuilder+string;
		}
		//System.out.println(newsubpkg.size());
		//System.out.println(HashHelper.md5_16(hashStringBuilder));
		return newsubpkg;
	}
	private static TreeSet<String> sortAgain(Set<String> subpkg) {
		sortPkgMap.clear();
		HashMap<String, List<String>> groupHashMap=new HashMap<>();
		TreeSet<String> subpkg1=new TreeSet<>();
    	subpkg1.addAll(subpkg);
    	for (int i = 0; i < maxlevel; i++) { 
    		TreeSet<String> curTreeSet=new TreeSet<>();
        	for (String string : subpkg1) {
        		if (getlevel(string)>i) {
        			String temp=getheadp(string,i);
				//System.out.println(temp);	
				curTreeSet.add(temp);
				}        		
        		if (getlevel(string)==i+1) {
        			//System.out.println(string);
        			curTreeSet.add(string);		
        			}
        		/**if (!curTreeSet.contains(getheadp(string,maxlevel-i))) {
        			curTreeSet.add(string);	
				}**/
    		}	
        	//System.out.println(curTreeSet.size());
        	if (curTreeSet.size()!=0) {
        		//System.out.println(curTreeSet);
        		//System.out.println(i);
            	sortPkgMap.put(i, curTreeSet);	
			}else {
				//System.out.println(gethead(subpkg1.first(),i));
				//System.out.println(i);
            	sortPkgMap.put(i, gethead(subpkg1.first(),i));	
			}
        	/**System.out.println("kaishi");
        	for (String string : curTreeSet) {
				System.out.println(string+"level:"+getlevel(string));
			}
        	System.out.println("end");**/
		}

    	for (int level = 0; level < maxlevel; level++){
			 int temp=1;
    		List<String> highLevelList=new ArrayList<>();
    		//System.out.println(level);
    		for (String sortpkg:sortPkgMap.get(level)){
    			String tempo=IntercepthigherPKG(sortpkg,level);
    			 if (!groupHashMap.keySet().contains(tempo)) {
    				 groupHashMap.put(tempo, new ArrayList<String>());
    			 }
    			 groupHashMap.get(tempo).add(sortpkg);
    		}
    		if (sortPkgMap.get(level)==null) {
    			obfuscatedPkgMap.put(subpkg1.first().substring(0, subpkg1.first().lastIndexOf(".")),sethead(level));
				//System.out.println(subpkg1.first().substring(0, subpkg1.first().lastIndexOf("."))+"-----"+sethead(level));
			}else {
			 for (String sortpkg:sortPkgMap.get(level)) {
				 if (!highLevelList.contains(IntercepthigherPKG(sortpkg,level))) {
					 temp=1;
					 highLevelList.add(IntercepthigherPKG(sortpkg,level));
					 //System.out.println();
				}
				 String obfuscatedletterString=numberToLetter(groupHashMap.get(IntercepthigherPKG(sortpkg,level)).size()+1-temp++);
			//System.out.println(sortpkg+"---"+IntercepthigherPKG(sortpkg,level)+"---"+replacelastlevel(sortpkg,obfuscatedletterString));
			obfuscatedPkgMap.put(sortpkg,replacelastlevel(sortpkg,obfuscatedletterString));
			 }	
			}
		} 
		for (String string : groupHashMap.keySet()) {
			//System.out.println(string+"---size---"+groupHashMap.get(string).size());
			//System.out.println(groupHashMap.get(string));
		}
    	return refreshobpkgName(subpkg1);
	}
	private static TreeSet<String> gethead(String first, int i) {
		TreeSet<String> poSet=new TreeSet<>();
		String[] Intercept=first.split("\\.");
		String poString="";
		for (int j = 0; j <=i; j++) {
			poString=poString+"."+Intercept[j];
		}
		//System.out.println("test"+poString);
		poSet.add(poString.substring(1));
		return poSet;
	}
	
	private static String getheadp(String first, int i) {
		if (first.contains(".")) {
			String[] Intercept=first.split("\\.");
			String poString="";
			for (int j = 0; j <=i; j++) {
				poString=poString+"."+Intercept[j];
			}
			//System.out.println("test"+poString);
			return poString.substring(1);
		}else {
			return first;
		}

	}
	private static String sethead(int level) {
		String headString ="";
		for (int i = 0; i < level; i++) {
			headString=headString+"."+numberToLetter(1);
		}
		return headString.substring(1);
	}
	private static String replacelastlevel(String sortpkg,
			String obfuscatedletterString) {
		if (sortpkg.contains(".")) {
			String obfuscatedString=sortpkg.substring(0,sortpkg.lastIndexOf("."))+"."+obfuscatedletterString;
			return obfuscatedString;
		}else {
			return obfuscatedletterString;
		}

	}
	private static String InterceptPKG(String first, int level) {
		String[] Intercept=first.split("\\.");
		return Intercept[level-1];
	}
	
	/**
     * 数字转字母
     * @param num
     * @return
     */
    private static String numberToLetter(int num) {
        if (num <= 0) {
            return null;
        }
        String letter = "";
        num--;
        do {
            if (letter.length() > 0) {
                num--;
            }
            letter = ((char) (num % 26 + (int) 'A')) + letter;
            num = (int) ((num - num % 26) / 26);
        } while (num > 0);

        return letter;
    }
    
	private static String IntercepthigherPKG(String first, int level) {
		if (first.contains(".")) {
		String[] Intercept=first.split("\\.");
		String  comIntercept=first.substring(0, first.lastIndexOf("."));
		return comIntercept;//Intercept[level-2]	
		}else {
			return "head";
		}

	}
	private static String ConvertPKG(String sortpkg, int subNodeNum, int allsubNodeNum) {
		String firstString=sortpkg.substring(0,sortpkg.lastIndexOf("."));
		String secondString=subNodeNum+"*"+allsubNodeNum+sortpkg.substring(sortpkg.lastIndexOf(".")+1);
		String newString=firstString+"."+secondString;
		return newString;
	}
	private static int getsubNodeNum(String sortpkg, Integer level) {
		int subNodeNum=0;
		if (level==maxlevel) {
			return subNodeNum;
		}
		if (sortPkgMap.get(level+1)==null) {
			return 0;
		}
		for (String subpkg:sortPkgMap.get(level+1)) {
			if (subpkg.contains(sortpkg)) {
				subNodeNum++;
			}
		}
		return subNodeNum;
	}
	private static int getlevel(String pkgname) {
		String[] pkgStrings=pkgname.split("\\.");
		return pkgStrings.length;
	}
}
