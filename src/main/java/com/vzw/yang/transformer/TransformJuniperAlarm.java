/*
Copyright Verizon Inc. 
Licensed under the terms of the Apache License 2.0 license.  See LICENSE file in project root for terms.
*/
package com.vzw.yang.transformer;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

public class TransformJuniperAlarm {

	static JSONObject transform(JSONObject jsonObj)
	{
		JSONObject retObj = new JSONObject();

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
		return retObj;
	}		


}
