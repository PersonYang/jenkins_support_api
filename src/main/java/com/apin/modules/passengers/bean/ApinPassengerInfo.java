package com.apin.modules.passengers.bean;

import java.util.Date;

import javax.persistence.Column;

import com.apin.base.bean.Base;

public class ApinPassengerInfo extends Base{
    /**
	 * 
	 */
	private static final long serialVersionUID = -1359958508014372949L;
	public static final String TABLE_NAME = "apin_passenger_info";

	@Column
	private Integer id;
	@Column
    private Long orderNo;
	@Column
    private Byte passengerIdCardType;
	@Column
    private String passengerIdCardNo;
	@Column
    private String passengerName;
	@Column
    private String passengerNamePinyin;
    @Column
    private String passengerNation;
    @Column
    private Byte passengerGender;
    @Column
    private Byte passengerType;
    @Column
    private String passengerPhone;
    @Column
    private String passengerBirthday;
    @Column
    private String passengerNationCode;
    @Column
    private String passengerFirstName;
    @Column
    private String passengerSecondName;
    @Column
    private Date cardExpireDate;
    @Column
    private Date createTime;
    @Column
    private String issueCountry;
    
    

    public String getIssueCountry() {
		return issueCountry;
	}

	public void setIssueCountry(String issueCountry) {
		this.issueCountry = issueCountry;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
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

    public Byte getPassengerIdCardType() {
        return passengerIdCardType;
    }

    public void setPassengerIdCardType(Byte passengerIdCardType) {
        this.passengerIdCardType = passengerIdCardType;
    }

    public String getPassengerIdCardNo() {
        return passengerIdCardNo;
    }

    public void setPassengerIdCardNo(String passengerIdCardNo) {
        this.passengerIdCardNo = passengerIdCardNo == null ? null : passengerIdCardNo.trim();
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName == null ? null : passengerName.trim();
    }

    public String getPassengerNamePinyin() {
        return passengerNamePinyin;
    }

    public void setPassengerNamePinyin(String passengerNamePinyin) {
        this.passengerNamePinyin = passengerNamePinyin == null ? null : passengerNamePinyin.trim();
    }

    public String getPassengerNation() {
        return passengerNation;
    }

    public void setPassengerNation(String passengerNation) {
        this.passengerNation = passengerNation == null ? null : passengerNation.trim();
    }

    public Byte getPassengerGender() {
        return passengerGender;
    }

    public void setPassengerGender(Byte passengerGender) {
        this.passengerGender = passengerGender;
    }

    public String getPassengerBirthday() {
        return passengerBirthday;
    }

    public void setPassengerBirthday(String passengerBirthday) {
        this.passengerBirthday = passengerBirthday == null ? null : passengerBirthday.trim();
    }

    public String getPassengerNationCode() {
        return passengerNationCode;
    }

    public void setPassengerNationCode(String passengerNationCode) {
        this.passengerNationCode = passengerNationCode == null ? null : passengerNationCode.trim();
    }

  
    public String getPassengerFirstName() {
		return passengerFirstName;
	}

	public void setPassengerFirstName(String passengerFirstName) {
		this.passengerFirstName = passengerFirstName;
	}

	public String getPassengerSecondName() {
		return passengerSecondName;
	}

	public void setPassengerSecondName(String passengerSecondName) {
		this.passengerSecondName = passengerSecondName;
	}

	public Date getCardExpireDate() {
        return cardExpireDate;
    }

    public void setCardExpireDate(Date cardExpireDate) {
        this.cardExpireDate = cardExpireDate;
    }

	public Byte getPassengerType() {
		return passengerType;
	}

	public void setPassengerType(Byte passengerType) {
		this.passengerType = passengerType;
	}

	public String getPassengerPhone() {
		return passengerPhone;
	}

	public void setPassengerPhone(String passengerPhone) {
		this.passengerPhone = passengerPhone;
	}
    
    
}