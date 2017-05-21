package com.apin.modules.passengers.bean;

import javax.persistence.Column;

import com.apin.base.bean.Base;

public class ApinPassengerTicket extends Base{
    /**
	 * 
	 */
	private static final long serialVersionUID = -2858153584128799802L;
	public static final String TABLE_NAME = "apin_passenger_ticket";
	
	@Column
	private Integer id;
	@Column
    private Integer passengerId;
	@Column
    private String ticketNo;

    @Override
	public Integer getId() {
        return id;
    }

    @Override
	public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Integer passengerId) {
        this.passengerId = passengerId;
    }

    public String getTicketNo() {
        return ticketNo;
    }

    public void setTicketNo(String ticketNo) {
        this.ticketNo = ticketNo == null ? null : ticketNo.trim();
    }
}