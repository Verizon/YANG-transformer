/*
Copyright Verizon Inc. 
Licensed under the terms of the Apache License 2.0 license.  See LICENSE file in project root for terms.
*/
package com.vzw.yang.services;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Created by randy on 8/25/2017.
 */
public class TransformerDisplayCount implements Runnable {
    private static final Logger logger = LogManager.getLogger(TransformerDisplayCount.class);

    private List<TransactionStatus> statuses;

    public TransformerDisplayCount(List<TransactionStatus> transStatus) {
      statuses = transStatus;
    }
    
    @Override
    public void run() {

        logger.info("TransformerDisplayCount.run: is running");

        while (true) {
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
        		logger.info("TransformerDisplayCount.init: Total Transactions for " + statuses.size() + " threads per minute is: " + totalTrans);
        		System.out.println("TransformerDisplayCount.init: Total Transactions for " + statuses.size() + " threads per minute is: " + totalTrans);

        	}
        }

        //logger.info("TransformerDisplayCount.run: " + transactionStatus.getName() + " has ended");

    }

	private void prettyPrint(String json) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			logger.info("KafkaJsonService.test1: Have request");
			Object jsonObj = mapper.readValue(json, Object.class);
			logger.info(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObj));
		} catch(Exception e) {
			
		}

	}
    
    public static void main(String[] args) {
    	
    }

    private String testJsonStr = "{\"eventTime\": \"2019-03-23T02:50:00+00:00\", \"data\": [{\"valueUnit\": \"%\", \"typeId\": 0, \"typeValue\": \"2.066667\", \"valueType\": \"float\", \"typeName\": \"ControlCpuUsage\"}, {\"valueUnit\": \"%\", \"typeId\": 1, \"typeValue\": \"26.000000\", \"valueType\": \"float\", \"typeName\": \"MemoryUsage\"}, {\"valueUnit\": \"%\", \"typeId\": 2, \"typeValue\": \"10.000000\", \"valueType\": \"float\", \"typeName\": \"DiskUsage\"}, {\"valueUnit\": \"\u00b0C\", \"typeId\": 3, \"typeValue\": \"27.000000\", \"valueType\": \"float\", \"typeName\": \"Temperature\"}], \"annotatedFamilyId\": \"CPE_RESOURCE\", \"neId\": \"100\", \"indexes\": [{\"indexName\": \"CPE ID\", \"indexId\": 0, \"indexValue\": \"20dbab03f5ec\"}], \"neType\": \"cpefama\", \"familyId\": 601, \"neVersion\": \"v_0_2_3_28\"}";

}
