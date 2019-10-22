package cn.njust.analysis.dep;

import cn.njust.analysis.profile.*;
import cn.njust.common.CodeContainer;
import cn.njust.common.Sdk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by lemonleaves on 2017/4/14.
 */
public class DepAnalysis {
    private CodeContainer codeContainer;
    public Set<ClassProfile> allClassProfiles;
    private Set<DepNode> depGraph; // all nodes including system nodes
    private Set<String> systemClassSet;
    public HashMap<String, ConcreteDepNode> ownNodeMap;
    public HashMap<String, DepNode> systemNodeMap;
    public HashMap<String, DepNode> lostOtherPackageNodeMap;
    public HashMap<String,DepNode> allNodeMap;

    public DepAnalysis(CodeContainer codeContainer, Set<String> systemClassSet) {
        this.codeContainer = codeContainer;
        this.systemClassSet = systemClassSet;
        this.allClassProfiles = ProfileGenerator.generate(codeContainer, systemClassSet);
        this.depGraph = new HashSet<>();
        this.ownNodeMap = new HashMap<>();
        for (ClassProfile cp : this.allClassProfiles) {
            ConcreteDepNode node = new ConcreteDepNode(cp);
            this.depGraph.add(node);
            this.ownNodeMap.put(cp.getClassName(), node);
        }
        this.systemNodeMap = new HashMap<>();
        this.lostOtherPackageNodeMap = new HashMap<>();
        this.allNodeMap = new HashMap<>();
        this.beginDepAnalysis();
        this.depGraph.addAll(this.systemNodeMap.values());
        this.allNodeMap.putAll(ownNodeMap);
        this.allNodeMap.putAll(systemNodeMap);
        this.allNodeMap.putAll(lostOtherPackageNodeMap);
    }

    public DepAnalysis(CodeContainer codeContainer) {// default android version: 20
        this.codeContainer = codeContainer;
        this.systemClassSet = new HashSet<>();
        this.systemClassSet.addAll(Sdk.loadDefaultSdk().getTargetSdkClassNameSet());
        this.allClassProfiles = ProfileGenerator.generate(codeContainer, systemClassSet);
        this.depGraph = new HashSet<>();
        this.ownNodeMap = new HashMap<>();
        for (ClassProfile cp : this.allClassProfiles) {
            ConcreteDepNode node = new ConcreteDepNode(cp);
            this.depGraph.add(node);
            this.ownNodeMap.put(cp.getClassName(), node);
        }
        this.lostOtherPackageNodeMap = new HashMap<>();
        this.systemNodeMap = new HashMap<>();
        this.beginDepAnalysis();
        this.depGraph.addAll(this.systemNodeMap.values());
    }

    public Set<String> getSystemClassSet() {
        return systemClassSet;
    }

    public ClassProfile getClassProfile(String className) {
        if (ownNodeMap.containsKey(className))
            return ownNodeMap.get(className).classProfile;
        else
            return null;
    }

    public  void printdepGraph(Map<String, Set<String>> classINsubPKG,Map<String, String> classTOsubPKG, Map<String, List<String>> rootPackageMap) {
    	TreeMap<String, Set<String>> ARPdep=new TreeMap<>();
    	Map<String, Integer> ARPdepWeight=new HashMap<>(); 
    	Map<String, String> subARPtoARP=new HashMap<>(); 
    	for (String rString:rootPackageMap.keySet()) {
			for (String sString:rootPackageMap.get(rString)) {
				subARPtoARP.put(sString, rString);
			}
		}
    	for (String subPKGname:classINsubPKG.keySet()) {
    		System.out.println("子包出厂："+subPKGname);
    		int depWeight=0;
    		ARPdep.put(subPKGname,new HashSet<String>());
    		Set<String> classinsubPKGname=classINsubPKG.get(subPKGname);
        	for (DepNode depNode : depGraph) {
        		if (classinsubPKGname.contains(depNode.classname)) {
        			//System.out.println(depNode.classname);
        			for (DepEdge depEdge : depNode.edges) {
        				//System.out.println("------->");
        				try {
        					if (classTOsubPKG.get(depEdge.getSink().classname)!=null) {
                				System.out.println(depEdge.getWeight());
                				System.out.println(depEdge.getEdgeType());
            					System.out.println(subPKGname+"--"+depNode.classname+"------->"+depEdge.getSink().classname+"--"+classTOsubPKG.get(depEdge.getSink().classname));	
							if (classTOsubPKG.get(depEdge.getSink().classname)!=subPKGname) {
								System.out.println("有情况！");
								if (!ARPdepWeight.keySet().contains(subPKGname+classTOsubPKG.get(depEdge.getSink().classname))) {
									ARPdepWeight.put(subPKGname+classTOsubPKG.get(depEdge.getSink().classname), 0);
								}
								int temp=ARPdepWeight.get(subPKGname+classTOsubPKG.get(depEdge.getSink().classname))+depEdge.getWeight();
								ARPdepWeight.put(subPKGname+classTOsubPKG.get(depEdge.getSink().classname), temp);
								ARPdep.get(subPKGname).add(classTOsubPKG.get(depEdge.getSink().classname));
							}
        					}
        				} catch (Exception e) {
        					System.out.println("NullPointerException");
        				}
        					
        			}	
    			}
    		}
		}
    	for (String rootPKG : rootPackageMap.keySet()) {
    		System.out.println("根包:"+rootPKG);
    		Map<String, Integer> aARPdepWeight=new HashMap<>(); 
    	for (String subPKGname:rootPackageMap.get(rootPKG)) { 
    		//System.out.println("子包依赖输出："+subPKGname);
			for (String depPKG : ARPdep.get(subPKGname)) {
				if (subARPtoARP.get(subPKGname)!=subARPtoARP.get(depPKG)) {
					if (!aARPdepWeight.containsKey(subARPtoARP.get(depPKG))) {
						aARPdepWeight.put(subARPtoARP.get(depPKG), 0);
					}
					int temp=aARPdepWeight.get(subARPtoARP.get(depPKG))+ARPdepWeight.get(subPKGname+depPKG);
					aARPdepWeight.put(subARPtoARP.get(depPKG), temp);
					//System.out.println(subPKGname+"------->"+depPKG+"-------"+ARPdepWeight.get(subPKGname+depPKG));
				}
			}
		}
    	for(String relatedARPSet:aARPdepWeight.keySet()){
        	System.out.println(relatedARPSet+"------->"+aARPdepWeight.get(relatedARPSet));
    	}
    }
    	/**Map<String, Set<String>> ARP=new HashMap<>();
    	for (String rootPKG : rootPackageMap.keySet()) {
    		System.out.println(rootPKG);
        	Set<String> RelatedPKG=new HashSet<>(); 
    		System.out.println(getALLRelatedPKG(RelatedPKG,rootPKG, ARPdep));
		}**/
	}
    
    
    public Set<String> getALLRelatedPKG(Set<String> RelatedPKG,String rootPKG,TreeMap<String, Set<String>> ARPdep) {
    	if (ARPdep.get(rootPKG)==null) {
			return null;
		}
			for (String depPKG : ARPdep.get(rootPKG)) {
				//System.out.println(depPKG);
				if (!RelatedPKG.contains(depPKG)) {
					RelatedPKG.add(depPKG);
					RelatedPKG.addAll(getALLRelatedPKG(RelatedPKG,depPKG,ARPdep));
				}
			}
		return RelatedPKG;
		
	}
    
    // Get weight it depends on, including system class
    public int getDependingweight(String classname) {
        int weight = 0;
        if (this.ownNodeMap.containsKey(classname)) {
            ConcreteDepNode node = this.ownNodeMap.get(classname);
            for (DepEdge de : node.edges) {
                weight+=de.getWeight();
                System.out.println(de.getSink().classname);
            }
            return weight;
        } else
            return 0;
    }

    // Get all ClassProfiles it depends on, except system class
    public HashSet<ClassProfile> getAllDependingClassProfile(String classname) {
        HashSet<ClassProfile> set = new HashSet<>();
        if (this.ownNodeMap.containsKey(classname)) {
            ConcreteDepNode node = this.ownNodeMap.get(classname);
            for (DepEdge de : node.edges) {
                if (de.getSink() instanceof ConcreteDepNode) {
                    set.add(((ConcreteDepNode) de.getSink()).classProfile);
                }
            }
            return set;
        } else
            return null;
    }

    // Get all class names it is depended on, including system class
    public Map<String, Integer> getAllDependedClassName(String pkgname, String subpkgname,String classname, List<String> subpkg) {
    	Map<String, Integer> set1 = new HashMap<>();
        HashSet<String> set = new HashSet<>();
        if (this.ownNodeMap.containsKey(classname)) {
            ConcreteDepNode node = this.ownNodeMap.get(classname);
            for (DepEdge de : node.sinkedges) {
            	set1.put(de.getSource().classname,de.printSinkDepEdge(pkgname,subpkgname,subpkg));//找到依赖的类名
                //set.add(de.getSource().classname);
              //de.printSinkDepEdge();               
            }
            return set1;
        } else
            return null;
    }
    // Get all class names it depends on, including system class
    public Map<String, Integer> getAllDependingClassName(String pkgname, String subpkgname,String classname, List<String> subpkg) {
    	Map<String, Integer> set1 = new HashMap<>();
        HashSet<String> set = new HashSet<>();
        if (this.ownNodeMap.containsKey(classname)) {
            ConcreteDepNode node = this.ownNodeMap.get(classname);
            for (DepEdge de : node.edges) {                
            	set1.put(de.getSink().classname,de.printDepEdge(pkgname,subpkgname,subpkg));
                //set.add(de.getSink().classname);
               //de.printDepEdge(); 
            }           	
            return set1;            
        } else
            return null;
    }
    
    public int getDependedweight(String classname) {
        int weight = 0;
        if (this.ownNodeMap.containsKey(classname)) {
            ConcreteDepNode node = this.ownNodeMap.get(classname);
            for (DepEdge de : node.edges) {
                weight+=de.getWeight();
                System.out.println(de.getSource().classname);
                System.out.println(de.getSink().classname);
            }
            return weight;
        } else
            return 0;
    }

    // Get all ClassProfiles it is depended on, except system class
    public HashSet<ClassProfile> getAllDependedClassProfile(String classname) {
        HashSet<ClassProfile> set = new HashSet<>();
        if (this.ownNodeMap.containsKey(classname)) {
            ConcreteDepNode node = this.ownNodeMap.get(classname);
            for (DepEdge de : node.sinkedges) {
                if (de.getSource() instanceof ConcreteDepNode) {
                    set.add(((ConcreteDepNode) de.getSource()).classProfile);
                }
            }
            return set;
        } else
            return null;
    }

    private void beginDepAnalysis() {
        for (ConcreteDepNode dn : this.ownNodeMap.values()) {
            // EXTENDS
            if (dn.classProfile.superClassProfile.type == ClassNameProfile.ClassType.SYS_LIB_CLASS) {
                DepNode sysdn;
                if (this.systemNodeMap.containsKey(dn.classProfile.superClassProfile.name)) {
                    sysdn = this.systemNodeMap.get(dn.classProfile.superClassProfile.name);
                } else {
                    sysdn = new DepNode(dn.classProfile.superClassProfile.name);
                    this.systemNodeMap.put(dn.classProfile.superClassProfile.name, sysdn);
                }
                DepEdge de = new DepEdge(DepEdge.DepEdgeType.DEP_EDGE_EXTENDS, dn,
                        sysdn);
                dn.addDepEdge(de);
                dn.addTypecount(0, 0);
                de.setClassType(ClassNameProfile.ClassType.SYS_LIB_CLASS);
                sysdn.addSinkEdge(de);
            } else if (dn.classProfile.superClassProfile.type == ClassNameProfile.ClassType.SAME_PKG_CLASS) {
                DepEdge de = new DepEdge(DepEdge.DepEdgeType.DEP_EDGE_EXTENDS, dn,
                        this.ownNodeMap.get(dn.classProfile.superClassProfile.name));
                dn.addDepEdge(de);
                dn.addTypecount(0, 1);
                de.setClassType(ClassNameProfile.ClassType.SAME_PKG_CLASS);
                if (this.ownNodeMap.containsKey(dn.classProfile.superClassProfile.name))
                    this.ownNodeMap.get(dn.classProfile.superClassProfile.name).addSinkEdge(de);
            } else if (dn.classProfile.superClassProfile.type == ClassNameProfile.ClassType.OTHER_PKG_CLASS) {
                DepEdge de;
                if (this.ownNodeMap.containsKey(dn.classProfile.superClassProfile.name)) {
                    de = new DepEdge(DepEdge.DepEdgeType.DEP_EDGE_EXTENDS, dn,
                            this.ownNodeMap.get(dn.classProfile.superClassProfile.name));
                    this.ownNodeMap.get(dn.classProfile.superClassProfile.name).addSinkEdge(de);
                } else {
                    DepNode node;
                    if (this.lostOtherPackageNodeMap.containsKey(dn.classProfile.superClassProfile.name))
                        node = this.lostOtherPackageNodeMap.get(dn.classProfile.superClassProfile.name);
                    else {
                        node = new DepNode(dn.classProfile.superClassProfile.name);
                        this.lostOtherPackageNodeMap.put(dn.classProfile.superClassProfile.name, node);
                    }
                    de = new DepEdge(DepEdge.DepEdgeType.DEP_EDGE_EXTENDS, dn, node);
                    node.addSinkEdge(de);
                }
                dn.addDepEdge(de);
                dn.addTypecount(0, 2);
                de.setClassType(ClassNameProfile.ClassType.OTHER_PKG_CLASS);
            }

            // IMPLEMENTS
            for (ClassNameProfile cnp : dn.classProfile.interfaceProfileSet) {
                if (cnp.type == ClassNameProfile.ClassType.SYS_LIB_CLASS) {
                    DepNode sysdn;
                    if (this.systemNodeMap.containsKey(cnp.name)) {
                        sysdn = this.systemNodeMap.get(cnp.name);
                    } else {
                        sysdn = new DepNode(cnp.name);
                        this.systemNodeMap.put(cnp.name, sysdn);
                    }
                    DepEdge de = new DepEdge(DepEdge.DepEdgeType.DEP_EDGE_IMPLEMENTS, dn,
                            sysdn);
                    dn.addDepEdge(de);
                    dn.addTypecount(1, 0);
                    de.setClassType(ClassNameProfile.ClassType.SYS_LIB_CLASS);
                    sysdn.addSinkEdge(de);
                } else if (cnp.type == ClassNameProfile.ClassType.SAME_PKG_CLASS) {
                    DepEdge de = new DepEdge(DepEdge.DepEdgeType.DEP_EDGE_IMPLEMENTS, dn,
                            this.ownNodeMap.get(cnp.name));
                    dn.addDepEdge(de);
                    dn.addTypecount(1, 1);
                    de.setClassType(ClassNameProfile.ClassType.SAME_PKG_CLASS);
                    if (this.ownNodeMap.containsKey(cnp.name))
                        this.ownNodeMap.get(cnp.name).addSinkEdge(de);
                } else if (cnp.type == ClassNameProfile.ClassType.OTHER_PKG_CLASS) {
                    DepEdge de;
                    if (this.ownNodeMap.containsKey(cnp.name)) {
                        de = new DepEdge(DepEdge.DepEdgeType.DEP_EDGE_IMPLEMENTS, dn,
                                this.ownNodeMap.get(cnp.name));
                        this.ownNodeMap.get(cnp.name).addSinkEdge(de);
                    } else {
                        DepNode node;
                        if (this.lostOtherPackageNodeMap.containsKey(cnp.name))
                            node = this.lostOtherPackageNodeMap.get(cnp.name);
                        else {
                            node = new DepNode(cnp.name);
                            this.lostOtherPackageNodeMap.put(cnp.name, node);
                        }
                        de = new DepEdge(DepEdge.DepEdgeType.DEP_EDGE_IMPLEMENTS, dn, node);
                        node.addSinkEdge(de);
                    }
                    dn.addDepEdge(de);
                    dn.addTypecount(1, 2);
                    de.setClassType(ClassNameProfile.ClassType.OTHER_PKG_CLASS);
                }
            }

            // FIELD_IN
            HashMap<String, DepEdge> existFieldMap = new HashMap<>();// point-to class -> DepEdge
            HashSet<FieldProfile> fieldProfileSet = new HashSet<>();
            fieldProfileSet.addAll(dn.classProfile.instanceFieldProfiles);
            fieldProfileSet.addAll(dn.classProfile.staticFieldProfiles);

            for (FieldProfile fp : fieldProfileSet) {
                if (existFieldMap.containsKey(fp.typeProfile.name)) {
                    existFieldMap.get(fp.typeProfile.name).addWeight();
                } else {
                    if (fp.typeProfile.type == ClassNameProfile.ClassType.SYS_LIB_CLASS) {
                        DepNode sysdn;
                        if (this.systemNodeMap.containsKey(fp.typeProfile.name)) {
                            sysdn = this.systemNodeMap.get(fp.typeProfile.name);
                        } else {
                            sysdn = new DepNode(fp.typeProfile.name);
                            this.systemNodeMap.put(fp.typeProfile.name, sysdn);
                        }
                        DepEdge de = new DepEdge(DepEdge.DepEdgeType.DEP_EDGE_FIELD_IN, dn,
                                sysdn);
                        dn.addDepEdge(de);
                        dn.addTypecount(2, 0);
                        de.setClassType(ClassNameProfile.ClassType.SYS_LIB_CLASS);
                        sysdn.addSinkEdge(de);
                        existFieldMap.put(fp.typeProfile.name, de);
                    } else if (fp.typeProfile.type == ClassNameProfile.ClassType.SAME_PKG_CLASS) {
                        DepEdge de = new DepEdge(DepEdge.DepEdgeType.DEP_EDGE_FIELD_IN, dn,
                                this.ownNodeMap.get(fp.typeProfile.name));
                        dn.addDepEdge(de);
                        dn.addTypecount(2, 1);
                        de.setClassType(ClassNameProfile.ClassType.SAME_PKG_CLASS);
                        if (this.ownNodeMap.containsKey(fp.typeProfile.name))
                            this.ownNodeMap.get(fp.typeProfile.name).addSinkEdge(de);
                        existFieldMap.put(fp.typeProfile.name, de);
                    } else if (fp.typeProfile.type == ClassNameProfile.ClassType.OTHER_PKG_CLASS) {
                        DepEdge de;
                        if (this.ownNodeMap.containsKey(fp.typeProfile.name)) {
                            de = new DepEdge(DepEdge.DepEdgeType.DEP_EDGE_FIELD_IN, dn,
                                    this.ownNodeMap.get(fp.typeProfile.name));
                            this.ownNodeMap.get(fp.typeProfile.name).addSinkEdge(de);
                        } else {
                            DepNode node;
                            if (this.lostOtherPackageNodeMap.containsKey(fp.typeProfile.name))
                                node = this.lostOtherPackageNodeMap.get(fp.typeProfile.name);
                            else {
                                node = new DepNode(fp.typeProfile.name);
                                this.lostOtherPackageNodeMap.put(fp.typeProfile.name, node);
                            }
                            de = new DepEdge(DepEdge.DepEdgeType.DEP_EDGE_FIELD_IN, dn, node);
                            node.addSinkEdge(de);
                        }
                        dn.addDepEdge(de);
                        dn.addTypecount(2, 2);
                        de.setClassType(ClassNameProfile.ClassType.OTHER_PKG_CLASS);
                        existFieldMap.put(fp.typeProfile.name, de);
                    }
                }
            }

            // Method level
            HashMap<String, DepEdge> existParameterMap = new HashMap<>();// point-to class -> DepEdge
            HashMap<String, DepEdge> existReturnMap = new HashMap<>();// point-to class -> DepEdge
            HashMap<String, DepEdge> existExceptionMap = new HashMap<>();// point-to class -> DepEdge

            HashSet<MethodProfile> allMethodProfile = new HashSet<>();
            allMethodProfile.addAll(dn.classProfile.staticMethodProfiles);
            allMethodProfile.addAll(dn.classProfile.instanceMethodProfiles);
            for (MethodProfile mp : allMethodProfile) {
                // METHOD_PARAMETER
                for (ClassNameProfile cnp : mp.parameterTypeProfileList) {
                    if (existParameterMap.containsKey(cnp.name)) {
                        existParameterMap.get(cnp.name).addWeight();
                    } else {
                        if (cnp.type == ClassNameProfile.ClassType.SYS_LIB_CLASS) {
                            DepNode sysdn;
                            if (this.systemNodeMap.containsKey(cnp.name)) {
                                sysdn = this.systemNodeMap.get(cnp.name);
                            } else {
                                sysdn = new DepNode(cnp.name);
                                this.systemNodeMap.put(cnp.name, sysdn);
                            }
                            DepEdge de = new DepEdge(DepEdge.DepEdgeType.DEP_EDGE_METHOD_PARAMETER, dn,
                                    sysdn);
                            dn.addDepEdge(de);
                            dn.addTypecount(3, 0);
                            de.setClassType(ClassNameProfile.ClassType.SYS_LIB_CLASS);
                            sysdn.addSinkEdge(de);
                            existParameterMap.put(cnp.name, de);
                        } else if (cnp.type == ClassNameProfile.ClassType.SAME_PKG_CLASS) {
                            DepEdge de = new DepEdge(DepEdge.DepEdgeType.DEP_EDGE_METHOD_PARAMETER, dn,
                                    this.ownNodeMap.get(cnp.name));
                            dn.addDepEdge(de);
                            dn.addTypecount(3, 1);
                            de.setClassType(ClassNameProfile.ClassType.SAME_PKG_CLASS);
                            if (this.ownNodeMap.containsKey(cnp.name))
                                this.ownNodeMap.get(cnp.name).addSinkEdge(de);
                            existParameterMap.put(cnp.name, de);
                        } else if (cnp.type == ClassNameProfile.ClassType.OTHER_PKG_CLASS) {
                            DepEdge de;
                            if (this.ownNodeMap.containsKey(cnp.name)) {
                                de = new DepEdge(DepEdge.DepEdgeType.DEP_EDGE_METHOD_PARAMETER, dn,
                                        this.ownNodeMap.get(cnp.name));
                                this.ownNodeMap.get(cnp.name).addSinkEdge(de);
                            } else {
                                DepNode node;
                                if (this.lostOtherPackageNodeMap.containsKey(cnp.name))
                                    node = this.lostOtherPackageNodeMap.get(cnp.name);
                                else {
                                    node = new DepNode(cnp.name);
                                    this.lostOtherPackageNodeMap.put(cnp.name, node);
                                }
                                de = new DepEdge(DepEdge.DepEdgeType.DEP_EDGE_METHOD_PARAMETER, dn, node);
                                node.addSinkEdge(de);
                            }

                            dn.addDepEdge(de);
                            dn.addTypecount(3, 2);
                            de.setClassType(ClassNameProfile.ClassType.OTHER_PKG_CLASS);
                            existParameterMap.put(cnp.name, de);
                        }
                    }
                }
                // METHOD_RETURN
                if (existReturnMap.containsKey(mp.returnTypeProfile.name)) {
                    existReturnMap.get(mp.returnTypeProfile.name).addWeight();
                } else {
                    if (mp.returnTypeProfile.type == ClassNameProfile.ClassType.SYS_LIB_CLASS) {
                        DepNode sysdn;
                        if (this.systemNodeMap.containsKey(mp.returnTypeProfile.name)) {
                            sysdn = this.systemNodeMap.get(mp.returnTypeProfile.name);
                        } else {
                            sysdn = new DepNode(mp.returnTypeProfile.name);
                            this.systemNodeMap.put(mp.returnTypeProfile.name, sysdn);
                        }
                        DepEdge de = new DepEdge(DepEdge.DepEdgeType.DEP_EDGE_METHOD_RETURN, dn,
                                sysdn);
                        dn.addDepEdge(de);
                        dn.addTypecount(4, 0);
                        de.setClassType(ClassNameProfile.ClassType.SYS_LIB_CLASS);
                        sysdn.addSinkEdge(de);
                        existReturnMap.put(mp.returnTypeProfile.name, de);
                    } else if (mp.returnTypeProfile.type == ClassNameProfile.ClassType.SAME_PKG_CLASS) {
                        DepEdge de = new DepEdge(DepEdge.DepEdgeType.DEP_EDGE_METHOD_RETURN, dn,
                                this.ownNodeMap.get(mp.returnTypeProfile.name));
                        dn.addDepEdge(de);
                        dn.addTypecount(4, 1);
                        de.setClassType(ClassNameProfile.ClassType.SAME_PKG_CLASS);
                        if (this.ownNodeMap.containsKey(mp.returnTypeProfile.name)) {
                            this.ownNodeMap.get(mp.returnTypeProfile.name).addSinkEdge(de);
                        }
                        existReturnMap.put(mp.returnTypeProfile.name, de);
                    } else if (mp.returnTypeProfile.type == ClassNameProfile.ClassType.OTHER_PKG_CLASS) {
                        DepEdge de;
                        if (this.ownNodeMap.containsKey(mp.returnTypeProfile.name)) {
                            de = new DepEdge(DepEdge.DepEdgeType.DEP_EDGE_METHOD_RETURN, dn,
                                    this.ownNodeMap.get(mp.returnTypeProfile.name));
                            this.ownNodeMap.get(mp.returnTypeProfile.name).addSinkEdge(de);
                        } else {
                            DepNode node;
                            if (this.lostOtherPackageNodeMap.containsKey(mp.returnTypeProfile.name))
                                node = this.lostOtherPackageNodeMap.get(mp.returnTypeProfile.name);
                            else {
                                node = new DepNode(mp.returnTypeProfile.name);
                                this.lostOtherPackageNodeMap.put(mp.returnTypeProfile.name, node);
                            }
                            de = new DepEdge(DepEdge.DepEdgeType.DEP_EDGE_METHOD_RETURN, dn, node);
                            node.addSinkEdge(de);
                        }
                        dn.addDepEdge(de);
                        dn.addTypecount(4, 2);
                        de.setClassType(ClassNameProfile.ClassType.OTHER_PKG_CLASS);
                        existReturnMap.put(mp.returnTypeProfile.name, de);
                    }
                }
                // METHOD_EXCEPTION
                if (mp.throwExceptionClassNameProfileSet != null) {
                    for (ClassNameProfile cnp : mp.throwExceptionClassNameProfileSet) {
                        if (existExceptionMap.containsKey(cnp.name)) {
                            existExceptionMap.get(cnp.name).addWeight();
                        } else {
                            if (cnp.type == ClassNameProfile.ClassType.SYS_LIB_CLASS) {
                                DepNode sysdn;
                                if (this.systemNodeMap.containsKey(cnp.name)) {
                                    sysdn = this.systemNodeMap.get(cnp.name);
                                } else {
                                    sysdn = new DepNode(cnp.name);
                                    this.systemNodeMap.put(cnp.name, sysdn);
                                }
                                DepEdge de = new DepEdge(DepEdge.DepEdgeType.DEP_EDGE_METHOD_EXCEPTION, dn,
                                        sysdn);
                                sysdn.addSinkEdge(de);
                                dn.addDepEdge(de);
                                dn.addTypecount(5, 0);
                                de.setClassType(ClassNameProfile.ClassType.SYS_LIB_CLASS);
                                existExceptionMap.put(cnp.name, de);
                            } else if (cnp.type == ClassNameProfile.ClassType.SAME_PKG_CLASS) {
                                DepEdge de = new DepEdge(DepEdge.DepEdgeType.DEP_EDGE_METHOD_EXCEPTION, dn,
                                        this.ownNodeMap.get(cnp.name));
                                if (this.ownNodeMap.containsKey(cnp.name))
                                    this.ownNodeMap.get(cnp.name).addSinkEdge(de);
                                dn.addDepEdge(de);
                                dn.addTypecount(5, 1);
                                de.setClassType(ClassNameProfile.ClassType.SAME_PKG_CLASS);
                                existExceptionMap.put(cnp.name, de);
                            } else if (cnp.type == ClassNameProfile.ClassType.OTHER_PKG_CLASS) {
                                DepEdge de;
                                if (this.ownNodeMap.containsKey(cnp.name)) {
                                    de = new DepEdge(DepEdge.DepEdgeType.DEP_EDGE_METHOD_EXCEPTION, dn,
                                            this.ownNodeMap.get(cnp.name));
                                    this.ownNodeMap.get(cnp.name).addSinkEdge(de);
                                } else {
                                    DepNode node;
                                    if (this.lostOtherPackageNodeMap.containsKey(cnp.name))
                                        node = this.lostOtherPackageNodeMap.get(cnp.name);
                                    else {
                                        node = new DepNode(cnp.name);
                                        this.lostOtherPackageNodeMap.put(cnp.name, node);
                                    }
                                    de = new DepEdge(DepEdge.DepEdgeType.DEP_EDGE_METHOD_EXCEPTION, dn, node);
                                    node.addSinkEdge(de);
                                }
                                dn.addDepEdge(de);
                                dn.addTypecount(5, 2);
                                de.setClassType(ClassNameProfile.ClassType.OTHER_PKG_CLASS);
                                existExceptionMap.put(cnp.name, de);
                            }
                        }
                    }
                }
            }

        }
    }

}
