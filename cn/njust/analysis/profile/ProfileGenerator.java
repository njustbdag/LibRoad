package cn.njust.analysis.profile;

import cn.njust.analysis.tree.ClassNode;
import cn.njust.analysis.tree.PackageNode;
import cn.njust.analysis.tree.TreeAnalysis;
import cn.njust.common.CodeContainer;
import cn.njust.common.Lib;
import cn.njust.common.Sdk;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by lemonleaves on 2017/4/17.
 */
public class ProfileGenerator {

    public static Set<ClassProfile> generate(CodeContainer codeContainer, Set<String> targetSdkClassNameSet) {
        Set<PackageNode> packageNodes = TreeAnalysis.analyze(codeContainer);
        Set<ClassProfile> allProfiles = new HashSet<>();
        for (PackageNode packageNode : packageNodes) {
            allProfiles.addAll(generate(packageNode, targetSdkClassNameSet));
        }
        return allProfiles;
    }

    // default android version: 20
    public static Set<ClassProfile> generate(CodeContainer codeContainer) {
        return generate(codeContainer, Sdk.loadDefaultSdk().getTargetSdkClassNameSet());
    }

    private static Map<PackageNode, Set<ClassProfile>> packageProfileMap = new HashMap<>();

    public static Set<ClassProfile> generate(PackageNode packageNode, Set<String> targetSdkClassNameSet) {
        if (packageProfileMap.containsKey(packageNode))
            return packageProfileMap.get(packageNode);
        else {
            Set<ClassNode> classNodes = packageNode.getIncludedClasss();
            Set<ClassProfile> classProfiles = new HashSet<>();
            for (ClassNode classNode : classNodes) {
                ClassProfile cp = new ClassProfile(classNode, targetSdkClassNameSet);
                classProfiles.add(cp);
            }

            if (packageProfileMap.containsKey(packageNode))
                return packageProfileMap.get(packageNode);
            synchronized (ProfileGenerator.class) {
                packageProfileMap.put(packageNode, classProfiles);
                return classProfiles;
            }
        }
    }

    public static void clearCache() {
        synchronized (ProfileGenerator.class) {
            packageProfileMap.clear();
        }
    }
}
