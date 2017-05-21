package com.apin.base.service;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.apin.base.bean.AirLine;
import com.apin.base.bean.AirLineFlight;
import com.apin.base.bean.DictAirport;
import com.apin.base.dao.AirLineDao;
import com.apin.base.dao.AirLineFlightDao;
import com.apin.common.global.DictSource;
import com.apin.common.response.ApinResponse;
import com.apin.common.response.ApinResponseUtil;
import com.apin.common.response.ErrorEnum;
import com.apin.common.utils.DateUtil;
import com.apin.common.utils.HttpJuheFlightUtils;
import com.apin.common.utils.JsonUtil;

import net.sf.json.JSONObject;

/**
 * 改变基本航班库中的存储逻辑
 * @author Young
 * @date 2017年3月13日 上午11:54:10
 * @version 1.0 
 */
@Service
public class AirLineFlightVersionService {

	
	private  final Logger logger = LoggerFactory.getLogger(AirLineFlightService.class);
	@Autowired
	AirLineFlightDao lineFlightDao;
	@Autowired
	AirLineDao airLineDao;
	@Value("${juhe.flight.info.url}")
	private String juheFlightInfoUrl;
	@Value("${juhe.appkey}")
	private String juheAppKey;
	
	@SuppressWarnings("unchecked")
	@Transactional(rollbackFor=Exception.class)
	public ApinResponse<Map<String, Object>> getListBySearchMap(String pageParam,String searchParam){
		long requestTime = System.currentTimeMillis();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("requestTime", requestTime);
		ApinResponse<Map<String, Object>> response = ApinResponseUtil.good(requestTime);
		response.setBody(resultMap);
		return response;
	}
	/**
	 * 
	 * @param param 航班号list
	 * @param auth 身份验证
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public ApinResponse<Map<String, Object>> getFlightInfo(String flightNo,String departDate) {
		long requestTime =System.currentTimeMillis();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		ApinResponse<Map<String, Object>> apinResponse =null;
		List<Map<String, String>> lineFlightList =new ArrayList<Map<String, String>>();
		try {
			if(StringUtils.isBlank(flightNo) || StringUtils.isBlank(departDate)){
				apinResponse = ApinResponseUtil.bad(requestTime, ErrorEnum.ERROR_PARAM_ISBLANK);
				resultMap.put("status", false);
				resultMap.put("message", "存在为空的参数");
				apinResponse.setBody(resultMap);
				return apinResponse;
			}
			Map<String, Object> flightParams = new HashMap<String, Object>();//请求参数
	    	flightParams.put("key",juheAppKey);
	    	flightParams.put("dtype","");
	    	flightParams.put("name", flightNo);
	    	flightParams.put("date", departDate);
	    	lineFlightList.add(getEachFlightInfo(flightParams));
	    	
	    	apinResponse = ApinResponseUtil.good(requestTime);
			resultMap.put("status", true);
			resultMap.put("flightInfoList", lineFlightList);
			apinResponse.setBody(resultMap);
			return apinResponse;
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("=========the method getFlightInfo get something wrong =============");
			apinResponse =ApinResponseUtil.bad(requestTime, ErrorEnum.ERROR_INTERNAL_SERVER_ERROR);
			resultMap.put("status", false);
			resultMap.put("message", "接口异常");
			apinResponse.setBody(resultMap);
			return apinResponse;
		}
	}
	

	/**
	 * 根据出发城市代码，目的城市代码，航班号确认
	 * 新版查询航班情况
	 * @param flightParams
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> getEachFlightInfo(Map<String, Object> flightParams) throws Exception{
		// 先查数据库中是否存在该航班的信息
		AirLineFlight lineFlight = lineFlightDao.findBySearchMap(flightParams);
		if(lineFlight != null ){
			return getAirLineInfoMap(lineFlight);
		}else{
			// 不存在 需要调用聚合的接口 查询航班情况
			AirLineFlight lineFlightInfo = getAirLineFlightInfo(flightParams);
			if(lineFlightInfo != null ){
				return getAirLineInfoMap(lineFlightInfo);
			}
			return null;
		}
	}
	
	/**
	 * 将航班数据封装放回
	 * @param LineFlight
	 * @return Map<String, String>
	 */
	public Map<String, String> getAirLineInfoMap(AirLineFlight LineFlight){
			Map<String, String> flightInfoMap = new HashMap<>();
			flightInfoMap.put("departPlace", LineFlight.getDepartPlace());
			flightInfoMap.put("departPlaceCode", LineFlight.getDepartPlaceCode());
			flightInfoMap.put("destPlace", LineFlight.getDestPlace());
			flightInfoMap.put("destPlaceCode", LineFlight.getDestPlaceCode());
			flightInfoMap.put("airComp", LineFlight.getAirComp());
			flightInfoMap.put("airCompCode", LineFlight.getAirCompCode());
			flightInfoMap.put("flightNo", LineFlight.getFlightNo());
			flightInfoMap.put("departAirportCode", LineFlight.getDepartAirportCode());
			flightInfoMap.put("departAirport", LineFlight.getDepartAirport());
			flightInfoMap.put("arriveAirportCode", LineFlight.getArriveAirportCode());
			flightInfoMap.put("arriveAirport", LineFlight.getArriveAirport());
			flightInfoMap.put("departTime", LineFlight.getDepartTime());
			flightInfoMap.put("arriveTime", LineFlight.getArriveTime());
			if (LineFlight.getIsMiddleStop() != null) {
				flightInfoMap.put("isMiddleStop", LineFlight.getIsMiddleStop().toString());
			}
			flightInfoMap.put("middleCity", LineFlight.getMiddleCity());
			flightInfoMap.put("middleCityCode", LineFlight.getMiddleCityCode());
			flightInfoMap.put("middleAirport", LineFlight.getMiddleAirport());
			flightInfoMap.put("middleAirportCode", LineFlight.getMiddleAirportCode());
			flightInfoMap.put("middleArriveTime", LineFlight.getMiddleArriveTime());
			flightInfoMap.put("middleDepartTime", LineFlight.getMiddleDepartTime());
			flightInfoMap.put("flyTime", LineFlight.getFlyTime());
			return flightInfoMap;
	}
	/**
	 * 按照新的逻辑存储 
	 * 如果是 有经停的航班情况。
	 * 例如:a-b-c  测存储 a-b ，b-c，a-c,三段航班信息
	 * 调用聚合数据接口 
	 * 将返回的数据封装成AirLineFlight
	 * @param flightParams
	 * @return AirLineFlight
	 * @throws Exception 
	 */
	public AirLineFlight getAirLineFlightInfo(Map<String, Object> flightParams) throws Exception{
			String result = HttpJuheFlightUtils.net(juheFlightInfoUrl, flightParams, "GET");
			JSONObject object = JSONObject.fromObject(result);
			AirLineFlight returnAirLineFlight = null;
			if(object.getInt("error_code")==0){
				String innerresult = object.get("result").toString();
	        	String flightListstr =  JsonUtil.getJsonStr("list", innerresult);
	        	String flightInfotstr =  JsonUtil.getJsonStr("info", innerresult);
	        	Map<String, String> flightInfoMap = JsonUtil.parseToMap(flightInfotstr);
	        	List<Map<String, String>> list = JsonUtil.parseToMapInList(flightListstr);
	        	Map<String, Object>  originalMap =new HashMap<>();
	        	originalMap.put("departPlaceCode", flightParams.get("departPlaceCode"));
	        	originalMap.put("destPlaceCode", flightParams.get("destPlaceCode"));
	        	for(Map<String, String> map : list){
	        		AirLine airLine = null;
	        		Map<String, String> lineSearchMap = new HashMap<>();
	        		DictAirport departAirport = DictSource.AIRPORT_CODE_MAP.get(map.get("qf_citycode"));
		        	DictAirport destAirport = DictSource.AIRPORT_CODE_MAP.get(map.get("dd_citycode"));
		        	AirLineFlight lineFlight = new AirLineFlight();
		        	if(departAirport != null && destAirport != null ){
		        		String departFullTime = map.get("jhqftime_full");
		        		String arriveFullTime = map.get("jhddtime_full");
		        		if(StringUtils.isNoneBlank(arriveFullTime)){
		        			Date commonDate = DateUtil.string2TimeZone(arriveFullTime, departAirport.getTimeZone(), destAirport.getTimeZone());
		        			long flyTime = commonDate.getTime()-DateUtil.toFormatDate(departFullTime, "yyyy-MM-dd HH:mm").getTime();
		        			lineFlight.setFlyTime(String.valueOf(flyTime));
		        		}
		        		flightParams.put("departPlaceCode", departAirport.getCityCode());
		        		flightParams.put("destPlaceCode", destAirport.getCityCode());
		        		AirLineFlight isExits =  lineFlightDao.findBySearchMap(flightParams);
			        	if(isExits != null ){
			        		continue;
			        	}
		        	}
		        	if(departAirport != null && destAirport != null ){
		        		lineSearchMap.put("departCode", departAirport.getCityCode());
		        		lineSearchMap.put("destCode", destAirport.getCityCode());
				        airLine =  airLineDao.findBySearchMap(lineSearchMap);
	        		}
		        	if(departAirport != null ){
		        		lineFlight.setDepartPlace(departAirport.getCityName());
		        		lineFlight.setDepartPlaceCode(departAirport.getCityCode());
		        	}
		        	if(destAirport != null ){
		        		lineFlight.setDestPlace(destAirport.getCityName());
		        		lineFlight.setDestPlaceCode(destAirport.getCityCode());
		        	}
		        	if(airLine == null ){
		        		airLine = new AirLine();
		        		if(departAirport != null ){
		        			airLine.setDepartPlace(departAirport.getCityName());
		        			airLine.setDepartPlaceCode(departAirport.getCityCode());
			        	}
			        	if(destAirport != null ){
			        		airLine.setDestPlace(destAirport.getCityName());
			        		airLine.setDestPlaceCode(destAirport.getCityCode());
			        	}
			        	airLineDao.insertNotNull(airLine);
		        	}
		        	lineFlight.setAirLineId(airLine.getId());
		        	lineFlight.setAirComp(flightInfoMap.get("company"));
		        	if(StringUtils.isNoneBlank(flightInfoMap.get("fno"))){
		        		lineFlight.setAirCompCode(flightInfoMap.get("fno").substring(0, 2));
		        	}
		        	lineFlight.setFlightNo(flightInfoMap.get("fno"));
		        	lineFlight.setDepartAirportCode(map.get("qf_citycode"));
		        	lineFlight.setDepartAirport(map.get("qf_simple")+"机场");
		        	lineFlight.setArriveAirportCode(map.get("dd_citycode"));
		        	lineFlight.setArriveAirport(map.get("dd_simple")+"机场");
		        	lineFlight.setAirComp(flightInfoMap.get("company"));
		        	if(StringUtils.isNoneBlank(flightInfoMap.get("fno"))){
		        		lineFlight.setAirCompCode(flightInfoMap.get("fno").substring(0, 2));
		        	}
		        	lineFlight.setDepartTime(map.get("jhqftime"));
		    		lineFlight.setDepTerminal(map.get("qfTerminal"));
		        	lineFlight.setArriveTime(map.get("jhddtime"));
		    		lineFlight.setArrTerminal(map.get("ddTerminal"));
		    		lineFlight.setBoardingGate(map.get("xlzp"));
	    			lineFlight.setDutyCounter(map.get("zjgt"));
	    			lineFlight.setBoardingGate(map.get("djk"));
	    			lineFlightDao.insertNotNull(lineFlight);
	    			if(departAirport != null && destAirport != null ){
		        		if(originalMap.get("departPlaceCode").equals(departAirport.getCityCode()) 
		    					&& originalMap.get("destPlaceCode").equals(destAirport.getCityCode())){
		    				returnAirLineFlight = lineFlight;
		    			}
		        	}
	        	}
	        	// 说明含有经停的情况 需要将最初的出发城市，和最后的到达城市存储到数据库中
	        	if(list != null && list.size()>1){
	        		Map<String, String> firstMap = list.get(0);
	        		Map<String, String> secondMap = list.get(1);
	        		AirLine airLine = null;
	        		Map<String, String> lineSearchMap = new HashMap<>();
	        		DictAirport departAirport = DictSource.AIRPORT_CODE_MAP.get(flightInfoMap.get("form_code"));
		        	DictAirport destAirport = DictSource.AIRPORT_CODE_MAP.get(flightInfoMap.get("to_code"));
		        	AirLineFlight lineFlight = new AirLineFlight();
		        	if(departAirport != null && destAirport != null ){
		        		String departFullTime = flightInfoMap.get("qftime_full");
		        		String arriveFullTime = flightInfoMap.get("ddtime_full");
		        		if(StringUtils.isNoneBlank(arriveFullTime)){
		        			Date commonDate = DateUtil.string2TimeZone(arriveFullTime, departAirport.getTimeZone(), destAirport.getTimeZone());
		        			long flyTime = commonDate.getTime()-DateUtil.toFormatDate(departFullTime, "yyyy-MM-dd HH:mm").getTime();
		        			lineFlight.setFlyTime(String.valueOf(flyTime));
		        		}
		        		lineSearchMap.put("departCode", flightInfoMap.get("form_code"));
		        		lineSearchMap.put("destCode", flightInfoMap.get("to_code"));
				        airLine =  airLineDao.findBySearchMap(lineSearchMap);
	        		}
		        	if(departAirport != null ){
		        		lineFlight.setDepartPlace(departAirport.getCityName());
		        		lineFlight.setDepartPlaceCode(departAirport.getCityCode());
		        	}
		        	if(destAirport != null ){
		        		lineFlight.setDestPlace(destAirport.getCityName());
		        		lineFlight.setDestPlaceCode(destAirport.getCityCode());
		        	}
		        	if(airLine == null ){
		        		airLine = new AirLine();
		        		if(departAirport != null ){
		        			airLine.setDepartPlace(departAirport.getCityName());
		        			airLine.setDepartPlaceCode(departAirport.getCityCode());
			        	}
			        	if(destAirport != null ){
			        		airLine.setDestPlace(destAirport.getCityName());
			        		airLine.setDestPlaceCode(destAirport.getCityCode());
			        	}
			        	airLineDao.insertNotNull(airLine);
		        	}
		        	lineFlight.setAirLineId(airLine.getId());
		        	lineFlight.setAirComp(flightInfoMap.get("company"));
		        	if(StringUtils.isNoneBlank(flightInfoMap.get("fno"))){
		        		lineFlight.setAirCompCode(flightInfoMap.get("fno").substring(0, 2));
		        	}
		        	lineFlight.setDepartAirport(flightInfoMap.get("from_simple")+"机场");
		        	lineFlight.setDepartAirportCode(flightInfoMap.get("form_code"));
		        	lineFlight.setArriveAirport(flightInfoMap.get("to_simple")+"机场");
		        	lineFlight.setArriveAirportCode(flightInfoMap.get("to_code"));
		        	
		    		lineFlight.setFlightNo(flightInfoMap.get("fno"));
		    		lineFlight.setAirLineId(airLine.getId());
		    		lineFlight.setArriveTime(flightInfoMap.get("ddtime"));
		    		lineFlight.setArrTerminal(flightInfoMap.get("toTerminal"));
		    		lineFlight.setDepartTime(flightInfoMap.get("qftime"));
		    		lineFlight.setDepTerminal(flightInfoMap.get("fromTerminal"));
		    		// 经停的相关信息
		    		lineFlight.setIsMiddleStop((byte)1);
	    			lineFlight.setMiddleCity(secondMap.get("qf_city"));
	    			lineFlight.setMiddleAirport(secondMap.get("qf"));
	    			lineFlight.setMiddleAirportCode(secondMap.get("qf_citycode"));
	    			// 到达经停机场时间
	    			lineFlight.setMiddleArriveTime(firstMap.get("jhddtime"));
	    			// 
	    			DictAirport middle_city_airport = DictSource.AIRPORT_CODE_MAP.get(secondMap.get("qf_citycode"));
	    			lineFlight.setMiddleCityCode(secondMap.get("qf_citycode"));
	    			lineFlight.setMiddleAirport(secondMap.get("qf_simple"));
	    			if(middle_city_airport != null ){
	    				lineFlight.setMiddleCityCode(middle_city_airport.getCityCode());
	    				lineFlight.setMiddleCity(middle_city_airport.getCityName());
	    			}
	    			lineFlight.setArrTerminal(secondMap.get("toTerminal"));
	    			lineFlight.setMiddleDepartTime(secondMap.get("jhqftime"));
	    			lineFlight.setBoardingGate(secondMap.get("xlzp"));
	    			lineFlight.setDutyCounter(secondMap.get("zjgt"));
	    			lineFlight.setBoardingGate(secondMap.get("djk"));
	    			lineFlightDao.insertNotNull(lineFlight);
	    			if(departAirport != null && destAirport != null ){
		        		if(originalMap.get("departPlaceCode").equals(departAirport.getCityCode()) 
		    					&& originalMap.get("destPlaceCode").equals(destAirport.getCityCode())){
		    				returnAirLineFlight = lineFlight;
		    			}
		        	}
	        	}
	        	return returnAirLineFlight;
			}else{
				logger.info("=================the juhe interface get someting wrong ===========");
			}
		return null;
	}
	
	/**
	 * 返回航线类别，国内，国际
	 * @param departAirport
	 * @param destAirport
	 * @return
	 */
	public Byte returnFlightType(DictAirport departAirport,DictAirport destAirport){
		Map<Byte, Byte> resultMap = new HashMap<>();
		if(departAirport != null ){
			resultMap.put(departAirport.getAbroad(), departAirport.getAbroad());
		}
		if(destAirport != null ){
			resultMap.put(destAirport.getAbroad(), destAirport.getAbroad());
		}
		if(resultMap.size()==2){
			return 1;
		}else if(resultMap.size()==1){
			if(resultMap.containsKey((byte)0)){
				return 0;
			}
			return 1;
		}
		return 0;
	}
	
}
