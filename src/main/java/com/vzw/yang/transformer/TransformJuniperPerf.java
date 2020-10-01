/*
Copyright Verizon Inc. 
Licensed under the terms of the Apache License 2.0 license.  See LICENSE file in project root for terms.
*/
package com.vzw.yang.transformer;

import java.util.Iterator;
import java.util.regex.*;  


import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TransformJuniperPerf {

	private static final Logger logger = LogManager.getLogger(TransformJuniperPerf.class);
	private static Pattern p = Pattern.compile("(\\w+)\\[(\\w+)='(.*)'\\]");

	static JSONObject transform(JSONObject jsonObj) {
		JSONObject retObj = new JSONObject();
		Iterator<String> keys = jsonObj.keys();
		while (keys.hasNext()) {
			String key = keys.next();
			if (!"kv".equals(key)) {
				Object obj = jsonObj.get(key);
			    retObj.put(key,obj);
			}
			if ("kv".equals(key)){
				JSONObject perfJSON = new JSONObject();
				JSONArray kvList = jsonObj.getJSONArray(key);
				JSONObject currentJSON = null;
				for (int i=0; i< kvList.length();i++){
					JSONObject item = kvList.getJSONObject(i);
					if (item.getString("key").equals("__prefix__")){
						logger.info(item.getString("str_value"));
						currentJSON = buildJsonObject(perfJSON,item.getString("str_value"), null);					
					}
					else if (item.getString("key").contains("/")){
						String value = valueType(item);
						buildJsonObject(currentJSON,item.getString("key"), item.getString(value));
					}
				}
				retObj.put("perf", perfJSON);
			}		
		}
		return retObj;
	}
	static String valueType(JSONObject jSON)
	{
		String retVal = null;
		Iterator<String> keys = jSON.keys();
		while (keys.hasNext()) {
			String key = keys.next();
			if (key.contains("_value")){
				return key;
			}
		}
		return retVal;
	}
	static JSONObject buildJsonObject(JSONObject tsd, String prefix, String value)
	{
		prefix = encodeSlash(prefix);
		JSONObject currentJsonObject = tsd;
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
						currentJsonObject.put(m.group(1), new JSONObject());
						currentJsonObject = currentJsonObject.getJSONObject(m.group(1));
					}
					else{
						currentJsonObject = currentJsonObject.getJSONObject(m.group(1));
					}
					if (!currentJsonObject.has(m.group(3))){
						currentJsonObject.put(m.group(3), new JSONObject());
						JSONObject jsonObject = currentJsonObject.getJSONObject(m.group(3));
						jsonObject.put(m.group(2),m.group(3));
						currentJsonObject = currentJsonObject.getJSONObject(m.group(3));
					}
						
				}
				continue;
			}
			if (currentJsonObject.has(prefixArray[i])){
				currentJsonObject = currentJsonObject.getJSONObject(prefixArray[i]);
				continue;
			}
			if (!currentJsonObject.has(prefixArray[i]) && i != prefixArray.length-1){
				currentJsonObject.put(prefixArray[i], new JSONObject());
				currentJsonObject = currentJsonObject.getJSONObject(prefixArray[i]);
				continue;
			}
            if (value != null && i == prefixArray.length-1){
            	currentJsonObject.put(prefixArray[i], value);
            }
            else if (!prefixArray[i].equals("") && !prefixArray[i].contains("[") && !currentJsonObject.has(prefixArray[i])){
            	currentJsonObject.put(prefixArray[i], new JSONObject());
            	currentJsonObject = currentJsonObject.getJSONObject(prefixArray[i]);
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
