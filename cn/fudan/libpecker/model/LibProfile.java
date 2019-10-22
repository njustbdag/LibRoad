package cn.fudan.libpecker.model;

import cn.fudan.common.util.HashHelper;
import cn.fudan.libpecker.analysis.ClassWeightAnalysis;
import cn.fudan.libpecker.analysis.RootPackageAnalysis;
import cn.fudan.libpecker.main.getdexfilepath;
import cn.njust.analysis.dep.DepAnalysis;
import cn.njust.analysis.profile.ClassProfile;
import cn.njust.analysis.util.DexHelper;
import cn.njust.common.Lib;
import cn.njust.common.Sdk;

import java.util.*;
import java.util.regex.Pattern;

import edu.njust.bean.LibPackagestructure;
import njust.lib.Service.LibClassInfoService;
import njust.lib.Service.LibInfoService;
import njust.lib.Service.LibPackagestructureService;
import njust.lib.Service.LibRootpackageInfoService;
import njust.lib.Service.LibSubpackageInfoService;
import njust.lib.Service.LitelibClassInfoService;

/**
 * Created by yuanxzhang on 27/04/2017.
 */
public class LibProfile {

    public Map<String, LibPackageProfile> packageProfileMap;//pkg name -> [LibPackageProfile, ...]
    public Map<String, List<String>> rootPackageMap;//root package -> [package names, ...]

    private LibProfile(){}

    public String getRootPackage(String packageName) {
        for (String rootPackageName : rootPackageMap.keySet()) {
        	//System.out.println("rootPackageName:"+rootPackageName);
            for (String pkg : rootPackageMap.get(rootPackageName)) {
            	//System.out.println("subPackageName:"+rootPackageMap.get(rootPackageName));
                if (pkg.equals(packageName))
                    return rootPackageName;
            }
        }
        return null;
    }

    public List<String> getPackagesWithSameRoot(String packageName) {
        for (String rootPackageName : rootPackageMap.keySet()) {
            for (String pkg : rootPackageMap.get(rootPackageName)) {
                if (pkg.equals(packageName))
                    return rootPackageMap.get(rootPackageName);
            }
        }
        return null;
    }

    public static LibProfile create(Lib lib, Set<String> targetSdkClassNameSet) {
        LibProfile libProfile = new LibProfile();
        libProfile.rootPackageMap = RootPackageAnalysis.previousextractRootPackages(lib);

        Map<String, Integer> classBBWeightMap = ClassWeightAnalysis.getClassBBWeight(lib);
        DepAnalysis depAnalysis = new DepAnalysis(lib, (HashSet)targetSdkClassNameSet);
        Map<String, Integer> classDepWeightMap = ClassWeightAnalysis.getClassDepWeight(depAnalysis);
        Map<String, Set<SimpleClassProfile>> packageProfileMap = getClassProfilesGroupedByPackage(depAnalysis.allClassProfiles);

        libProfile.packageProfileMap = new HashMap<>();
        for (String packageName : packageProfileMap.keySet()) {
            libProfile.packageProfileMap.put(packageName, new LibPackageProfile(packageName, classBBWeightMap, classDepWeightMap, packageProfileMap.get(packageName)));
        }

        classBBWeightMap.clear();
        classDepWeightMap.clear();
        packageProfileMap.clear();
        return libProfile;
    }

    private static Map<String, Set<SimpleClassProfile>> getClassProfilesGroupedByPackage(Set<ClassProfile> classProfileSet) {
        Map<String, Set<SimpleClassProfile>> packageClassProfileMap = new HashMap<>();
        for (ClassProfile classProfile : classProfileSet) {
            String packageName = DexHelper.getPackageName(classProfile.getClassName());
            if (! packageClassProfileMap.containsKey(packageName))
                packageClassProfileMap.put(packageName, new HashSet<SimpleClassProfile>());
            packageClassProfileMap.get(packageName).add(classProfile);
        }

        return packageClassProfileMap;
    }
    
    /**
     * 把lib信息存入数据库
     * @param args
     */
    private static void deposit(){
    	//LibRootpackageInfoService libRootpackageInfoService=new LibRootpackageInfoService();
    	//LibSubpackageInfoService LibSubpackageInfoService=new LibSubpackageInfoService();
    	//LibClassInfoService LibClassInfoService=new LibClassInfoService();
    	//LitelibClassInfoService LitelibClassInfoService=new LitelibClassInfoService();		
        List<String> dexfilepath=new ArrayList<>();
        getdexfilepath aa=new getdexfilepath();
    	dexfilepath.addAll(aa.traverseFolder1("E:\\groundtruth\\createdex\\test"));
    	Sdk sdk = Sdk.loadDefaultSdk();
        if (sdk == null) {
            fail("default sdk not parsed");
        }
    	Set<String> targetSdkClassNameSet = sdk.getTargetSdkClassNameSet();
    	List<String> allPackagestructureHashList=new ArrayList<>();
    	Set<String> allPackagestructureHashset=new HashSet();
    	for(String libPath:dexfilepath){
    		StringBuilder PackagestructureHashList = new StringBuilder();
    		Lib lib = Lib.loadFromFile(libPath);
            if (lib == null) {
                fail("lib not parsed");
            }
            String libname=libPath.substring(libPath.lastIndexOf("\\")).substring(1);
            if (libname.contains("classes.dex")) {
            	libname=libname.substring(0, libname.lastIndexOf(".")-7)+".dex";
            	}
            System.out.println("lib名:"+libname);
            String description = Pattern.compile("[\\d]").matcher(libname).replaceAll("");
            System.out.println("删除版本号后lib名:"+description);
           //// LibInfoService.addLib(libname);
            LibProfile libProfile = LibProfile.create(lib, targetSdkClassNameSet); 
            for (String keystring: libProfile.rootPackageMap.keySet()) {
            	StringBuilder PackagestructureHashList0 = new StringBuilder();
            	int rootDirnum=Directorycount(keystring);
            	 System.out.println("根包名:"+keystring);
            	 System.out.println("根包目录级数"+rootDirnum);
            	 int subpckNum=libProfile.rootPackageMap.get(keystring).size();
            	 System.out.println("子包个数："+subpckNum);
            	 //libRootpackageInfoService.addLibRootpackage(keystring,libname);
 				List<String>subList= libProfile.rootPackageMap.get(keystring);
 				Collections.sort(subList);
 				Map<String,Integer> levelListtest=new HashMap<>();
 				ConversionPkgName conversionPkgName=new ConversionPkgName(subList,levelListtest);
 				List<String> conversedList=conversionPkgName.PackageTreeGenerator();
 				String PackagestructureList = null;
				for (String astring : conversedList) {
					//System.out.println(astring); 
					PackagestructureList=PackagestructureList+astring+",";
					PackagestructureHashList0.append(astring);	
					PackagestructureHashList.append(astring);
			}		
				PackagestructureList=PackagestructureList.substring(4);
				System.out.println(PackagestructureList);
				String PackagestructureHash=HashHelper.hash(PackagestructureHashList0.toString());
				//LibRootpackageInfoService.updatepackage(libname, keystring, subpckNum, rootDirnum, PackagestructureList,PackagestructureHash);
				////LibRootpackageInfoService.addonepackage(description,libname, keystring, subpckNum, rootDirnum, PackagestructureList,PackagestructureHash);
				// for (String substring :subList) {
                	// System.out.println("lib子包:"+substring);	 
                	 //LibSubpackageInfoService.addLibSubpackage(substring,libname,keystring);
                	// LibPackageProfile libPackageProfile=libProfile.packageProfileMap.get(substring);
                	// System.out.println("lib包的类数目:"+libPackageProfile.includeClassNum);
                	/** for (String classstring : libPackageProfile.classProfileMap.keySet()) {
              			System.out.println("类名:"+classstring);
              			String classHash=libPackageProfile.classProfileMap.get(classstring).getClassHash();
              			String classHashStrict=libPackageProfile.classProfileMap.get(classstring).getClassHashStrict();
              			System.out.println(classHash);
              			System.out.println(classHashStrict);
              			//LibClassInfoService.addLibClass(classstring,libname,substring,classHash,classHashStrict);
              			//LitelibClassInfoService.addLibClass(classstring,libname,substring,classHash,classHashStrict);
                	 }**/
    			//}
            	 System.out.println();
    		}
            String PackagestructureHash=HashHelper.hash(PackagestructureHashList.toString());
            System.out.println("lib名:"+libname);
       	 System.out.println("哈希值："+PackagestructureHash);
       	////LibRootpackageInfoService.updatepackagestructureHash(libname, PackagestructureHash);
 		////LibInfoService.updatepackagestructureHash(libname,PackagestructureHash);
 		//LibPackagestructureService LibPackagestructureService=new LibPackagestructureService();
 		////LibPackagestructureService.addLib(description, PackagestructureHash);
 		System.out.println("插入数据完成");
       	 //allPackagestructureHashList.add(PackagestructureHash);
       	 //allPackagestructureHashset.add(PackagestructureHash);
           /**for (String keyString : libProfile.packageProfileMap.keySet()) {
    			System.out.println("子包名:"+keyString);
    			LibPackageProfile libPackageProfile=libProfile.packageProfileMap.get(keyString);
    			//System.out.println(libPackageProfile.includeClassNum);
    			System.out.println("类名:"+libPackageProfile.classProfileMap.keySet());
            }**/
    	}
    	//String libPath="G:\\libdetectiongroundtruth\\lib340\\967af7be58c4efde245efea26e544d4dabb351f09f396f41b40c78d32c2723a0.dex";
    	 //System.out.println("allPackagestructureHashset.size()："+allPackagestructureHashset.size());
         /**for (String astring : allPackagestructureHashset) {
				int count=0;
		         for (String bstring : allPackagestructureHashList) {
		        	 if (bstring.equals(astring)) {
						count++;
					}
					}
		         if (count>1) {
			     System.out.print(astring+":");
		         System.out.println(count);	
				}

			}**/
         //System.out.println("allPackagestructureHashList.size()："+allPackagestructureHashList.size());
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
       System.out.println(num+"级目录");
		return num;
		
	}
    
    
    
    public static void main(String[] args){
    	deposit();
    }

    private static void fail(String message) {
        System.err.println(message);
        System.exit(0);
    }

}
