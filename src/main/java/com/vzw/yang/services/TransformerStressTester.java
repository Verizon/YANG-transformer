package com.vzw.yang.services;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.vzw.yang.transformer.YangTransformer;
import com.vzw.yang.util.YangTransformerService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Created by randy on 8/25/2017.
 */
public class TransformerStressTester implements Runnable {
    private static final Logger logger = LogManager.getLogger(TransformerStressTester.class);

    @Autowired
    @Qualifier("yangTransformerService")
    private YangTransformerService transformer;
    
    @Autowired
    @Qualifier("yangTransformer")
    private YangTransformer yangTrans;


    private TransactionStatus transactionStatus;

    public TransformerStressTester(TransactionStatus transStatus) {
      transactionStatus = transStatus;
    }
    
    @Override
    public void run() {
    	//InvokePyangScript script = new InvokePyangScript();

        logger.info("TransformerStressTester.run: " + transactionStatus.getName() + " is running");

        transactionStatus.resetCounter();
        while (true) {
        	try {
        		//JsonToYangJsonRequest request = new JsonToYangJsonRequest("ENMV_SAMSUNG5G_CPEDATA", testJsonStr);
        		//String yangJsonStr = transformer.jsonToYangJson(request);
        		JSONObject response = yangTrans.jsonToYangJson("ENMV_SAMSUNG5G_CPEDATA", testJsonStr, false);
        		transactionStatus.addCounter();
        		//prettyPrint(yangJsonStr);
        	} catch(Exception e) {
        		e.printStackTrace();
        	}
        }

        //logger.info("TransformerStressTester.run: " + transactionStatus.getName() + " has ended");

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
