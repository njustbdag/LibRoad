package edu.njust.bean;

/**
 * LibPackagestructure entity. @author MyEclipse Persistence Tools
 */

public class LibPackagestructure implements java.io.Serializable {

	// Fields

	private Integer id;
	private String libName;
	private String packagestructureHash;

	// Constructors

	/** default constructor */
	public LibPackagestructure() {
	}

	/** full constructor */
	public LibPackagestructure(String libName, String packagestructureHash) {
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