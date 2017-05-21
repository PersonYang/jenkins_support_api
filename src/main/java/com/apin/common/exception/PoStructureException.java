package com.apin.common.exception;
  
public class PoStructureException extends RuntimeException{  
  
    private static final long serialVersionUID = 1L;  
  
    public PoStructureException(String msg) {  
        super(msg);  
    }  
      
    public PoStructureException(String msg,Throwable e) {  
        super(msg,e);  
    }  
}  