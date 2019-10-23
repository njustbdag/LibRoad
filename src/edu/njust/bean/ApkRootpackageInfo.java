package edu.njust.bean;

/**
 * ApkRootpackageInfo entity. @author MyEclipse Persistence Tools
 */

public class ApkRootpackageInfo implements java.io.Serializable {

	// Fields

	private Integer id;
	private String apkRootpackagename;
	private Integer apkId;
	private String apkName;
	private String packagestructureHash;
	private Integer subpckNum;
	private Integer directorynum;
	private String pckStructure;

	// Constructors

	/** default constructor */
	public ApkRootpackageInfo() {
	}

	/** full constructor */
	public ApkRootpackageInfo(String apkRootpackagename, Integer apkId,
			String apkName, String packagestructureHash, Integer subpckNum,
			Integer directorynum, String pckStructure) {
		this.apkRootpackagename = apkRootpackagename;
		this.apkId = apkId;
		this.apkName = apkName;
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

	public String getApkRootpackagename() {
		return this.apkRootpackagename;
	}

	public void setApkRootpackagename(String apkRootpackagename) {
		this.apkRootpackagename = apkRootpackagename;
	}

	public Integer getApkId() {
		return this.apkId;
	}

	public void setApkId(Integer apkId) {
		this.apkId = apkId;
	}

	public String getApkName() {
		return this.apkName;
	}

	public void setApkName(String apkName) {
		this.apkName = apkName;
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