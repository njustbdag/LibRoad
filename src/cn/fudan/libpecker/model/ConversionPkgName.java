package cn.fudan.libpecker.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.njust.analysis.tree.PackageNode;

public class ConversionPkgName {
	static List<String> packageNames;
	static Map<String,Integer> levelListtest=new HashMap<>();
	public ConversionPkgName(List<String> packageNames,Map<String,Integer> levelListtest){
		this.packageNames=packageNames;
		this.levelListtest=levelListtest;
	}
/**
 * 根据一系列包名生成包树
 */
	 public static List<String> PackageTreeGenerator(){
   	  List<PackageTreeNode> rootNodes = new ArrayList<>();
         for (String packageName :packageNames) {
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
         List<String> conversedList=PrintPackageTree(rootNodes);//输出包树结构
		return conversedList;
   }   
	    private static class PackageTreeNode {
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

	            String newPackageName = nextLevelName(packageName);
	            if (newPackageName == null) {
	                subNodes.add(CURRENT);
	                return true;
	            }

	            String newNodeName = topNodeName(newPackageName);
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
	    }
	    

		private static List<String> PrintPackageTree(List<PackageTreeNode> rootNodes) {
			for (PackageTreeNode node : rootNodes) {
		    	//System.out.println(node.nodeName);
		    	//System.out.println(node.subNodes);
		    	//System.out.println("下面是第"+node.level+"层节点");
		    	PrintSubPackageTree(node.subNodes,node.nodeName);
		    	levelListtest.put(node.nodeName,node.level);
		    	//System.out.println(levelListtest);
		    	//System.out.println("*******根节点输出结束***********");
					}
			List<String> conversedList=settlelevel(levelListtest);
			return conversedList;
		    	 }
			

		private static List<String> settlelevel(Map<String,Integer> levelListtest) {
			//System.out.println("*******整理节点***********");
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
						if (pkgname.substring(0, pkgname.indexOf(".")).equals(parentname)) {
							sublList.add(pkgname);
						}										
				}	
				alevelList.put(parentname, sublList);
				}
				finallevelList.put(i, alevelList);
			}
			for (int i = 1; i <= maxlevel; i++) {
				//System.out.println("第"+i+"层");
				List<String>parentList=new ArrayList<>();
				parentList.addAll(finallevelList.get(i).keySet());
				Collections.sort(parentList);
				for (String Parent:parentList) {
					//System.out.println("Parent:"+Parent);
					List<String>subList=finallevelList.get(i).get(Parent);
					Collections.sort(subList);
					//System.out.println("Sub:"+subList);
					//for (String Sub: finallevelList.get(i).get(Parent)) {
					//	System.out.println("Sub:"+Sub);
					//}
				}
			}	
			//System.out.println("*******整理节点完毕***********");
			//System.out.println("*******转换节点开始***********");
			Map<String, String>conversionmapMap=conversion(maxlevel,finallevelList);
			//System.out.println("*******转换节点完毕***********");
			//System.out.println("*******转换包名开始***********");
			List<String> conversedList=conversepkg(conversionmapMap);
			//System.out.println("*******转换包名完毕***********");
			return conversedList;
		}


		private static List<String> conversepkg(Map<String, String> conversionmapMap) {
			List<String> conversedList=new ArrayList<>();
			List<String> finalconversedList=new ArrayList<>();
			Collections.sort(packageNames);
			for (String pkg:packageNames) {
				int i=1;
				//System.out.print(pkg+"------->");
				//System.out.print(pkg+"------->");
				String conversedpkgString ="";
				String[] StrArray = pkg.split("\\.");
				for (String j:StrArray) {
					//System.out.println(j);
					conversedpkgString=conversedpkgString+i+j+".";
					i++;
				}
				conversedpkgString=conversedpkgString.substring(0, conversedpkgString.lastIndexOf("."));
				String pkg1=conversedpkgString;
				String[] StrArray1 = pkg1.split("\\.");
				String conversedpkgString0 ="";
				for (String d:StrArray1){
					conversedpkgString0=conversedpkgString0+d+"."+d+"*";
				}
				conversedpkgString0=conversedpkgString0.substring(0,conversedpkgString0.lastIndexOf("*"));
				conversedpkgString0=conversedpkgString0.substring(0,conversedpkgString0.lastIndexOf("."));
				//System.out.println(conversedpkgString0);
				String[] StrArray2 = conversedpkgString0.split("\\.");
				String conversedpkgString1 ="";
				for (String j:StrArray2) {
					j=j.substring(1);
					j=j.replace("*",".");
					//System.out.println(j);
					conversedpkgString1=conversedpkgString1+conversionmapMap.get(j)+".";
				}
				 finalconversedList.add(conversedpkgString1.substring(0, conversedpkgString1.lastIndexOf(".")));
				//System.out.println(conversedpkgString1.substring(0, conversedpkgString1.lastIndexOf(".")));
				conversedList.add(conversedpkgString1.substring(0, conversedpkgString1.lastIndexOf(".")));
			}
			for (String astring : conversedList) {
				//System.out.println(astring);
			}
			return conversedList; 
		}
		private static Map<String, String> conversion(int maxlevel, Map<Integer, Map<String, List<String>>> finallevelList) {
			Map<String, String>conversionmapMap=new HashMap<String, String>();
			for (int i = 1; i <= maxlevel; i++) {
				//System.out.println("第"+i+"层");
				List<String>parentList=new ArrayList<>();
				parentList.addAll(finallevelList.get(i).keySet());
				Collections.sort(parentList);
				int p=1;
				for (String Parent:parentList) {
					//System.out.println("Parent:"+Parent);
					if (i==2) {
					//System.out.println("正在转换"+Parent+"--->"+p+"--->"+numberToLetter(p));
					conversionmapMap.put(Parent,numberToLetter(p));		
					}
					p++;
					List<String>subList=finallevelList.get(i).get(Parent);
					Collections.sort(subList);
					//System.out.println("Sub:"+subList);
					int s=1;
					for (String substring : subList) {
						//substring=substring.substring(substring.indexOf(".")+1);
						//System.out.println("正在转换"+substring+"--->"+s+"--->"+numberToLetter(s));
						conversionmapMap.put(substring,numberToLetter(s));
						s++;
					}
				}
			}
			//System.out.println("*******输出转换后节点***********");
			List<String>converList=new ArrayList<>();
			converList.addAll(conversionmapMap.keySet());
			Collections.sort(converList);
			for (String nodename:converList) {
				//System.out.println(nodename+"---"+conversionmapMap.get(nodename));
			}
			//System.out.println("*******转换后节点输出完毕***********");
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
			         	//System.out.println("层数"+subbnode.level+":"+subbnode.nodeName+"      上一层:"+parentnodeName);
			         	levelListtest.put(parentnodeName+"."+subbnode.level+subbnode.nodeName,subbnode.level);
			         	if (subbnode.subNodes.size()!=0) {
			               	if (subbnode.subNodes.get(0)!=null) {
								PrintSubPackageTree(subbnode.subNodes,subbnode.nodeName);
							}
			               	
				}
					}
			     	else {
						//System.out.println("");
					}
			}
	}

	
	
}
