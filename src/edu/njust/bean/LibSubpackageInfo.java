package edu.njust.bean;

/**
 * LibSubpackageInfo entity. @author MyEclipse Persistence Tools
 */

public class LibSubpackageInfo implements java.io.Serializable {

	// Fields

	private Integer id;
	private Integer libId;
	private String libName;
	private Integer libRootpackageid;
	private String libRootpackagename;
	private String libSubpackagename;

	// Constructors

	/** default constructor */
	public LibSubpackageInfo() {
	}

	/** minimal constructor */
	public LibSubpackageInfo(String libSubpackagename) {
		this.libSubpackagename = libSubpackagename;
	}

	/** full constructor */
	public LibSubpackageInfo(Integer libId, String libName,
			Integer libRootpackageid, String libRootpackagename,
			String libSubpackagename) {
		this.libId = libId;
		this.libName = libName;
		this.libRootpackageid = libRootpackageid;
		this.libRootpackagename = libRootpackagename;
		this.libSubpackagename = libSubpackagename;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public Integer getLibRootpackageid() {
		return this.libRootpackageid;
	}

	public void setLibRootpackageid(Integer libRootpackageid) {
		this.libRootpackageid = libRootpackageid;
	}

	public String getLibRootpackagename() {
		return this.libRootpackagename;
	}

	public void setLibRootpackagename(String libRootpackagename) {
		this.libRootpackagename = libRootpackagename;
	}

	public String getLibSubpackagename() {
		return this.libSubpackagename;
	}

	public void setLibSubpackagename(String libSubpackagename) {
		this.libSubpackagename = libSubpackagename;
	}

}