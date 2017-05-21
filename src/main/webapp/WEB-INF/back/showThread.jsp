<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>  
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">  
<html>  
  <head>  
    <title>服务器线程信息</title>  
  </head>  
<body >   
	<%
		for(Map.Entry<Thread,StackTraceElement[]> stackTrace : Thread.getAllStackTraces().entrySet()){
			Thread thread = (Thread)stackTrace.getKey();
			StackTraceElement [] stack = (StackTraceElement [])stackTrace.getValue();
			if(thread.equals(Thread.currentThread())){
				continue;
			}
			out.println("\n 线程"+thread.getName()+"\n");
			for(StackTraceElement element : stack){
				out.println("\t"+element+"\n");
			}
		}
	%>
</body>  
</html>  