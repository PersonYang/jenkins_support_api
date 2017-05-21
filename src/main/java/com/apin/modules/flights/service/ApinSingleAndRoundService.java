package com.apin.modules.flights.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apin.base.bean.DictAirport;
import com.apin.common.utils.DateUtil;
import com.apin.common.utils.ExcelUtil;
import com.apin.common.utils.FlightUtil;
import com.apin.common.utils.ValidateImportParamUtil;
import com.apin.modules.flights.bean.ApinJourneyIdSequence;
import com.apin.modules.flights.bean.ApinMerchantFlightDetailInfo;
import com.apin.modules.flights.bean.ApinMerchantFlightInfo;
import com.apin.modules.flights.bean.ApinMerchantFlightTurnInfo;
import com.apin.modules.flights.dao.ApinJourneyIdSequenceDao;
import com.apin.modules.flights.dao.ApinMerchantFlightDetailInfoDao;
import com.apin.modules.flights.dao.ApinMerchantFlightInfoDao;
import com.apin.modules.flights.dao.ApinMerchantFlightTurnInfoDao;

/**
 * excel 单程 往返导入
 * @author Young
 * @date 2016年10月18日
 */
@Service
public class ApinSingleAndRoundService {
	// 生成jouramyId 生成
	@Autowired	
	ApinJourneyIdSequenceDao journeyDao;
	// 航班转机
	@Autowired
	ApinMerchantFlightTurnInfoDao flightTurnDao;
	// 航班详细信息
	@Autowired
	ApinMerchantFlightDetailInfoDao flightDetailDao;
	// 航班主体信息
	@Autowired
	ApinMerchantFlightInfoDao flightMainDao;
	
	/**
	 * 
	 * 保存model_1即单程往返的航班信息
	 * @param row
	 * @param type  1, 属于推荐行程导入的航班，0,我的航线导入的航班
	 */
	//@Transactional(rollbackFor=Exception.class)
	public List<ApinMerchantFlightInfo> saveModelOneFlight(Row row,String suppliarId,String type,String groupId) throws Exception{
		List<ApinMerchantFlightInfo> mainFlightInfoList = new ArrayList<>();
		Byte routeType = returnRouteType(ExcelUtil.getCellValue(row.getCell(1)));
		Byte departHasTurn = FlightUtil.returnHasTurn(ExcelUtil.getCellValue(row.getCell(14)));
		// 单程  成航班信息
		// 去程出发日期
		String goDepartDate = ExcelUtil.getCellValue(row.getCell(7));
		String [] goDepartDateArray = goDepartDate.split(",");
		// 定义返程的出发日期
		String[] backdepartDateArray = null;
		String departAirCode = ExcelUtil.getCellValue(row.getCell(3));
		String arriveAirCode = ExcelUtil.getCellValue(row.getCell(4));
		if(2 == routeType) {
			// 返程出发时间
			String backDepartDate = ExcelUtil.getCellValue(row.getCell(11));
			// 有返程 将返程的出发时间取出
			backdepartDateArray = backDepartDate.split(",");
		}
		for (int i = 0; i < goDepartDateArray.length; i++) {
			// 封装一个航班主体信息
			// 每一次循环就是一个航班主体信息
			ApinMerchantFlightInfo mainFlightInfo = packageMainFlightInfo(row, suppliarId,goDepartDateArray[i]);
			mainFlightInfo.setGroupId(groupId);
			if("0".equals(type)){
				mainFlightInfo.setIsFareTicket((byte)0);
			}else if("1".equals(type)){
				mainFlightInfo.setIsFareTicket((byte)1);
			}
			// 需要生成一个journey_id
			ApinJourneyIdSequence journey = new ApinJourneyIdSequence();
			journeyDao.insertJourneyIdSequence(journey);
			mainFlightInfo.setJourneyId(journey.getJourneyId());
			// 去程航班的 具体信息
			ApinMerchantFlightDetailInfo departDetailFlightInfo = packageDetailFlightInfo(row, departAirCode, 
					arriveAirCode, goDepartDateArray[i],"1",journey.getJourneyId());
			departDetailFlightInfo.setParentId(0);
			Byte tripNumber =0;
			departDetailFlightInfo.setTripNumber(tripNumber);
			// 不可描述的flag
			/*if(flag ==null ){
				mainFlightInfo.setFlag((byte)0);
			}
			mainFlightInfo.setFlag(flag);*/
			// 航班类型 0.国内，1.国际
			mainFlightInfo.setFlightType(returnFlightType(departAirCode, arriveAirCode));
			flightMainDao.insert(mainFlightInfo);
			mainFlightInfoList.add(mainFlightInfo);
			departDetailFlightInfo.setHasTurn(departHasTurn);
			/*// 不可描述的flag
			departDetailFlightInfo.setFlag(flag);*/
			//保存去程信息
			flightDetailDao.insert(departDetailFlightInfo);
			// 去程  有转机信息
			if(1 == departHasTurn){
				ApinMerchantFlightTurnInfo departFlightTurnInfoOne =null;
				// 去程第一段转机的时间组
				String turnDepartDateOne = ExcelUtil.getCellValue(row.getCell(19));
				String [] turnDepartDateArrayOne = turnDepartDateOne.split(","); 
				if(turnDepartDateArrayOne.length == goDepartDateArray.length && StringUtils.isNoneBlank(turnDepartDateArrayOne[0])){
					if(departDetailFlightInfo != null ){
						departFlightTurnInfoOne = packageFlightTurnInfo("0", row, turnDepartDateArrayOne[i],
								departDetailFlightInfo.getAirComp(), departDetailFlightInfo.getFlightNo(),
								departDetailFlightInfo.getDepartTime(),departDetailFlightInfo.getDepartDate(),
								departDetailFlightInfo.getDepartPlaceCode());
						departFlightTurnInfoOne.setFlightId(departDetailFlightInfo.getId());
						flightTurnDao.insert(departFlightTurnInfoOne);
					}
				}
				// 去程第二次转机所有转机信息不为空 才转机
				if(ValidateImportParamUtil.validateTurnTwo(row, "0")){
					// 去程的第二段转机
					ApinMerchantFlightTurnInfo departFlightTurnInfoTwo =null;
					// 去程第二段转机的时间组
					String turnDepartDateTwo= ExcelUtil.getCellValue(row.getCell(24));
					String [] turnDepartDateArrayTwo = turnDepartDateTwo.split(","); 
					if(turnDepartDateArrayTwo.length == goDepartDateArray.length && StringUtils.isNoneBlank(turnDepartDateArrayTwo[0])){
						if(departFlightTurnInfoOne != null ){
							departFlightTurnInfoTwo = packageFlightTurnInfo("1", row, turnDepartDateArrayTwo[i],
									departDetailFlightInfo.getAirComp(), departDetailFlightInfo.getFlightNo(),
									departFlightTurnInfoOne.getDepartTime(),departFlightTurnInfoOne.getDepartDate(),
									departFlightTurnInfoOne.getTurnCityCode());
							departFlightTurnInfoTwo.setFlightId(departDetailFlightInfo.getId());
							flightTurnDao.insert(departFlightTurnInfoTwo);
						}
					}
				}
			}
			if(2 == routeType){
				// 若果有返程信息 返程航班的具体信息 
				// 并且 将 departAirCode 和 arriveAirCode 反过来传入
				ApinMerchantFlightDetailInfo destDetailFlightInfo = packageDetailFlightInfo(row,arriveAirCode,
						departAirCode, backdepartDateArray[i], "2",journey.getJourneyId());
				tripNumber =1;
				// 判断返程是否有转机
				destDetailFlightInfo.setTripNumber(tripNumber);
				destDetailFlightInfo.setParentId(departDetailFlightInfo.getId());
				// 返程是否有转机信息
				Byte destHasTurn = FlightUtil.returnHasTurn(ExcelUtil.getCellValue(row.getCell(25)));
				destDetailFlightInfo.setHasTurn(destHasTurn);
			/*	// 不可描述的flag
				if(flag ==null ){
					destDetailFlightInfo.setFlag((byte)0);
				}
				destDetailFlightInfo.setFlag(flag);*/
				//保存返程信息
				flightDetailDao.insert(destDetailFlightInfo);
				// 返程有转机信息
				if(1 == destHasTurn){
					ApinMerchantFlightTurnInfo destFlightTurnInfoOne =null;
					// 返程第一段转机的时间组
					String turnDestDateOne = ExcelUtil.getCellValue(row.getCell(30));
					String [] turnDestDateArrayOne = turnDestDateOne.split(","); 
					if(turnDestDateArrayOne.length == goDepartDateArray.length && StringUtils.isNoneBlank(turnDestDateArrayOne[0])){
						if(destDetailFlightInfo != null ){
							destFlightTurnInfoOne = packageFlightTurnInfo("2", row, turnDestDateArrayOne[i],
									destDetailFlightInfo.getAirComp(), destDetailFlightInfo.getFlightNo(),
									destDetailFlightInfo.getDepartTime(),destDetailFlightInfo.getDepartDate(),
									destDetailFlightInfo.getDepartPlaceCode());
							destFlightTurnInfoOne.setFlightId(destDetailFlightInfo.getId());
							flightTurnDao.insert(destFlightTurnInfoOne);
						}
					}
					// 去程的第二段转机
					if(ValidateImportParamUtil.validateTurnTwo(row, "1")){
						ApinMerchantFlightTurnInfo destFlightTurnInfoTwo =null;
						// 去程第二段转机的时间组
						String turnDestDateTwo= ExcelUtil.getCellValue(row.getCell(35));
						String [] turnDestDateArrayTwo = turnDestDateTwo.split(","); 
						if(turnDestDateArrayTwo.length == goDepartDateArray.length && StringUtils.isNoneBlank(turnDestDateArrayTwo[0])){
							if(destFlightTurnInfoOne != null ){
								destFlightTurnInfoTwo = packageFlightTurnInfo("3", row, turnDestDateArrayTwo[i],
										destFlightTurnInfoOne.getDepartAirComp(), destFlightTurnInfoOne.getDepartFlightNo(),
										destFlightTurnInfoOne.getDepartTime(),destFlightTurnInfoOne.getDepartDate(),
										destFlightTurnInfoOne.getTurnCityCode());
								destFlightTurnInfoTwo.setFlightId(destDetailFlightInfo.getId());
								flightTurnDao.insert(destFlightTurnInfoTwo);
							}
						}
					}
				}
			}
		}
		return mainFlightInfoList;
	}
	
	
	
	
	/**
	 * 封装一个航班主体信息 
	 * @return
	 */
	public ApinMerchantFlightInfo packageMainFlightInfo(Row row,String suppliarId,String departDate)throws Exception{
		ApinMerchantFlightInfo flightInfo = new ApinMerchantFlightInfo();
		flightInfo.setSupplierId(suppliarId);
		// 位置
		flightInfo.setCurrentTicketNum(Integer.valueOf(ExcelUtil.getCellValue(row.getCell(13))));
		flightInfo.setTotalPriceInctax(new BigDecimal(ExcelUtil.getCellValue(row.getCell(12))));
		// 单程 往返
		flightInfo.setRouteType(returnRouteType(ExcelUtil.getCellValue(row.getCell(1))));
		flightInfo.setUpdateTime(new Date());
		flightInfo.setCreateTime(new Date());
		Byte isShelves = 1;
		flightInfo.setIsShelves(isShelves);
		flightInfo.setIsAudit(isShelves);
		flightInfo.setValidTime(DateUtil.toFormatDate(departDate, "yyyyMMdd"));
		flightInfo.setVersion(1);
		return flightInfo;
	}

	/**
	 * 航班详情信息
	 * @param row
	 * @param departAirCode 出发机场代码
	 * @param arriveAirCode 抵达机场代码
	 * @param departDateStr 出发时间str
	 * @param type  类型 单程 1 ,往返 2,
	 * @return
	 */
	public ApinMerchantFlightDetailInfo packageDetailFlightInfo(Row row,String departAirCode,String arriveAirCode,
			String departDateStr,String type,Long journeyId)throws Exception{
		ApinMerchantFlightDetailInfo detailFlight = new ApinMerchantFlightDetailInfo();
		DictAirport departAirPort = FlightUtil.returnDictAirport(departAirCode);
		DictAirport arriveAirPort = FlightUtil.returnDictAirport(arriveAirCode);
		// 出发日期
		Date departDate = DateUtil.toFormatDate(departDateStr, "yyyyMMdd");
		// 抵达日期
		Date arriveDate = departDate;
		// 去程出发时间
	    String departTime = null; 
	    String arriveTime = null;
		if("1".equals(type)){
			departTime = DateUtil.formatDate(DateUtil.toFormatDate(ExcelUtil.getCellValue(row.getCell(5)), "HHmm"), "HH:mm");
			// 去程抵达时间
			arriveTime = ExcelUtil.getCellValue(row.getCell(6));
			// 航空公司
			detailFlight.setAirComp(FlightUtil.returnAirComp(ExcelUtil.getCellValue(row.getCell(2))));
			// 航班号
			detailFlight.setFlightNo(ExcelUtil.getCellValue(row.getCell(2)));
			// 返程的时间获取
		}else if("2".equals(type)){
			departTime = DateUtil.formatDate(DateUtil.toFormatDate(ExcelUtil.getCellValue(row.getCell(9)), "HHmm"), "HH:mm");
			arriveTime = ExcelUtil.getCellValue(row.getCell(10));
			//返程 航空公司
			detailFlight.setAirComp(FlightUtil.returnAirComp(ExcelUtil.getCellValue(row.getCell(8))));
			// 返程航班号
			detailFlight.setFlightNo(ExcelUtil.getCellValue(row.getCell(8)));
		}
		String newArriveTime=null;
		if (arriveTime.contains("+")) {
			arriveTime = arriveTime.substring(0, 4);
			// 时区情况
			if (FlightUtil.isDifferentTimeZone(departAirCode, arriveAirCode)) {
				arriveDate = DateUtil.changeDay(departDate, 1, true);
			} else {
				int differTime = DateUtil.getDiffTimeZoneRawOffset(FlightUtil.returnTimeZone(departAirCode),
						FlightUtil.returnTimeZone(arriveAirCode));
				if (differTime < (12 * 1000 * 60 * 60)) {
					arriveDate = DateUtil.changeDay(departDate, 1, true);
				}
			}
			newArriveTime =  DateUtil.formatDate(DateUtil.toFormatDate(arriveTime, "HHmm"), "HH:mm");
			arriveTime = DateUtil.formatDate(DateUtil.toFormatDate(arriveTime, "HHmm"), "HH:mm");
			detailFlight.setArriveTime(newArriveTime);
		}else{
			arriveTime = DateUtil.formatDate(DateUtil.toFormatDate(arriveTime, "HHmm"), "HH:mm");
			detailFlight.setArriveTime(arriveTime);
		}
		departDateStr = DateUtil.dateToStr(DateUtil.toFormatDate(departDateStr, "yyyyMMdd"), "yyyy-MM-dd")+" "+departTime;
		if (!FlightUtil.isDifferentTimeZone(departAirCode, arriveAirCode)){
			// 需要将时间转为同一个时区的时间在进行计算
			departDateStr = DateUtil.dateToStr(DateUtil.string2TimeZone(departDateStr,
					FlightUtil.returnTimeZone(departAirCode), FlightUtil.returnTimeZone(arriveAirCode)), "yyyy-MM-dd HH:mm");
		}
		String arriveDateStr = DateUtil.formatDate(arriveDate, "yyyy-MM-dd") +" "+arriveTime;
		String flyTime = DateUtil.redurnStayTime(departDateStr, arriveDateStr, "yyyy-MM-dd HH:mm", "hm");
		if (departAirPort != null) {
			// 出发机场
			detailFlight.setDepartAirport(departAirPort.getAirportName());
			// 出发地代码
			detailFlight.setDepartPlaceCode(departAirPort.getCityCode());
			// 出发地名称
			detailFlight.setDepartPlace(departAirPort.getCityName());
		}
		if(arriveAirPort != null ){
			// 目的地名称
			detailFlight.setDestPlace(arriveAirPort.getCityName());
			// 目的地代码
			detailFlight.setDestPlaceCode(arriveAirPort.getCityCode());
			// 目的地机场
			detailFlight.setArriveAirport(arriveAirPort.getAirportName());
		}
		detailFlight.setCabinName("经济舱");
		detailFlight.setCabinType((byte)0);
		detailFlight.setPlaneModel("0");
		detailFlight.setDepartDate(departDate);
		detailFlight.setDepartTime(departTime);
		detailFlight.setArriveDate(arriveDate);
		detailFlight.setJourneyId(journeyId);
		detailFlight.setFlyingTime(flyTime);
		return detailFlight;
	}
	/**
	 * 航班转机信息
	 * @param type  type 0:去程第一次转机，1：去程第二次转机，2：返程第一次转机。3：返程第二次转机。
	 * @param row 
	 * @param departDate 转机出发时间
	 * @param lastAirComp
	 * @param lastFlightNo
	 * @param lastDepartDate 上一段航程出发时间
	 * @param lastCityCode 上一段航程出发城市代码
	 * @return
	 */
	public ApinMerchantFlightTurnInfo packageFlightTurnInfo(String type,Row row,String departDate,String lastAirComp,
			String lastFlightNo,String lastDepartTime,Date lastDepartDate,String lastCityCode)throws Exception{
		ApinMerchantFlightTurnInfo  flightTurn = new ApinMerchantFlightTurnInfo();
		DictAirport airPort = null;
		String airComp = null;
		String flightNo = null;
		String departTime =null;
		String arriveAirportTime =null;
		Date allArriveTime = lastDepartDate;
		if("0".equals(type)){
			// 抵达转机机场时间
			arriveAirportTime = ExcelUtil.getCellValue(row.getCell(16));
			airPort = FlightUtil.returnDictAirport(ExcelUtil.getCellValue(row.getCell(15)));
			airComp = FlightUtil.returnAirComp(ExcelUtil.getCellValue(row.getCell(17)));
			flightNo =ExcelUtil.getCellValue(row.getCell(17));
			departTime = ExcelUtil.getCellValue(row.getCell(18));
		}else if("1".equals(type)){
			// 抵达转机机场时间
			arriveAirportTime = ExcelUtil.getCellValue(row.getCell(21));
			airPort = FlightUtil.returnDictAirport(ExcelUtil.getCellValue(row.getCell(20)));
			airComp = FlightUtil.returnAirComp(ExcelUtil.getCellValue(row.getCell(22)));
			flightNo =ExcelUtil.getCellValue(row.getCell(22)); 
			departTime = ExcelUtil.getCellValue(row.getCell(23));
		}else if("2".equals(type)){
			// 抵达转机机场时间
			arriveAirportTime = ExcelUtil.getCellValue(row.getCell(27));
			airPort = FlightUtil.returnDictAirport(ExcelUtil.getCellValue(row.getCell(26)));
			airComp = FlightUtil.returnAirComp(ExcelUtil.getCellValue(row.getCell(28)));
			flightNo =ExcelUtil.getCellValue(row.getCell(28)); 
			departTime = ExcelUtil.getCellValue(row.getCell(29));
		}else if("3".equals(type)){
			// 抵达转机机场时间
			arriveAirportTime =ExcelUtil.getCellValue(row.getCell(32));
			airPort = FlightUtil.returnDictAirport(ExcelUtil.getCellValue(row.getCell(31)));
			airComp = FlightUtil.returnAirComp(ExcelUtil.getCellValue(row.getCell(33)));
			flightNo =ExcelUtil.getCellValue(row.getCell(33)); 
			departTime = ExcelUtil.getCellValue(row.getCell(34));
		}
		if(departTime.contains("+")){
			departTime = DateUtil.formatDate(DateUtil.toFormatDate(departTime.substring(0, 4), "HHmm"), "HH:mm");
		}else{
			departTime = DateUtil.formatDate(DateUtil.toFormatDate(departTime, "HHmm"), "HH:mm");
		}
		String stayTime =null;
		String flyTime =null;
		if(arriveAirportTime.contains("+")){
			arriveAirportTime = DateUtil.formatDate(DateUtil.toFormatDate(arriveAirportTime.substring(0, 4), "HHmm"), "HH:mm");
			flightTurn.setArriveTime(arriveAirportTime);
			if(airPort != null){
				if(FlightUtil.isDifferentTimeZoneByCityCode(lastCityCode, airPort.getCityCode())){
					   allArriveTime = DateUtil.changeDay(lastDepartDate, 1, true);
					}else{
						int differTime = DateUtil.getDiffTimeZoneRawOffset(FlightUtil.returnTimeZoneByCityCode(lastCityCode),
								FlightUtil.returnTimeZoneByCityCode(airPort.getCityCode()));
						if (differTime < (12 * 1000 * 60 * 60)) {
							allArriveTime = DateUtil.changeDay(lastDepartDate, 1, true);
						}
					}
			}
		}else{
			arriveAirportTime = DateUtil.formatDate(DateUtil.toFormatDate(arriveAirportTime, "HHmm"), "HH:mm");
			flightTurn.setArriveTime(arriveAirportTime);
		}
		// 抵达转机机场时间
		String allArriveTimeStr = DateUtil.formatDate(allArriveTime, "yyyy-MM-dd")+" "+arriveAirportTime;
		/* String allDepartTimeStr = DateUtil.formatDate(lastDepartDate, "yyyy-MM-dd")+" "+departTime;*/
		// 转机出发时间
		String turnDepartDateStr = DateUtil.formatDate(DateUtil.toFormatDate(departDate, "yyyyMMdd"), "yyyy-MM-dd")+" "+departTime;
		if(StringUtils.isNoneBlank(allArriveTimeStr) && StringUtils.isNoneBlank(turnDepartDateStr)){
			stayTime = DateUtil.redurnStayTime(allArriveTimeStr, turnDepartDateStr, "yyyy-MM-dd HH:mm", "hm");
		}
		if(StringUtils.isNoneBlank(lastDepartTime) && StringUtils.isNoneBlank(arriveAirportTime)){
			flyTime = DateUtil.redurnStayTime(lastDepartTime, arriveAirportTime, "HH:mm", "hm");
		}
		flightTurn.setDepartAirComp(airComp);
		flightTurn.setDepartFlightNo(flightNo);
		if(airPort != null ){
			flightTurn.setTurnCity(airPort.getCityName());
			flightTurn.setTurnCityCode(airPort.getCityCode());
			flightTurn.setDepartAirport(airPort.getAirportName());
			flightTurn.setArriveAirport(airPort.getAirportName());
		}
		flightTurn.setArriveDate(allArriveTime);
		flightTurn.setArriveAirComp(lastAirComp);
		flightTurn.setArriveFlightNo(lastFlightNo);
		flightTurn.setDepartTime(departTime);
		flightTurn.setDepartDate(DateUtil.toFormatDate(departDate, "yyyyMMdd"));
		flightTurn.setStayTime(stayTime);
		flightTurn.setArriveFlyingTime(flyTime);
		return flightTurn;
	}
	
	public Byte returnRouteType(String cellValue){
		if("单程".equals(cellValue)){
			return 1;
		}else if("往返".equals(cellValue)){
			return 2;
		}else{
			return 1;
		}
	}
	
	/**
	 * 
	 * 根据机场代码 返回 航班属于国内 还是国际线路
	 * @param departCode
	 * @param destCode
	 * @return
	 */
	public Byte returnFlightType(String departCode,String destCode){
		Map<Byte, Byte> resultMap = new HashMap<>();
		DictAirport departAirport = FlightUtil.returnDictAirport(departCode);
		DictAirport destAirport = FlightUtil.returnDictAirport(destCode);
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
