package com.apin.common.exception;

public class TransactionExcetion extends RuntimeException{  
  
    private static final long serialVersionUID = 1L;  
  
    public TransactionExcetion(String msg) {  
        super(msg);  
    }  
      
    public TransactionExcetion(String msg,Throwable e) {  
        super(msg,e);  
    }  
}
