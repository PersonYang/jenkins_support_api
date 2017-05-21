package com.apin.modules.flights.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apin.base.bean.DictAirport;
import com.apin.common.utils.DateUtil;
import com.apin.common.utils.ExcelUtil;
import com.apin.common.utils.FlightUtil;
import com.apin.modules.flights.bean.ApinJourneyIdSequence;
import com.apin.modules.flights.bean.ApinMerchantFlightDetailInfo;
import com.apin.modules.flights.bean.ApinMerchantFlightInfo;
import com.apin.modules.flights.bean.ApinMerchantFlightTurnInfo;
import com.apin.modules.flights.dao.ApinJourneyIdSequenceDao;
import com.apin.modules.flights.dao.ApinMerchantFlightDetailInfoDao;
import com.apin.modules.flights.dao.ApinMerchantFlightInfoDao;
import com.apin.modules.flights.dao.ApinMerchantFlightTurnInfoDao;


/**
 * excel 联程相关航班导入情况
 * @author Young
 * @date 2016年10月18日
 */
@Service
public class ApinUnionFlightService {
	
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
	 * @param row 解析后的每行数据
	 * @param client 当前登录用户
	 * @param type  1, 属于推荐行程导入的航班，0,我的航线导入的航班
	 */
	//@Transactional(rollbackFor=Exception.class)
	public List<ApinMerchantFlightInfo> saveModelTwoFlight(Row row, String userId,Sheet sheet,int j,
			int columnIndex,Map<String, Object> paramMap,String type,String groupId) throws Exception{
		List<ApinMerchantFlightInfo> mainFlightInfoList = new ArrayList<>();
		boolean isMergedRegion =  isMergedRegion(sheet, j, row.getCell(0).getColumnIndex());
		Byte hasTurn = returnHasTurn(ExcelUtil.getCellValue(row.getCell(10)));
		if(isMergedRegion){
			String proNum = ExcelUtil.getCellValue(row.getCell(0));
			if(StringUtils.isNumeric(proNum)){
				paramMap.put("tripNum", (byte)0);
				ApinJourneyIdSequence journeyIdSequence = new ApinJourneyIdSequence();
				journeyDao.insertJourneyIdSequence(journeyIdSequence);
				String departDate = ExcelUtil.getCellValue(row.getCell(4));
				ApinMerchantFlightInfo mainFlightInfo = packageMainFlightInfo(row, userId, departDate);
				mainFlightInfo.setJourneyId(journeyIdSequence.getJourneyId());
				mainFlightInfo.setFlightType(returnUnoinFlightType(proNum, sheet, j));
				mainFlightInfo.setGroupId(groupId);
				if("0".equals(type)){
					mainFlightInfo.setIsFareTicket((byte)0);
				}else if("1".equals(type)){
					mainFlightInfo.setIsFareTicket((byte)1);
				}
				flightMainDao.insert(mainFlightInfo);
				mainFlightInfoList.add(mainFlightInfo);
				paramMap.put("journeyId", journeyIdSequence.getJourneyId());
			 // new 一个航班主体信息	
			}
			String departAirCode = ExcelUtil.getCellValue(row.getCell(2)); 
			String destAirCode = ExcelUtil.getCellValue(row.getCell(3));
			ApinMerchantFlightDetailInfo detailFlightInfo =packageUnionFlightDetail(row,departAirCode,destAirCode, userId);
			if((byte)paramMap.get("tripNum")==(byte)0){
				detailFlightInfo.setParentId(0);
			}else{
				detailFlightInfo.setParentId((Integer)paramMap.get("parentId"));
			}
			detailFlightInfo.setHasTurn(hasTurn);
			detailFlightInfo.setTripNumber((byte)paramMap.get("tripNum"));
			detailFlightInfo.setJourneyId((Long)paramMap.get("journeyId"));
			flightDetailDao.insert(detailFlightInfo);
			paramMap.put("parentId", detailFlightInfo.getId());
			paramMap.put("tripNum", (byte)((byte)paramMap.get("tripNum")+(byte)1));
			// hasTurn == 0 没有转机,1有转机
		    if(hasTurn == 1){
		    	if(detailFlightInfo != null ){
		    		ApinMerchantFlightTurnInfo turnFlightInfo = packageFlightTurnInfo(row,detailFlightInfo.getAirComp(),
		    				detailFlightInfo.getFlightNo(), detailFlightInfo.getDepartTime(),
		    				detailFlightInfo.getDepartDate());
		    		turnFlightInfo.setFlightId(detailFlightInfo.getId());
		    		flightTurnDao.insert(turnFlightInfo);
		    	}
			}
		}
		return  mainFlightInfoList;
	}
	/**
	 * 封装联程航班
	 * @param row 每行数据 
	 * @param client  当前登录用户
	 * @return 
	 */
	public ApinMerchantFlightDetailInfo packageUnionFlightDetail(Row row,String departAirCode,String destAirCode,String userId) throws Exception{
		ApinMerchantFlightDetailInfo detailFlightInfo = new ApinMerchantFlightDetailInfo();
		DictAirport departAirPort = FlightUtil.returnDictAirport(departAirCode); //ExcelUtil.getCellValue(row.getCell(2))
		DictAirport arriveAirPort = FlightUtil.returnDictAirport(destAirCode);//ExcelUtil.getCellValue(row.getCell(3))
		//返程 航空公司
		detailFlightInfo.setAirComp(FlightUtil.returnAirComp(ExcelUtil.getCellValue(row.getCell(1))));
		detailFlightInfo.setFlightNo(ExcelUtil.getCellValue(row.getCell(1)));
		String arriveTime = ExcelUtil.getCellValue(row.getCell(7));
		if (departAirPort != null) {
			detailFlightInfo.setDepartAirport(departAirPort.getAirportName());
			detailFlightInfo.setDepartPlace(departAirPort.getCityName());
			detailFlightInfo.setDepartPlaceCode(departAirPort.getCityCode());
		}
		if (arriveAirPort != null) {
			// 目的地名称
			detailFlightInfo.setDestPlace(arriveAirPort.getCityName());
			// 目的地代码
			detailFlightInfo.setDestPlaceCode(arriveAirPort.getCityCode());
			// 目的地机场
			detailFlightInfo.setArriveAirport(arriveAirPort.getAirportName());
		}
		detailFlightInfo.setDepartTime(DateUtil.dateToStr(DateUtil.toFormatDate(ExcelUtil.getCellValue(row.getCell(5)), "HHmm"), "HH:mm"));
		String newArrive = null;
		if(arriveTime.contains("+")){
			arriveTime = arriveTime.substring(0, 4);
			newArrive = DateUtil.dateToStr(DateUtil.toFormatDate(arriveTime, "HHmm"), "HH:mm");
			detailFlightInfo.setArriveTime(newArrive+"+1");
		}else{
			newArrive = DateUtil.dateToStr(DateUtil.toFormatDate(arriveTime, "HHmm"), "HH:mm");
			detailFlightInfo.setArriveTime(newArrive);
		}
		detailFlightInfo.setDepartDate(DateUtil.toFormatDate( ExcelUtil.getCellValue(row.getCell(4)), "yyyyMMdd"));
		detailFlightInfo.setArriveDate(DateUtil.toFormatDate(ExcelUtil.getCellValue(row.getCell(6)), "yyyyMMdd"));
		String departDateStr = DateUtil.dateToStr(detailFlightInfo.getDepartDate(), "yyyy-MM-dd")+" "+detailFlightInfo.getDepartTime();
		String destDateStr = DateUtil.dateToStr(detailFlightInfo.getArriveDate(), "yyyy-MM-dd")+" "+newArrive;
		String flyIngTime =null;
		// 同一个时区的时间
		if(FlightUtil.isDifferentTimeZone(departAirCode, destAirCode)){
			flyIngTime = DateUtil.redurnStayTime(departDateStr, destDateStr, "yyyy-MM-dd HH:mm", "hm");
		}else{
			// 需要将时间转为同一个时区的时间在进行计算
			departDateStr = DateUtil.dateToStr(DateUtil.string2TimeZone(departDateStr,FlightUtil.returnTimeZone(departAirCode),
					FlightUtil.returnTimeZone(destAirCode)), "yyyy-MM-dd HH:mm");
			flyIngTime = DateUtil.redurnStayTime(departDateStr, destDateStr, "yyyy-MM-dd HH:mm", "hm");
		}
		detailFlightInfo.setFlyingTime(flyIngTime);
		return detailFlightInfo;
	}
	
	/**
	 * 封装一个航班主体信息 
	 * @return
	 */
	public ApinMerchantFlightInfo packageMainFlightInfo(Row row,String userId,String departDate) throws Exception{
		ApinMerchantFlightInfo flightInfo = new ApinMerchantFlightInfo();
		//flightInfo.setMerchantId(userId);
		flightInfo.setSupplierId(userId);
		// 位置
		flightInfo.setCurrentTicketNum(Integer.valueOf(ExcelUtil.getCellValue(row.getCell(9))));
		flightInfo.setTotalPriceInctax(new BigDecimal(ExcelUtil.getCellValue(row.getCell(8))));
		// 单程 往返
		flightInfo.setRouteType((byte)3);
		flightInfo.setUpdateTime(new Date());
		flightInfo.setCreateTime(new Date());
		flightInfo.setIsShelves((byte)1);
		flightInfo.setIsAudit((byte)1);
		flightInfo.setValidTime(DateUtil.toFormatDate(departDate, "yyyyMMdd"));
		flightInfo.setVersion(1);
		return flightInfo;
	}
	
	
	
	/**
	 * 航班转机信息
	 * @param type  type 0:去程第一次转机，1：去程第二次转机，2：返程第一次转机。3：返程第二次转机。
	 * @param row 
	 * @param departDate 转机出发时间
	 * @param lastAirComp
	 * @param lastFlightNo
	 * @return
	 */
	public ApinMerchantFlightTurnInfo packageFlightTurnInfo(Row row,String lastAirComp,
			String lastFlightNo,String lastDepartTime,Date lastDepartDate) throws Exception{
		ApinMerchantFlightTurnInfo flightTurn = new ApinMerchantFlightTurnInfo();
		DictAirport airPort = null;
		String airComp = null;
		String flightNo = null;
		String departTime = null;
		// 抵达转机城市日期
		Date arriveDate = lastDepartDate;
		String departDate = ExcelUtil.getCellValue(row.getCell(15));
		// 抵达转机机场时间
		String arriveAirportTime = ExcelUtil.getCellValue(row.getCell(12));
		Date turnDepartDate = DateUtil.toFormatDate(departDate, "yyyyMMdd");
		String newarriveAirportTime = null;
		if (arriveAirportTime.contains("+")) {
			arriveAirportTime = DateUtil.formatDate(DateUtil.toFormatDate(arriveAirportTime.substring(0, 4), "HHmm"), "HH:mm");
			newarriveAirportTime =arriveAirportTime + "+1";
		} else {
			arriveAirportTime = DateUtil.formatDate(DateUtil.toFormatDate(arriveAirportTime, "HHmm"), "HH:mm");
			newarriveAirportTime = arriveAirportTime;
		}
		// 转机机场
		airPort = FlightUtil.returnDictAirport(ExcelUtil.getCellValue(row.getCell(11)));
		// 转机航空公司
		airComp = FlightUtil.returnAirComp(ExcelUtil.getCellValue(row.getCell(13)));
		flightNo = ExcelUtil.getCellValue(row.getCell(13));
		departTime = ExcelUtil.getCellValue(row.getCell(14));
		String newturnDepartTime=null;
		if (departTime.contains("+")) {
			departTime = DateUtil.formatDate(DateUtil.toFormatDate(departTime.substring(0, 4), "HHmm"), "HH:mm");
			arriveDate = DateUtil.changeDay(turnDepartDate, 1, false);
			newturnDepartTime = departTime+"+1";
		} else {
			departTime = DateUtil.formatDate(DateUtil.toFormatDate(departTime, "HHmm"), "HH:mm");
			arriveDate = turnDepartDate;
			newturnDepartTime = departTime;
		}
		String stayTime = null;
		//String flyTime = null;
		String arriveTurnTimeStr = DateUtil.formatDate(arriveDate, "yyyy-MM-dd")+" "+arriveAirportTime;
		String departTurnTimeStr = DateUtil.formatDate(turnDepartDate, "yyyy-MM-dd")+" "+departTime;
		if (StringUtils.isNoneBlank(arriveTurnTimeStr) && StringUtils.isNoneBlank(departTurnTimeStr)) {
			stayTime = DateUtil.redurnStayTime(arriveTurnTimeStr, departTurnTimeStr, "yyyy-MM-dd HH:mm", "hm");
		}
		/*if (StringUtils.isNoneBlank(lastDepartTime) && StringUtils.isNoneBlank(arriveAirportTime)) {
			flyTime = DateUtil.redurnStayTime(lastDepartTime, arriveAirportTime, "HH:mm", "hm");
		}*/
		flightTurn.setDepartAirComp(airComp);
		flightTurn.setDepartFlightNo(flightNo);
		if (airPort != null) {
			flightTurn.setTurnCity(airPort.getCityName());
			flightTurn.setTurnCityCode(airPort.getCityCode());
			flightTurn.setDepartAirport(airPort.getAirportName());
			flightTurn.setArriveAirport(airPort.getAirportName());
		}
		flightTurn.setArriveDate(arriveDate);
		flightTurn.setArriveAirComp(lastAirComp);
		flightTurn.setArriveFlightNo(lastFlightNo);
		flightTurn.setArriveTime(newarriveAirportTime);
		flightTurn.setDepartTime(newturnDepartTime);
		if(StringUtils.isNoneBlank(departDate)){
			flightTurn.setDepartDate(DateUtil.toFormatDate(departDate, "yyyyMMdd"));
		}
		flightTurn.setStayTime(stayTime);
	//	flightTurn.setArriveFlyingTime(flyTime);
		return flightTurn;
	}
	
	/**
	 * 是否是合并的单元格
	 * @param sheet sheet1的数据
	 * @param row 每行数据
	 * @param column 
	 * @return
	 */
	public boolean isMergedRegion(Sheet sheet,int row ,int column) {  
		  int sheetMergeCount = sheet.getNumMergedRegions();  
		  for (int i = 0; i < sheetMergeCount; i++) {  
			CellRangeAddress range = sheet.getMergedRegion(i);  
			int firstColumn = range.getFirstColumn();  
			int lastColumn = range.getLastColumn();  
			int firstRow = range.getFirstRow();  
			int lastRow = range.getLastRow();  
			if(row >= firstRow && row <= lastRow){  
				if(column >= firstColumn && column <= lastColumn){  
			       return true;  
				}  
			}  
		  }  
		  return false;  
		}  
	
	
	
	/**
	 * 联程判断航班的 flightType 0，国内。1，国际
	 * @param firstValue
	 * @param sheet
	 * @param valueIndex
	 * @return
	 */
	public static Byte returnUnoinFlightType(String firstValue,Sheet sheet,int valueIndex){
		Map<Byte, Byte> resultMap = new HashMap<>();
		for (int j = valueIndex; j < sheet.getLastRowNum(); j++) {
			Row row = sheet.getRow(j);
			if (row == null || row.getCell(1) == null) {
				break;
			}
			// 当前的值不为空 并且与firstValue 不同时 break 循环
			if(StringUtils.isNoneBlank(ExcelUtil.getCellValue(row.getCell(0))) &&
					!firstValue.equals(ExcelUtil.getCellValue(row.getCell(0))) ){
				break;
			}else{
				Byte result = returnFlightType(ExcelUtil.getCellValue(row.getCell(2)), ExcelUtil.getCellValue(row.getCell(3)));
				resultMap.put(result, result);
			}
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
	/**
	 * 
	 * 根据机场代码 返回 航班属于国内 还是国际线路
	 * @param departCode
	 * @param destCode
	 * @return
	 */
	public static Byte returnFlightType(String departCode,String destCode){
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
	
	
	/**
	 * 是否有转机
	 * @param cellValue
	 * @return 0否，1是
	 */
	public Byte returnHasTurn(String cellValue) {
		if("是".equals(cellValue)){
			return 1;
		}else if("否".equals(cellValue)){
			return 0;
		}
		return 0;
	}
	
	
}
