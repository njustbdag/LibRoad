
package cn.fudan.libpecker.main;

import antlr.Version;
import cn.fudan.common.util.PackageNameUtil;
import cn.fudan.libpecker.core.LibApkMapper;
import cn.fudan.libpecker.core.PackageMapEnumerator;
import cn.fudan.libpecker.core.PackagePairCandidate;
import cn.fudan.libpecker.core.ParseApkTest;
import cn.fudan.libpecker.core.PerfectMatch;
import cn.fudan.libpecker.core.ProfileComparator;
import cn.fudan.libpecker.model.*;
import cn.njust.analysis.tree.PackageNode;
import cn.njust.common.Apk;
import cn.njust.common.Lib;
import cn.njust.common.LibPeckerConfig;
import cn.njust.common.Sdk;
import groundtruth.calpkgsim;

import java.io.File;
import java.io.IOException;
import java.util.*;

import njust.lib.Service.LibdetectionResultService;

import org.xmlpull.v1.XmlPullParserException;


/**
 * Created by yuanxzhang on 27/04/2017.
 */
public class ProfileBasedLibPeckertest {

    Set<String> targetSdkClassNameSet;
    LibProfile libProfile;
    List<String> libClassBasicSigList = new ArrayList<>();
    List<String> apkClassBasicSigList = new ArrayList<>();
	static Map<String, String> PkgNameMap = new HashMap<>();//apkpkgname---lib:libpkgname--apk包的最佳配对lib
	static Map<String, List<String>> LibPkgNameMap = new HashMap<>();//libname---lib:libpkgname
	static Map<String, Double> PkgSimilarity = new HashMap<>();//apkpkgname---similarity---apk包最佳配对值
	static Map<String, Double> LibpkgWeight = new HashMap<>();//lib+libpkgname---similarity
    public Map<String, ApkPackageProfile> apkPackageProfileMap;//pkg name -> ApkPackageProfile
    public Map<String, LibPackageProfile> libPackageProfileMap;//pkg name -> LibPackageProfile
    static Map<String, Integer>set=new HashMap<>();//set1是不需要对比检测的包，包括2类：1.（主模块的包，该包与主包的依赖值）；2.已经被完美匹配到的包（包名，相似度）。
    static List<String>set1=new ArrayList<>();//set1是不需要对比检测的包，包括2类：1.主模块的包；2.已经被完美匹配到的包。

    public ProfileBasedLibPeckertest(LibProfile libProfile, ApkProfile apkProfile, Set<String> targetSdkClassNameSet) {
        this.targetSdkClassNameSet = targetSdkClassNameSet;
        this.libProfile = libProfile;
        this.apkPackageProfileMap = apkProfile.packageProfileMap;
        this.libPackageProfileMap = this.libProfile.packageProfileMap;
    }
    
    private static double getClassMatchSimilarityThreshold(SimpleClassProfile libClassProfile) {
        int memberCount = 1 + libClassProfile.getMethodHashList().size() + libClassProfile.getFieldHashList().size();
        if (memberCount <= 5)
            return 1.0;
        if (memberCount <= 10)
            return 0.9;
        if (memberCount <= 15)
            return 0.8;
        if (memberCount <= 20)
            return 0.7;
        if (memberCount <= 25)
            return 0.6;
        else
            return 0.5;
    }
    public double desribecompare(ApkPackageProfile apkPackageProfile,LibPackageProfile libPackageProfile){
    	int samecount=0;
    	int libclassbum=0;
    	List<String>apkclasssigList=new ArrayList<>();
    	for(SimpleClassProfile apkClassProfile :apkPackageProfile.classProfileMap.values()){
    		apkclasssigList.add(apkClassProfile.getMethoddescriptorHashList());
    	}
    	for(SimpleClassProfile libClassProfile :libPackageProfile.classProfileMap.values()){   	
    		libclassbum++;
        		        if(apkclasssigList.contains(libClassProfile.getMethoddescriptorHashList())){
        		        	samecount++;           		        	  						          			
    	}

    }
		return samecount/libclassbum;
    	
    }
    public static double rawPackageSimilarity(LibPackageProfile libPackageProfile, ApkPackageProfile apkPackageProfile,
            /*as return value*/Map<String, String> classNameMap, /*as return value*/Map<String, Double> classRawSimilarity) {
List<String> ClassList = libPackageProfile.getWeightClassList();
      for (String libClassName : ClassList) {
SimpleClassProfile simpleLibClassProfile = libPackageProfile.classProfileMap.get(libClassName);
      double RAW_CLASS_SIMILARITY_THRESHOLD = getClassMatchSimilarityThreshold(simpleLibClassProfile);

SimpleClassProfile bestMatchApkClassProfile = null;
        double maxSimilarity = 0;

for (SimpleClassProfile apkClassProfile : apkPackageProfile.classProfileMap.values()) {
if (classNameMap.values().contains(apkClassProfile.getClassName()))
continue;

double similarity = ProfileComparator.rawClassSimilarity(simpleLibClassProfile, apkClassProfile);
if (similarity >= RAW_CLASS_SIMILARITY_THRESHOLD) {
if (similarity > maxSimilarity) {
maxSimilarity = similarity;
bestMatchApkClassProfile = apkClassProfile;
}
}
}

if (bestMatchApkClassProfile != null) {
classNameMap.put(libClassName, bestMatchApkClassProfile.getClassName());
classRawSimilarity.put(libClassName, maxSimilarity);
}
else {
classNameMap.put(libClassName, null);
classRawSimilarity.put(libClassName, 0.0);
}
}
    //  System.out.println("classRawSimilarity.size():"+classRawSimilarity.size());
double Similarity = 0.0;
for (String libClassName : ClassList) {
if (LibPeckerConfig.DEBUG_LIBPECKER) {
if (libPackageProfile.packageName.equals(LibPeckerConfig.DEBUG_LIBPECKER_LIB_PKG_NAME)
&& apkPackageProfile.packageName.equals(LibPeckerConfig.DEBUG_LIBPECKER_APK_PKG_NAME)) {
System.out.println("\t class name: "+libClassName);
System.out.println("\t\t class weight: "+libPackageProfile.getClassWeight(libClassName));
System.out.println("\t\t class similarity: "+classRawSimilarity.get(libClassName));
System.out.println("\t\t class match: "+classNameMap.get(libClassName));
}
}
//System.out.println("\t\t classRawSimilarity.get(libClassName): "+classRawSimilarity.get(libClassName));
//System.out.println("\t\t libPackageProfile.getClassWeight(libClassName): "+libPackageProfile.getClassWeight(libClassName));
//System.out.println("\t\t classRawSimilarity.size(): "+classRawSimilarity.size());
//Similarity += classRawSimilarity.get(libClassName)/classRawSimilarity.size();//*libPackageProfile.getClassWeight(libClassName);
Similarity += classRawSimilarity.get(libClassName)*libPackageProfile.getClassWeight(libClassName);
//System.out.println("Similarity:"+Similarity);
}
if(Double.isNaN(Similarity)){
	System.out.println("出现NANlllllllllllllllllllllllllllllllllllllllll");
	for (String libClassName : ClassList) {
		System.out.println("\t\t classRawSimilarity.get(libClassName): "+classRawSimilarity.get(libClassName));
		System.out.println("\t\t libPackageProfile.getClassWeight(libClassName): "+libPackageProfile.getClassWeight(libClassName));	
	}
	
}

return Similarity;
}
    
  
    public double calculatelibapkpkgsim(ApkPackageProfile apkPackageProfile,LibPackageProfile libPackageProfile){
   	        int matchLibClassBasicHashSize = 0;
            for (SimpleClassProfile simpleClassProfile : apkPackageProfile.classProfileMap.values())           	
                apkClassBasicSigList.add(simpleClassProfile.getClassHashStrict());        
            for (SimpleClassProfile simpleClassProfile : libPackageProfile.classProfileMap.values()){
            	 libClassBasicSigList.add(simpleClassProfile.getClassHashStrict());           	 
            }          
            //System.out.println("apkClassBasicSigList的大小："+apkClassBasicSigList.size());
            //System.out.println("libClassBasicSigList的大小："+libClassBasicSigList.size());
            for (String basicClassHash : libClassBasicSigList) {
                if (apkClassBasicSigList.contains(basicClassHash)) {
                    matchLibClassBasicHashSize ++;
                    apkClassBasicSigList.remove(basicClassHash);
                }
            }
            //System.out.println("matchLibClassBasicHashSize 的大小："+matchLibClassBasicHashSize);
            double classBasicHashRatioUpperBound = 1.0*matchLibClassBasicHashSize/libClassBasicSigList.size();
            
		return classBasicHashRatioUpperBound;
    	
    }
    
    
    public  double calculateMaxProbabilitytest00(String libname){
    	double libsim=0;
    	//List<String> LibPkgFlag = new ArrayList<>();
    	List<ApkPackageProfile>apkPackageProfileset=new ArrayList<>();
    	List<LibPackageProfile>libPackageProfileset=new ArrayList<>();
    	List<String>apkpkgname=new ArrayList<>();
    	List<String>libpkgname=new ArrayList<>();
    	Map<String, Double> libpkgweighted= new HashMap<>();
    	double libweightsum = 0;
    	System.out.println("这里是第一步，通过匹配包名快速找到该lib是否存在于apk中：");
        for (ApkPackageProfile apkPackageProfile : apkPackageProfileMap.values()) {
        	apkpkgname.add(apkPackageProfile.packageName);
        	apkPackageProfileset.add(apkPackageProfile);
        }
        for (LibPackageProfile libPackageProfile : libPackageProfileMap.values()) {
        	double libpkgweight=libPackageProfile.getPackageWeight1();
        	libweightsum+=libpkgweight;
        	libPackageProfileset.add(libPackageProfile);
        	libpkgname.add(libPackageProfile.packageName);                      
        }
        
        int matchpkgcount=0;
        List<String>matchedLibPkgList=new ArrayList<>();
        for(String a:libpkgname){
       	 if(apkpkgname.contains(a)){
       		matchedLibPkgList.add(a);
       		 //System.out.println("存在apk包名与lib包名相同："+a);
       		 //System.out.println("两个相同包名的包相似度为："+calculatelibapkpkg,a));
       		 matchpkgcount++;
       	 }
        }
        LibPkgNameMap.put(libname, matchedLibPkgList);
        if(matchpkgcount==0){
        	System.out.println("apk中不存在包与lib包名相同-------------第一步结束-------------");
        	return 0;
        }
        
        System.out.println("该lib一共有"+libPackageProfileset.size()+"个包");
        //System.out.println("找到包名匹配的有"+matchpkgcount+++"对apkpkg---libpkg");
        //System.out.println("libweightsum"+libweightsum);
       /** if(matchpkgcount!=libpkgname.size()){
        	System.out.println("-------------第一步结束，没有在该apk中找到与lib包名相同的包,下面进入第二步仔细匹配-----------------");
        	return 0;
        }**/
        if(libpkgname.size()==1){
        	for(String a:libpkgname){
            	LibpkgWeight.put(libname+"\\"+a, (double) 1);
        	}
        	System.out.println("这是唯一的lib包名："+libpkgname);
        	LibPackageProfile libPkg = null;
        	for (LibPackageProfile libPackageProfile : libPackageProfileMap.values()){
        		libPkg=libPackageProfile;
        	}
			PackagePairCandidate packageCandidate = new PackagePairCandidate(libPkg, apkPackageProfileMap.values(),set);
        	LibApkMapper mapper = new LibApkMapper(libPackageProfileMap, apkPackageProfileMap, libProfile.rootPackageMap);
                 ApkPackageProfile perfectMatch = packageCandidate.perfectMatch();//找到perfectMatch的apk包就是找到与lib包名相同的apk包而已
                 if (perfectMatch != null) {
                     boolean paired = mapper.makePair(packageCandidate, perfectMatch);
                     if (! paired) {
                         throw new RuntimeException("can not be true");
                     }
                     else {
               			 Map<String, String> classNameMap = new HashMap<>();
              	        Map<String, Double> classRawSimilarity = new HashMap<>();
              	        double sim0=rawPackageSimilarity(libPkg,perfectMatch, classNameMap, classRawSimilarity);
          	        	if(PkgSimilarity.keySet().contains(perfectMatch.packageName)){
          	        		if(PkgSimilarity.get(perfectMatch.packageName)>sim0)
          	        			return -1;
          	        	}         	        		          	        	
              	        if(sim0>=LibPeckerConfig.LIB_APK_PAIR_THRESHOLD){
              	        	if(PkgSimilarity.keySet().contains(perfectMatch.packageName)){
              	        		if(PkgSimilarity.get(perfectMatch.packageName)<sim0){
              	        		PkgNameMap.put(perfectMatch.packageName, libname+"\\"+libPkg.packageName);
              	        	PkgSimilarity.put(perfectMatch.packageName, sim0);
              	        		}
              	        		/**if(PkgSimilarity.get(perfectMatch.packageName)>sim0){
              	        			System.out.println("该lib只有一个包，匹配相似度为"+sim0);
              	        			System.out.println("但是这个apk包已经有最佳匹配了"+perfectMatch.packageName);
              	        			return -1;
              	        		}**/
              	        			
              	        	}
              	        	if(!PkgSimilarity.keySet().contains(perfectMatch.packageName)){
                  	            PkgNameMap.put(perfectMatch.packageName, libname+"\\"+libPkg.packageName);
                  	        	PkgSimilarity.put(perfectMatch.packageName, sim0);
                  	        	System.out.println("test1");
              	        	}
              	        	else if(PkgSimilarity.keySet().contains(perfectMatch.packageName)){
              	        		if(sim0-PkgSimilarity.get(perfectMatch.packageName)>=0||PkgSimilarity.get(perfectMatch.packageName)-sim0<0.2){
              	        	     PkgNameMap.put(perfectMatch.packageName, libname+"\\"+libPkg.packageName);
                  	        	PkgSimilarity.put(perfectMatch.packageName, sim0);
                  	        	System.out.println("test2");
              	        		}
                  	        	}
              	        System.out.println("第一步成功，该lib只有一个包，匹配相似度为"+sim0+"超过阈值，进程结束");
              	        return sim0;
              	        }
              	        else {
              	        	System.out.println("该lib只有一个包，匹配到的apk包与lib包相似度为"+sim0+"不能超过阈值，不需要进入第二步，进程结束");	
              	        	return 2;
						}
                         //packageCandidate.justKeepPerfectMatch();//如果能够找到lib包和某个apk包是perfectMatch的话，之前的packageCandidate都无效清零，只剩下这一个perfectMatch的apk包。
                     }
                 }
             
        }
        for(LibPackageProfile a:libPackageProfileset){
        	double weighted=a.getPackageWeight1()/libweightsum;
        	libpkgweighted.put(a.packageName, weighted);
       	 System.out.println("lib包名"+a.packageName+"---weighted:"+weighted);
       	LibpkgWeight.put(libname+"\\"+a.packageName, weighted);
       	 for(ApkPackageProfile b:apkPackageProfileset){
       		 if(a.packageName.equals(b.packageName)){
       			 //double sim=calculatelibapkpkgsim(b,a);
       			 Map<String, String> classNameMap = new HashMap<>();
     	        Map<String, Double> classRawSimilarity = new HashMap<>();
     	        double sim0=rawPackageSimilarity(a,b, classNameMap, classRawSimilarity);
  	        	if(PkgSimilarity.keySet().contains(b.packageName)){
  	        		if(sim0-PkgSimilarity.get(b.packageName)>=0){
  	        	     PkgNameMap.put(b.packageName, libname+"\\"+a.packageName);
      	        	PkgSimilarity.put(b.packageName, sim0);
  	        		}
  	        		/**if(PkgSimilarity.get(b.packageName)>sim0){
  	        			LibPkgFlag.add(b.packageName);
  	        			System.out.println("但是这个apk包已经有最佳匹配了"+PkgNameMap.get(b.packageName));
  	        			//return -1;
  	        		}**/

      	        	}
      	        	if(!PkgSimilarity.keySet().contains(b.packageName)){
          	            PkgNameMap.put(b.packageName, libname+"\\"+a.packageName);
          	        	PkgSimilarity.put(b.packageName, sim0);
      	        	}	    
     	        if(Double.isNaN(sim0)){
     	        	System.out.println("出现NAN,sim0=0");
     	        	sim0=0;
     	        }
       			 libsim+=sim0*libpkgweighted.get(b.packageName);;
       			 //System.out.println("lib包："+a.packageName+"和匹配到的apk包："+b.packageName+"相似度为sim："+sim0);
       			//LibPkgNameMap.put(libname, libpkgname);
       			System.out.println("lib包："+a.packageName+"和匹配到的apk包："+b.packageName+"相似度为sim0："+sim0);
       		 }
       		 
       	 }
        }
        if(matchpkgcount==libpkgname.size()){
        	 System.out.println("---------第一步结束，该lib所有包名都存在于该apk中，libsim："+libsim+"--------------");
        	 return libsim;
        }
        if(matchpkgcount>0&matchpkgcount<libpkgname.size()){       
        	double matchpercent=matchpkgcount*100/libpkgname.size();
         	 System.out.println("---------第一步结束，只有"+matchpercent+"%lib在apk中，不能完全匹配到包名，libsim："+libsim+"--------------");
         	 return libsim;
         }
		return libsim; 	
    }
    public  double calculateMaxProbabilitytest0(String libname){
    	double libsim=0;
    	double libweightsum = 0;
    	List<ApkPackageProfile>apkPackageProfileset=new ArrayList<>();
    	List<LibPackageProfile>libPackageProfileset=new ArrayList<>();
    	List<String>apkpkgname=new ArrayList<>();
    	List<String>libpkgname=new ArrayList<>();
    	List<SimpleClassProfile>libSimpleClassProfile=new ArrayList<>();
    	List<SimpleClassProfile>apkSimpleClassProfile=new ArrayList<>();
    	Map<String, Double> classRawSimilarity0= new HashMap<>();
    	Map<String, Double> libpkgweighted= new HashMap<>();
    	Map<String, String> classNameMap0 = new HashMap<>();
    	System.out.println("                                      ");
    	System.out.println("这里是第二步，通过仔细比对包相似度确定该lib是否存在于apk中：");
        for (ApkPackageProfile apkPackageProfile : apkPackageProfileMap.values()) {
        	if(!set1.contains(apkPackageProfile.packageName)){
        		apkpkgname.add(apkPackageProfile.packageName);
            	apkPackageProfileset.add(apkPackageProfile);
                for (SimpleClassProfile simpleClassProfile : apkPackageProfile.classProfileMap.values()) {
                    apkClassBasicSigList.add(simpleClassProfile.getClassHashStrict());
                    apkSimpleClassProfile.add(simpleClassProfile);
                }
        	}       	
        }

        for (LibPackageProfile libPackageProfile : libPackageProfileMap.values()) {
        	double pkgweight=libPackageProfile.getPackageWeight1();
        	libweightsum+=pkgweight;
        	libPackageProfileset.add(libPackageProfile);
        	libpkgname.add(libPackageProfile.packageName);
            for (SimpleClassProfile simpleClassProfile : libPackageProfile.classProfileMap.values()){
            	 libClassBasicSigList.add(simpleClassProfile.getClassHashStrict());  
            	 libSimpleClassProfile.add(simpleClassProfile);
            }                      
        }
        int n=1;
        System.out.println("该lib一共有"+libPackageProfileset.size()+"个包");
        //System.out.println("libweightsum"+libweightsum);
        for (LibPackageProfile libPackageProfile : libPackageProfileMap.values()){   
        	double pkgweight=libPackageProfile.getPackageWeight1();
        	double weighted=pkgweight/libweightsum;
        	libpkgweighted.put(libPackageProfile.packageName, weighted);
        	System.out.println("正在比对lib的第"+n+"个包："+libPackageProfile.packageName+"--------weight:"+pkgweight+"---weighted:"+weighted);
        	double maxsim=0;
        	String matchpkgname = null;
        	for (ApkPackageProfile apkPackageProfile : apkPackageProfileMap.values()) {
        	      Map<String, String> classNameMap = new HashMap<>();
        	        Map<String, Double> classRawSimilarity = new HashMap<>();
        	        double similarity=rawPackageSimilarity(libPackageProfile,apkPackageProfile, classNameMap, classRawSimilarity);
        	        if(Double.isNaN(similarity)){
        	        	similarity=0;
        	        }
        	        if(similarity>=0){
        	        	if(maxsim<similarity||maxsim==similarity){
              	    	   maxsim=similarity;
              	    	   matchpkgname=apkPackageProfile.packageName;             	    	   
              	       }
         		//System.out.println("libpkg--apkpkg相似性："+libPackageProfile.packageName+"--"+apkPackageProfile.packageName+"相似值"+similarity);
        	        }
            	        
        	}
        	if(maxsim==0){
        		matchpkgname=null;
        	}
	        	
        	if(maxsim>0){
            	classRawSimilarity0.put(matchpkgname, maxsim);//matchpkgname是apk包名
            	classNameMap0.put(matchpkgname, libPackageProfile.packageName);
  	        	if(PkgSimilarity.keySet().contains(matchpkgname)){
  	        		if(maxsim-PkgSimilarity.get(matchpkgname)>0.3&!matchpkgname.equals(libPackageProfile.packageName)){
  	        		PkgNameMap.put(matchpkgname, libname+"\\"+libPackageProfile.packageName);
  	        	PkgSimilarity.put(matchpkgname, maxsim);
  	        		}
  	        		if(maxsim-PkgSimilarity.get(matchpkgname)>0&matchpkgname.equals(libPackageProfile.packageName)){
  	        		PkgNameMap.put(matchpkgname, libname+"\\"+libPackageProfile.packageName);
  	        	PkgSimilarity.put(matchpkgname, maxsim);
  	        		}

  	        	}
  	        	if(!PkgSimilarity.keySet().contains(matchpkgname)){
      	            PkgNameMap.put(matchpkgname, libname+"\\"+libPackageProfile.packageName);
      	        	PkgSimilarity.put(matchpkgname, maxsim);
  	        	}
        	}
        	System.out.println("匹配结果------"+"lib包名："+libPackageProfile.packageName+"--------apk包名："+matchpkgname+"---------"+classRawSimilarity0.get(matchpkgname));
        	n++;
        }
        System.out.println("classRawSimilarity0.size():"+classRawSimilarity0.size());
        //System.out.println("classRawSimilarity0.size()"+libPackageProfileset.size()+classRawSimilarity0.size());
        for(String pkgname:classRawSimilarity0.keySet()){
            String libnameString=classNameMap0.get(pkgname);
        	libsim+=classRawSimilarity0.get(pkgname)*libpkgweighted.get(libnameString);
        	//System.out.println("--------lib包名："+libnameString+"---"+libpkgweighted.get(libnameString));
        	//System.out.println("匹配结果---"+"apk包名："+pkgname+"--------lib包名："+libnameString+"---------"+classRawSimilarity0.get(pkgname));
        }
        System.out.println("-------------第二步结束,该lib在apk中的可能性为"+libsim+"-----------------");

		return libsim;   	
    }
    
    

    private static void fail(String message) {
        System.err.println(message);
        System.exit(0);
    }

  
    
    public static boolean JudgePkgname(ApkProfile apkProfile){ 
        int Obfuscatedpkgnum=0;
        Map<String, ApkPackageProfile> apkPackageProfileMapSource=apkProfile.packageProfileMap;
    	System.out.println("下面输出apk的包信息：该apk一共有"+apkPackageProfileMapSource.size()+"个包");
        for(String pkgname:apkPackageProfileMapSource.keySet()){
        	if(JudgeObfuscated(pkgname)){
        		Obfuscatedpkgnum++;
        		System.out.print("该包被混淆处理过"); 
        	}    	
        	//System.out.println(pkgname);   	
        }
        if(Obfuscatedpkgnum!=0){
        System.out.println("其中有"+Obfuscatedpkgnum+"个包被模糊处理了");
		return true;
        }
        System.out.println("输入的apk没有被混淆处理过，只需要第一步检测");
        return false;

    }

    public static boolean JudgeObfuscated(String pkgname){
    	if(pkgname.contains(".a.")||pkgname.contains(".b.")||pkgname.contains(".c.")||pkgname.contains(".d.")||pkgname.contains(".e.")||pkgname.contains(".f."))
    		return true;
    	if(pkgname.equals("a")||pkgname.equals("b")||pkgname.equals("c")||pkgname.equals("d")||pkgname.equals("e")||pkgname.equals("f"))
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
    
    
    public static void main(String[] args) throws Exception {
    	Map<String,Float> rPackageMap=new HashMap<>();
    	Map<String,Double>Version=new HashMap<>();
    	Map<String,Integer>Vers=new HashMap<>();
    	List<String>verList=new ArrayList<>();
    	List<String>verList1=new ArrayList<>();//不同的版本号
    	boolean ObfuscatedFlag;
    	List<String>candidateliblist=new ArrayList<>();
    	Map<String,Double>MatchedLib=new HashMap<>();
    	Map<String,Double>libset6=new HashMap<>();
    	Map<String,Double>libset5=new HashMap<>();
    	Map<String,Double>libset3=new HashMap<>();
    	Map<String,Double>libset4=new HashMap<>();
    	Map<String,Double>libset2=new HashMap<>();
    	Map<String,Double>libset1=new HashMap<>();
		 String apkPath = null;
	        //String libPath0 = "G:\\libdetectiongroundtruth\\lib340";
    	String libPath0 =null;
    	
	        if (args == null || args.length == 2) {
	        	apkPath = args[0];
	            libPath0 = args[1];
	        }
	        else {
	            fail("Usage: java -cp LibPecker3.jar cn.fudan.libpecker.mainProfileBasedLibPecker <apk_path> <lib_path>");
	        }
        //String apkPath ="E:\\LibDetect实验groundtruth\\smali2apk\\00e74c118fa3902e5c85fd8e37f3d084.apk"; //"C:\\Users\\ZJY\\Desktop\\23.apk";
       System.out.println(apkPath);
	        getdexfilepath aa=new getdexfilepath();
        List<String> dexfilepath=new ArrayList<>();
    	dexfilepath.addAll(aa.traverseFolder1(libPath0));//"G:\\libpecker备份\\袁倩婷\\lib340"
    	long current = System.currentTimeMillis();
    	String pkgname=ParseApkTest.test(apkPath);//找到主包名
    	System.out.println("pkgname"+pkgname);
    	set.put(pkgname, 100);
        set.putAll(defanaly.singleMain(apkPath,pkgname));//测试时杠掉，找到主模块的其他包
        System.out.println("下面是真正输出的是主模块包名以及该包与主包间的依赖值：");
        for(String key : set.keySet()){
        	System.out.println(key);
        	set1.add(key);
        	System.out.println(set.get(key));
        }
        Sdk sdk = Sdk.loadDefaultSdk();
        if (sdk == null) {
            fail("default sdk not parsed");
        }
        Apk apk = Apk.loadFromFile(apkPath);
        if (apk == null) {
            fail("apk not parsed");
        }
        Set<String> targetSdkClassNameSet = sdk.getTargetSdkClassNameSet();
        ApkProfile apkProfile = ApkProfile.create(apk, targetSdkClassNameSet);
        Map<String,List<String>> rootPackageMap=apkProfile.rootanalysisMap(apkPath);
        //输出一些apk的包信息
        ObfuscatedFlag=JudgePkgname(apkProfile);//做实验删掉     
        // ObfuscatedFlag=false;
    	for(String libPath:dexfilepath){
    		String libname=libPath.substring(libPath.lastIndexOf("\\")).substring(1);
            Lib lib = Lib.loadFromFile(libPath);
            //File libFilePath= new File(libPath);
            //Lib lib = Lib.loadFromFile(libFilePath);
            if (lib == null) {
                fail("lib not parsed");
            }
            LibProfile libProfile = LibProfile.create(lib, targetSdkClassNameSet);     
            ProfileBasedLibPeckertest pecker = new ProfileBasedLibPeckertest(libProfile, apkProfile, targetSdkClassNameSet);
            System.out.println("--------------"+libPath.substring(libPath.lastIndexOf("\\"))+"\\进程开始--------------");
            double fastmatchsim=pecker.calculateMaxProbabilitytest00(libPath.substring(libPath.lastIndexOf("\\")));
            if(fastmatchsim>=0.6){
            	if(fastmatchsim<0.8){//fastmatchsim>=LibPeckerConfig.LIB_APK_PAIR_THRESHOLD
            		libset3.put(libname, fastmatchsim);
            	System.out.println("--------------通过第一步即可确定该lib在apk中--------------");
            	}
            	if(fastmatchsim>=0.45&fastmatchsim<0.5){
            		libset6.put(libname, fastmatchsim);
            	System.out.println("--------------通过第一步即可确定该lib在apk中--------------");
            	}
            	if(fastmatchsim>0.8&fastmatchsim<2){
            		libset1.put(libname, fastmatchsim);
            	System.out.println("--------------通过第一步即可确定该lib在apk中--------------");
            	}
            	if(fastmatchsim==2){           		
            	System.out.println("--------------通过第一步即可确定该lib不在apk中--------------");
            	}
            } 
            if(fastmatchsim<0.6&fastmatchsim>=0&ObfuscatedFlag){       		
                //double fastmatchsim1 = pecker.calculateMaxProbability();//这里是libpecker的结果
                double fastmatchsim1 =  pecker.calculateMaxProbabilitytest0(libPath.substring(libPath.lastIndexOf("\\")));//这里是我的第二步
         		if(fastmatchsim1>LibPeckerConfig.LIB_APK_PAIR_THRESHOLD&fastmatchsim1<0.8){
         			if(!libset3.containsKey(libname)&!libset1.containsKey(libname)){
         				libset4.put(libname, fastmatchsim1);
         			}       			
         		}
         		
         		if(fastmatchsim1>0.8){
         			if(!libset3.containsKey(libname)&!libset1.containsKey(libname)){
         				libset2.put(libname, fastmatchsim1);
         			}     			
         		}	
               /** if (fastmatchsim1<0.6) {
                	for (String a:PkgNameMap.keySet()) {
    					if (PkgNameMap.get(a).contains(libname)) {
    						PkgSimilarity.put(a, (double) 0);
    					}
    				}
    			}**/
         	}
        	
        	System.out.println("--------------"+libPath.substring(libPath.lastIndexOf("\\"))+"\\进程完毕--------------");
            System.out.println("                 ");
        	
        }
    	MatchedLib.putAll(libset1);
    	MatchedLib.putAll(libset2);
    	MatchedLib.putAll(libset3);
    	MatchedLib.putAll(libset4);
    	calpkgsim pecker = new calpkgsim();
    	String apkname=apkPath.substring(apkPath.lastIndexOf("\\")).substring(1);
    	String filenameTemp=pecker.creatdexFile(apkname);
    	for(String ver:MatchedLib.keySet()){
    		//pecker.writeTxtFile("："+apkname+"------"+ver+"*0",filenameTemp);
    		if (ver.contains("-")) {
        		String a=ver.substring(0, ver.lastIndexOf("-")+1);
        		if(!verList.contains(a))
        			verList.add(a);
			}

    	}
    	for(String ver:verList){
    		int cou=0;
    		for(String a:MatchedLib.keySet()){
    			if (a.contains("-")) {
        			String b=a.substring(0, a.lastIndexOf("-")+1);
            		if(b.equals(ver)){
            			cou++;
            		}
				}
    		}
    		if (cou>1) {
        		Vers.put(ver, cou);
			}
    	}
    	for(String ver:Vers.keySet()){
        	for(String a:MatchedLib.keySet()){
        		if(a.contains(ver)){
        			verList1.add(a);
        		}
    		}
    	}

    	int cou=libset2.size()+libset1.size()+libset3.size()+libset4.size();
    	System.out.println("最终检测结果有"+cou+"对:");
    	if(libset1.size()!=0||libset3.size()!=0){
    		int i1=libset1.size()+libset3.size();
    		System.out.println("通过第1步即可确定该lib在apk中的有 "+i1+"对:");
    		System.out.println("不做任何修改，直接把lib注入apk中的有 ");
        	for(String aaa:libset1.keySet()){
        		double NotPerfectWeight = 0;
        		System.out.println(aaa+"：similarity: "+libset1.get(aaa));
        		//System.out.println("这是匹配到的lib包："+LibPkgNameMap.get("\\"+aaa));
        		for(String libpkg:LibPkgNameMap.get("\\"+aaa)){
        			System.out.print(libpkg+"-------------weighted：");
        			String libnameandlibpkg="\\"+aaa+"\\"+libpkg;
        			System.out.println(LibpkgWeight.get(libnameandlibpkg));
        			String perfectmatchString=PkgNameMap.get(libpkg);
        			if(perfectmatchString!=null){
            		System.out.println("对应的apk包的perfectmatched---------------"+perfectmatchString+PkgSimilarity.get(libpkg));
            		if(!perfectmatchString.subSequence(1,perfectmatchString.lastIndexOf("\\")).equals(aaa)&PkgSimilarity.get(libpkg)!=0){
          			NotPerfectWeight+=LibpkgWeight.get(libnameandlibpkg);
            		System.out.println("注意，这个lib包不是perfectmatched");	
            		}
	
        			}

        		}
        		
        		//System.out.println("："+apkname+"------"+aaa+"*1"+filenameTemp);
        		//pecker.writeTxtFile("："+apkname+"------"+aaa+"*1",filenameTemp);
        		//apkLibService.addapklibbyname(apkname,aaa,1);
        		System.out.println("NotPerfectWeight:"+NotPerfectWeight);
        		if(NotPerfectWeight>0.7&libset1.get(aaa)!=1){
        			MatchedLib.remove(aaa);//做实验把这一步删除了，做完实验取消//
        		}
      			//if(verList1.contains(aaa)){
      				Version.put(aaa, NotPerfectWeight);
      			//}
        		System.out.println("---------------------------------------------"+aaa+"分析结束---------------------------------------------");
        		System.out.println("                     ");
        	}
        	System.out.println("不改lib名，改lib内容注入apk中的有 ");
        	for(String aaa:libset3.keySet()){
        		double NotPerfectWeight = 0;
        		System.out.println(aaa+"：similarity: "+libset3.get(aaa));
        		//System.out.println("这是匹配到的lib包："+LibPkgNameMap.get("\\"+aaa));
        		for(String libpkg:LibPkgNameMap.get("\\"+aaa)){
        			System.out.print(libpkg+"-------------weighted：");
        			String libnameandlibpkg="\\"+aaa+"\\"+libpkg;
        			System.out.println(LibpkgWeight.get(libnameandlibpkg));
        			String perfectmatchString=PkgNameMap.get(libpkg);
        			if(perfectmatchString!=null){
              		System.out.println("对应的apk包的perfectmatched：---------------"+perfectmatchString+PkgSimilarity.get(libpkg));
            		if(!perfectmatchString.subSequence(1,perfectmatchString.lastIndexOf("\\")).equals(aaa)&PkgSimilarity.get(libpkg)!=0){
          			NotPerfectWeight+=LibpkgWeight.get(libnameandlibpkg);
          			//if(verList1.contains(aaa)){
          				Version.put(aaa, NotPerfectWeight);
          			//}
            		System.out.println("注意，这个lib包不是perfectmatched");	
            		}

        			}

        		}
        		//pecker.writeTxtFile("："+apkname+"------"+aaa+"*3",filenameTemp);
        		//apkLibService.addapklibbyname(apkname,aaa,1);
        		System.out.println("NotPerfectWeight:"+NotPerfectWeight);
        		if(NotPerfectWeight>0.7&libset3.get(aaa)!=1){
        			MatchedLib.remove(aaa);//做实验把这一步删除了，做完实验取消//
        		}
        		System.out.println("---------------------------------------------"+aaa+"分析结束---------------------------------------------");
        		System.out.println("           ");
        	}
    	}
    	if(libset2.size()!=0||libset4.size()!=0){
    		int i2=libset2.size()+libset4.size();
        	System.out.println("通过第2步确定该lib在apk中的有 "+i2+"对:");
        	System.out.println("只改变lib包名后注入apk中的 ");
        	for(String aaa:libset2.keySet()){
        		System.out.println(aaa+"：similarity: "+libset2.get(aaa));
        		candidateliblist.add("G:\\libdetectiongroundtruth\\lib340\\"+aaa);
        		//pecker.writeTxtFile("："+apkname+"------"+aaa+"*2",filenameTemp);
        	}
        	System.out.println("既改变lib包名又修改lib内容注入apk中的有 ");
        	for(String aaa:libset4.keySet()){
        		System.out.println(aaa+"：similarity: "+libset4.get(aaa));
        		candidateliblist.add("G:\\libdetectiongroundtruth\\lib340\\"+aaa);
        		//pecker.writeTxtFile("："+apkname+"------"+aaa+"*4",filenameTemp);
        	}
    	}
    	System.out.println("下面是libpecker的检测结果");
    	for(String aaa:libset5.keySet()){
    		System.out.println(aaa+"：similarity: "+libset5.get(aaa));
    		//pecker.writeTxtFile("："+apkname+"------"+aaa+"*5",filenameTemp);
    	}
    	for(String aaa:libset6.keySet()){
    		System.out.println(aaa+"：similarity: "+libset6.get(aaa));
    		//pecker.writeTxtFile("："+apkname+"------"+aaa+"*6",filenameTemp);
    	}
    	for(String apkpkgname:PkgNameMap.keySet()){
    		if(PkgSimilarity.get(apkpkgname)>0.6){
    		//System.out.println("apkpkgname:"+apkpkgname);
    		//System.out.println("最佳匹配的lib包："+PkgNameMap.get(apkpkgname));
    		//System.out.println("与该lib包的相似值"+ PkgSimilarity.get(apkpkgname));
    		}

    	}
  	//通过第二步得出来的candidate才会加入到candidatelist并执行下面的筛选步骤
    	System.out.println("---------------------------------------------候选的lib筛选开始---------------------------------------------");
    	Filtration filtration=new Filtration(apkPath,candidateliblist,PkgNameMap,PkgSimilarity);
    	for(String libPath:candidateliblist){
    		System.out.println(libPath);
    		if(!filtration.JudgeCandidate2(apkProfile,rootPackageMap,libPath)){
    			//System.out.println(libPath.substring(libPath.lastIndexOf("\\")+1));
    			MatchedLib.remove(libPath.substring(libPath.lastIndexOf("\\")+1));//做实验把这一步删除了，做完实验取消//
    		}
    	}
    	System.out.println("---------------------------------------------输出每一对perfectmatch对---------------------------------------------");
    	//Map<String,List<String>> rootPackageMap=apkProfile.rootanalysisMap(apkPath);
    	int i=0;
    	System.out.println("---------------------------------根包有"+cou+"个------------------------------------");
    	for (String root:rootPackageMap.keySet()) {
        	String PerfectMatchlib = null;
        	int maxmatchcount=0;
    		i++;
    	    Map<String, Integer> matchlibMap=new HashMap<>();
        	System.out.println("------------------------------这是第"+i+"根包:"+root+"------------------------------");
        	List<String> subpkg=rootPackageMap.get(root);
    	    List<String> matchlib=new ArrayList<>();
    	    List<String> specialmatchlib=new ArrayList<>();
        	for(String sub:subpkg){
        		String bestmatchlib=PkgNameMap.get(sub);
        		System.out.println("apk子包："+sub);
        		System.out.println("这个apk包最佳匹配："+bestmatchlib+"---"+PkgSimilarity.get(sub));
        		if (bestmatchlib!=null) {
            	    String libname=bestmatchlib.substring(0, bestmatchlib.indexOf("x\\")+2);
            	    if (!specialmatchlib.contains(libname)&libname!=null) 
            	    specialmatchlib.add(libname);
            	    if (!matchlib.contains(libname)&PkgSimilarity.get(sub)>0.5) {
    					matchlib.add(libname);
    				} 
				}      
        	}
        	for (String lib:matchlib) {   
        		int count=0;
            	for(String sub:subpkg){
            		String bestmatchlib=PkgNameMap.get(sub);
            		if (bestmatchlib!=null) {
                		if (bestmatchlib.contains(lib)&PkgSimilarity.get(sub)>0.5) {
    						count++;
    					}
            		}
            	}
        	    matchlibMap.put(lib, count);	
        	    if (maxmatchcount<count) {
        	    	maxmatchcount=count;
        	    	PerfectMatchlib=lib;
				}
			}
        	//System.out.println("specialmatchlib.size()"+specialmatchlib.size());
        	if(specialmatchlib.size()==1){
        		PerfectMatchlib=specialmatchlib.get(0);
        		System.out.println("special situation");
        	}
        	for (String libString:matchlibMap.keySet()) {
        		if (MatchedLib.keySet().contains(libString.substring(1, libString.lastIndexOf("\\")))) {
        			System.out.println(libString);
    				LibAnalysis libAnalysis=new LibAnalysis(libPath0);
    				float num=(float) matchlibMap.get(libString)/libAnalysis.getlibpkgnum(libString);
    				//System.out.println("匹配到了"+matchlibMap.get(libString)+"个apk子包"+num);
    				rPackageMap.put(libString.substring(1, libString.lastIndexOf("\\")), num);
    				System.out.println();
				}
			}
        	if (PerfectMatchlib==null&specialmatchlib.size()!=0) {
				System.out.println(specialmatchlib);
				System.out.println("special situation");
			}
        	System.out.println("这个根包有"+subpkg.size()+"个子包");
        	System.out.println("------------------------------第"+i+"个apk根包"+root+"的最佳匹配lib是"+PerfectMatchlib+"------------------------------");
        	System.out.println();
    	}
    	/**for(String apkn:PkgNameMap.keySet()){
    		String bestmatchlib=PkgNameMap.get(apkn);
    		//if(!apkn.equals(bestmatchlib.substring(bestmatchlib.lastIndexOf("\\")+1)))
    		System.out.println("apk包："+apkn+"----lib包："+bestmatchlib+"---"+PkgSimilarity.get(apkn));
    	}**/
    	System.out.println("---------------------------------------------perfectmatch对输出完毕---------------------------------------------");
    	System.out.println("筛选排除后共有"+MatchedLib.size()+"个lib结果如下：");
    	for(String aaa:MatchedLib.keySet()){
    		//pecker.writeTxtFile("："+apkname+"------"+aaa+"*1",filenameTemp);
    		System.out.println(aaa+"-----similarity:"+MatchedLib.get(aaa));
    	}
    	Map<String, Double>perfectverMap=new HashMap<>();
    	System.out.println(Vers.size());
    	System.out.println(Vers.keySet());
    	System.out.println(Version.keySet());
    	for(String n:Vers.keySet()){
    		System.out.println("第n组,只能留1个");
    	
    		perfectverMap.put(n, (double) 0);
        	for(String v:Version.keySet()){
        		if(v.contains(n)&MatchedLib.get(v)!=null){
        			Double perscoreDouble=1-Version.get(v);
            		System.out.println(v+"-----PerfectScore:"+perscoreDouble+"-----similarity:"+MatchedLib.get(v));
            		System.out.println(perscoreDouble+MatchedLib.get(v));
            		if (MatchedLib.get(v)>perfectverMap.get(n)) 
            			perfectverMap.put(n, MatchedLib.get(v));
        		}
        	}
    	}
    	//System.out.println(perfectverMap.keySet());
    	for(String n:perfectverMap.keySet()){
    	  	System.out.println("-----这一组最佳版本：-----");
    		//System.out.println(n+perfectverMap.get(n));
        	for(String v:Version.keySet()){
        		//System.out.println(v+Version.get(v));
        		if (v.contains(n)&MatchedLib.get(v)!=null) {
            		Double perscoreDouble=1-Version.get(v);
        			if (MatchedLib.get(v)==perfectverMap.get(n)) {
        				System.out.println("找到最佳版本为：");
            			System.out.println(v+"-----PerfectScore:"+perscoreDouble+"-----similarity:"+MatchedLib.get(v));
					}  
            		else {
    					MatchedLib.remove(v);
    				}
    			}
        	}
    	}
    	System.out.println("筛选排除后共有"+MatchedLib.size()+"个lib结果如下：");
    	for(String aaa:MatchedLib.keySet()){
    		pecker.writeTxtFile("："+apkname+"------"+aaa+"*1",filenameTemp);
    		Double perscoreDouble=(double) 1;
    		if (Version.get(aaa)!=null) {
                  perscoreDouble=1-Version.get(aaa);
			}
    		//System.out.println(MatchedLib.get(aaa));
    		System.out.println(aaa+"-----:"+"<"+rPackageMap.get(aaa)+","+perscoreDouble+">-----similarity:"+MatchedLib.get(aaa));
    	}
        System.out.println("time: " + (System.currentTimeMillis() - current));
    }

}
