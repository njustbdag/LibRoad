package edu.njust.bean;

/**
 * LibRootpackageInfo entity. @author MyEclipse Persistence Tools
 */

public class LibRootpackageInfo implements java.io.Serializable {

	// Fields

	private Integer id;
	private String libRootpackagename;
	private Integer libId;
	private String libName;
	private String libType;
	private String packagestructureHash;
	private Integer subpckNum;
	private Integer directorynum;
	private String pckStructure;

	// Constructors

	/** default constructor */
	public LibRootpackageInfo() {
	}

	/** full constructor */
	public LibRootpackageInfo(String libRootpackagename, Integer libId,
			String libName, String libType, String packagestructureHash,
			Integer subpckNum, Integer directorynum, String pckStructure) {
		this.libRootpackagename = libRootpackagename;
		this.libId = libId;
		this.libName = libName;
		this.libType = libType;
		this.packagestructureHash = packagestructureHash;
		this.subpckNum = subpckNum;
		this.directorynum = directorynum;
		this.pckStructure = pckStructure;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLibRootpackagename() {
		return this.libRootpackagename;
	}

	public void setLibRootpackagename(String libRootpackagename) {
		this.libRootpackagename = libRootpackagename;
	}

	public Integer getLibId() {
		return this.libId;
	}

	public void setLibId(Integer libId) {
		this.libId = libId;
	}

	public String getLibName() {
		return this.libName;
	}

	public void setLibName(String libName) {
		this.libName = libName;
	}

	public String getLibType() {
		return this.libType;
	}

	public void setLibType(String libType) {
		this.libType = libType;
	}

	public String getPackagestructureHash() {
		return this.packagestructureHash;
	}

	public void setPackagestructureHash(String packagestructureHash) {
		this.packagestructureHash = packagestructureHash;
	}

	public Integer getSubpckNum() {
		return this.subpckNum;
	}

	public void setSubpckNum(Integer subpckNum) {
		this.subpckNum = subpckNum;
	}

	public Integer getDirectorynum() {
		return this.directorynum;
	}

	public void setDirectorynum(Integer directorynum) {
		this.directorynum = directorynum;
	}

	public String getPckStructure() {
		return this.pckStructure;
	}

	public void setPckStructure(String pckStructure) {
		this.pckStructure = pckStructure;
	}

}