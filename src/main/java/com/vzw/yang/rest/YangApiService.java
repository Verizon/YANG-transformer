package com.vzw.yang.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vzw.yang.data.JsonToYangJsonRequest;
import com.vzw.yang.transformer.YangTransformer;
import com.vzw.yang.util.YangTransformerService;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;


/**
 * Restful web service for fetching issue specific questions.
 *
 */


@RestController
@RequestMapping("/yangApiService")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8080"})
public class YangApiService {
    private Logger logger = LogManager.getLogger(YangApiService.class);
    
    @Autowired
    @Qualifier("yangTransformer")
    private YangTransformer transformer;
    
    @Autowired
    @Qualifier("yangTransformerService")
    private YangTransformerService transService;
   
    @PostConstruct
    public void init() {
    	logger.info("init: Entered:");
    	logger.info("init: Exited:");
    }
    
    @PostMapping(value = "jsonToYangJson", produces="application/json; charset=UTF-8")
    public @ResponseBody String jsonToYangJson(
      HttpServletRequest request, @RequestBody String jsonRequest) throws IOException {
       
    	JSONObject jsonResult = null;
    	
        logger.info("YangApiService.jsonToYangJson: Entered: Request = " + jsonRequest);

        try {
			do {
				
				JSONObject jsonObj = new JSONObject(jsonRequest);
				String topic = jsonObj.getString("topic");
				String jsonStr = jsonObj.getJSONObject("jsonStr").toString();
				boolean validate = false;
				if (jsonObj.has("validate")) {
					validate = jsonObj.getBoolean("validate");
				}

				jsonResult = transformer.jsonToYangJson(topic, jsonStr, validate);
				
		  		// display the string
	    		prettyPrintJson(jsonResult.toString());

			} while (false);
        } catch(Exception e) {
        	e.printStackTrace();
        	return getModelMapError(e.getMessage());
        }
        logger.info("YangApiService.jsonToYangJson: Exited");
        
        return getSuccessMap(jsonResult);

    }
    
    @PostMapping(value = "validate", produces="application/json; charset=UTF-8")
    public String validate(@RequestBody String jsonRequest) throws IOException {
       
    	JSONObject jsonResult = new JSONObject();
    	
        logger.info("YangApiService.validate: Entered: Request = " + jsonRequest);

        try {
			do {
				
				JSONObject jsonObj = new JSONObject(jsonRequest);
				String topic = jsonObj.getString("topic");
				String jsonStr = jsonObj.getJSONObject("jsonStr").toString();
				
				JsonToYangJsonRequest request = new JsonToYangJsonRequest();
    			request.setTopic(topic);
    			request.setJsonStr(jsonStr);
    			
    			String result = transService.validateYang(request);
    			logger.info("validate: Validationg result: " + result);
    			jsonResult.put("Result", result);

			} while (false);
        } catch(Exception e) {
        	e.printStackTrace();
        	jsonResult.put("Error", e.getMessage());
        }
        logger.info("YangApiService.validate: Exited");
        
        return jsonResult.toString();

    }
    
    @GetMapping(value = "getMappingDetails")
    public String getMappingDetails() {
       
    	String mappings = "";
    	
        logger.info("YangApiService.getMappingDetails: Entered:");

        try {
				
			mappings = transService.getMappingDetails();
	    	prettyPrintJson(mappings);

        } catch(Exception e) {
        	e.printStackTrace();
        	return getModelMapError(e.getMessage());
        }
        logger.info("YangApiService.getMappingDetails: Exited");
        
        return mappings;

    }
    
    private void prettyPrintJson(String jsonStr) {
    	try {
			ObjectMapper mapper = new ObjectMapper();
			Object jsonObj = mapper.readValue(jsonStr, Object.class);
			logger.info("Have criteria object as json: " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObj));
		} catch(Exception e) {
			e.printStackTrace();
		}
    }
    
    private String getSuccessMap(JSONObject jsonObj){

    	JSONObject resultObj = new JSONObject();
		resultObj.put("response", jsonObj);
		resultObj.put("success", true);

        return resultObj.toString();
    } 
    
    private String getModelMapError(String msg){
        
    	JSONObject resultObj = new JSONObject();
		resultObj.put("message", msg);
		resultObj.put("success", false);

        return resultObj.toString();
    } 
    
}
