package com.apin.common.utils;

import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author Young
 * @date 2016年5月27日
 * execl 操作相关的工具类
 */
public class ExcelUtil {

	 /**
     * 正则表达式：验证身份证
     */
    public static final String REGEX_ID_CARD = "(^\\d{18}$)|(^\\d{15}$)";
    public static final String REGEX_IS_NUM = "(^\\d{1}$)";
	 
	 /**
	  * 判断证件类型 如果不是规定的类型 返回false
	 * @param type
	 * @return boolean
	 */
	public static boolean judgeCertType(String type){
		 boolean flag = false;
		 if(StringUtils.isNumeric(type)){
			 int key = Integer.valueOf(type);
			 switch (key) {
			case 1: 
				flag = true;
				break;
			case 2: 
				flag = true;
				break;
			case 3: 
				flag = true;
				break;
			case 4: 
				flag = true;
				break;
			case 5: 
				flag = true;
				break;
			}
		 }
		 return flag;
	 }
	
	/**
	 * 乘客类型 成人 儿童
	 * @param type
	 * @return
	 */
	public static boolean judgeUserType(String type){
		boolean flag = false;
		if(StringUtils.isNumeric(type)){
			int key = Integer.valueOf(type);
			switch (key) {
			case 1: 
				flag = true;
				break;
			case 2: 
				flag = true;
				break;
			}
		}
		return flag;
	}
	/**
	 * 性别
	 * @param type
	 * @return
	 */
	public static boolean judgeUserGender(String type){
		boolean flag = false;
		if(StringUtils.isNumeric(type)){
			int key = Integer.valueOf(type);
			switch (key) {
			case 0: 
				flag = true;
				break;
			case 1: 
				flag = true;
				break;
			}
		}
		return flag;
	}
	
	
	/**
	 * 对excel进行解析工作 返回excel中的sheet
	 * @param filePath
	 * @param position sheet 位置
	 * @return Sheet
	 */
	public static Map<String, Object> importExcel(FileInputStream fileInput,String fileName,int position){
		Map<String, Object> resultMap = new HashMap<>();
		Sheet sheet = null;
		try {
			//FileInputStream fileInput = new FileInputStream(file);
			// 获取文件输入流
			Workbook workBook = null ;
			// 验证数据格式
			if(ExcelUtil.validateExcel(fileName)){
				// 2003
				if(ExcelUtil.isExcel2003(fileName)){
					workBook = new HSSFWorkbook(fileInput);  
				}else{
					workBook = new XSSFWorkbook(fileInput);
				}
				// 获取
				sheet = workBook.getSheetAt(position);	
				resultMap.put("status", "success");
			}else{
				resultMap.put("status", "false");
				resultMap.put("msg", "上传文件不符合格式");
			}
		} catch (Exception e) {
			resultMap.put("status", "false");
			e.printStackTrace();
			return resultMap;
		}
		resultMap.put("sheet", sheet);
		return resultMap;
	}
	
	
	
	
	/**
	 * 判断文件是excel2003
	 * @param filePath
	 * @return boolean
	 */
	public static boolean isExcel2003(String filePath){
		return filePath.matches("^.+\\.(?i)(xls)$");
	}
	/**
	 * 判断文件是excel2007
	 * @param filePath
	 * @return boolean
	 */
	public static boolean isExcel2007(String filePath){
		return filePath.matches("^.+\\.(?i)(xlsx)$");
	}
	
    /**
     * validateExcel
     * @param filePath
     * @return 
     */
    public static boolean validateExcel(String filePath)  
    {  
  
        /** 检查文件名是否为空或者是否是Excel格式的文件 */  
        if (filePath == null || !(isExcel2003(filePath) || isExcel2007(filePath)))  
        {  
            return false;  
        }  
      /*  *//** 检查文件是否存在 *//*  
        File file = new File(filePath);  
        if (file == null || !file.exists())  
        {  
            return false;  
        }  */
        return true;  
    } 
	
    
    /**
     * 校验身份证
     * 
     * @param idCard
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isIDCard(String idCard) {
        return Pattern.matches(REGEX_ID_CARD, idCard);
    }
	
    /**
     * 校验数字
     * 
     * @param strNum
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isNumric(String strNum) {
    	return Pattern.matches(REGEX_IS_NUM, strNum);
    }
    
    
    
    /**
     * 判断 cellType
     * 并返回cellValue
     * @param cell
     * @return
     */
    public static String getCellValue(Cell cell) {  
        String cellValue = "";  
        DecimalFormat df = new DecimalFormat("#");  
        switch (cell.getCellType()) {  
        case Cell.CELL_TYPE_STRING:  
            cellValue = cell.getRichStringCellValue().getString().trim();  
            break;  
        case Cell.CELL_TYPE_NUMERIC:  
            cellValue = df.format(cell.getNumericCellValue()).toString();  
            break;  
        case Cell.CELL_TYPE_BOOLEAN:  
            cellValue = String.valueOf(cell.getBooleanCellValue()).trim();  
            break;  
        case Cell.CELL_TYPE_FORMULA:  
            cellValue = cell.getCellFormula();  
            break;  
        default:  
            cellValue = "";  
        }  
        return cellValue;  
    }  
    
	
	
	
}
