package edu.njust.bean;

/**
 * ApkLib entity. @author MyEclipse Persistence Tools
 */

public class ApkLib implements java.io.Serializable {

	// Fields

	private Integer id;
	private String apk;
	private String lib;

	// Constructors

	/** default constructor */
	public ApkLib() {
	}

	/** full constructor */
	public ApkLib(String apk, String lib) {
		this.apk = apk;
		this.lib = lib;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getApk() {
		return this.apk;
	}

	public void setApk(String apk) {
		this.apk = apk;
	}

	public String getLib() {
		return this.lib;
	}

	public void setLib(String lib) {
		this.lib = lib;
	}

}