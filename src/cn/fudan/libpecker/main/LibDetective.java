package cn.fudan.libpecker.main;



import cn.fudan.libpecker.core.LibApkMapper;
import cn.fudan.libpecker.core.PackagePairCandidate;
import cn.fudan.libpecker.core.ProcessingDirectory;
import cn.fudan.libpecker.core.ProfileComparator;
import cn.fudan.libpecker.model.*;
import cn.njust.common.Apk;
import cn.njust.common.Lib;
import cn.njust.common.LibPeckerConfig;
import cn.njust.common.Sdk;
import groundtruth.calpkgsim;
import groundtruth.downloadjar;



import java.util.*;
import java.util.regex.Pattern;


public class LibDetective {

    Set<String> targetSdkClassNameSet;
    LibProfile libProfile;
    List<String> libClassBasicSigList = new ArrayList<>();
    List<String> apkClassBasicSigList = new ArrayList<>();
	static Map<String, String> PkgNameMap = new HashMap<>();//apkpkgname---lib:libpkgname--apk包的最佳配对lib
	static Map<String, List<String>> PkgNameMaxMap = new HashMap<>();//apkpkgname---lib:libpkgname--apk包的最佳配对libs
	static Map<String, List<String>> LibPkgNameMap = new HashMap<>();//libname---lib:libpkgname
	static Map<String, Double> PkgSimilarity = new HashMap<>();//apkpkgname---similarity---apk包最佳配对值
	static Map<String, Double> LibpkgWeight = new HashMap<>();//lib+libpkgname---similarity
    public Map<String, ApkPackageProfile> apkPackageProfileMap;//pkg name -> ApkPackageProfile
    public static Map<String, ApkPackageProfile> apkPackageProfileMapcom;//pkg name -> ApkPackageProfile
    public Map<String, LibPackageProfile> libPackageProfileMap;//pkg name -> LibPackageProfile
    static Map<String, Integer>set=new HashMap<>();//set1是不需要对比检测的包，包括2类：1.（主模块的包，该包与主包的依赖值）；2.已经被完美匹配到的包（包名，相似度）。
    static List<String>set1=new ArrayList<>();//set1是不需要对比检测的包，包括2类：1.主模块的包；2.已经被完美匹配到的包。
    static List<String>notConfusedset=new ArrayList<>();//notConfusedset是包名没有被混淆的apk根包
    static List<String>Confusedset=new ArrayList<>();//Confusedset是包名被混淆的apk根包

    public LibDetective(LibProfile libProfile, ApkProfile apkProfile, Set<String> targetSdkClassNameSet) {
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
return Similarity;
}
    public static double rawPackageSimilaritythird(LibPackageProfile libPackageProfile, ApkPackageProfile apkPackageProfile,
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
if (classNameMap.get(libClassName)!=null) {
Similarity += classRawSimilarity.get(libClassName)*apkPackageProfile.getClassWeight(classNameMap.get(libClassName));	
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
    
    
    public  double firstStep(List<String> notConfusedset, String libname){
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
       		 matchpkgcount++;
       	 }
        }
        LibPkgNameMap.put(libname, matchedLibPkgList);
        if(matchpkgcount==0){
        	System.out.println("apk中不存在包与lib包名相同-------------第一步结束-------------");
        	return 0;
        }
        
        System.out.println("该lib一共有"+libPackageProfileset.size()+"个包");
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
              	        	}
              	        	if(!PkgSimilarity.keySet().contains(perfectMatch.packageName)){
                  	            PkgNameMap.put(perfectMatch.packageName, libname+"\\"+libPkg.packageName);
                  	        	PkgSimilarity.put(perfectMatch.packageName, sim0);
                  	        	List<String> dList=new ArrayList<>();
                  	        	dList.add(libname+"\\"+libPkg.packageName);
            	  	        		PkgNameMaxMap.put(perfectMatch.packageName, dList);
              	        	}
              	        	else if(PkgSimilarity.keySet().contains(perfectMatch.packageName)&PkgNameMaxMap.get(perfectMatch.packageName)!=null){
              	        		if(sim0-PkgSimilarity.get(perfectMatch.packageName)>0||PkgSimilarity.get(perfectMatch.packageName)-sim0<0.2){
              	        	     PkgNameMap.put(perfectMatch.packageName, libname+"\\"+libPkg.packageName);
                  	        	PkgSimilarity.put(perfectMatch.packageName, sim0);
              	        		}
              	        		if(sim0==PkgSimilarity.get(perfectMatch.packageName)){
                      	        	List<String> dList=PkgNameMaxMap.get(perfectMatch.packageName);
                      	        	dList.add(libname+"\\"+libPkg.packageName);
                	  	        		PkgNameMaxMap.put(perfectMatch.packageName, dList);
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
      	        	}
      	        	if(!PkgSimilarity.keySet().contains(b.packageName)){
          	            PkgNameMap.put(b.packageName, libname+"\\"+a.packageName);
          	        	PkgSimilarity.put(b.packageName, sim0);
      	        	}	    
       			 libsim+=sim0*libpkgweighted.get(b.packageName);;
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
    @SuppressWarnings("null")
	public  double secondStep(List<String> confusedset, String libname){
    	double libsim=0;
    	double libweightsum = 0;
    	List<ApkPackageProfile>apkPackageProfileset=new ArrayList<>();
    	List<LibPackageProfile>libPackageProfileset=new ArrayList<>();
    	List<String>apkpkgname=new ArrayList<>();
    	List<String>libpkgname=new ArrayList<>();
    	List<SimpleClassProfile>libSimpleClassProfile=new ArrayList<>();
    	Map<String, Double> classRawSimilarity0= new HashMap<>();
    	Map<String, Double> libpkgweighted= new HashMap<>();
    	Map<String, String> classNameMap0 = new HashMap<>();
    	System.out.println("                                      ");
    	System.out.println("这里是第二步，通过仔细比对包相似度确定该lib是否存在于apk中：");
        for (LibPackageProfile libPackageProfile : libPackageProfileMap.values()) {
        	double pkgweight=libPackageProfile.getPackageWeight1();
        	libweightsum+=pkgweight;
        	libPackageProfileset.add(libPackageProfile);
        	libpkgname.add(libPackageProfile.packageName);                  
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
        		if (confusedset.contains(apkPackageProfile.packageName)) {
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
        	}
        	if(maxsim==0){
        		matchpkgname=null;
        	}
	        	
        	if(maxsim>0){
            	classRawSimilarity0.put(matchpkgname, maxsim);//matchpkgname是apk包名
            	classNameMap0.put(matchpkgname, libPackageProfile.packageName);
  	        	if(PkgSimilarity.keySet().contains(matchpkgname)){
  	        		if(maxsim==PkgSimilarity.get(matchpkgname)){
  	        			List<String> dList=new ArrayList<>();
  	        			if (PkgNameMaxMap.get(matchpkgname)!=null) {
							dList.addAll(PkgNameMaxMap.get(matchpkgname));
						}  	        			
  	        			dList.add(libname+"\\"+libPackageProfile.packageName);
  	  	        		PkgNameMaxMap.put(matchpkgname, dList);
  	        		}
  	        		if(maxsim-PkgSimilarity.get(matchpkgname)>0.01&!matchpkgname.equals(libPackageProfile.packageName)){
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
      	        	if (libname+"\\"+libPackageProfile.packageName!=null) {
					List<String> dList=new ArrayList<>();
      	        	dList.add(libname+"\\"+libPackageProfile.packageName);
	  	        		PkgNameMaxMap.put(matchpkgname, dList);
					}

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
    
    public  double thirdStep(List<String> notConfusedset, String libname, List<String> subpkg){//用新的公式重新计算包相似度
    	double libsim=0;
    	double apksim=0;
    	//List<String> LibPkgFlag = new ArrayList<>();
    	List<ApkPackageProfile>apkPackageProfileset=new ArrayList<>();
    	List<LibPackageProfile>libPackageProfileset=new ArrayList<>();
    	List<String>apkpkgname=new ArrayList<>();
    	List<String>libpkgname=new ArrayList<>();
    	Map<String, Double> libpkgweighted= new HashMap<>();
    	double libweightsum = 0;
    	double apkweightsum = 0;    
        for (LibPackageProfile libPackageProfile : libPackageProfileMap.values()) {
        	double libpkgweight=libPackageProfile.getPackageWeight1();
        	libweightsum+=libpkgweight;
        	libPackageProfileset.add(libPackageProfile);
        	libpkgname.add(libPackageProfile.packageName);                      
        }
        for (ApkPackageProfile apkPackageProfile : apkPackageProfileMapcom.values()) {
        	if (subpkg.contains(apkPackageProfile.packageName)&libpkgname.contains(apkPackageProfile.packageName)) {
			double apkpkgweight=apkPackageProfile.getPackageWeight();
        	apkweightsum+=apkpkgweight;
        	apkpkgname.add(apkPackageProfile.packageName);
        	apkPackageProfileset.add(apkPackageProfile);	
			}
        }  
        System.out.println("该lib一共有"+libPackageProfileset.size()+"个包");
        for(LibPackageProfile a:libPackageProfileset){
        	double weighted=a.getPackageWeight1()/libweightsum;        	
        	libpkgweighted.put(a.packageName, weighted);
       	LibpkgWeight.put(libname+"\\"+a.packageName, weighted);
       	 for(ApkPackageProfile b:apkPackageProfileset){
       		 if(a.packageName.equals(b.packageName)){
       			 Map<String, String> classNameMap = new HashMap<>();
     	        Map<String, Double> classRawSimilarity = new HashMap<>();
     	        double sim0=rawPackageSimilaritythird(a,b, classNameMap, classRawSimilarity);
  	        	if(PkgSimilarity.keySet().contains(b.packageName)){
  	        		if(sim0-PkgSimilarity.get(b.packageName)>=0){
  	        	     PkgNameMap.put(b.packageName, libname+"\\"+a.packageName);
      	        	PkgSimilarity.put(b.packageName, sim0);
  	        		}
      	        	}
      	        	if(!PkgSimilarity.keySet().contains(b.packageName)){
          	            PkgNameMap.put(b.packageName, libname+"\\"+a.packageName);
          	        	PkgSimilarity.put(b.packageName, sim0);
      	        	}
      	        	double apkweighted=b.getPackageWeight()/apkweightsum;
       			 libsim+=sim0*libpkgweighted.get(b.packageName);
       			apksim+=sim0*apkweighted;
       			System.out.println("lib包："+a.packageName+"和匹配到的apk包："+b.packageName+"----apkweighted:"+apkweighted+"相似度为sim0："+sim0);
       		 }
       		 
       	 }
        }
        if (apksim>libsim) {
        	libsim=apksim;
		}
        if (libsim!=0) {
			System.out.println("libsim:"+libsim);
		}    
		return libsim; 	
    }

    private static void fail(String message) {
        System.err.println(message);
        System.exit(0);
    }

    public  double fourthStep(List<String> confusedset, String libname){//用新的公式重新计算包相似度
    	double libsim=0;
    	double libweightsum = 0;
    	List<LibPackageProfile>libPackageProfileset=new ArrayList<>();
    	List<String>libpkgname=new ArrayList<>();
    	Map<String, Double> classRawSimilarity0= new HashMap<>();
    	Map<String, Double> libpkgweighted= new HashMap<>();
    	Map<String, String> classNameMap0 = new HashMap<>();
        for (LibPackageProfile libPackageProfile : libPackageProfileMap.values()) {
        	double pkgweight=libPackageProfile.getPackageWeight1();
        	libweightsum+=pkgweight;
        	libPackageProfileset.add(libPackageProfile);
        	libpkgname.add(libPackageProfile.packageName);                  
        }
        int n=1;    
        for (LibPackageProfile libPackageProfile : libPackageProfileMap.values()){   
        	double pkgweight=libPackageProfile.getPackageWeight1();
        	double weighted=pkgweight/libweightsum;
        	libpkgweighted.put(libPackageProfile.packageName, weighted);        	
        	double maxsim=0;
        	String matchpkgname = null;
        	for (ApkPackageProfile apkPackageProfile : apkPackageProfileMapcom.values()) {
        		if (confusedset.contains(apkPackageProfile.packageName)) {
        			Map<String, String> classNameMap = new HashMap<>();
        	        Map<String, Double> classRawSimilarity = new HashMap<>();
        	        double similarity=rawPackageSimilaritythird(libPackageProfile,apkPackageProfile, classNameMap, classRawSimilarity);
        	        if(similarity>=0){
        	        	if(maxsim<similarity||maxsim==similarity){
              	    	   maxsim=similarity;
              	    	   matchpkgname=apkPackageProfile.packageName;             	    	   
              	       }
         		}	
				}	        
        	}
        	if(maxsim==0){
        		matchpkgname=null;
        	}
	        	
        	if(maxsim>0){
            	classRawSimilarity0.put(matchpkgname, maxsim);//matchpkgname是apk包名
            	classNameMap0.put(matchpkgname, libPackageProfile.packageName);
        	}
        	n++;
        }
        for(String pkgname:classRawSimilarity0.keySet()){
            String libnameString=classNameMap0.get(pkgname);
        	libsim+=classRawSimilarity0.get(pkgname)*libpkgweighted.get(libnameString);
      }
        System.out.println("libsim"+libsim);
		return libsim;   	
    }
    
    public static boolean JudgePkgname(ApkProfile apkProfile){ 
        int Obfuscatedpkgnum=0;
        Map<String, ApkPackageProfile> apkPackageProfileMapSource=apkProfile.packageProfileMap;
    	System.out.println("下面输出apk的包信息：该apk一共有"+apkPackageProfileMapSource.size()+"个包");
        for(String pkgname:apkPackageProfileMapSource.keySet()){
        	if(JudgeObfuscated(pkgname)){
        		Obfuscatedpkgnum++;
        		//System.out.print("该包被混淆处理过"); 
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
 
    public static void start(String apkPath,String libPath0) throws Exception {
    	ProcessingDirectory processingDirectory=new ProcessingDirectory();
    	Map<String,Float> rPackageMap=new HashMap<>();
    	Map<String,Double>Version=new HashMap<>();
    	Map<String,Integer>Vers=new HashMap<>();
    	List<String>verList=new ArrayList<>();
    	List<String>verList1=new ArrayList<>();//不同的版本号
    	boolean ObfuscatedFlag = false;
    	List<String>candidateliblist=new ArrayList<>();
    	Map<String,Double>MatchedLib=new HashMap<>();
    	Map<String,Double>finalMatchedLib=new HashMap<>();
    	Map<String,Double>tempMatchedLib=new HashMap<>();
    	Map<String,String>finalMatchedrootpkg=new HashMap<>();
    	Map<String,String>IntegrityfinalMatchedrootpkg=new HashMap<>();
    	Map<String,Double>libset6=new HashMap<>();
    	Map<String,Double>libset5=new HashMap<>();
    	Map<String,Double>libset3=new HashMap<>();
    	Map<String,Double>libset4=new HashMap<>();
    	Map<String,Double>libset2=new HashMap<>();
    	Map<String,Double>libset1=new HashMap<>();  
    	
       System.out.println(apkPath);
	        getdexfilepath aa=new getdexfilepath();
        List<String> dexfilepath=new ArrayList<>();
    	dexfilepath.addAll(aa.traverseFolder1(libPath0));//"G:\\libpecker备份\\袁倩婷\\lib340"

    	long current = System.currentTimeMillis();
        System.out.println("下面是真正输出的是主模块包名");
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
        String apkname=apkPath.substring(apkPath.lastIndexOf("\\")).substring(1);
        calpkgsim pecker1 = new calpkgsim();
        String filenameTemp=pecker1.creatdexFile(apkname);
        ApkProfile apkProfile = ApkProfile.create(apk, targetSdkClassNameSet);
        ApkProfile apkProfile1 = ApkProfile.create1(apk, targetSdkClassNameSet);
        apkPackageProfileMapcom=apkProfile1.packageProfileMap;
        Map<String,ApkPackageProfile> apkPackageProfilemap=apkProfile1.packageProfileMap;
    	System.out.println("---------------------------------apk分析开始------------------------------------");
    	System.out.println("下面输出apk的包信息：该apk一共有"+apkProfile.packageProfileMap.size()+"个包");
        Map<String,List<String>> rootPackageMap=apkProfile.SimplerootanalysisMap(apkPath);
        for (String rootPackagename: rootPackageMap.keySet()) {
        	for (String subPackagename :rootPackageMap.get(rootPackagename)) {
            	if(JudgeObfuscated(subPackagename)){
            		Confusedset.add(subPackagename);
            	}
            	else {
            		notConfusedset.add(subPackagename);
    			}
			}
		}
        System.out.println("被混淆过的根包有"+Confusedset.size()+"个:"+Confusedset);
        System.out.println("未被混淆过的根包有"+notConfusedset.size()+"个:"+notConfusedset);
    	System.out.println("---------------------------------apk分析完成------------------------------------");
        //输出一些apk的包信息
    	System.out.println("time: " + (System.currentTimeMillis() - current));
    	pecker1.writeTxtFile("time0:" + (System.currentTimeMillis() - current),filenameTemp);
        if (Confusedset.size()!=0) {
            ObfuscatedFlag=true;
		}    
        
    	for(String libPath:dexfilepath){
    		String libname=libPath.substring(libPath.lastIndexOf("\\")).substring(1);
            Lib lib = Lib.loadFromFile(libPath);
            if (lib == null) {
                fail("lib not parsed");
            }
            LibProfile libProfile = LibProfile.create(lib, targetSdkClassNameSet);     
            LibDetective detective = new LibDetective(libProfile, apkProfile, targetSdkClassNameSet);
            System.out.println("--------------"+libPath.substring(libPath.lastIndexOf("\\"))+"\\进程开始--------------");
            double fastmatchsim=detective.firstStep(notConfusedset,libPath.substring(libPath.lastIndexOf("\\")));
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
                double fastmatchsim1 =  detective.secondStep(Confusedset,libPath.substring(libPath.lastIndexOf("\\")));//这里是我的第二步
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
         	}
        	
        	System.out.println("--------------"+libPath.substring(libPath.lastIndexOf("\\"))+"\\进程完毕--------------");
            System.out.println("                 ");
        	
        }
    	MatchedLib.putAll(libset1);
    	MatchedLib.putAll(libset2);
    	MatchedLib.putAll(libset3);
    	MatchedLib.putAll(libset4);
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
  	//通过第二步得出来的candidate才会加入到candidatelist并执行下面的筛选步骤
    	/**System.out.println("---------------------------------------------候选的lib筛选开始---------------------------------------------");
    	Filtration filtration=new Filtration(apkPath,candidateliblist,PkgNameMap,PkgSimilarity);
    	for(String libPath:candidateliblist){
    		System.out.println(libPath);
    		if(!filtration.JudgeCandidate2(apkProfile,rootPackageMap,libPath)){
    			//System.out.println(libPath.substring(libPath.lastIndexOf("\\")+1));
    			MatchedLib.remove(libPath.substring(libPath.lastIndexOf("\\")+1));//做实验把这一步删除了，做完实验取消//
    		}
    	}**/
    	System.out.println("---------------------------------------------输出每一对perfectmatch对---------------------------------------------");
    	//Map<String,List<String>> rootPackageMap=apkProfile.rootanalysisMap(apkPath);
    	int i=0;
    	System.out.println("---------------------------------根包有"+rootPackageMap.size()+"个------------------------------------");
    	System.out.println("time1: " + (System.currentTimeMillis() - current));
    	pecker1.writeTxtFile("time1:" + (System.currentTimeMillis() - current),filenameTemp);
    	for (String root:rootPackageMap.keySet()) {
    		List<String> perfectMatchlibList=new ArrayList<>();//该list中无重复的key值
    		List<String> perfectMatchlibListsum=new ArrayList<>();//该list中有重复的key值
        	String PerfectMatchlib = null;
        	int maxmatchcount=0;
    		i++;
    	    Map<String, Integer> matchlibMap=new HashMap<>();
        	System.out.println("------------------------------这是第"+i+"根包:"+root+"------------------------------");
        	List<String> subpkg=rootPackageMap.get(root);
        	System.out.println("这个根包有"+subpkg.size()+"个子包");
    	    List<String> matchlib=new ArrayList<>();
    	    List<String> specialmatchlib=new ArrayList<>();
        	for(String sub:subpkg){
        		System.out.println("apk子包："+sub);
        		int subnum=Directorycount(sub);
        		String bestmatchlib=PkgNameMap.get(sub);
        		if (bestmatchlib!=null) {
            		List<String> bestmatchliblist=PkgNameMaxMap.get(sub);
            		if (bestmatchliblist!=null) {
    			        		if (bestmatchliblist.contains(bestmatchlib)) {
                		bestmatchliblist.remove(bestmatchlib);
    				}		
    				}
        			//ApkPackageProfile apkPackageProfile=apkPackageProfilemap.get(sub);
         			//System.out.println(apkPackageProfile.getPackageWeight());
            		String unobfusDir=processingDirectory.InterceptunobfusDir(sub);
            		System.out.println("这个apk包最佳匹配：");
            		if (Directorycount(bestmatchlib)==subnum&bestmatchlib.contains(unobfusDir)) {
            			String a=bestmatchlib;
						a=a.substring(1,a.lastIndexOf("\\"));
						perfectMatchlibListsum.add(a);
						if (!perfectMatchlibList.contains(a)) {
							perfectMatchlibList.add(a);	
						}
                		System.out.println(bestmatchlib+"---"+PkgSimilarity.get(sub));	
					}
            		if (bestmatchliblist!=null) {
            			if (bestmatchliblist.size()!=0) {
						//System.out.println("这个apk包还有其他的最佳匹配：");
                   		//System.out.println(bestmatchliblist.size());
            			for (String a : bestmatchliblist) {
            				if (Directorycount(a)==subnum&bestmatchlib.contains(unobfusDir)) {
							System.out.println(a);
							a=a.substring(1,a.lastIndexOf("\\"));
							perfectMatchlibListsum.add(a);
							if (!perfectMatchlibList.contains(a)) {
								perfectMatchlibList.add(a);	
							}
							}
    					}	
						}
    				}
            		if (bestmatchlib!=null) {
                	    String libname=bestmatchlib.substring(0, bestmatchlib.indexOf("x\\")+2);
                	    if (!specialmatchlib.contains(libname)&libname!=null) 
                	    specialmatchlib.add(libname);
                	    if (!matchlib.contains(libname)&PkgSimilarity.get(sub)>0.5) {
        					matchlib.add(libname);
        				} 
    				} 	
            		System.out.println();
				}else {
					System.out.println("该apk子包没有最佳匹配");
					System.out.println();
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
        	if(specialmatchlib.size()==1){
        		PerfectMatchlib=specialmatchlib.get(0);
        		System.out.println("special situation");
        	}
        	 System.out.println("匹配到的有可能的lib有："+perfectMatchlibList);
        	for (String libString:perfectMatchlibList){
        		if (!MatchedLib.keySet().contains(libString)) {
        			System.out.println(libString+"-----相似度低于阈值");
        			System.out.println("匹配到了"+Collections.frequency(perfectMatchlibListsum,libString)+"个apk子包");
        			String libPath=libPath0+"\\"+libString;
        			Lib lib = Lib.loadFromFile(libPath);
                    LibProfile libProfile = LibProfile.create(lib, targetSdkClassNameSet);     
                    LibDetective detective = new LibDetective(libProfile, apkProfile, targetSdkClassNameSet);
        			 double fastmatchsim=detective.thirdStep(notConfusedset,libPath.substring(libPath.lastIndexOf("\\")),subpkg);      			
        			if (fastmatchsim==0&Confusedset.size()!=0) {
        				double fastmatchsim1 = detective.fourthStep(Confusedset,libPath.substring(libPath.lastIndexOf("\\")));    			 
        				fastmatchsim=fastmatchsim1;
        			}
        			 tempMatchedLib.put(libString, fastmatchsim);
        			 if (fastmatchsim>0.6) {
			        	finalMatchedLib.put(libString, fastmatchsim);			    
			        	finalMatchedrootpkg.put(libString, root);
					}
			        else if(perfectMatchlibList.size()==1){
			        	finalMatchedLib.put(libString, fastmatchsim);
			        finalMatchedrootpkg.put(libString, root);
			        }
        		}	
                		if (MatchedLib.keySet().contains(libString)) {
                			int matchcou=Collections.frequency(perfectMatchlibListsum,libString);
                			System.out.println(libString+"-----similarity:"+MatchedLib.get(libString));
                			tempMatchedLib.put(libString, MatchedLib.get(libString));
        			        	finalMatchedLib.put(libString, MatchedLib.get(libString)); 
        			        	finalMatchedrootpkg.put(libString, root);
            				LibAnalysis libAnalysis=new LibAnalysis(libPath0);
            				float num=(float) matchcou/libAnalysis.getlibpkgnum("\\"+libString+"\\");
            				//float num=(float) matchlibMap.get(libString)/subpkg.size();
            				System.out.println("匹配到了"+matchcou+"个apk子包"+num);
            				rPackageMap.put(libString, num);
        				}       							
        	}
        	if (!finalMatchedrootpkg.containsValue(root)&perfectMatchlibList.size()!=0) {    
        		for (String a : perfectMatchlibList) {
        			finalMatchedLib.put(a, tempMatchedLib.get(a));
        			finalMatchedrootpkg.put(a, root);
				}        		
			}
        	System.out.println("------------------------------第"+i+"个apk根包"+root+"的最佳匹配lib是"+PerfectMatchlib+"------------------------------");
        	System.out.println();
    	}
    	pecker1.writeTxtFile("time2:" + (System.currentTimeMillis() - current),filenameTemp);
    	System.out.println("time2:" + (System.currentTimeMillis() - current));
    	System.out.println("---------------------------------------------perfectmatch对输出完毕---------------------------------------------");
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
    	int j=1;
    	List<String> retestRootPackageList=new ArrayList<>();
    	for (String root:rootPackageMap.keySet()) {
    		System.out.println("第"+j+"个根包："+root);
    		int p=0;
        	for(String aaa:finalMatchedLib.keySet()){
        		if (finalMatchedrootpkg.get(aaa)==root) {
				System.out.println(aaa);
				pecker1.writeTxtFile("："+root+"------"+aaa,filenameTemp);
				p++;
				}        		
        	}
        	for(String aaa:finalMatchedLib.keySet()){
        		if (finalMatchedrootpkg.get(aaa)==root) {
				System.out.println(finalMatchedLib.get(aaa));	
				pecker1.writeTxtFile(String.valueOf(finalMatchedLib.get(aaa)),filenameTemp);
				if (finalMatchedLib.get(aaa)<0.99) {
					String description = Pattern.compile("[\\d]").matcher(aaa).replaceAll("");
					IntegrityfinalMatchedrootpkg.put(description,root);
				}
				}        		
        	}
        	if (p==0) {
        		retestRootPackageList.add(root);
			}
        	j++;
		}
    	System.out.println("retestRootPackageList.size():"+retestRootPackageList.size());
    	System.out.println("retestRootPackageList:"+retestRootPackageList);
    	/**
    	 * 下面部分是在线搜索根包功能
    	 */
    	downloadjar downLoadjar=new downloadjar(retestRootPackageList);
    	String newdexPath=downLoadjar.startDownload(apkname);
    	SupplementDetective supplementDetective=new SupplementDetective();
    	IntegrityfinalMatchedrootpkg.putAll(supplementDetective.start(apkPath, newdexPath));
    	//System.out.println("***************************现在开始完整性检测*****************************");
    	/**for (String astring : IntegrityfinalMatchedrootpkg.keySet()) {
			System.out.println(astring+"---------"+IntegrityfinalMatchedrootpkg.get(astring));
			System.out.println();
		}**/
    	//Integrityquickmatch integrityquickmatch=new Integrityquickmatch(apkPath,apkProfile,IntegrityfinalMatchedrootpkg);
    	//integrityquickmatch.IntegrityAnalysis();;
    	/**
    	 * 下面部分是APP_Repository搜索根包功能
    	 
    	for (String ARPName : retestRootPackageList) {
    		Set<String> dexPath=ExtractlibbyARP.findapkbyARPname(ARPName);
		}*/
    	pecker1.writeTxtFile("time3:" + (System.currentTimeMillis() - current),filenameTemp);
    	System.out.println("time3:" + (System.currentTimeMillis() - current));
        }
    
	

	public static int Directorycount(String Directory) {
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
       //System.out.println(num+"级目录");
		return num;
		
	}
    
    public static void main(String[] args) throws Exception {
		 String apkPath = null;
 	     String libPath =null;
        if (args == null || args.length == 2) {
        	 apkPath = args[0];
             libPath = args[1];
        }
        else {
            fail("Usage: java -cp LibPecker3.jar cn.fudan.libpecker.mainProfileBasedLibPecker <apk_path> <lib_path>");
        }
        start(apkPath,libPath);
        
}
}
