package com.apin.common.utils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
/**
 * 
 *航班导入数据验证
 * @author Young
 * @date 2016年10月21日
 */
public class ValidateImportParamUtil {
	/**
	 * 单程 往返参数验证
	 * @param sheet
	 * @param i 
	 * @return
	 */
	public static boolean validateSingleRound(Row row){
		if(StringUtils.isNoneBlank(ExcelUtil.getCellValue(row.getCell(1)))&&
			   StringUtils.isNoneBlank(ExcelUtil.getCellValue(row.getCell(2)))&&
			   StringUtils.isNoneBlank(ExcelUtil.getCellValue(row.getCell(3)))&&
			   StringUtils.isNoneBlank(ExcelUtil.getCellValue(row.getCell(4)))&&
			   StringUtils.isNoneBlank(ExcelUtil.getCellValue(row.getCell(5)))&&
			   StringUtils.isNoneBlank(ExcelUtil.getCellValue(row.getCell(6)))&&
			   StringUtils.isNoneBlank(ExcelUtil.getCellValue(row.getCell(7)))){
				return true;
			}
		return false;
	}
	
	/**
	 * 单程 往返
	 * 验证去程第二段或者 返程第二段的航班转机
	 * @param row 每行数据
	 * @param Type 0，去程；1，返程
	 * @return
	 */
	public static boolean validateTurnTwo(Row row,String Type){
		if("0".equals(Type)){
			if(StringUtils.isNoneBlank(ExcelUtil.getCellValue(row.getCell(20)))&&
					   StringUtils.isNoneBlank(ExcelUtil.getCellValue(row.getCell(21)))&&
					   StringUtils.isNoneBlank(ExcelUtil.getCellValue(row.getCell(22)))&&
					   StringUtils.isNoneBlank(ExcelUtil.getCellValue(row.getCell(23)))&&
					   StringUtils.isNoneBlank(ExcelUtil.getCellValue(row.getCell(24)))){
						return true;
			}
		}else if("1".equals(Type)){
			if(StringUtils.isNoneBlank(ExcelUtil.getCellValue(row.getCell(31)))&&
					   StringUtils.isNoneBlank(ExcelUtil.getCellValue(row.getCell(32)))&&
					   StringUtils.isNoneBlank(ExcelUtil.getCellValue(row.getCell(33)))&&
					   StringUtils.isNoneBlank(ExcelUtil.getCellValue(row.getCell(34)))&&
					   StringUtils.isNoneBlank(ExcelUtil.getCellValue(row.getCell(35)))){
						return true;
			}
		}
		return false;
	}
}
