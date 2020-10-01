package com.vzw.yang.transformer.ucs;

import java.util.Iterator;
import java.util.regex.*;  


import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TransformJuniperPerf {

	private static final Logger logger = LogManager.getLogger(TransformJuniperPerf.class);
	private static Pattern p = Pattern.compile("(\\S+)\\[(\\S+)='(.*)'\\]");

	public static ObjectNode transform(ObjectNode jsonObj) {
		ObjectNode retObj =JsonNodeFactory.instance.objectNode();
		Iterator<String> keys = jsonObj.fieldNames();
		while (keys.hasNext()) {
			String key = keys.next();
			if (!"kv".equals(key)) {
				JsonNode obj = jsonObj.get(key);
			    retObj.set(key,obj);
			}
			if ("kv".equals(key)){
				ObjectNode perfJSON = JsonNodeFactory.instance.objectNode();
				JsonNode kvList = jsonObj.get(key);
				ObjectNode currentJSON = null;
				for (JsonNode item : kvList){
					if (item.get("key").asText().equals("__prefix__")){
						logger.info(item.get("str_value"));
						currentJSON = buildJsonObject(perfJSON,item.get("str_value").asText(), null);					
					}
					else if (item.get("key").asText().contains("/")){
						String value = valueType((ObjectNode)item);
						buildJsonObject(currentJSON,item.get("key").asText(), item.get(value).asText());
					}
				}
				retObj.set("perf", perfJSON);
			}		
		}
		return retObj;
	}
	static String valueType(ObjectNode jSON)
	{
		String retVal = null;
		Iterator<String> keys = jSON.fieldNames();
		while (keys.hasNext()) {
			String key = keys.next();
			if (key.contains("_value")){
				return key;
			}
		}
		return retVal;
	}
	static ObjectNode buildJsonObject(ObjectNode tsd, String prefix, String value)
	{
		prefix = encodeSlash(prefix);
		ObjectNode currentJsonObject = tsd;
		String prefixArray[] = prefix.split("/");
		
		
		for (int i=0;i<prefixArray.length;i++){
			prefixArray[i] = decodeSlash(prefixArray[i]);
			if (prefixArray[i].equals("")){
				continue;
			}
			if (prefixArray[i].contains("[")){
				Matcher m = p.matcher(prefixArray[i]);
				if (m.find()){
					if (!currentJsonObject.has(m.group(1))){
						currentJsonObject.set(m.group(1), JsonNodeFactory.instance.objectNode());
						currentJsonObject = (ObjectNode)currentJsonObject.get(m.group(1));
					}
					else{
						currentJsonObject = (ObjectNode) currentJsonObject.get(m.group(1));
					}
					if (!currentJsonObject.has(m.group(3))){
						currentJsonObject.set(m.group(3), JsonNodeFactory.instance.objectNode());
						ObjectNode jsonObject = (ObjectNode) currentJsonObject.get(m.group(3));
						jsonObject.put(m.group(2),m.group(3));
						currentJsonObject = (ObjectNode) currentJsonObject.get(m.group(3));
					}
					else
					{
						currentJsonObject = (ObjectNode) currentJsonObject.get(m.group(3));
					}
						
				}
				continue;
			}
			if (currentJsonObject.has(prefixArray[i])){
				currentJsonObject = (ObjectNode) currentJsonObject.get(prefixArray[i]);
				continue;
			}
			if (!currentJsonObject.has(prefixArray[i]) && i != prefixArray.length-1){
				currentJsonObject.set(prefixArray[i], JsonNodeFactory.instance.objectNode());
				currentJsonObject = (ObjectNode) currentJsonObject.get(prefixArray[i]);
				continue;
			}
            if (value != null && i == prefixArray.length-1){
            	currentJsonObject.put(prefixArray[i], value);
            }
            else if (!prefixArray[i].equals("") && !prefixArray[i].contains("[") && !currentJsonObject.has(prefixArray[i])){
            	currentJsonObject.set(prefixArray[i], JsonNodeFactory.instance.objectNode());
            	currentJsonObject = (ObjectNode) currentJsonObject.get(prefixArray[i]);
            }
		}
		
		return currentJsonObject;
	}
    static String encodeSlash(String prefix)
    {
    	boolean inparam = false;
    	for (int i=0;i<prefix.length();i++){
    		if (prefix.charAt(i) == '\''){
               inparam =  !inparam;
    		}
    		if (prefix.charAt(i) == '/' && inparam){
    			String temp = prefix.substring(0,i) + '|' + prefix.substring(i+1,prefix.length());
    			prefix = temp;
    		}
    	}
    	return prefix;
    }
    static String decodeSlash(String prefix)
    {
    	if (prefix.contains("|")){
    		return prefix.replace("|","/");
    	}
    	return (prefix);
    }
   
}
