package cn.fudan.libpecker.analysis;

import cn.fudan.libpecker.main.LibSeacher;
import cn.fudan.libpecker.model.ApkPackageProfile;
import cn.fudan.libpecker.model.ApkProfile;
import cn.njust.analysis.cfg.CFGAnalysis;
import cn.njust.analysis.dep.DepAnalysis;
import cn.njust.analysis.profile.ClassProfile;
import cn.njust.analysis.util.DexHelper;
import cn.njust.common.Apk;
import cn.njust.common.CodeContainer;
import cn.njust.common.Sdk;

import org.jf.dexlib2.iface.Annotation;
import org.jf.dexlib2.iface.ClassDef;
import org.jf.dexlib2.iface.MethodImplementation;

import java.util.*;

/**
 * Created by yuanxzhang on 26/04/2017.
 */
public class ClassWeightAnalysis {
	static List<String> dangerouStrings=new ArrayList<>();
	
	public static void main(String[] args){
		dangerouStrings.add("DeleteContactlnfo");
		dangerouStrings.add("AddContaetlnfo");
		dangerouStrings.add("UploadFile");
		dangerouStrings.add("GetAppList");
		dangerouStrings.add("SendIntent");
		dangerouStrings.add("GetPaekagelnfo");
		dangerouStrings.add("DowrdoadFile");
		dangerouStrings.add("GetApn");
		dangerouStrings.add("SetAvReeorder");
		dangerouStrings.add("StartlmageCapture");
		dangerouStrings.add("invoke");
		dangerouStrings.add("newlnstance");
		dangerouStrings.add("DexClassLoader");
		long current = System.currentTimeMillis();
		Start();
		System.out.println("time: " + (System.currentTimeMillis() - current));
	}

    private static void Start() {
		String apkpathString="G:\\libdetectiongroundtruth\\APKset\\bacth2\\"+
    "Abstract_Painting_v1.1_apkpure.com.apk";    	
		 Sdk sdk = Sdk.loadDefaultSdk();
	        if (sdk == null) {
	            fail("default sdk not parsed");
	        }
	        Apk apk = Apk.loadFromFile(apkpathString);
	        if (apk == null) {
	            fail("apk not parsed");
	        }
		Set<String> targetSdkClassNameSet = sdk.getTargetSdkClassNameSet();
   	ApkProfile apkProfile= ApkProfile.create(apk, targetSdkClassNameSet); 
       ApkProfile apkProfile1 = ApkProfile.create1(apk, targetSdkClassNameSet);
       Map<String, ApkPackageProfile> apkPackageProfileMapcom = apkProfile1.packageProfileMap;
		
	}

	public static Map<String, Integer> getClassBBWeight(CodeContainer container) {
        Set<? extends ClassDef> allClasses = container.getClasses();
        //System.out.println(container.codeHash()+":"+container.codePath());
        Map<String, Integer> mapClassName2BBCount = new HashMap<>();
        for (ClassDef classDef : allClasses) {
            String className = DexHelper.classType2Name(classDef.getType());
            //System.out.println(className+":"+classDef);
            String pkgName = DexHelper.getPackageName(className);
            //System.out.println(className);
            int classBBCount = 0;
            for (org.jf.dexlib2.iface.Method method : classDef.getMethods()) {
                MethodImplementation impl = method.getImplementation();
                /**String methodName = method.getName();
                for (String dangerouName :dangerouStrings) {
                    if (methodName.contains(dangerouName)) {
    					System.out.println(pkgName+"&"+className+":"+methodName+"*"+method.getReturnType());
    				}
				}**/
                if (impl != null) {
                    classBBCount += CFGAnalysis.getBasicBlocks(impl).size();
                }
            }

            mapClassName2BBCount.put(className, classBBCount);
        }

        return mapClassName2BBCount;
    }
	
	private static void fail(String message) {
        System.err.println(message);
        System.exit(0);
    }

    public static Map<String, Integer> getClassDepWeight(CodeContainer container, HashSet<String> sdkClassSet) {
        DepAnalysis depAnalysis = new DepAnalysis(container, sdkClassSet);
        return getClassDepWeight(depAnalysis);
    }

    public static Map<String, Integer> getClassDepWeight(DepAnalysis depAnalysis) {
        Map<String, Integer> mapClassName2DepWeight = new HashMap<>();
        for (ClassProfile classProfile : depAnalysis.allClassProfiles) {
            String className = classProfile.getClassName();
            mapClassName2DepWeight.put(className, depAnalysis.getAllDependingClassProfile(className).size());
        }
        return mapClassName2DepWeight;
    }
}
