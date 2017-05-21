package com.apin.modules.routes.bean;

import java.util.Date;

import javax.persistence.Column;

import com.apin.base.bean.Base;

/**
 * 
 * 用户行程派发的商户
 * @author Young
 * @date 2016年9月29日
 */
public class ApinRouteDistributeMerchantInfo extends Base {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -2563186548915798023L;
	public static final String TABLE_NAME = "apin_route_distribute_merchant_info";
	@Column
	private Integer id;
	@Column
    private Integer merchantId;
	@Column
    private Long routeNo;
	@Column
    private Date expireTime;
	@Column
    private Byte handleStatus;
	@Column
	private Byte isMatched;
	@Column
    private Date updateTime;
	@Column
    private Date createTime;
	@Column
	private String supplierId;
	
	
    public String getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}

	@Override
	public Integer getId() {
        return id;
    }

    @Override
	public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Integer merchantId) {
        this.merchantId = merchantId;
    }

    public Long getRouteNo() {
        return routeNo;
    }

    public void setRouteNo(Long routeNo) {
        this.routeNo = routeNo;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public Byte getHandleStatus() {
        return handleStatus;
    }

    public void setHandleStatus(Byte handleStatus) {
        this.handleStatus = handleStatus;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

	public Byte getIsMatched() {
		return isMatched;
	}

	public void setIsMatched(Byte isMatched) {
		this.isMatched = isMatched;
	}
    
    
}