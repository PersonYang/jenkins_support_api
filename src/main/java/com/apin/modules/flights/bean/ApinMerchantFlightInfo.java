package com.apin.modules.flights.bean;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;

import com.apin.base.bean.Base;

/**
 * @author Young
 * @date 2017年1月17日 下午7:26:48
 * @version 1.0 
 */
public class ApinMerchantFlightInfo extends Base{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7759266399144024428L;
	public static final String TABLE_NAME = "apin_merchant_flight_info";
	
	@Column
    private Integer id;
	@Column
    private Long journeyId;
	@Column
    private Byte flightType;
	@Column
    private Integer merchantId;
	@Column
    private Integer currentTicketNum;
	@Column
    private String currency;
	@Column
    private BigDecimal totalPriceInctax;
	@Column
    private Byte routeType;
	@Column
    private Byte isShelves;
	@Column
    private Date validTime;
	@Column
    private Byte isAudit;
	@Column
    private Byte hasInsurance;
	@Column
    private BigDecimal insurance;
	@Column
    private Date updateTime;
	@Column
    private Byte isFareTicket;
	@Column
	private Integer version;
    @Column
    private Date createTime;
    @Column
    private Byte flag;
    @Column
    private String supplierId;
    @Column
    private Byte isSpecialTicket;
    @Column
    private String baggageRules;
    @Column
    private String groupId;
    
    
    

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public Byte getIsSpecialTicket() {
		return isSpecialTicket;
	}

	public void setIsSpecialTicket(Byte isSpecialTicket) {
		this.isSpecialTicket = isSpecialTicket;
	}

	public String getBaggageRules() {
		return baggageRules;
	}

	public void setBaggageRules(String baggageRules) {
		this.baggageRules = baggageRules;
	}

	public String getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}

	public Byte getFlag() {
		return flag;
	}

	public void setFlag(Byte flag) {
		this.flag = flag;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getJourneyId() {
        return journeyId;
    }

    public void setJourneyId(Long journeyId) {
        this.journeyId = journeyId;
    }

    public Byte getFlightType() {
        return flightType;
    }

    public void setFlightType(Byte flightType) {
        this.flightType = flightType;
    }

    public Integer getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Integer merchantId) {
        this.merchantId = merchantId;
    }

    public Integer getCurrentTicketNum() {
        return currentTicketNum;
    }

    public void setCurrentTicketNum(Integer currentTicketNum) {
        this.currentTicketNum = currentTicketNum;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency == null ? null : currency.trim();
    }

    public BigDecimal getTotalPriceInctax() {
        return totalPriceInctax;
    }

    public void setTotalPriceInctax(BigDecimal totalPriceInctax) {
        this.totalPriceInctax = totalPriceInctax;
    }

    public Byte getRouteType() {
        return routeType;
    }

    public void setRouteType(Byte routeType) {
        this.routeType = routeType;
    }

    public Byte getIsShelves() {
        return isShelves;
    }

    public void setIsShelves(Byte isShelves) {
        this.isShelves = isShelves;
    }

    public Date getValidTime() {
        return validTime;
    }

    public void setValidTime(Date validTime) {
        this.validTime = validTime;
    }

    public Byte getIsAudit() {
        return isAudit;
    }

    public void setIsAudit(Byte isAudit) {
        this.isAudit = isAudit;
    }

    public Byte getHasInsurance() {
        return hasInsurance;
    }

    public void setHasInsurance(Byte hasInsurance) {
        this.hasInsurance = hasInsurance;
    }

    public BigDecimal getInsurance() {
        return insurance;
    }

    public void setInsurance(BigDecimal insurance) {
        this.insurance = insurance;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Byte getIsFareTicket() {
        return isFareTicket;
    }

    public void setIsFareTicket(Byte isFareTicket) {
        this.isFareTicket = isFareTicket;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
    
    
}