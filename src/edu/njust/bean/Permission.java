package edu.njust.bean;

/**
 * Permission entity. @author MyEclipse Persistence Tools
 */

public class Permission implements java.io.Serializable {

	// Fields

	private Integer id;
	private String detailDesc;
	private String callerClass;
	private String callerMethod;
	private String callerMethodDesc;
	private String permission;
	private String remarks;

	// Constructors

	/** default constructor */
	public Permission() {
	}

	/** minimal constructor */
	public Permission(String detailDesc) {
		this.detailDesc = detailDesc;
	}

	/** full constructor */
	public Permission(String detailDesc, String callerClass,
			String callerMethod, String callerMethodDesc, String permission,
			String remarks) {
		this.detailDesc = detailDesc;
		this.callerClass = callerClass;
		this.callerMethod = callerMethod;
		this.callerMethodDesc = callerMethodDesc;
		this.permission = permission;
		this.remarks = remarks;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDetailDesc() {
		return this.detailDesc;
	}

	public void setDetailDesc(String detailDesc) {
		this.detailDesc = detailDesc;
	}

	public String getCallerClass() {
		return this.callerClass;
	}

	public void setCallerClass(String callerClass) {
		this.callerClass = callerClass;
	}

	public String getCallerMethod() {
		return this.callerMethod;
	}

	public void setCallerMethod(String callerMethod) {
		this.callerMethod = callerMethod;
	}

	public String getCallerMethodDesc() {
		return this.callerMethodDesc;
	}

	public void setCallerMethodDesc(String callerMethodDesc) {
		this.callerMethodDesc = callerMethodDesc;
	}

	public String getPermission() {
		return this.permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public String getRemarks() {
		return this.remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

}