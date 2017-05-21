package com.apin.modules.flights.dao;

import com.apin.base.dao.BaseDAO;
import com.apin.modules.flights.bean.ApinJourneyIdSequence;

public interface ApinJourneyIdSequenceDao extends BaseDAO<ApinJourneyIdSequence>{

	int insertJourneyIdSequence(ApinJourneyIdSequence journey);
   
}