<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>  
<%  
String path = request.getContextPath();  
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";  
%>  
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">  
<html>  
  <head>  
    <base href="<%=basePath%>">  
    <title>DWR  DEMO</title>  
    <meta http-equiv="pragma" content="no-cache">  
    <meta http-equiv="cache-control" content="no-cache">  
    <meta http-equiv="expires" content="0">      
    <meta http-equiv="keywords" content="keyword1,keyword2,keyword3">  
    <meta http-equiv="description" content="This is my page">  
  </head>  
  <script typet="text/javascript" src="http://libs.baidu.com/jquery/1.9.1/jquery.min.js"></script>
 
  <script type="text/javascript">
  	$(function(){
  	//跨域（可跨所有域名）
  	//alert("hello");
  		
  		$.ajax({
  			 url: "http://192.168.1.40:8078/api/web/support/flight/detail?journeyId=76883&jsoncallback=?",
  			 type: "GET",
  			 data: {},
  			 success: function(json){
  				 
  			 },
  			 dataType: "json",
  			 beforeSend: function(request) {
                 request.setRequestHeader("applicationId", "123");
             },
  			});
  		
  	});
  
  </script>
    
  <body >   
    This is my DWR DEOM page. <hr>  
    <br>  
    <div id="DemoDiv">demo</div>  
  </body>  
</html>  