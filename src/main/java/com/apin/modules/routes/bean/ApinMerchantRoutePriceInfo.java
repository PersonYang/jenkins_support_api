package com.apin.modules.routes.bean;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;

import com.apin.base.bean.Base;

/**
 * 
 * 商户出价表
 * @author Young
 * @date 2016年9月29日
 */
public class ApinMerchantRoutePriceInfo extends Base{
    /**
	 * 
	 */
	private static final long serialVersionUID = -7176668604741026020L;
	public static final String TABLE_NAME = "apin_merchant_route_price_info";
	@Column
	private Integer id;
	@Column
    private Long routeNo;
	@Column
    private Integer merchantId;
	@Column
    private Long journeyId;
	@Column
    private BigDecimal price;
	@Column
	private BigDecimal ticketRate;
	@Column
	private BigDecimal depositRatio;
	@Column
	private Date restLastPayTime;
	@Column
	private String supplierId;
	@Column
    private Date validTime;
	@Column
	private Byte hasChat;
	
	

	
    public Byte getHasChat() {
		return hasChat;
	}

	public void setHasChat(Byte hasChat) {
		this.hasChat = hasChat;
	}

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

    public Long getRouteNo() {
        return routeNo;
    }

    public void setRouteNo(Long routeNo) {
        this.routeNo = routeNo;
    }

    public Integer getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Integer merchantId) {
        this.merchantId = merchantId;
    }

    public Long getJourneyId() {
        return journeyId;
    }

    public void setJourneyId(Long journeyId) {
        this.journeyId = journeyId;
    }
    public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Date getValidTime() {
        return validTime;
    }

    public void setValidTime(Date validTime) {
        this.validTime = validTime;
    }

	public BigDecimal getTicketRate() {
		return ticketRate;
	}

	public void setTicketRate(BigDecimal ticketRate) {
		this.ticketRate = ticketRate;
	}

	public BigDecimal getDepositRatio() {
		return depositRatio;
	}

	public void setDepositRatio(BigDecimal depositRatio) {
		this.depositRatio = depositRatio;
	}

	public Date getRestLastPayTime() {
		return restLastPayTime;
	}

	public void setRestLastPayTime(Date restLastPayTime) {
		this.restLastPayTime = restLastPayTime;
	}


    
}