package edu.njust.bean;

/**
 * LibInfo entity. @author MyEclipse Persistence Tools
 */

public class LibInfo implements java.io.Serializable {

	// Fields

	private Integer id;
	private Integer libId;
	private String libName;
	private String packagestructureHash;

	// Constructors

	/** default constructor */
	public LibInfo() {
	}

	/** full constructor */
	public LibInfo(Integer libId, String libName, String packagestructureHash) {
		this.libId = libId;
		this.libName = libName;
		this.packagestructureHash = packagestructureHash;
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

	public String getPackagestructureHash() {
		return this.packagestructureHash;
	}

	public void setPackagestructureHash(String packagestructureHash) {
		this.packagestructureHash = packagestructureHash;
	}

}