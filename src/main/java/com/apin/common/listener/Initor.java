package com.apin.common.listener;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.apin.common.init.Initializable;

/**
 * 
 * 初始化  listener
 * @author Young
 *
 */
@Component  
public class Initor implements ApplicationListener<ContextRefreshedEvent>{
	
   @Autowired(required=false)   
   List<Initializable> initors;
	public void onApplicationEvent(ContextRefreshedEvent event) {
		 if(null==initors)   
	        {   
	            return;   
	        }   
	        if(event instanceof ContextRefreshedEvent)   
	        {
	            if(event.getApplicationContext().getParent() == null){
	                for(Initializable initor:initors)
	                {
	                    initor.init();
	                }
	            }
	        }
	}

}
