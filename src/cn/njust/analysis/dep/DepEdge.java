package cn.njust.analysis.dep;

import java.util.ArrayList;
import java.util.List;

import cn.njust.analysis.profile.ClassNameProfile;

/**
 * Created by lemonleaves on 2017/4/14.
 */
public class DepEdge {
    public enum DepEdgeType {
        DEP_EDGE_EXTENDS, DEP_EDGE_IMPLEMENTS, DEP_EDGE_FIELD_IN, DEP_EDGE_METHOD_PARAMETER, DEP_EDGE_METHOD_RETURN, DEP_EDGE_METHOD_EXCEPTION
    }

    private DepEdgeType edgeType;
    private DepNode source;
    private DepNode sink;
    private int weight;
    private ClassNameProfile.ClassType classType; // respect to source

    public DepEdge(DepEdgeType edgeType, DepNode source, DepNode sink) {
        this.setEdgeType(edgeType);
        this.setSource(source);
        this.setSink(sink);
        this.setWeight(1);
    }
    
    public boolean ismainpackage(String classname,String packagename){
    	if(classname.contains(packagename)){
    		return true;
    	}
		return false;
    	
    }
    public void printDepEdge(String pkgname, String subpkgname) {
        System.out.println(this.getEdgeType().toString() + ": ->"
                + this.getSink().classname + "\tweight: " + this.getWeight());
    }

    public void printSinkDepEdge(String pkgname, String subpkgname) {
        System.out.println(this.getEdgeType().toString() + ": <-"
                + this.getSource().classname + "\tweight: " + this.getWeight());
    }
    public int printDepEdge(String pkgname, String subpkgname, List<String> subpkg) {
        if(this.getSink().classname.contains(pkgname)){
        	if (!this.getSink().classname.contains(subpkgname)) {//依赖的包不包括该包本身
                //System.out.println(this.getSource().classname + " ->("+ this.getEdgeType().toString() + ") ->"
                       // + this.getSink().classname + "\tweight: " + this.getWeight());	
			}
       	//System.out.println(this.getWeight());
        	return  this.getWeight();
        }
		return 0;
        
    }

    public int printSinkDepEdge(String pkgname, String subpkgname, List<String> subpkg) {
        if(this.getSource().classname.contains(pkgname)){
        	if (!this.getSource().classname.contains(subpkgname)) {//依赖的包不包括该包本身
          //System.out.println(this.getSink().classname+ "<-("+this.getEdgeType().toString() + ") <-"
                    //+ this.getSource().classname + "\tweight: " + this.getWeight());
			}
        	//System.out.println(this.getWeight());
        	return  this.getWeight();//计算依赖权重
        }
		return 0;
        
    }

    public ClassNameProfile.ClassType getClassType() {
        return classType;
    }

    public void setClassType(ClassNameProfile.ClassType classType) {
        this.classType = classType;
    }

    public int getWeight() {//计算依赖权重
    	if(this.getEdgeType().toString().contentEquals("DEP_EDGE_EXTENDS")||this.getEdgeType().toString().contentEquals("DEP_EDGE_IMPLEMENTS")){
    		this.setWeight(10);
    	}
    	if(this.getEdgeType().toString().contains("METHOD")){
    		this.setWeight(2);
    	}
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void addWeight() {
        this.weight++;
    }

    public DepNode getSource() {
        return source;
    }

    public void setSource(DepNode source) {
        this.source = source;
    }

    public DepNode getSink() {
        return sink;
    }

    public void setSink(DepNode sink) {
        this.sink = sink;
    }

    public DepEdgeType getEdgeType() {
        return edgeType;
    }

    public void setEdgeType(DepEdgeType edgeType) {
        this.edgeType = edgeType;
    }
}
