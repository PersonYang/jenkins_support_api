package com.apin.modules.orders.bean;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;

import com.apin.base.bean.Base;

/**
 * 订单
 * @author Young
 * @date 2016年11月17日
 */
public class ApinOrder extends Base{
    /**
	 * 
	 */
	private static final long serialVersionUID = 7317897549978135703L;
	public static final String TABLE_NAME = "apin_order";
	@Column
	private Integer id;
	@Column
    private Long orderNo;
	@Column
    private Long journeyId;
	@Column
    private Integer userId;
	@Column
    private Integer merchantId;
	@Column
    private BigDecimal unitPrice;
	@Column
    private Integer passengerNum;
	@Column
    private BigDecimal insurancePrice;
	@Column
    private Integer insuranceNumber;
	@Column
    private BigDecimal totalPrice;
	@Column
    private BigDecimal actualPayCash;
	@Column
    private Integer payModel;
	@Column
    private String currency;
	@Column
    private Byte status;
	@Column
    private Byte statusByApp;
	@Column
    private String payVoucher;
	@Column
    private Date createTime;
	@Column
    private Date updateTime;
	@Column
    private String account;
	@Column
    private Date payTime;
	@Column
    private String payer;
	@Column
    private Integer actualPassengerNum;
	@Column
    private BigDecimal restPrice;
	@Column
    private Integer priceId;
	@Column
    private Byte restStatusByApp;
	@Column
    private String restPayVoucher;
	@Column
    private String restAccount;
	@Column
    private String restPayer;
	@Column
    private Date restPayTime;
	@Column
    private Integer restPayModel;
	@Column
	private String  travelAgencyId;
	@Column
	private String supplierId;
	@Column
    private Long routeNo;
	@Column
	private Byte flag;
	

	public Byte getFlag() {
		return flag;
	}

	public void setFlag(Byte flag) {
		this.flag = flag;
	}

	public Long getRouteNo() {
		return routeNo;
	}

	public void setRouteNo(Long routeNo) {
		this.routeNo = routeNo;
	}

	public String getTravelAgencyId() {
		return travelAgencyId;
	}

	public void setTravelAgencyId(String travelAgencyId) {
		this.travelAgencyId = travelAgencyId;
	}

	public String getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}

	public Integer getRestPayModel() {
		return restPayModel;
	}

	public void setRestPayModel(Integer restPayModel) {
		this.restPayModel = restPayModel;
	}

	@Override
	public Integer getId() {
        return id;
    }

    @Override
	public void setId(Integer id) {
        this.id = id;
    }

    public Long getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    public Long getJourneyId() {
        return journeyId;
    }

    public void setJourneyId(Long journeyId) {
        this.journeyId = journeyId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Integer merchantId) {
        this.merchantId = merchantId;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getPassengerNum() {
        return passengerNum;
    }

    public void setPassengerNum(Integer passengerNum) {
        this.passengerNum = passengerNum;
    }

    public BigDecimal getInsurancePrice() {
        return insurancePrice;
    }

    public void setInsurancePrice(BigDecimal insurancePrice) {
        this.insurancePrice = insurancePrice;
    }

    public Integer getInsuranceNumber() {
        return insuranceNumber;
    }

    public void setInsuranceNumber(Integer insuranceNumber) {
        this.insuranceNumber = insuranceNumber;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getActualPayCash() {
        return actualPayCash;
    }

    public void setActualPayCash(BigDecimal actualPayCash) {
        this.actualPayCash = actualPayCash;
    }

    public Integer getPayModel() {
        return payModel;
    }

    public void setPayModel(Integer payModel) {
        this.payModel = payModel;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency == null ? null : currency.trim();
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Byte getStatusByApp() {
        return statusByApp;
    }

    public void setStatusByApp(Byte statusByApp) {
        this.statusByApp = statusByApp;
    }

    public String getPayVoucher() {
        return payVoucher;
    }

    public void setPayVoucher(String payVoucher) {
        this.payVoucher = payVoucher == null ? null : payVoucher.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account == null ? null : account.trim();
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public String getPayer() {
        return payer;
    }

    public void setPayer(String payer) {
        this.payer = payer == null ? null : payer.trim();
    }

    public Integer getActualPassengerNum() {
        return actualPassengerNum;
    }

    public void setActualPassengerNum(Integer actualPassengerNum) {
        this.actualPassengerNum = actualPassengerNum;
    }

    public BigDecimal getRestPrice() {
        return restPrice;
    }

    public void setRestPrice(BigDecimal restPrice) {
        this.restPrice = restPrice;
    }

    public Integer getPriceId() {
        return priceId;
    }

    public void setPriceId(Integer priceId) {
        this.priceId = priceId;
    }

    public Byte getRestStatusByApp() {
        return restStatusByApp;
    }

    public void setRestStatusByApp(Byte restStatusByApp) {
        this.restStatusByApp = restStatusByApp;
    }

    public String getRestPayVoucher() {
        return restPayVoucher;
    }

    public void setRestPayVoucher(String restPayVoucher) {
        this.restPayVoucher = restPayVoucher == null ? null : restPayVoucher.trim();
    }

    public String getRestAccount() {
        return restAccount;
    }

    public void setRestAccount(String restAccount) {
        this.restAccount = restAccount == null ? null : restAccount.trim();
    }

    public String getRestPayer() {
        return restPayer;
    }

    public void setRestPayer(String restPayer) {
        this.restPayer = restPayer == null ? null : restPayer.trim();
    }

    public Date getRestPayTime() {
        return restPayTime;
    }

    public void setRestPayTime(Date restPayTime) {
        this.restPayTime = restPayTime;
    }
}