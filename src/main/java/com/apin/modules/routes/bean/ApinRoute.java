package com.apin.modules.routes.bean;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.apin.base.bean.Base;

public class ApinRoute extends Base{
    /**
	 * 
	 */
	private static final long serialVersionUID = -8885891387137480847L;
	public static final String TABLE_NAME = "apin_route";
	@Autowired
	private Integer id;
	@Autowired
    private Long routeNo;
	@Autowired
    private Byte routeStatus;
	@Autowired
    private String userMobile;
	@Autowired
    private Integer userId;
	@Autowired
    private Byte routeType;
	@Autowired
    private Integer passengerNum;
	@Autowired
    private String description;
	@Autowired
    private Integer acceptId;
	@Autowired
    private Date createTime;
	@Autowired
    private Date updateTime;
	@Autowired
    private String cancelReason;
	@Autowired
    private Byte isTemporary;
	@Autowired
    private Byte flag;
	@Autowired
    private String userMaintenancerId;
	@Autowired
    private String travelAgencyId;
	@Autowired
    private String supplierId;
	@Autowired
    private String applicationId;
	@Autowired
    private String channelId;
	@Autowired
    private Integer adultPassenger;
	@Autowired
    private Integer childPassenger;
	@Autowired
    private Integer routeDays;
	@Autowired
    private Date expireTime;
	@Autowired
	private String showNo;

    public String getShowNo() {
		return showNo;
	}

	public void setShowNo(String showNo) {
		this.showNo = showNo;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getRouteNo() {
        return routeNo;
    }

    public void setRouteNo(Long routeNo) {
        this.routeNo = routeNo;
    }

    public Byte getRouteStatus() {
        return routeStatus;
    }

    public void setRouteStatus(Byte routeStatus) {
        this.routeStatus = routeStatus;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile == null ? null : userMobile.trim();
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Byte getRouteType() {
        return routeType;
    }

    public void setRouteType(Byte routeType) {
        this.routeType = routeType;
    }

    public Integer getPassengerNum() {
        return passengerNum;
    }

    public void setPassengerNum(Integer passengerNum) {
        this.passengerNum = passengerNum;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public Integer getAcceptId() {
        return acceptId;
    }

    public void setAcceptId(Integer acceptId) {
        this.acceptId = acceptId;
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

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason == null ? null : cancelReason.trim();
    }

    public Byte getIsTemporary() {
        return isTemporary;
    }

    public void setIsTemporary(Byte isTemporary) {
        this.isTemporary = isTemporary;
    }

    public Byte getFlag() {
        return flag;
    }

    public void setFlag(Byte flag) {
        this.flag = flag;
    }

    public String getUserMaintenancerId() {
        return userMaintenancerId;
    }

    public void setUserMaintenancerId(String userMaintenancerId) {
        this.userMaintenancerId = userMaintenancerId == null ? null : userMaintenancerId.trim();
    }

    public String getTravelAgencyId() {
        return travelAgencyId;
    }

    public void setTravelAgencyId(String travelAgencyId) {
        this.travelAgencyId = travelAgencyId == null ? null : travelAgencyId.trim();
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId == null ? null : supplierId.trim();
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId == null ? null : applicationId.trim();
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId == null ? null : channelId.trim();
    }

    public Integer getAdultPassenger() {
        return adultPassenger;
    }

    public void setAdultPassenger(Integer adultPassenger) {
        this.adultPassenger = adultPassenger;
    }

    public Integer getChildPassenger() {
        return childPassenger;
    }

    public void setChildPassenger(Integer childPassenger) {
        this.childPassenger = childPassenger;
    }

    public Integer getRouteDays() {
        return routeDays;
    }

    public void setRouteDays(Integer routeDays) {
        this.routeDays = routeDays;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }
}