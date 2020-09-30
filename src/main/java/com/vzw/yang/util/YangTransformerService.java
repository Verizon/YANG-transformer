package com.vzw.yang.util;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vzw.yang.data.JsonToYangJsonRequest;

/**********************************************************************
 ** Process responses from CAMEO
 **********************************************************************/
@Service("yangTransformerService")
@PropertySource("classpath:pyang.properties")
public class YangTransformerService {

    private static final Logger logger = LogManager.getLogger(YangTransformerService.class);
    
    private String key;
    //private String url;
    @Value("${pyang.host}")
    private String host;
    @Value("${pyang.port}")
    private int port;
    private String api;
    private int portIncrement = 0;


    public YangTransformerService() {
        logger.debug("YangTransformerService: Entered");

        logger.debug("YangTransformerService: Exited");
    }
    
    public String jsonToYangJson(JsonToYangJsonRequest request) throws Exception {

	  String        Terror         = null;
	  String        Tresponse      = null;

	  do {
	      try {
	    	  ObjectMapper mapper = new ObjectMapper();
	    	  String requestStr = mapper.writeValueAsString(request);
				
	    	  RestTemplate restTemplate = new RestTemplate();
	    	  HttpHeaders requestHeaders = new HttpHeaders();
	    	  requestHeaders.setContentType(MediaType.APPLICATION_JSON);
	    	  //requestHeaders.add("apikey", key);
	    	  // requestHeaders.set("Authorization", "Bearer " + userIdPasswordEncoded);
	    	     	  
              //MultiValueMap<String, String> body = getQueryMap(Txml);
              ///HttpEntity<?> httpEntity = new HttpEntity<Object>(body, requestHeaders);

	    	  HttpEntity<String> httpEntity = new HttpEntity<String>(requestStr, requestHeaders);
	    	  String url = "http://" + host + ":" + port + "/" + api;
	    	  Tresponse = restTemplate.postForObject(url, httpEntity, String.class);

              //String wellFormedJson = JsonSanitizer.sanitize(Tresponse);
	    	  //JSONObject json = new JSONObject(Tresponse);
              //logger.info("Have criteria object as json: " + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(Tresponse));
              
	      } catch(Exception e) {
	          Terror = "Unable to open the URL: " + e.getMessage();
	          System.err.println("ValidateAddress.performAddressValidation " +Terror);
	          e.printStackTrace();
	          break;
	      }

	  } while(false);

        if (Terror != null) {
            throw new Exception(Terror);
        }

        return (Tresponse);

    } 
    
    public String validateYang(JsonToYangJsonRequest request) throws Exception {

	  String        Terror         = null;
	  String        Tresponse      = null;

	  do {
	      try {
	    	  ObjectMapper mapper = new ObjectMapper();
	    	  String requestStr = mapper.writeValueAsString(request);
				
	    	  RestTemplate restTemplate = new RestTemplate();
	    	  HttpHeaders requestHeaders = new HttpHeaders();
	    	  requestHeaders.setContentType(MediaType.APPLICATION_JSON);

	    	  HttpEntity<String> httpEntity = new HttpEntity<String>(requestStr, requestHeaders);
	    	  String url = "http://" + host + ":" + port + "/validateYang";
	    	  logger.info("YangTrnasfoerm.jsonToYangJson: Using url: " + url);
	
	    	  Tresponse = restTemplate.postForObject(url, httpEntity, String.class);

	      } catch(Exception e) {
	          Terror = "Unable to open the URL: " + e.getMessage();
	          System.err.println("ValidateAddress.validateYang " +Terror);
	          e.printStackTrace();
	          break;
	      }

	  } while(false);

        if (Terror != null) {
            throw new Exception(Terror);
        }

        return (Tresponse);

    } 
    
    public String getMappingDetails() throws Exception {

	  String        Terror         = null;
	  String        Tresponse      = null;

	  do {
	      try {				
	    	  RestTemplate restTemplate = new RestTemplate();
	    	 
	    	  String url = "http://" + host + ":" + (port + portIncrement) + "/getMappingDetails";
	    	  Tresponse = restTemplate.getForObject(url, String.class);
	      } catch(Exception e) {
	          Terror = "Unable to open the URL: " + e.getMessage();
	          System.err.println("ValidateAddress.validateYang " +Terror);
	          e.printStackTrace();
	          break;
	      }

	  } while(false);

        if (Terror != null) {
            throw new Exception(Terror);
        }

        return (Tresponse);

    } 
    

}
