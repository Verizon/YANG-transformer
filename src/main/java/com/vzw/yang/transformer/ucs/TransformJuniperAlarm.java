package com.vzw.yang.transformer.ucs;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class TransformJuniperAlarm {

	public static ObjectNode transform(ObjectNode jsonObj)
	{
		ObjectNode retObj =JsonNodeFactory.instance.objectNode();

		Iterator<String> keys = jsonObj.fieldNames();
		while (keys.hasNext()) {
			String key = keys.next();
			if (!"kv".equals(key)) {
				JsonNode obj = jsonObj.get(key);
				retObj.set(key, obj);
			} else {
				JsonNode kvlist = jsonObj.get(key);
				if (kvlist.isArray())
				{
					for (JsonNode jo : kvlist) {
						if (jo.has("uintValue")) {
							retObj.set(jo.get("key").asText(), jo.get("uintValue"));
						}
						if (jo.has("strValue")) {
							retObj.set(jo.get("key").asText(), jo.get("strValue"));
						}
					}
				}
			}
		}
		ObjectNode j = JsonNodeFactory.instance.objectNode();
		j.set("data", retObj);
		retObj = JsonNodeFactory.instance.objectNode();
		retObj.set("data", j);
		return retObj;
	}		


}
