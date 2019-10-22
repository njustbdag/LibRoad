package cn.fudan.libpecker.analysis;

import cn.fudan.common.util.PackageNameUtil;
import cn.fudan.libpecker.model.LibProfile;
import cn.njust.analysis.name.NameAnalysis;
import cn.njust.analysis.name.NameCollector;
import cn.njust.analysis.tree.PackageNode;
import cn.njust.common.CodeContainer;
import cn.njust.common.Lib;
import cn.njust.common.Sdk;
import groundtruth.FindTxtContent;

import java.awt.print.Printable;
import java.util.*;
import java.util.Map.Entry;

/**
 * Created by yuanxzhang on 26/04/2017.
 */
public class RootPackageAnalysis {
	static Map<String,Integer> levelListtest=new HashMap<>();
    private static class PackageTreeNode {//内部类
        int level;//start from 1
        String nodeName;
        List<PackageTreeNode> subNodes;

        public int  getSubNodesNum(){
            return subNodes.size();
        }

        public static final PackageTreeNode CURRENT = new PackageTreeNode();

        public boolean belong(String packageName) {
            assert (packageName != null);

            if (packageName.equals(PackageNode.Factory.DEFAULT_PACKAGE)) {
                if (nodeName.equals(PackageNode.Factory.DEFAULT_PACKAGE))
                    return true;
                else
                    return false;
            }
            else {
                String rootName = topNodeName(packageName);
                return rootName.equals(nodeName);
            }
        }

        public static String topNodeName(String packageName) {
            if (! packageName.contains("."))
                return packageName;
            else
                return packageName.substring(0, packageName.indexOf('.'));
        }

        public static String nextLevelName(String packageName) {
            if (! packageName.contains("."))
                return null;
            else
                return packageName.substring(packageName.indexOf('.')+1);
        }


        public boolean insert(String packageName) { 
            if (! belong(packageName))
                return false;

            if (subNodes == null)
                subNodes = new ArrayList<>();

            String newPackageName = nextLevelName(packageName);//第一节已经变成rootnode处理完了，第二节开始的包名
            if (newPackageName == null) {
                subNodes.add(CURRENT);
                return true;
            }

            String newNodeName = topNodeName(newPackageName);//第二节的包名
            for (PackageTreeNode node : subNodes)  {
                if (node == CURRENT)
                    continue;
                if (node.nodeName.equals(newNodeName))
                    return node.insert(newPackageName);
            }

            PackageTreeNode newSubNode = new PackageTreeNode();
            newSubNode.level = level + 1;
            newSubNode.nodeName = newNodeName;
            subNodes.add(newSubNode);
            return newSubNode.insert(newPackageName);
        }

		public String Findname() {
            String currentRootPackageName =nodeName;
            for (int i = 0; i < subNodes.size(); i++) {
			  while (subNodes != null) {
            	PackageTreeNode newSubNode = subNodes.get(i);
                currentRootPackageName += ".";
                currentRootPackageName += newSubNode.nodeName;
                return currentRootPackageName;
            }	
			}

			return currentRootPackageName;	
		}
    }
    
    public static void main(String[] args) {
    	Sdk sdk = Sdk.loadDefaultSdk();
    	Set<String> targetSdkClassNameSet = sdk.getTargetSdkClassNameSet();
    	String libPath="D:\\所有的dex集合\\glide-3.8.0.dex";
    	Lib lib = Lib.loadFromFile(libPath);
    	LibProfile libProfile = LibProfile.create(lib, targetSdkClassNameSet); 
    	Set<String> subpkg=libProfile.packageProfileMap.keySet();
    	TreeSet<String> subpkg1=new TreeSet<>();
    	subpkg1.addAll(subpkg);
    	PackageTreeGenerator(subpkg1);
	}
    /**
     * 深度优先搜索遍历包树
     * @param rootNode
     * @return
     */
    
    private static String iteratorTree(PackageTreeNode rootNode) {
    	StringBuilder bufferBuilder=new StringBuilder();
    	bufferBuilder.append("|");
    	int count=0;
    	if (rootNode.subNodes!=null) {
		    	for (PackageTreeNode subNode : rootNode.subNodes) {
		    		if (subNode.level!=0) {
		    			count++;
						bufferBuilder.append(subNode.level+"*"+count+"*"+subNode.nodeName);
					}
    		
			if (subNode.subNodes!=null&&subNode.subNodes.size()!=0) {
				bufferBuilder.append(iteratorTree(subNode));
			}
		}	
		}

		return bufferBuilder.toString();
		
	}
    
    
    public static void PackageTreeGenerator(Set<String> subpkg){
    	  List<PackageTreeNode> rootNodes = new ArrayList<>();//第一层，第一个.之前的那一节
          for (String packageName : subpkg) {
          	System.out.println("开始构建包的树："+packageName);
              if (packageName.equals(PackageNode.Factory.DEFAULT_PACKAGE)) {
                  PackageTreeNode newRootNode = new PackageTreeNode();
                  newRootNode.level = 1;
                  newRootNode.nodeName = PackageNode.Factory.DEFAULT_PACKAGE;
                  newRootNode.subNodes = new ArrayList<>();
                  newRootNode.subNodes.add(PackageTreeNode.CURRENT);
                  rootNodes.add(newRootNode);
                  continue;
              } 
              boolean handled = false;//判断是否需要添加newRootNode，false表示需要
              //System.out.println(rootNodes.size());
              for (PackageTreeNode node : rootNodes) {//不添加newRootNode，把packageName插入已有rootNodes中就行
                  if (node.belong(packageName)) {//查找packageName属于哪个已有rootNodes       	  
                	  //System.out.println("node.insert(packageName)1"+node.nodeName);
                  	node.insert(packageName);                 
                      handled = true;
                  }
              }

              if (! handled) {//需要添加newRootNode
                  PackageTreeNode newRootNode = new PackageTreeNode();
                  newRootNode.level = 1; 
                  newRootNode.nodeName = PackageTreeNode.topNodeName(packageName);//根节点
                  //System.out.println("node.insert(packageName)2");
                  newRootNode.insert(packageName);
                  rootNodes.add(newRootNode);
                 // System.out.println("handled = false;");
              } 
          }
          //System.out.println("rootNodes.size()"+rootNodes.size());
          PrintPackageTree(rootNodes);//输出包树结构
          for (PackageTreeNode packageTreeNode : rootNodes) {
        	  System.out.println("迭代第一个根节点："+packageTreeNode.nodeName);
        	  System.out.println(iteratorTree(packageTreeNode));
		}
    }   
    
    public static Map<String, List<String>> previousextractRootPackages(CodeContainer container) {

        NameCollector names = NameAnalysis.analyzeNames(container);
        Set<String> packageNames = names.allPackageNames();

        //treeilize all package names
        List<PackageTreeNode> rootNodes = new ArrayList<>();
        for (String packageName : packageNames) {
            if (packageName.equals(PackageNode.Factory.DEFAULT_PACKAGE)) {
                PackageTreeNode newRootNode = new PackageTreeNode();
                newRootNode.level = 1;
                newRootNode.nodeName = PackageNode.Factory.DEFAULT_PACKAGE;
                newRootNode.subNodes = new ArrayList<>();
                newRootNode.subNodes.add(PackageTreeNode.CURRENT);
                rootNodes.add(newRootNode);
                continue;
            }

            boolean handled = false;
            for (PackageTreeNode node : rootNodes) {
                if (node.belong(packageName)) {
                    node.insert(packageName);
                    handled = true;
                }
            }

            if (! handled) {
                PackageTreeNode newRootNode = new PackageTreeNode();
                newRootNode.level = 1;
                newRootNode.nodeName = PackageTreeNode.topNodeName(packageName);
                newRootNode.insert(packageName);
                rootNodes.add(newRootNode);
            }
        }

        //find root packages from package trees
        Set<String> rootPackageNames = new HashSet<>();
        for (PackageTreeNode rootNode : rootNodes) {
            if (rootNode.subNodes == null)
                rootPackageNames.add(rootNode.nodeName);
            else {
                if (rootNode.subNodes.contains(PackageTreeNode.CURRENT)) {
                    rootPackageNames.add(rootNode.nodeName);
                }
                else {
                    for (PackageTreeNode subNode : rootNode.subNodes) {
                        String currentRootPackageName = rootNode.nodeName+"."+subNode.nodeName;
                        while (subNode.subNodes != null && subNode.subNodes.size() == 1 && ! subNode.subNodes.contains(PackageTreeNode.CURRENT)) {
                            subNode = subNode.subNodes.get(0);
                            currentRootPackageName += ".";
                            currentRootPackageName += subNode.nodeName;
                        }
                        rootPackageNames.add(currentRootPackageName);
                    }
                }
            }
        }
        
       /** //find all obfuscated packages from package trees树的遍历
        Set<String> allPackageNames = new HashSet<>();
        for (PackageTreeNode rootNode : rootNodes) {
            if (rootNode.subNodes == null)
                rootPackageNames.add(rootNode.nodeName);
            else {
                if (rootNode.subNodes.contains(PackageTreeNode.CURRENT)) {
                    rootPackageNames.add(rootNode.nodeName);
                }
                else {
                    for (PackageTreeNode subNode : rootNode.subNodes) {
                           allPackageNames.add(rootNode.nodeName+"."+subNode.Findname());
                    }
                }
            }
        }**/

        //group package names into root packages
        Map<String, List<String>> rootPackageNameMap = new HashMap<>();
        for (String rootPackageName : rootPackageNames)
            rootPackageNameMap.put(rootPackageName, new ArrayList<String>());
        for (String packageName : packageNames) {
            boolean categorized = false;
            for (String rootPackageName : rootPackageNames) {
                if (rootPackageName.equals(packageName)
                        || PackageNameUtil.isParentPackageName(rootPackageName, packageName) > 0) {
                    rootPackageNameMap.get(rootPackageName).add(packageName);
                    categorized = true;
                    break;
                }
            }

            if (! categorized) {
                throw new RuntimeException("can not happen");
            }
        }
      /** Iterator<Entry<String, List<String>>> ita= rootPackageNameMap.entrySet().iterator();
       while (ita.hasNext()) {
		Map.Entry<java.lang.String, java.util.List<java.lang.String>> entry = (Map.Entry<java.lang.String, java.util.List<java.lang.String>>) ita
				.next();
		System.out.println(entry.getKey());
		System.out.println(entry.getValue());
		
	}**/
        return rootPackageNameMap;
    }
    

	//return value锛歳oot package name -> [sub package name in container including the root package name itself if it presents in container]
    public static Map<String, List<String>> extractRootPackages(CodeContainer container) {

        NameCollector names = NameAnalysis.analyzeNames(container);
        Set<String> packageNames = names.allPackageNames();
       // System.out.println(packageNames.size());
        //treeilize all package names
        List<PackageTreeNode> rootNodes = new ArrayList<>();
        for (String packageName : packageNames) {
        	//System.out.println("判断是否为根包"+packageName);
            if (packageName.equals(PackageNode.Factory.DEFAULT_PACKAGE)) {
                PackageTreeNode newRootNode = new PackageTreeNode();
                newRootNode.level = 1;
                newRootNode.nodeName = PackageNode.Factory.DEFAULT_PACKAGE;
                newRootNode.subNodes = new ArrayList<>();
                newRootNode.subNodes.add(PackageTreeNode.CURRENT);
                rootNodes.add(newRootNode);
                continue;
            }
            boolean handled = false;
            for (PackageTreeNode node : rootNodes) {
                if (node.belong(packageName)) {
                	node.insert(packageName);                 
                    handled = true;
                    //System.out.println("handled = true;");
                }
            }

            if (! handled) {
                PackageTreeNode newRootNode = new PackageTreeNode();
                newRootNode.level = 1;
                newRootNode.nodeName = PackageTreeNode.topNodeName(packageName);
                newRootNode.insert(packageName);
                rootNodes.add(newRootNode);
               // System.out.println("handled = false;");
            }
        }
        //System.out.println("rootNodes.size()"+rootNodes.size());
        //PrintPackageTree(rootNodes);//输出包树结构
        //find root packages from package trees
        Set<String> rootPackageNames = new HashSet<>();
        for (PackageTreeNode rootNode : rootNodes) {//遍历第一层
        	//System.out.println(rootNode.nodeName);
            if (rootNode.subNodes == null){//没有第二层
            rootPackageNames.add(rootNode.nodeName);
           // System.out.println("通过第1种情况成为rootPackageNames："+rootNode.nodeName);
            }else {
                if (rootNode.subNodes.contains(PackageTreeNode.CURRENT)) {//第二层
                    rootPackageNames.add(rootNode.nodeName);
                   // System.out.println("通过第2种情况成为rootPackageNames："+rootNode.nodeName);
                    //System.out.println("rootNode.subNodes.contains(PackageTreeNode.CURRENT)："+PackageTreeNode.CURRENT.nodeName);
                }
                else {
                    for (PackageTreeNode subNode : rootNode.subNodes) {//遍历第二层
                    	//System.out.println("第3种情况：");
                        String currentRootPackageName = rootNode.nodeName+"."+subNode.nodeName;
                        if (currentRootPackageName.equals("android.support")||currentRootPackageName.equals("com.facebook")||currentRootPackageName.equals("com.tencent")
                        		||currentRootPackageName.equals("com.alibaba")||currentRootPackageName.equals("com.taobao")||currentRootPackageName.equals("com.huawei")||currentRootPackageName.equals("com.google")) {
                        		for (PackageTreeNode subsubNode : subNode.subNodes) {
                        			//System.out.println("sjdfhu");
                        			 String currentRootPackageName0 =currentRootPackageName +"."+subsubNode.nodeName;
                        			//System.out.println("currentRootPackageName:"+currentRootPackageName0);
                        			//System.out.println("subsubNode.nodeName:"+subsubNode.nodeName);
		                        rootPackageNames.add(currentRootPackageName0);
                        		}
                        	}
						else {
	                        //System.out.println("currentRootPackageName:"+currentRootPackageName);
		                       // System.out.println("subNode.nodeName:"+subNode.nodeName);
		                       // System.out.println("subNode.subNodes.size():"+subNode.subNodes.size());
		                        while (subNode.subNodes != null && subNode.subNodes.size() == 1 && ! subNode.subNodes.contains(PackageTreeNode.CURRENT)) {
		                            subNode = subNode.subNodes.get(0);
		                            currentRootPackageName += ".";
		                            currentRootPackageName += subNode.nodeName;
		                            //System.out.println("currentRootPackageName:"+currentRootPackageName);
		                            //System.out.println("subNode.nodeName:"+subNode.nodeName);
		                            //System.out.println("subNode.subNodes.size():"+subNode.subNodes.size());
		                        }
		                        rootPackageNames.add(currentRootPackageName);
		                        //System.out.println("通过第3种情况成为rootPackageNames："+currentRootPackageName);	
						}
                    }
                }
            }
        }

        //group package names into root packages
        Map<String, List<String>> rootPackageNameMap = new HashMap<>();
        for (String rootPackageName : rootPackageNames)
        	//if (!rootPackageName.equals(".")) {
				rootPackageNameMap.put(rootPackageName, new ArrayList<String>());       
        for (String packageName : packageNames) {
            boolean categorized = false;
            for (String rootPackageName : rootPackageNames) {
            	if (!rootPackageName.equals(".")) {
					 if (rootPackageName.equals(packageName)
                        ||packageName.contains(rootPackageName) ) {//PackageNameUtil.isParentPackageName(rootPackageName, packageName) > 0
                    rootPackageNameMap.get(rootPackageName).add(packageName);
                    categorized = true;
                    break;
                }
				}

            }

            /**if (! categorized) {
                throw new RuntimeException("can not happen");
            }**/
        }
        
        /**for (String rootPackageName : rootPackageNameMap.keySet()){
        	System.out.println("rootPackageName:"+rootPackageName);
        	System.out.println("subpkgNum:"+rootPackageNameMap.get(rootPackageName).size());
        	System.out.println("subpkgName:"+rootPackageNameMap.get(rootPackageName));
        	System.out.println();
        }**/
        return rootPackageNameMap;
    }

	private static void PrintPackageTree(List<PackageTreeNode> rootNodes) {
		for (PackageTreeNode node : rootNodes) {
	    	System.out.println(node.nodeName);
	    	//System.out.println(node.subNodes);
	    	System.out.println("下面是第"+node.level+"层节点");
	    	PrintSubPackageTree(node.subNodes,node.nodeName);
	    	levelListtest.put(node.nodeName,node.level);
	    	//System.out.println(levelListtest);
	    	System.out.println("*******根节点输出结束***********");
	    	settlelevel(levelListtest);
				}
	    	 }
		

	private static void settlelevel(Map<String,Integer> levelListtest) {
		System.out.println("*******整理节点***********");
		int maxlevel=1;
		Map<Integer, List<String>> levelList=new HashMap<>();
		Map<Integer, Map<String, List<String>>> finallevelList=new HashMap<>();
		for (String level : levelListtest.keySet()) {
			//System.out.println(level+":"+levelListtest.get(level));
			if (levelListtest.get(level)>maxlevel) {
				maxlevel=levelListtest.get(level);
			}
		}
		//System.out.println(maxlevel);
		for (int i = 1; i <=maxlevel; i++) {
			List<String> nodeNamelList=new ArrayList<>();
			for (String level : levelListtest.keySet()) {
				if (levelListtest.get(level)==i&!nodeNamelList.contains(level)){
					nodeNamelList.add(level);
				}
			}
			levelList.put(i, nodeNamelList);
		}
		
		for (int i = 1; i <= maxlevel; i++) {
			 Map<String, List<String>> alevelList=new HashMap<>();
			 List<String> parentlList=new ArrayList<>();
			//System.out.println("第"+i+"层");
			//System.out.println(levelList.get(i)); 
			for (String pkgname:levelList.get(i)) {
				if (pkgname.contains(".")) {
				String parentpkg=pkgname.substring(0, pkgname.indexOf("."));
				if (!parentlList.contains(parentpkg)) {
					parentlList.add(parentpkg);
				}		
				}

			}
			for (String parentname:parentlList){
				 List<String> sublList=new ArrayList<>();
			for (String pkgname:levelList.get(i)){
				if (pkgname.contains(parentname)) {
					sublList.add(pkgname);
				}
			}	
			alevelList.put(parentname, sublList);
			}
			finallevelList.put(i, alevelList);
		}
		for (int i = 1; i <= maxlevel; i++) {
			System.out.println("第"+i+"层");
			List<String>parentList=new ArrayList<>();
			parentList.addAll(finallevelList.get(i).keySet());
			Collections.sort(parentList);
			for (String Parent:parentList) {
				System.out.println("Parent:"+Parent);
				List<String>subList=finallevelList.get(i).get(Parent);
				Collections.sort(subList);
				System.out.println("Sub:"+subList);
				//for (String Sub: finallevelList.get(i).get(Parent)) {
				//	System.out.println("Sub:"+Sub);
				//}
			}
		}	
		System.out.println("*******整理节点完毕***********");
		System.out.println("*******转换节点开始***********");
		conversion(maxlevel,finallevelList);
		System.out.println("*******转换节点完毕***********");
	}


	private static Map<String, String> conversion(int maxlevel, Map<Integer, Map<String, List<String>>> finallevelList) {
		Map<String, String>conversionmapMap=new HashMap<String, String>();
		for (int i = 1; i <= maxlevel; i++) {
			System.out.println("第"+i+"层");
			List<String>parentList=new ArrayList<>();
			parentList.addAll(finallevelList.get(i).keySet());
			Collections.sort(parentList);
			int p=1;
			for (String Parent:parentList) {
				System.out.println("Parent:"+Parent);
				if (i==2) {
				System.out.println("正在转换"+Parent+"--->"+p+"--->"+numberToLetter(p));
				conversionmapMap.put(i-1+Parent,numberToLetter(p));		
				}
				p++;
				List<String>subList=finallevelList.get(i).get(Parent);
				Collections.sort(subList);
				System.out.println("Sub:"+subList);
				int s=1;
				for (String substring : subList) {
					substring=substring.substring(substring.indexOf(".")+1);
					System.out.println("正在转换"+substring+"--->"+s+"--->"+numberToLetter(s));
					conversionmapMap.put(substring,numberToLetter(s));
					s++;
				}
			}
		}
		System.out.println("*******输出转换后节点***********");
		List<String>converList=new ArrayList<>();
		converList.addAll(conversionmapMap.keySet());
		Collections.sort(converList);
		for (String nodename:converList) {
			System.out.println(nodename+"---"+conversionmapMap.get(nodename));
		}
		System.out.println("*******转换后节点输出完毕***********");
		return conversionmapMap;	
		
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

	
	
	
	private static void PrintSubPackageTree(List<PackageTreeNode> subNodes,String parentnodeName) {
			for (PackageTreeNode subbnode :subNodes){
				if (subbnode.nodeName!=null) {
		         	System.out.println("层数"+subbnode.level+":"+subbnode.nodeName+"      上一层:"+parentnodeName);
		         	levelListtest.put(parentnodeName+"."+subbnode.level+subbnode.nodeName,subbnode.level);
		         	if (subbnode.subNodes.size()!=0) {
		               	if (subbnode.subNodes.get(0)!=null) {
							PrintSubPackageTree(subbnode.subNodes,subbnode.nodeName);
						}
		               	
			}
				}
		     	else {
					System.out.println("");
				}
		}
}
}
