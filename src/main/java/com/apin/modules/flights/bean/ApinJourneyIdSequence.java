package com.apin.modules.flights.bean;

import com.apin.base.bean.Base;

/**
 * @author Young
 * @date 2017年1月17日 下午7:27:15
 * @version 1.0 
 */
public class ApinJourneyIdSequence extends Base{
    /**
	 * 
	 */
	private static final long serialVersionUID = -7039594192166623078L;
	public static final String TABLE_NAME = "apin_journey_id_sequence";
	
	private Long journeyId;

    public Long getJourneyId() {
        return journeyId;
    }

    public void setJourneyId(Long journeyId) {
        this.journeyId = journeyId;
    }
}