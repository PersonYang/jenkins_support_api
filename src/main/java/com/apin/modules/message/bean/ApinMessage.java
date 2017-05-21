package com.apin.modules.message.bean;

import java.util.Date;

/**
 * 
 * 消息
 * @author Young
 * @date 2017年2月22日 上午9:18:08
 * @version 1.0 
 */
public class ApinMessage {
	
    private String id;
    private Date createTime;
    private Byte messageType;
    private Byte hasRead;
    private Byte hasDelete;
    private Byte isMerchant;
    private String accountId;
    private String messageDetail;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Byte getMessageType() {
        return messageType;
    }

    public void setMessageType(Byte messageType) {
        this.messageType = messageType;
    }

    public Byte getHasRead() {
        return hasRead;
    }

    public void setHasRead(Byte hasRead) {
        this.hasRead = hasRead;
    }

    public Byte getHasDelete() {
        return hasDelete;
    }

    public void setHasDelete(Byte hasDelete) {
        this.hasDelete = hasDelete;
    }

    public Byte getIsMerchant() {
        return isMerchant;
    }

    public void setIsMerchant(Byte isMerchant) {
        this.isMerchant = isMerchant;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId == null ? null : accountId.trim();
    }

    public String getMessageDetail() {
        return messageDetail;
    }

    public void setMessageDetail(String messageDetail) {
        this.messageDetail = messageDetail == null ? null : messageDetail.trim();
    }
}