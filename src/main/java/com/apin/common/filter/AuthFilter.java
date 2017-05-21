package com.apin.common.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.apin.base.bean.ApinApplication;
import com.apin.base.dao.ApplicationMapper;
import com.apin.common.response.ErrorEnum;

/**
 * Created by Administrator on 2017/1/11.
 */
public class AuthFilter implements Filter {

    private final static Logger logger= LoggerFactory.getLogger(AuthFilter.class);

    @Autowired
    private ApplicationMapper applicationMapper;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //do nothing
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    	long startTime=System.currentTimeMillis();
        HttpServletRequest httpServletRequest=(HttpServletRequest)request;

        String isWeb=httpServletRequest.getHeader("isWeb");
        String uri=httpServletRequest.getRequestURI();
        boolean filteredUri=true;
        if(uri.equalsIgnoreCase("/")){
            filteredUri=false;
        }
        Map<String, String> map = new HashMap<String, String>();
        Enumeration  headerNames = httpServletRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = httpServletRequest.getHeader(key);
            System.out.println("ket:"+key+"value:"+value);
        }
        if(filteredUri&&StringUtils.isBlank(isWeb)) {
 //       	String applicationId = httpServletRequest.getHeader("applicationid");
            String applicationId = httpServletRequest.getHeader("applicationId");
            String sign = httpServletRequest.getHeader("sign");
            if (StringUtils.isBlank(applicationId)) {
                writeResponse(response, startTime);
                return;
            }

            ApinApplication apinApplication = applicationMapper.selectApplicationById(applicationId);
            if (apinApplication == null) {
                writeResponse(response, startTime);
                return;
            }

            if ("POST".equalsIgnoreCase(httpServletRequest.getMethod())) {
                String secret = apinApplication.getApplicationSecret();
                MyRequestWrapper myRequestWrapper = new MyRequestWrapper(httpServletRequest);
                String body = MyRequestWrapper.getBodyString(myRequestWrapper);
                if (!StringUtils.isBlank(body)) {
                    JSONObject json = new JSONObject(body);
                    String signContent = getSignContent(json);
                    String signContentWithSecret = signContent + secret;
                    System.out.println("signed content:" + signContentWithSecret);
                    String md5Value = DigestUtils.md5Hex(signContentWithSecret);
                    System.out.println(md5Value);
                    if (!md5Value.equalsIgnoreCase(sign)) {
                        writeResponse(response, startTime);
                        return;
                    }
                }
                chain.doFilter(myRequestWrapper, response);
            } else {
                chain.doFilter(request, response);
            }
        }else{
            chain.doFilter(request,response);
        }

    }

    private String getSignContent(JSONObject params) {
        StringBuffer content = new StringBuffer();
        ArrayList keys = new ArrayList(params.keySet());
        Collections.sort(keys);

        for(int i = 0; i < keys.size(); ++i) {
            String key = (String)keys.get(i);
            String value = String.valueOf(params.get(key));
            content.append((i == 0?"":"&") + key + "=" + value);
        }

        return content.toString();
    }



    @Override
    public void destroy() {
        //do nothing
    }

    private void writeResponse(ServletResponse response,long startTime){
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("utf-8");
        PrintWriter printWriter=null;
        try{
            printWriter=response.getWriter();
            JSONObject responseJson=new JSONObject();
            JSONObject responseHead=new JSONObject();
            JSONObject responseBody=new JSONObject();
            responseHead.put("code", ErrorEnum.ERROR_FAILED_VERIFIED.getErrorCode());
            responseHead.put("msg",ErrorEnum.ERROR_FAILED_VERIFIED.getErrorMsg());
            long currentTime=System.currentTimeMillis();
            long delayInMs=currentTime-startTime;
            float delayInS=(float)delayInMs/1000;
            String delayStr=String.valueOf(delayInS);
            responseHead.put("time",delayStr);
            responseJson.put("head",responseHead);
            responseJson.put("body",responseBody);
            printWriter.write(responseJson.toString());
            printWriter.flush();
        }catch (Throwable e){
            logger.error("Throwable error info:");
        }finally {
            if(printWriter!=null){
                printWriter.close();
            }
        }
    }
}
