package com.apin.common.global;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.apin.base.bean.ApinAirComp;
import com.apin.base.bean.ApinCity;
import com.apin.base.bean.DictAirport;
import com.apin.base.dao.ApinAirCompDao;
import com.apin.base.dao.ApinCityDao;
import com.apin.base.dao.DictAirportDAO;
import com.apin.common.init.Initializable;
/**
 * 加载全局资源
 * @author Young
 * @date 2017年1月11日 下午4:54:36
 * @version 1.0 
 */
@Component
public class GlobalSource implements Initializable {

    private static Logger logger = LoggerFactory.getLogger(GlobalSource.class);
    @Autowired
    private DictAirportDAO dictAirportDAO;
    @Autowired
    private ApinAirCompDao airCompDao;
    @Autowired
    private ApinCityDao cityDao;

    /**
     * 启动时load缓存数据
     */
    public void init() {
        logger.info("====================================初始化ing==========================================================");
        DictSource.DICT_AIRPORT = dictAirportDAO.getListAll(DictAirport.class);
        DictSource.DICT_AIR_COMP = airCompDao.getListAll(ApinAirComp.class);
        DictSource.DICT_CITY = cityDao.getListAll(ApinCity.class);
        for (DictAirport airport : DictSource.DICT_AIRPORT) {
        	DictSource.AIRPORT_CODE_MAP.put(airport.getIataCode(), airport);
        }
        
        for (ApinAirComp airComp : DictSource.DICT_AIR_COMP) {
        	DictSource.APIN_AIR_COMP_MAP.put(airComp.getIataCode(), airComp);
        }
        for (ApinCity city : DictSource.DICT_CITY) {
        	DictSource.APIN_CITY_MAP.put(city.getCityCode(), city);
        }
    }

    public void reset() {
        logger.info("===================================重置ing==========================================================");
        init();
    }




}
