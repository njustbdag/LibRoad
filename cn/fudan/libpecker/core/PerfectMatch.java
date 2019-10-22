package cn.fudan.libpecker.core;
import cn.fudan.common.util.PackageNameUtil;
import cn.fudan.libpecker.model.ApkPackageProfile;
import cn.fudan.libpecker.model.LibPackageProfile;

import java.util.*;

public class PerfectMatch {
    private Set<String> libPackages;
    private Set<String> apkPackages;

    private Map<String, String> libPackageToApkPackageMap;
    private Map<String, String> libClassToApkClassMap;
    private Map<String, List<String>> libRootPackageMap;

    private PerfectMatch(){}
    public PerfectMatch(LibPackageProfile libPackageProfileMap,ApkPackageProfile apkPackageProfileMap, Map<String, List<String>> rootPackageMap) {
        libPackages = new HashSet<>();
        apkPackages = new HashSet<>();

        libPackageToApkPackageMap = new HashMap<>();
        libClassToApkClassMap = new HashMap<>();

        libPackages.add(libPackageProfileMap.packageName);
        apkPackages.add(apkPackageProfileMap.packageName);
        libRootPackageMap = rootPackageMap;
    }
	 public boolean makePair(LibPackageProfile libPackagePairCandidate, ApkPackageProfile apkPackageProfile) {
	        String libPackageName = libPackagePairCandidate.packageName;
	        String apkPackageName = apkPackageProfile == null ? null : apkPackageProfile.packageName;

	        if (! libPackages.contains(libPackageName))
	            return false;
	        if (apkPackageName == null) {//always true if pair to null
	            libPackages.remove(libPackageName);
	            libPackageToApkPackageMap.put(libPackageName, null);
	            for (String libClassName : libPackagePairCandidate.getClassList())
	                libClassToApkClassMap.put(libClassName, null);
	            return true;
	        }
	        else {
	            if (! apkPackages.contains(apkPackageName))
	                return false;
	            else {
	                if (libPackageName.equals(apkPackageName)) {//always true if same
	                    libPackages.remove(libPackageName);
	                    apkPackages.remove(apkPackageName);
	                    libPackageToApkPackageMap.put(libPackageName, apkPackageName);
	                   // Map<String, String> classNameMap = libPackagePairCandidate.getClassNameMap(apkPackageProfile);
	                    //for (String libClassName : classNameMap.keySet())
	                     //   libClassToApkClassMap.put(libClassName, classNameMap.get(libClassName));
	                    for (String libClassName : libPackagePairCandidate.getClassList()) {
	                        if (! libClassToApkClassMap.containsKey(libClassName))
	                            libClassToApkClassMap.put(libClassName, null);
	                    }

	                    return true;
	                }
	                else {
	                    for (String existingLibPackageName : libPackageToApkPackageMap.keySet()) {
	                        if (libPackageToApkPackageMap.get(existingLibPackageName) == null)
	                            continue;

	                        int distance = 0;
	                        if (PackageNameUtil.isSiblingPackageName(existingLibPackageName, libPackageName)) {
	                            if (PackageNameUtil.isSiblingPackageName(libPackageToApkPackageMap.get(existingLibPackageName), apkPackageName))
	                                ;
	                            else
	                                return false;
	                        }
	                        else if ((distance = PackageNameUtil.isParentPackageName(existingLibPackageName, libPackageName)) > 0) {
	                            if (distance == PackageNameUtil.isParentPackageName(libPackageToApkPackageMap.get(existingLibPackageName), apkPackageName))
	                                ;
	                            else
	                                return false;
	                        }
	                        else if ((distance = PackageNameUtil.isChildPackageName(existingLibPackageName, libPackageName)) > 0) {
	                            if (distance == PackageNameUtil.isChildPackageName(libPackageToApkPackageMap.get(existingLibPackageName), apkPackageName))
	                                ;
	                            else
	                                return false;
	                        }
	                        else if (PackageNameUtil.inSameRootPackage(libPackageName, existingLibPackageName, libRootPackageMap)) {
	                            String maxCommonLibPackageName = PackageNameUtil.maxCommonPackageName(libPackageName, existingLibPackageName);
	                            String maxCommonApkPackageName = PackageNameUtil.maxCommonPackageName(apkPackageName, libPackageToApkPackageMap.get(existingLibPackageName));

	                            int libPackageNameDistance1 = PackageNameUtil.packageNameDistance(maxCommonLibPackageName, existingLibPackageName);
	                            int apkPackageNameDistance1 = PackageNameUtil.packageNameDistance(maxCommonApkPackageName, libPackageToApkPackageMap.get(existingLibPackageName));
	                            if (libPackageNameDistance1 < 0 || apkPackageNameDistance1 < 0 || libPackageNameDistance1 != apkPackageNameDistance1)
	                                return false;

	                            int libPackageNameDistance2 = PackageNameUtil.packageNameDistance(maxCommonLibPackageName, libPackageName);
	                            int apkPackageNameDistance2 = PackageNameUtil.packageNameDistance(maxCommonApkPackageName, apkPackageName);
	                            if (libPackageNameDistance2 < 0 || apkPackageNameDistance2 < 0 || libPackageNameDistance2 != apkPackageNameDistance2)
	                                return false;
	                        }
	                        else
	                            ;
	                    }
	                    libPackages.remove(libPackageName);
	                    apkPackages.remove(apkPackageName);
	                    libPackageToApkPackageMap.put(libPackageName, apkPackageName);
	                   // Map<String, String> classNameMap = libPackagePairCandidate.getClassNameMap(apkPackageProfile);
	                    //for (String libClassName : classNameMap.keySet())
	                    //    libClassToApkClassMap.put(libClassName, classNameMap.get(libClassName));
	                    for (String libClassName : libPackagePairCandidate.getClassList()) {
	                        if (! libClassToApkClassMap.containsKey(libClassName))
	                            libClassToApkClassMap.put(libClassName, null);
	                    }
	                    return true;
	                }
	            }
	        }
	    }
}
