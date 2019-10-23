package edu.njust.bean;

/**
 * ApkLibInfos entity. @author MyEclipse Persistence Tools
 */

public class ApkLibInfos implements java.io.Serializable {

	// Fields

	private Integer id;
	private Integer apkid;
	private String apkname;
	private Integer libid;
	private String libname;
	private Integer attribute;

	// Constructors

	/** default constructor */
	public ApkLibInfos() {
	}

	/** full constructor */
	public ApkLibInfos(Integer apkid, String apkname, Integer libid,
			String libname, Integer attribute) {
		this.apkid = apkid;
		this.apkname = apkname;
		this.libid = libid;
		this.libname = libname;
		this.attribute = attribute;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getApkid() {
		return this.apkid;
	}

	public void setApkid(Integer apkid) {
		this.apkid = apkid;
	}

	public String getApkname() {
		return this.apkname;
	}

	public void setApkname(String apkname) {
		this.apkname = apkname;
	}

	public Integer getLibid() {
		return this.libid;
	}

	public void setLibid(Integer libid) {
		this.libid = libid;
	}

	public String getLibname() {
		return this.libname;
	}

	public void setLibname(String libname) {
		this.libname = libname;
	}

	public Integer getAttribute() {
		return this.attribute;
	}

	public void setAttribute(Integer attribute) {
		this.attribute = attribute;
	}

}