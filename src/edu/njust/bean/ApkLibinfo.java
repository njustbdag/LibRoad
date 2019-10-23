package edu.njust.bean;

/**
 * ApkLibinfo entity. @author MyEclipse Persistence Tools
 */

public class ApkLibinfo implements java.io.Serializable {

	// Fields

	private Integer id;
	private Integer apkId;
	private String apkName;
	private Integer libId;
	private String libName;

	// Constructors

	/** default constructor */
	public ApkLibinfo() {
	}

	/** full constructor */
	public ApkLibinfo(Integer apkId, String apkName, Integer libId,
			String libName) {
		this.apkId = apkId;
		this.apkName = apkName;
		this.libId = libId;
		this.libName = libName;
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

}