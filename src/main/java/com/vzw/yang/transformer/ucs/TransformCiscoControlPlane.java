package com.vzw.yang.transformer.ucs;

import java.util.Iterator;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TransformCiscoControlPlane {

	private static final Logger logger = LogManager.getLogger(TransformCiscoControlPlane.class);

	public static ObjectNode transform(ObjectNode jsonObj) {
		
		ObjectNode append = JsonNodeFactory.instance.objectNode();
		append.put("severity", "INFO");
		append.put("text","EPNM Control Plane message");
		walkJSON(jsonObj,append);
		ObjectNode retObj = jsonObj;
				
		ObjectNode j = JsonNodeFactory.instance.objectNode();
		j.set("data", append);
		retObj = JsonNodeFactory.instance.objectNode();
		retObj.set("data", j);
		retObj.put("raw", jsonObj.toString());
		return retObj;
	}
	static void walkJSON(ObjectNode jsonObj, ObjectNode append)
	{
		Iterator<String> keys = jsonObj.fieldNames();
		while (keys.hasNext()) {
			String key = keys.next();
			JsonNode obj = jsonObj.get(key);
			if (obj instanceof ObjectNode)
			{
				walkJSON((ObjectNode)obj,append);
			}
			if (key.contains("fdn")){
				append.set("resource", jsonObj.get(key));
			}
			if (key.contains("notification-id")){
				append.put("id", jsonObj.get(key).asLong());
			}
			if (key.equals("push.time-of-update")){
				append.set("timestamp", jsonObj.get(key));
			}
		}
		
	}
   
}
