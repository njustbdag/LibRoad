package edu.njust.bean;

/**
 * PermissionClassname entity. @author MyEclipse Persistence Tools
 */

public class PermissionClassname implements java.io.Serializable {

	// Fields

	private Integer id;
	private String callerClass;
	private String remarks;

	// Constructors

	/** default constructor */
	public PermissionClassname() {
	}

	/** full constructor */
	public PermissionClassname(String callerClass, String remarks) {
		this.callerClass = callerClass;
		this.remarks = remarks;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCallerClass() {
		return this.callerClass;
	}

	public void setCallerClass(String callerClass) {
		this.callerClass = callerClass;
	}

	public String getRemarks() {
		return this.remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

}