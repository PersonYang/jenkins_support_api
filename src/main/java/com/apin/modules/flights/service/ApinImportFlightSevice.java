package com.apin.modules.flights.service;

import java.io.FileInputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apin.common.response.ApinResponse;
import com.apin.common.response.ApinResponseUtil;
import com.apin.common.response.ErrorEnum;
import com.apin.common.utils.ExcelUtil;


/**
 * 供应商表格航班导入
 * @author Young
 * @date 2017年2月4日 上午9:29:52
 * @version 1.0 
 */
@Service
public class ApinImportFlightSevice {

	
	private static final Logger logger = LoggerFactory.getLogger(ApinImportFlightSevice.class);
	
	// 联程信息解析保存service
	@Autowired
	ApinUnionFlightService unionFlightService;
	@Autowired
	ApinSingleAndRoundService singleAndRoundService;
	/**
	 * 批量导入航班信息
	 * @param fileInput
	 * @param client
	 * @param filename
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ApinResponse<Map<String, Object>> importExecel(FileInputStream fileInput,String userId,
			String fileName) {
		ApinResponse<Map<String, Object>> apinResponse = null;
		Date nowDate = new Date();
		Map<String, Object> resultMap = new HashMap<>();
		// 获取sheet2 中的数据
		Map<String, Object> excleMap = ExcelUtil.importExcel(fileInput,fileName,0);
		Sheet sheet = (Sheet) excleMap.get("sheet");
		String status = (String) excleMap.get("status");
		if("success".equals(status) && sheet != null ){
			try {
				apinResponse = ApinResponseUtil.good(nowDate.getTime());
				logger.info("====lastRowNum=="+sheet.getLastRowNum());
				String modelType =null;
				Row row=null;
				Map<String, Object> paramMap = new HashMap<>();
				for (int j = 0; j <= sheet.getLastRowNum(); j++) {
					row = sheet.getRow(j);
					if (row == null || row.getCell(1) == null) {
						break;
					}
					if (j == 0) {
						modelType = ExcelUtil.getCellValue(row.getCell(0));
					}
					if (StringUtils.isBlank(modelType)) {
						break;
					}
					logger.info("=============the model_1 starting get flightInfo from the sheet ======="
							+ ExcelUtil.getCellValue(row.getCell(0)));
						// 单程 往返 模板
						if ("model_1".equals(modelType)) {
							if (StringUtils.isNumeric(ExcelUtil.getCellValue(row.getCell(0)))) {
								logger.info("=============the model_1 starting get flightInfo from the sheet ======="
										+ ExcelUtil.getCellValue(row.getCell(0)));
								// 保存模板 一  的航班信息单程 往返
								String groupId = UUID.randomUUID().toString();
								singleAndRoundService.saveModelOneFlight(row, userId,"0",groupId);
							}
						} else if ("model_2".equals(modelType)) {
							// 联程模板
							if(j<=2){
								continue;
							}
							String groupId = UUID.randomUUID().toString();
							// 保存模板2 的航班信息 联程航班
							// 为了某些不可描述  添加的flag
							unionFlightService.saveModelTwoFlight(row,userId,sheet,j,row.getCell(0).getColumnIndex(),paramMap,"0",groupId);
					   }
					}
				// 将rightApinList 数据存储到数据库中
				resultMap.put("status", true);
				resultMap.put("message", "导入成功");
			} catch (Exception e) {
				apinResponse = ApinResponseUtil.bad(nowDate.getTime(), ErrorEnum.ERROR_INTERNAL_SERVER_ERROR);
				e.printStackTrace();
				resultMap.put("status", false);
				resultMap.put("message", "导入失败，请检查数据文件是否复合模板规则");
				logger.info("=============the excel to sheet is right,but getting data from sheet is in trouble==================");
				return apinResponse;
			}
			apinResponse.setBody(resultMap);
			return apinResponse;
		}else{
			apinResponse = ApinResponseUtil.bad(nowDate.getTime(),ErrorEnum.ERROR_INTERNAL_SERVER_ERROR);
			resultMap.put("status", false);
			resultMap.put("msg", "导入失败，请检查数据文件是否复合模板规则");
			logger.info("=============the excel to sheet is in trouble ==================");
			apinResponse.setBody(resultMap);
			return apinResponse;
		}
	}
}
