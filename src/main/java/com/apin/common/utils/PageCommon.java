package com.apin.common.utils;

/**
 * 
 * 后台分页组件
 * 构造函数传入pageSize
 * recordCount 总记录数
 * currentPage 当前页数
 * @author Young
 * @date 2016年7月29日
 */
public class PageCommon {
    
	 /**
	 * 每页条数
	 */
	private long pageSize;
	/**
	 * 总记录数
	 */
	private long recordCount;
	/**
	 * 当前页码
	 */
	private long currentPage;
	/**
	 * 排序方式
	 */
	private String sortOrder;
	/**
	 * 排序字段
	 */
	private String sortField;

	/**
	 * 
	 * 构造
	 * @param pageSize
	 * @param recordCount
	 * @param currentPage
	 * @param sortField
	 * @param sortOrder
	 */
	public PageCommon(long pageSize, long recordCount, long currentPage, String sortField, String sortOrder) {
		this.pageSize = pageSize;
		this.recordCount = recordCount;
		this.sortField = sortField;
		this.sortOrder = sortOrder;
		setCurrentPage(currentPage);
	}

	// 构造方法
	public PageCommon(long pageSize, long recordCount, String sortField, String sortOrder) {
		this(pageSize, recordCount, 1, sortField, sortOrder);
	}

	/**
	 * 计算总页数
	 * @return
	 */
	public long getPageCount() {
		// 总条数/每页显示的条数=总页数
		long size = recordCount / pageSize;
		// 最后一页的条数
		long mod = recordCount % pageSize;
		if (mod != 0)
			size++;
		return recordCount == 0 ? 1 : size;
	}

	// 包含，起始索引为0
	public long getStartPageIndex() {
		return (currentPage - 1) * pageSize;
	}

	// 不包含
	public long getToIndex() {
		return Math.min(recordCount, currentPage * pageSize);
	}

	// 得到当前页
	public long getCurrentPage() {
		return currentPage;
	}

	// 设置当前页
	public void setCurrentPage(long currentPage) {
		long validPage = currentPage <= 0 ? 1 : currentPage;
		validPage = validPage > getPageCount() ? getPageCount() : validPage;
		this.currentPage = validPage;
	}

	// 得到每页显示的条数
	public long getPageSize() {
		return pageSize;
	}

	// 设置每页显示的条数
	public void setPageSize(long pageSize) {
		this.pageSize = pageSize;
	}

	// 得到总共的条数
	public long getRecordCount() {
		return recordCount;
	}

	// 设置总共的条数
	public void setRecordCount(long recordCount) {
		this.recordCount = recordCount;
	}

	public String getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}

	public String getSortField() {
		return sortField;
	}

	public void setSortField(String sortField) {
		this.sortField = sortField;
	}
    
	
	 
	 
}
	 