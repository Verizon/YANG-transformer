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

public class TransformCiscoControlPlane {

	private static final Logger logger = LogManager.getLogger(TransformCiscoControlPlane.class);

	static JSONObject transform(JSONObject jsonObj) {
		
		JSONObject append = new JSONObject();
		append.put("severity", "INFO");
		append.put("text","EPNM Control Plane message");
		walkJSON(jsonObj,append);
		JSONObject retObj = jsonObj;
				
		JSONObject j = new JSONObject();
		j.put("data", append);
		retObj = new JSONObject();
		retObj.put("data", j);
		return retObj;
	}
	static void walkJSON(JSONObject jsonObj, JSONObject append)
	{
		Iterator<String> keys = jsonObj.keys();
		while (keys.hasNext()) {
			String key = keys.next();
			Object obj = jsonObj.get(key);
			if (obj instanceof JSONObject)
			{
				walkJSON((JSONObject)obj,append);
			}
			if (key.contains("fdn")){
				append.put("resource", jsonObj.get(key));
			}
			if (key.contains("notification-id")){
				append.put("id", ((Long)jsonObj.get(key)).toString());
			}
			if (key.equals("push.time-of-update")){
				append.put("timestamp", jsonObj.get(key));
			}
		}
		
	}
   
}
