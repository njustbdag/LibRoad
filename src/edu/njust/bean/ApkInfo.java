package edu.njust.bean;

/**
 * ApkInfo entity. @author MyEclipse Persistence Tools
 */

public class ApkInfo implements java.io.Serializable {

	// Fields

	private Integer id;
	private Integer apkId;
	private String apkName;

	// Constructors

	/** default constructor */
	public ApkInfo() {
	}

	/** full constructor */
	public ApkInfo(Integer apkId, String apkName) {
		this.apkId = apkId;
		this.apkName = apkName;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
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

}