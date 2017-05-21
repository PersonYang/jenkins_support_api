package com.apin.common.interceptor;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.apin.common.response.ErrorEnum;
import com.apin.common.utils.Redis;
import com.apin.common.utils.RedisType;

/**
 * 登录拦截验证
 * @author Young
 * @date 2017年2月4日 下午5:36:51
 * @version 1.0 
 */
public class LoginInterceptor implements HandlerInterceptor {

	public static String [] urls={"/api/web/support/routes/admin/offer/price",
			"/api/web/support/orders/admin/push",
			"/api/web/support/flight/admin/publish",
			"/api/web/airline/info",
			"/api/web/support/sys/thread"
			};
	private final static Logger logger= LoggerFactory.getLogger(LoginInterceptor.class);
	
	@Autowired
	Redis redis; 
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String requestURL = request.getRequestURI();
		for (int i = 0; i < urls.length; i++) {
			if(urls[i].equals(requestURL)){
				return true;
			}
		}
		long requestTime = System.currentTimeMillis();
		String userId = request.getHeader("userId");
		String token = request.getParameter("token");
		String deviceToken;
		try {
			deviceToken = redis.hget(RedisType.client, userId,"deviceToken");
			logger.info("=======get the userinfo deviceToken from redis "+deviceToken+"==============");
			if(StringUtils.isNoneBlank(userId) && StringUtils.isNoneBlank(token) &&
					StringUtils.isNoneBlank(deviceToken)  && deviceToken.equals(token)){
				return true;
			}else{
				writeResponse(response, requestTime);
			}
		} catch (Throwable e) {
			writeResponse(response, requestTime);
		}
		return false;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		
	}
	
	  public void writeResponse(HttpServletResponse response,long startTime){
	        response.setContentType("application/json;charset=UTF-8");
	        response.setCharacterEncoding("utf-8");
	        PrintWriter printWriter=null;
	        try{
	            printWriter=response.getWriter();
	            JSONObject responseJson=new JSONObject();
	            JSONObject responseHead=new JSONObject();
	            JSONObject responseBody=new JSONObject();
	            responseHead.put("code", ErrorEnum.ERROR_FAILED_LOGIN.getErrorCode());
	            responseHead.put("msg",ErrorEnum.ERROR_FAILED_LOGIN.getErrorMsg());
	            long currentTime=System.currentTimeMillis();
	            long delayInMs=currentTime-startTime;
	            float delayInS=(float)delayInMs/1000;
	            String delayStr=String.valueOf(delayInS);
	            responseHead.put("time",delayStr);
	            responseJson.put("head",responseHead);
	            responseJson.put("body",responseBody);
	            printWriter.append(responseJson.toString());
	        }catch (Throwable e){
	            logger.error("Throwable error info:");
	        }finally {
	            if(printWriter!=null){
	                printWriter.close();
	            }
	        }
	    }
	

}
