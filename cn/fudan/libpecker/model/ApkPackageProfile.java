package cn.fudan.libpecker.model;

import cn.njust.analysis.profile.ClassProfile;
import cn.njust.analysis.profile.ProfileGenerator;
import cn.njust.analysis.tree.PackageNode;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by yuanxzhang on 27/04/2017.
 */
public class ApkPackageProfile implements Serializable {
    static final long serialVersionUID = 179792789887672412L;

    public String packageName;

    public Map<String, Integer> classBBWeightMap;
    public Map<String, Integer> classDepWeightMap;

    public Map<String, SimpleClassProfile> classProfileMap = new HashMap<>();

    public int includeClassNum;
    public int subPackagesNum;
    private Map<String, Double> classWeights = null;
    protected void finalize(){
        packageName = null;
        classProfileMap.clear();
        classBBWeightMap.clear();
        classDepWeightMap.clear();
    }

    public ApkPackageProfile(PackageNode packageNode, Set<String> targetSdkClassNameSet) {
        this.packageName = packageNode.getPackageName();

        Set<ClassProfile> classProfiles = ProfileGenerator.generate(packageNode, targetSdkClassNameSet);
        for (ClassProfile classProfile : classProfiles) {
            classProfileMap.put(classProfile.getClassName(), classProfile);
        }

        this.subPackagesNum = packageNode.getSubPackagesNum();
        this.includeClassNum = this.classProfileMap.size();
    }
    
    public ApkPackageProfile(String packageName, Map<String, Integer> classBBWeightMap, Map<String, Integer> classDepWeightMap, Set<SimpleClassProfile> simpleLibClassProfiles) {
        this.packageName = packageName;
        this.classBBWeightMap = new HashMap<>();
        this.classDepWeightMap = new HashMap<>();
        this.classProfileMap = new HashMap<>();


        for (SimpleClassProfile classProfile : simpleLibClassProfiles) {
            this.classBBWeightMap.put(classProfile.getClassName(), classBBWeightMap.get(classProfile.getClassName()));
            this.classDepWeightMap.put(classProfile.getClassName(), classDepWeightMap.get(classProfile.getClassName()));
            this.classProfileMap.put(classProfile.getClassName(), classProfile);
        }

        this.includeClassNum = classProfileMap.size();
    }

    public ApkPackageProfile(PackageNode packageNode, Map<String, Integer> classBBWeightMap, Map<String, Integer> classDepWeightMap, Set<String> targetSdkClassNameSet) {
        this.packageName = packageNode.getPackageName();

        this.subPackagesNum = packageNode.getSubPackagesNum();
        this.includeClassNum = packageNode.getIncludedClassNum();
        this.classBBWeightMap = new HashMap<>();
        this.classDepWeightMap = new HashMap<>();

        Set<ClassProfile> classProfiles = ProfileGenerator.generate(packageNode, targetSdkClassNameSet);
        for (ClassProfile classProfile : classProfiles) {
            this.classProfileMap.put(classProfile.getClassName(), classProfile);
            this.classBBWeightMap.put(classProfile.getClassName(), classBBWeightMap.get(classProfile.getClassName()));
            this.classDepWeightMap.put(classProfile.getClassName(), classDepWeightMap.get(classProfile.getClassName()));
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(packageName);

        Map<String, SimpleClassProfile> cachedClassProfileMap = new HashMap<>();
        for (String className : classProfileMap.keySet()) {
            if (classProfileMap.get(className) instanceof CachedClassProfile)
                cachedClassProfileMap.put(className, classProfileMap.get(className));
            else
                cachedClassProfileMap.put(className, CachedClassProfile.create((ClassProfile)classProfileMap.get(className)));
        }
        out.writeObject(cachedClassProfileMap);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.packageName = (String)in.readObject();
        this.classProfileMap = (Map<String, SimpleClassProfile>)in.readObject();
    }
    
    public String getapkpkgsig(){
    	StringBuilder pkgsigString = new StringBuilder();
    	 for (SimpleClassProfile simpleClassProfile : classProfileMap.values()) {
    		 pkgsigString.append(simpleClassProfile.getBasicHashStrict());
         }
		return pkgsigString.toString();    	
    }
    
    public double getPackageWeight() {
        if (classWeights == null)
            constructClassWeights();
        //packageWeight=includeClassNum+packageWeight;
        return packageWeight+includeClassNum;
    }

    public double getClassWeight(String className) {
        if (classWeights == null)
            constructClassWeights();
        return classWeights.get(className);
    }
    
    
   // private Map<String, Double> classWeights = null;
    private double packageWeight = 0;
    private synchronized void constructClassWeights() {
        Map<String, Double> classRanks = new HashMap<>();
        for (String className : classBBWeightMap.keySet()) {
            double bbWeight = classBBWeightMap.get(className);
            double depWeight = classDepWeightMap.get(className);

            depWeight +=1;
            //classRanks.put(className, bbWeight);
            classRanks.put(className, bbWeight+depWeight);
        }

        for (double weight : classRanks.values())
            packageWeight += weight;

        classWeights = new HashMap<>();
        for (String className : classRanks.keySet())
            classWeights.put(className, classRanks.get(className)/packageWeight);
    }
    

    public List<String> getWeightClassList() {
        if (classWeights == null)
            constructClassWeights();

        List<String> classNames = new ArrayList<>(classWeights.keySet());
        Collections.sort(classNames, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                if (classWeights.get(o1).equals(classWeights.get(o2)))
                    return 0;
                return (classWeights.get(o1) < classWeights.get(o2)) ? 1 : -1;
            }
        });

        return classNames;
    }
    
}
