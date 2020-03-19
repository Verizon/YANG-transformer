/*
Copyright Verizon Inc. 
Licensed under the terms of the Apache License 2.0 license.  See LICENSE file in project root for terms.
*/
package com.vzw.yang.transformer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vzw.yang.data.JsonToYangJsonRequest;
import com.vzw.yang.util.YangTransformerService;

/**********************************************************************
 ** Process responses from CAMEO
 **********************************************************************/
@Service("yangTransformer")
public class YangTransformer {

    private static final Logger logger = LogManager.getLogger(YangTransformer.class);

    @Autowired
    @Qualifier("yangTransformerService")
    private YangTransformerService transService;
    
	private static List<MappingDetail> mappingDetails = new ArrayList<>();

    public YangTransformer() {
        logger.debug("YangTransformer: Entered");

        logger.debug("YangTransformer: Exited");

    }

    @Autowired
    ThreadPoolTaskExecutor taskExecutor;
    
    private synchronized void loadMappings() {
        logger.info("loadMappings: Entered");
        
        if ((mappingDetails != null) && (mappingDetails.size() > 0)) {
        	return;
        }

    	try {
    		String mappingDetails = transService.getMappingDetails();
    		setMappingDetails(mappingDetails);
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
        logger.info("loadMappings: Exited");

    }
    
    public JSONObject jsonToYangJson(String topic, String jsonStr, boolean validate) throws Exception {
    	JSONObject yangJson = null;
    	String error = null;
    	
    	logger.info("YangTransformer.jsonToYangJson: Entered");
    	do {
    		if ((mappingDetails == null) || (mappingDetails.size() == 0)) {
    			loadMappings();
    		}
    		MappingDetail md = findMappingDetail(topic);
    		if (md == null) {
    			error = "Unable to find topic: " + topic;
    			break;
    		}
    		
    		if (md.isEquipmentTopic()) {
    			error = "Equipment not supported yet: " + topic;
    			break;
    		}
    		
    		// load the mapping details
    		//loadMappingDetails(md);
    		
    		yangJson = convertToYangJson(md, jsonStr, validate);
    		
    	} while(false);
    	logger.info("YangTransformer.jsonToYangJson: Exited");
    	
    	if (error != null) {
    		throw new Exception(error);
    	}
    	
    	return yangJson;
    }
    
    private boolean loadMappingDetails(MappingDetail md) {
    	boolean rc = false;
    	InputStream in = null;
    	
    	logger.info("YangTransformer.loadMappingDetails: Entered");
    	do {
    		if (md.getMappingObj() != null) {
    			// already loaded
    			logger.info("YangTransformer.loadMappingDetails: mappings already loaded");
    			break;
    		}
    		
			logger.info("YangTransformer.loadMappingDetails: mappings not loaded yet");
    		in = this.getClass().getClassLoader().getResourceAsStream(md.getMappingFile());
    		if (in == null) {
    			logger.info("YangTransformer.loadMappingDetails: could not find mappings: " + md.getMappingFile());
    			break;
    		}
    		
    		String mappingJsonStr = loadFile(in);
    		
    		// Now convert to a jsonObject
    		JSONObject json = new JSONObject(mappingJsonStr);
    		
    		// display the string
    		prettyPrintJson(mappingJsonStr);
    		
            md.setMappingObj(json);
			logger.info("YangTransformer.loadMappingDetails: mappings loaded");

    	} while(false);
    	if (in != null) {
    		try {
    			in.close();
    		} catch(Exception e) {
    			
    		}
    	}
    	logger.info("YangTransformer.loadMappingDetails: Exited");

    	return rc;
    	
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
    
    private String loadFile(InputStream in) {

        /*---- Local Variable Declares ----*/
        String           Txml    = null;
        LineNumberReader Tinput  = null;
        StringBuffer     Tbuffer = new StringBuffer();

        /*---- Start of Code ----*/
        do {
          /*-----------------------------------------------------------------+
          | Open the file                                                    |
          +-----------------------------------------------------------------*/
          Tinput = new LineNumberReader(new InputStreamReader(in));

          /*-----------------------------------------------------------------+
          | Now loop through this file and process the records               |
          +-----------------------------------------------------------------*/
          while (true) {
            String Tline;
            try {
              Tline = Tinput.readLine();
            } catch (IOException e) {
              logger.error("YangTransformer.loadFile: Unable to read file: " + e.getMessage());
              break;
            }

            /*-----------------------------------------------------------------+
            | If no more data then we are done                                 |
            +-----------------------------------------------------------------*/
            if (Tline == null) {
              break;
            }

            /*-----------------------------------------------------------------+
            | Make sure this is not a blank line                               |
            +-----------------------------------------------------------------*/
            Tbuffer.append(Tline + "\n");
          }
     
          Txml = Tbuffer.toString();

        }
        while (false);

        /*-----------------------------------------------------------------+
        | Free Resources                                                   |
        +-----------------------------------------------------------------*/
        if (Tinput != null) {
          try {
            Tinput.close();
          } catch (IOException e) {}
        }

        return (Txml);

    }

    
    private JSONObject convertToYangJson(MappingDetail md, String jsonStr, boolean validate) throws Exception {
    	String yangJsonStr = null;
    	String error = null;
		JSONObject resultYangObj = new JSONObject();

    	
    	logger.info("YangTransformer.convertToYangJson: Entered");
    	do {
    		JSONObject mapper = md.getMappingObj();
    		if (mapper == null) {
    			error = "No mappings loaded";
    			break;
    		}
    		
    		JSONObject jsonObj = null;
    		try {
    			jsonObj = new JSONObject(jsonStr);
    		} catch(JSONException e) {
    			error = "Unable to convert entered json string: " + e.getMessage();
    			break;
    		}
    		
    		// Check for basetransformer processing required
    		if (mapper.has("basetransformer")) {
    			// Perform base mapping logic here
    			String baseTransformer = mapper.getString("basetransformer");
    			jsonObj = transformBaseJson(jsonObj, baseTransformer);
    		}
    		JSONObject rootJson = jsonObj;
    		
    		JSONObject yangObj = new JSONObject();
    		resultYangObj.put(mapper.getString("rootNode"), yangObj);
    		
    		convertToYangJsonWithMapping(mapper, yangObj, jsonObj, rootJson);
    		    		
    		if (validate) {
    			yangJsonStr = resultYangObj.toString();
        		prettyPrintJson(yangJsonStr);
        		
    			JsonToYangJsonRequest request = new JsonToYangJsonRequest();
    			request.setTopic(md.getTopic());
    			request.setJsonStr(yangJsonStr);
    			
    			String result = transService.validateYang(request);
    			logger.info("YangTransformer.convertToYangJson: Validationg result: " + result);
    		}
    		
    	} while(false);
    	logger.info("YangTransformer.convertToYangJson: Exited");
    	
    	if (error != null) {
    		throw new Exception(error);
    	}
    	
    	return resultYangObj;
    }
    
    private JSONObject transformBaseJson(JSONObject jsonObj, String baseTransformer) {
    	JSONObject retObj = jsonObj;
    	
    	logger.info("YangTransformer.transformBaseJson: Entered");
    	do {
    		if ("TransformJuniperAlarm.TransformJuniperAlarm.transform".equals(baseTransformer)) {
    			retObj = new JSONObject();
    			Iterator<String> keys = jsonObj.keys();
    			while (keys.hasNext()) {
    				String key = keys.next();
    				if (!"kv".equals(key)) {
    					Object obj = jsonObj.get(key);
    					if (obj instanceof String) {
    						retObj.put(key, (String)obj);
    					} else if (obj instanceof Long) {
    						retObj.put(key, ((Long)obj).toString());
      					} else if (obj instanceof Integer) {
    						retObj.put(key, ((Integer)obj).toString());
      					} else if (obj instanceof Float) {
    						retObj.put(key, ((Float)obj).toString());
      					} else if (obj instanceof Double) {
    						retObj.put(key, ((Double)obj).toString());
    					} else {
    						retObj.put(key, obj.toString());
    					}
    				} else {
    					JSONArray kvlist = jsonObj.getJSONArray(key);
    					for (int i = 0; i < kvlist.length(); i++) {
    						JSONObject jo = kvlist.getJSONObject(i);
    						if (jo.has("uintValue")) {
    							retObj.put(jo.getString("key"), jo.getString("uintValue"));
    						}
    						if (jo.has("strValue")) {
    							retObj.put(jo.getString("key"), jo.getString("strValue"));
    						}
    					}
    				}
    			}
    			JSONObject j = new JSONObject();
    			j.put("data", retObj);
    			retObj = new JSONObject();
    			retObj.put("data", j);
    		}
    	} while(false);
    	logger.info("YangTransformer.transformBaseJson: Exited");

    	
    	return retObj;
    }
    
    private void convertToYangJsonWithMapping(JSONObject mapper, JSONObject yangObj, JSONObject jsonObj, JSONObject rootJson) throws Exception {
    	String error = null;
    	
    	logger.info("YangTransformer.convertToYangJsonWithMapping: Entered");
    	do {
    		Iterator<String> keys = (Iterator<String>)mapper.keys();
    		while (keys.hasNext()) {
    			String key = keys.next();
    			if (key.equals("modelName") || key.equals("pyangObj") || key.equals("rootNode") || key.equals("basetransformer") || key.contentEquals("convert")) {
    				continue;
    			}
    			
    			JSONObject mObj = mapper.getJSONObject(key);
    			if (mObj.getString("type").equals("string")) {
    				String result = getJsonStringValue(jsonObj, mObj.getString("tag"), mObj, rootJson);    
    				if (result != null) {
    					yangObj.put(key, result);
    				}
    			} else if (mObj.getString("type").equals("string-list")) {
    				String result = getJsonStringValue(jsonObj, mObj.getString("tag"), mObj, rootJson);
    				JSONArray ja = new JSONArray();
    				yangObj.put(key, ja);
    				String[] stringList = result.split(",");
    				for (String str : stringList) {
    					ja.put(str);
    				}			
    			} else if (mObj.getString("type").equals("int")) {
    				Integer result = getJsonIntValue(jsonObj, mObj.getString("tag"), mObj, rootJson);   
    				if (result != null) {
    					yangObj.put(key, result);
    				}
    			} else if (mObj.getString("type").equals("rawAlarmText")) {
    				String rawAlarm = rootJson.toString();
    				rawAlarm = rawAlarm.replace("\n", "");
    				yangObj.put(key, rawAlarm);
    			} else if (mObj.getString("type").equals("metaData")) {
    				String rawAlarm = rootJson.optJSONObject("metaData").toString();
    				if (rawAlarm != null) {
    					rawAlarm = rawAlarm.replace("\n", "");
    					yangObj.put(key, rawAlarm);
    				}
    			} else if (mObj.getString("type").equals("list")) {
    				JSONObject list = getJsonListValue(jsonObj, mObj.getString("tag"), mObj, rootJson);
    				if (list != null) {
    					yangObj.put(key, list);
    				}
    			} else if (mObj.getString("type").equals("stringx")) {
    				JSONObject jo = this.getJsonStringXValue(jsonObj, mObj.getString("tag"), mObj, rootJson);
    				if (jo != null) {
    					yangObj.put(key, jo);
    				}
    			} else if (mObj.getString("type").equals("grouping")) {
    				JSONObject groupJsonObj = getJsonObject(jsonObj, mObj.getString("tag"));
    				JSONObject groupMObj = mObj.getJSONObject("grouping");
    				JSONObject newObj = new JSONObject();
    				yangObj.put(key, newObj);
    				
    				JSONObject saveRootObj = rootJson;
    				convertToYangJsonWithMapping(groupMObj, newObj, groupJsonObj, rootJson);
    				rootJson = saveRootObj;
    				
    			}
    		}
    		
    	} while(false);
    	logger.info("YangTransformer.convertToYangJsonWithMapping: Exited");
    	
    	if (error != null) {
    		throw new Exception(error);
    	}
    	
    }
    
    private JSONObject getJsonObject(JSONObject jsonObj, String tag) {
    	JSONObject currentObj = jsonObj;
    	try {
    		String[] tags = tag.split("->");
    		for (String t : tags) {
    			if (!currentObj.has(t)) {
    				continue;
    			}
    			if (currentObj.get(t) instanceof JSONArray) {
    				currentObj = currentObj.getJSONArray(t).getJSONObject(0);
    			} else {
    				currentObj = currentObj.getJSONObject(t);
    			}
    		}
    	} catch(Exception e) {
    		e.printStackTrace();
    		currentObj = null;
    	}
    	return currentObj;
    }
    
    private String getJsonStringValue(JSONObject jsonObj, String tag, JSONObject mObj, JSONObject rootJson) {
    	String retValue = null;
    	JSONObject currentObj = jsonObj;
    	
    	try {
    		String[] tags = tag.split("->");
    		for (String t : tags) {
    			if (retValue != null) {
    				throw new Exception("bad json walk");
    			}
    			if ((tags.length > 1) && t.equals("ROOT")) {
    				currentObj = rootJson;
    				continue;
    			}
    			if (!currentObj.has(t)) {
    				continue;
    			}
    			if (currentObj.get(t) instanceof JSONArray) {
    				currentObj = currentObj.getJSONArray(t).getJSONObject(0);
    			} else {
    				if (currentObj.get(t) instanceof JSONObject) {
    					currentObj = currentObj.getJSONObject(t);
    				} else {
    					retValue = currentObj.getString(t);
    				}
    			}
    		}
    	} catch(Exception e) {
    		e.printStackTrace();
    		currentObj = null;
    	}
    	
    	if (mObj.has("convert") && !StringUtils.isEmpty(retValue)) {
    		String convert = mObj.getString("convert");
    		if (convert.equals("re")) {
    			JSONArray regexList = mObj.getJSONArray("regex");
    			for (int i = 0; i < regexList.length(); i++) {
    				String regex = regexList.getString(i);
    				Matcher matcher = Pattern.compile(regex).matcher(retValue);
    				if (matcher != null) {
    					retValue = matcher.group();
    				}
    			}
    		} else {
    			if (convert.equals("convert1")) {
    				String[] severity = retValue.split("-");
    				retValue = severity[2].toUpperCase();
    			} else if (convert.equals("convert2")) {
    				Calendar cal = Calendar.getInstance();
    				cal.setTimeInMillis(new Long(retValue));
    				SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    				retValue = dateFmt.format(cal.getTime());
    			}
    		}
    	}
    	
    	
    	return retValue;
    }
    
    private JSONObject getJsonStringXValue(JSONObject jsonObj, String tag, JSONObject mObj, JSONObject rootJson) {
    	JSONObject yangObject = new JSONObject();
    	JSONObject currentObj = jsonObj;
    	
    	try {
    		String[] tags = tag.split("->");
    		for (String t : tags) {
    			if (!currentObj.has(t)) {
    				continue;
    			}
    			if (currentObj.get(t) instanceof JSONArray) {
    				currentObj = currentObj.getJSONArray(t).getJSONObject(0);
    			} else {
    				if (currentObj.get(t) instanceof JSONObject) {
    					currentObj = currentObj.getJSONObject(t);
    				}
    			}
    		}
    	} catch(Exception e) {
    		e.printStackTrace();
    		currentObj = null;
    	}
    	
    	if (currentObj == null) {
    		return yangObject;
    	}
    	    	
    	JSONObject item = new JSONObject();
    	
    	String keyTag = mObj.getJSONObject("keys").getJSONObject(mObj.getString("key")).getString("tag");
		String value = getJsonStringValue(currentObj, keyTag, mObj.getJSONObject("keys").getJSONObject(mObj.getString("key")), rootJson);
		yangObject.put(value, item);
		
		Iterator<String> keys = mObj.getJSONObject("keys").keys();
		while (keys.hasNext()) {
			String key = keys.next();
			JSONObject mo = mObj.getJSONObject("keys").getJSONObject(key);
			if (mo.getString("type").equals("string")) {
				String result = getJsonStringValue(currentObj, mo.getString("tag"), mo, rootJson);    
				if (result != null) {
					item.put(key, result);
				}
			} else if (mo.getString("type").equals("string-list")) {
				String result = getJsonStringValue(jsonObj, mObj.getString("tag"), mObj, rootJson);
				JSONArray ja = new JSONArray();
				String[] stringList = result.split(",");
				for (String str : stringList) {
					ja.put(str);
				}			
			} else if (mo.getString("type").equals("int")) {
				Integer result = getJsonIntValue(currentObj, mo.getString("tag"), mo, rootJson);   
				if (result != null) {
					item.put(key, result);
				}
			} else if (mo.getString("type").equals("grouping")) {
				JSONObject groupJsonObj = getJsonObject(currentObj, mo.getString("tag"));
				JSONObject groupMObj = mo.getJSONObject("grouping");
				JSONObject newObj = new JSONObject();
				item.put(key, newObj);
				
				JSONObject saveRootObj = rootJson;
				try {
					convertToYangJsonWithMapping(groupMObj, newObj, groupJsonObj, rootJson);
				} catch(Exception e) {
					e.printStackTrace();
				}
				rootJson = saveRootObj;
			}
		}
    	
    	
    	return yangObject;
    }
    
    private Integer getJsonIntValue(JSONObject jsonObj, String tag, JSONObject mObj, JSONObject rootJson) {
    	Integer retValue = null;
    	JSONObject currentObj = jsonObj;
    	
    	try {
    		String[] tags = tag.split("->");
    		for (String t : tags) {
    			if (retValue != null) {
    				throw new Exception("bad json walk");
    			}
    			
    			if (!currentObj.has(t)) {
    				continue;
    			}
    			if (currentObj.get(t) instanceof JSONArray) {
    				currentObj = currentObj.getJSONArray(t).getJSONObject(0);
    			} else {
    				if (currentObj.get(t) instanceof JSONObject) {
    					currentObj = currentObj.getJSONObject(t);
    				} else {
    					retValue = currentObj.getInt(t);
    				}
    			}
    		}
    	} catch(Exception e) {
    		e.printStackTrace();
    		currentObj = null;
    	}	
    	
    	return retValue;
    }

    private JSONObject getJsonListValue(JSONObject jsonObj, String tag, JSONObject mObj, JSONObject rootJson) {
    	Object currentObj = jsonObj;
    	JSONObject yangObject = new JSONObject();
    	
    	try {
    		String[] tags = tag.split("->");
    		for (String t : tags) {
    			JSONObject o = null;
    			if (currentObj instanceof JSONArray) {
    				o = ((JSONArray) currentObj).getJSONObject(0);
    			} else {
    				o = (JSONObject)currentObj;
    			}
    			if (!o.has(t)) {
    				break;
    			}
    			if (o.get(t) instanceof JSONArray) {
    				currentObj = o.get(t);
    			} else {
    				if (o.get(t) instanceof JSONObject) {
    					currentObj = o.get(t);
    				}
    			}
    		}
    	} catch(Exception e) {
    		e.printStackTrace();
    		currentObj = null;
    	}
    	
    	if (!(currentObj instanceof JSONArray)) {
    		return getJsonObjectValue(jsonObj, (JSONObject)currentObj, rootJson);
    	}
    	
    	JSONArray jArray = (JSONArray)currentObj;
    	for (int i = 0; i < jArray.length(); i++) {
    		JSONObject co = jArray.getJSONObject(i);
    		JSONObject item = new JSONObject();
    		
    		String keyTag = mObj.getJSONObject("keys").getJSONObject(mObj.getString("key")).getString("tag");
    		String value = getJsonStringValue(co, keyTag, mObj.getJSONObject("keys").getJSONObject(mObj.getString("key")), rootJson);
    		yangObject.put(value, item);
    		
    		Iterator<String> keys = mObj.getJSONObject("keys").keys();
    		while (keys.hasNext()) {
    			String key = keys.next();
    			JSONObject mo = mObj.getJSONObject("keys").getJSONObject(key);
    			if (mo.getString("type").equals("string")) {
    				String result = getJsonStringValue(co, mo.getString("tag"), mo, rootJson);    
    				if (result != null) {
    					item.put(key, result);
    				}
    			} else if (mo.getString("type").equals("string-list")) {
    				String result = getJsonStringValue(co, mo.getString("tag"), mo, rootJson);
    				JSONArray ja = new JSONArray();
    				item.put(key, ja);
    				String[] stringList = result.split(",");
    				for (String str : stringList) {
    					ja.put(str);
    				}			
    			} else if (mo.getString("type").equals("int")) {
    				Integer result = getJsonIntValue(co, mo.getString("tag"), mo, rootJson);   
    				if (result != null) {
    					item.put(key, result);
    				}
    			} else if (mo.getString("type").equals("grouping")) {
    				JSONObject groupJsonObj = getJsonObject(jsonObj, mo.getString("tag"));
    				JSONObject groupMObj = mo.getJSONObject("grouping");
    				JSONObject newObj = new JSONObject();
    				item.put(key, newObj);
    				
    				JSONObject saveRootObj = rootJson;
    				try {
    					convertToYangJsonWithMapping(groupMObj, newObj, groupJsonObj, rootJson);
    				} catch(Exception e) {
    					e.printStackTrace();
    				}
    				rootJson = saveRootObj;
    			}
    		}
    	}
    	
    	
    	return yangObject;
    }
    
    private JSONObject getJsonObjectValue(JSONObject jsonObj, JSONObject mObj, JSONObject rootJson) {
    	JSONObject yangObject = new JSONObject();
   		JSONObject item = new JSONObject();

		String keyTag = mObj.getJSONObject("keys").getJSONObject(mObj.getString("key")).getString("tag");
		String value = getJsonStringValue(mObj, keyTag, mObj.getJSONObject("keys").getJSONObject(mObj.getString("key")), rootJson);
		yangObject.put(value, item);

		Iterator<String> keys = mObj.keys();
		while (keys.hasNext()) {
			String key = keys.next();
			JSONObject mo = mObj.getJSONObject(key);
			if (mo.getString("type").equals("string")) {
				String result = getJsonStringValue(jsonObj, mo.getString("tag"), mo, rootJson);
				if (result != null) {
					item.put(key, result);
				}
			} else if (mObj.getString("type").equals("string-list")) {
				String result = getJsonStringValue(jsonObj, mObj.getString("tag"), mObj, rootJson);
				JSONArray ja = new JSONArray();
				item.put(key, ja);
				String[] stringList = result.split(",");
				for (String str : stringList) {
					ja.put(str);
				}			
			} else if (mObj.getString("type").equals("int")) {
				Integer result = getJsonIntValue(jsonObj, mObj.getString("tag"), mObj, rootJson);
				if (result != null) {
					item.put(key, result);
				}
			} else if (mObj.getString("type").equals("grouping")) {
				JSONObject groupJsonObj = getJsonObject(jsonObj, mo.getString("tag"));
				JSONObject groupMObj = mo.getJSONObject("grouping");
				JSONObject newObj = new JSONObject();
				item.put(key, newObj);
				
				JSONObject saveRootObj = rootJson;
				try {
					convertToYangJsonWithMapping(groupMObj, newObj, groupJsonObj, rootJson);
				} catch(Exception e) {
					e.printStackTrace();
				}
				rootJson = saveRootObj;
			}
		}
    	
    	return yangObject;
    }

	public MappingDetail findMappingDetail(String topic) {
		MappingDetail md = null;
		
		// Find the mapping
		for (MappingDetail m : mappingDetails) {
			if (topic.equals(m.getTopic())) {
				md = m;
				break;
			}
		}
		
		return md;
	}
	
	public void setMappingDetails(String mappingDetailsJson) {
		JSONArray jsonArray = new JSONArray(mappingDetailsJson);
		
		mappingDetails.clear();
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObj = jsonArray.getJSONObject(i);
			
			MappingDetail m = new MappingDetail(jsonObj.getString("topic"), null, "true".equals(jsonObj.getString("isEquipmentTopic")) ? true :false);

    		m.setMappingObj(jsonObj.getJSONObject("mappingObj"));
			
			mappingDetails.add(m);
		}
		
	}

}
