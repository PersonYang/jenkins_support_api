package com.apin.base.bean;


import javax.persistence.Column;

/**
 * 字典代码
 * @author DIGO
 */
public abstract class Dict extends Base {

	private static final long serialVersionUID = -6032929676371505124L;

	@Column(name="name")
	private String name;
	@Column(name="code")
	private String code;

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	 @Override  
	 public String toString() {  
		 return super.toString();
	 }

}
