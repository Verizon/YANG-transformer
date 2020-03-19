/*
Copyright Verizon Inc. 
Licensed under the terms of the Apache License 2.0 license.  See LICENSE file in project root for terms.
*/
package com.vzw.yang.services;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;


/**********************************************************************
 ** Process responses from CAMEO
 **********************************************************************/
@Service("transformerStressService")
public class TransformerStressService {

    private static final Logger logger = LogManager.getLogger(TransformerStressService.class);

    private long numThreads = 1;

    public TransformerStressService() {
        logger.debug("TransformerStressService: Entered");

        logger.debug("TransformerStressService: Exited");

    }

    @Autowired
    ThreadPoolTaskExecutor taskExecutor;

    //@PostConstruct
    public void init() {

    	List<TransactionStatus> statuses = new ArrayList<TransactionStatus>();
        logger.info("TransformerStressService.init: Entered");
        
        do {

            for (int i = 0; i < numThreads; i++) {
            	TransactionStatus status = new TransactionStatus();
            	status.setName("Stress Tester: " + i);
            	statuses.add(status);
                taskExecutor.execute(new TransformerStressTester(status));
            }
            
            taskExecutor.execute(new TransformerDisplayCount(statuses));
            /*
            try {
            	while (true) {
            		try {
            			Thread.sleep(60 * 1000);
            		} catch(Exception e) {
            			e.printStackTrace();
            		}
            		int totalTrans = 0;
            		for (TransactionStatus st : statuses) {
            			totalTrans = totalTrans + st.getCounter();
            			st.resetCounter();
            		}
            		logger.info("TransformerStressService.init: Total Transactions per minute is: " + totalTrans);
            	}
            } catch(Exception e) {
                logger.error("TransformerStressService.init: Error waiting on threads: " + e.getMessage());
            	e.printStackTrace();
            }
            */
            //logger.info("TransformerStressService.init: Done waiting for threads to complete");
            
        } while (false);
        logger.info("TransformerStressService.init: Exited: ");

    }
    

}
