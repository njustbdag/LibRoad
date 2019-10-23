package edu.njust.bean;

/**
 * LitelibClassInfo entity. @author MyEclipse Persistence Tools
 */

public class LitelibClassInfo implements java.io.Serializable {

	// Fields

	private Integer id;
	private String libClassname;
	private String classHashStrict;
	private String classHash;
	private Integer libId;
	private String libName;
	private Integer libPackageid;
	private String libPackagename;

	// Constructors

	/** default constructor */
	public LitelibClassInfo() {
	}

	/** minimal constructor */
	public LitelibClassInfo(String libClassname) {
		this.libClassname = libClassname;
	}

	/** full constructor */
	public LitelibClassInfo(String libClassname, String classHashStrict,
			String classHash, Integer libId, String libName,
			Integer libPackageid, String libPackagename) {
		this.libClassname = libClassname;
		this.classHashStrict = classHashStrict;
		this.classHash = classHash;
		this.libId = libId;
		this.libName = libName;
		this.libPackageid = libPackageid;
		this.libPackagename = libPackagename;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLibClassname() {
		return this.libClassname;
	}

	public void setLibClassname(String libClassname) {
		this.libClassname = libClassname;
	}

	public String getClassHashStrict() {
		return this.classHashStrict;
	}

	public void setClassHashStrict(String classHashStrict) {
		this.classHashStrict = classHashStrict;
	}

	public String getClassHash() {
		return this.classHash;
	}

	public void setClassHash(String classHash) {
		this.classHash = classHash;
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

	public Integer getLibPackageid() {
		return this.libPackageid;
	}

	public void setLibPackageid(Integer libPackageid) {
		this.libPackageid = libPackageid;
	}

	public String getLibPackagename() {
		return this.libPackagename;
	}

	public void setLibPackagename(String libPackagename) {
		this.libPackagename = libPackagename;
	}

}