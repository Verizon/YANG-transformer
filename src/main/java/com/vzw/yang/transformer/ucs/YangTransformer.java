//package com.verizon.eclipse.stream.processor.yang;
package com.vzw.yang.transformer.ucs;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.json.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
//import com.verizon.eclipse.stream.mapper.MappingDetail;

/**********************************************************************
 ** Process responses from CAMEO
 **********************************************************************/

public class YangTransformer {

	private static final Logger logger = LogManager.getLogger(YangTransformer.class);
	private static final Hashtable <String,Hashtable<String,String>> hashTable = new Hashtable <String,Hashtable<String,String>> ();
	ObjectMapper mapper = new ObjectMapper();

	public String jsonToYangJson(String topic, ObjectNode rootNode, boolean validate) throws Exception {
		String yangJsonStr = null;
		String error = null;
		MappingDetail md = MappingDetail.findMappingDetail(topic);
		if (md == null) {
			error = "Unable to find topic: " + topic;

		} else if (md.isEquipmentTopic()) {
			error = "Equipment not supported yet: " + topic;

		}else {
			yangJsonStr = convertToYangJson(md, rootNode, validate);
		}
		if (error != null) {
			throw new Exception(error);
		}

		return yangJsonStr;
	}





	@SuppressWarnings("deprecation")
	public String convertToYangJson(MappingDetail md, ObjectNode jsonObj, boolean validate) throws Exception {
		String yangJsonStr = null;
		String error = null;

		 
		JsonNode mapper = md.getMappingObj();
		if (mapper == null) {
			error = "No mappings loaded";

		}else {


			ObjectNode rootJson = jsonObj;

			// Check for basetransformer processing required
			if (mapper.has("basetransformer")) {
				// Perform base mapping logic here
				String baseTransformer = mapper.get("basetransformer").asText();
				jsonObj = transformBaseJson(jsonObj, baseTransformer);
			}

			ObjectNode resultYangObj =JsonNodeFactory.instance.objectNode();
			ObjectNode yangObj = JsonNodeFactory.instance.objectNode();

			resultYangObj.put(mapper.get("rootNode").asText(), yangObj);

    		if (hasDict(mapper))
			      convertToYangJsonWithMapping(mapper, yangObj, jsonObj, rootJson);
    		else
    			resultYangObj.put(mapper.get("rootNode").asText(), jsonObj);
			

			yangJsonStr = resultYangObj.toString();
			
			//if(time>200) {
				//System.out.println("Processing Time : "+time);
			//}
			
			
			/*if (validate) {
        		prettyPrintJson(yangJsonStr);

    			JsonToYangJsonRequest request = new JsonToYangJsonRequest();
    			request.setTopic(md.getTopic());
    			request.setJsonStr(yangJsonStr);

    			String result = transService.validateYang(request);
    			logger.info("YangTransformer.convertToYangJson: Validationg result: " + result);
    		}*/

		}

	 

		if (error != null) {
			throw new Exception(error);
		}

		return yangJsonStr;
	}

	private ObjectNode transformBaseJson(ObjectNode jsonObj, String baseTransformer) {
		ObjectNode retObj = jsonObj;

		logger.info("YangTransformer.transformBaseJson: Entered");

		String[] moduleClassMethod = baseTransformer.split("\\.");
		try {
			Class<?> klas = Class.forName("com.vzw.yang.transformer.ucs." + moduleClassMethod[1]);

			Method method = klas.getDeclaredMethod(moduleClassMethod[2], ObjectNode.class);
			retObj = (ObjectNode) method.invoke(null, jsonObj);
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			return (retObj);
		}
        logger.info(retObj.toString());
		logger.info("YangTransformer.transformBaseJson: Exited");
		return retObj;
	}

	@SuppressWarnings("deprecation")
	private void convertToYangJsonWithMapping(JsonNode mapper, ObjectNode yangObj, ObjectNode jsonObj, ObjectNode rootJson) throws Exception {
		String error = null;

	 
		do {
			Iterator<String> keys =mapper.fieldNames();
			while (keys.hasNext()) {
				String key = keys.next();
				if (key.equals("modelName") || key.equals("pyangObj") || key.equals("rootNode") || key.equals("basetransformer") || key.contentEquals("convert")) {
					continue;
				}

				JsonNode mObj = mapper.get(key);
				String type=mObj.get("type").asText();
				if (type.equals("string")) {
					String result = getJsonStringValue(jsonObj, mObj.get("tag").asText(), mObj, rootJson);    
					if (result != null) {
						yangObj.put(key, result);
					}
				} else if (type.equals("string-list")) {
					String result = getJsonStringValue(jsonObj, mObj.get("tag").asText(), mObj, rootJson);
					ArrayNode ja = JsonNodeFactory.instance.arrayNode();
					yangObj.put(key, ja);
					String[] stringList = result.split(",");
					for (String str : stringList) {
						ja.add(str);
					}			
				} else if (type.equals("int")) {
					Integer result = getJsonIntValue(jsonObj, mObj.get("tag").asText(), mObj, rootJson);   
					if (result != null) {
						yangObj.put(key, result);
					}
				} else if (type.equals("rawAlarmText")) {
					
					String rawAlarm = null;
					if (rootJson.get("raw") != null ){
						rawAlarm = rootJson.get("raw").asText();
					}
					else
					{
						rawAlarm = rootJson.toString();
					}
					rawAlarm = rawAlarm.replaceAll("\n", "");
					yangObj.put(key, rawAlarm);
				} else if (type.equals("metaData")) {
					if (rootJson.has("metaData")) {
						String metaData = rootJson.get("metaData").toString();
						if (metaData != null) {
							metaData = metaData.replaceAll("\n", "");
							yangObj.put(key, metaData);
						}
					}
				} else if (type.equals("list")) {
					ObjectNode list = getJsonListValue(jsonObj, mObj.get("tag").asText(), mObj, rootJson);
					if (list != null) {
						yangObj.put(key, list);
					}
				} else if (type.equals("stringx")) {
					ObjectNode jo = this.getJsonStringXValue(jsonObj, mObj.get("tag").asText(), mObj, rootJson);
					if (jo != null) {
						yangObj.put(key, jo);
					}
				} else if (type.equals("grouping")) {
					ObjectNode groupJsonObj = getJsonObject(jsonObj, mObj.get("tag").asText());
					JsonNode groupMObj = mObj.get("grouping");
					ObjectNode newObj = JsonNodeFactory.instance.objectNode();
					yangObj.put(key, newObj);

					ObjectNode saveRootObj = rootJson;
					convertToYangJsonWithMapping(groupMObj, newObj, groupJsonObj, rootJson);
					rootJson = saveRootObj;

				}
			}

		} while(false);
		 

		if (error != null) {
			throw new Exception(error);
		}

	}
	private boolean hasDict(JsonNode mapper2) {
		boolean hasDict = false;
		Iterator<String> keys = (Iterator<String>) mapper2.fieldNames();
		while (keys.hasNext()) {
			String key = keys.next();

			Object mObj = mapper2.get(key);
			if (mObj instanceof ObjectNode) {
				hasDict = true;
			}
		}

		return hasDict;
	}
	private ObjectNode getJsonObject(ObjectNode jsonObj, String tag) {
		ObjectNode currentObj = jsonObj;
		try {
			String[] tags = tag.split("->");
			for (String t : tags) {
				if (!currentObj.has(t)) {
					continue;
				}
				if (currentObj.get(t).isArray()) {
					for (final JsonNode objNode : currentObj.get(t)) {
						currentObj=(ObjectNode)objNode;
						break;
					}

				} else {
					currentObj = (ObjectNode)currentObj.get(t);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
			currentObj = null;
		}
		return currentObj;
	}

	private String getJsonStringValue(ObjectNode jsonObj, String tag, JsonNode mObj, ObjectNode rootJson) {
		String retValue = null;
		ObjectNode currentObj = jsonObj;

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
				if (currentObj.get(t).isArray()) {
					for (final JsonNode objNode : currentObj.get(t)) {
						currentObj=(ObjectNode)objNode;
						break;
					}
				} else {
					if (!currentObj.get(t).isValueNode()) {
						currentObj = (ObjectNode)currentObj.get(t);
					} else {
						retValue = currentObj.get(t).asText();
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
			currentObj = null;
		}

		if (mObj.has("convert") && !StringUtils.isEmpty(retValue)) {
			String convert = mObj.get("convert").asText();
			if (convert.equals("re")) {
				JsonNode regexList = mObj.get("regex");
				for (final JsonNode objNode : regexList) {
				
				
					String regex = objNode.asText();
					Matcher matcher = Pattern.compile(regex).matcher(retValue);
					if (matcher != null) {
						try {
						       retValue = matcher.group();
						} catch (Exception ex){
							// If no match, return the actual unparsed value.
						}
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
				} else if (convert.equals("map")){
					String hashName = mObj.get("mapname").asText();
					if (!hashTable.containsKey(hashName)){
						addToHashTable(hashName,mObj.get("maplist").asText());
					}
					retValue = lookUpHashTable(hashName, retValue);
				}
			}
		}


		return retValue;
	}

	private ObjectNode getJsonStringXValue(ObjectNode jsonObj, String tag, JsonNode mObj, ObjectNode rootJson) {
		ObjectNode yangObject = JsonNodeFactory.instance.objectNode();
		ObjectNode currentObj = jsonObj;

		try {
			String[] tags = tag.split("->");
			for (String t : tags) {
				if (!currentObj.has(t)) {
					continue;
				}
				if (currentObj.get(t).isArray()) {
					for (final JsonNode objNode : currentObj.get(t)) {
						currentObj=(ObjectNode)objNode;
						break;
					}
				} else {
					if (!currentObj.get(t).isValueNode()) {
						currentObj = (ObjectNode)currentObj.get(t);
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

		ObjectNode item = JsonNodeFactory.instance.objectNode();

		String keyTag = mObj.get("keys").get(mObj.get("key").asText()).get("tag").asText();
		String value = getJsonStringValue(currentObj, keyTag, mObj.get("keys").get(mObj.get("key").asText()), rootJson);
		yangObject.put(value, item);

		Iterator<String> keys = mObj.get("keys").fieldNames();
		while (keys.hasNext()) {
			String key = keys.next();
			JsonNode mo = mObj.get("keys").get(key);
			if (mo.get("type").asText().equals("string")) {
				String result = getJsonStringValue(currentObj, mo.get("tag").asText(), mo, rootJson);    
				if (result != null) {
					item.put(key, result);
				}
			} else if (mo.get("type").asText().equals("string-list")) {
				String result = getJsonStringValue(jsonObj, mObj.get("tag").asText(), mObj, rootJson);
				ArrayNode ja = JsonNodeFactory.instance.arrayNode();
				String[] stringList = result.split(",");
				for (String str : stringList) {
					ja.add(str);
				}			
			} else if (mo.get("type").asText().equals("int")) {
				Integer result = getJsonIntValue(currentObj, mo.get("tag").asText(), mo, rootJson);   
				if (result != null) {
					item.put(key, result);
				}
			} else if (mo.get("type").asText().equals("grouping")) {
				ObjectNode groupJsonObj = getJsonObject(currentObj, mo.get("tag").asText());
				JsonNode groupMObj = mo.get("grouping");
				ObjectNode newObj =JsonNodeFactory.instance.objectNode();
				item.put(key, newObj);

				ObjectNode saveRootObj = rootJson;
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

	private Integer getJsonIntValue(ObjectNode jsonObj, String tag, JsonNode mObj, ObjectNode rootJson) {
		Integer retValue = null;
		ObjectNode currentObj = jsonObj;

		try {
			String[] tags = tag.split("->");
			for (String t : tags) {
				if (retValue != null) {
					throw new Exception("bad json walk");
				}

				if (!currentObj.has(t)) {
					continue;
				}



				if (currentObj.get(t).isArray()) {
					for (final JsonNode objNode : currentObj.get(t)) {
						currentObj=(ObjectNode)objNode;
						break;
					}
				} else {
					if (!currentObj.get(t).isValueNode()) {
						currentObj = (ObjectNode)currentObj.get(t);
					} else {
						retValue = currentObj.get(t).asInt();
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
			currentObj = null;
		}	

		return retValue;
	}

	private ObjectNode getJsonListValue(ObjectNode jsonObj, String tag, JsonNode mObj, ObjectNode rootJson) {
		ObjectNode currentObj = jsonObj;
		ArrayNode currentObjAry = JsonNodeFactory.instance.arrayNode();
		ObjectNode yangObject = JsonNodeFactory.instance.objectNode();
		Boolean isArray = Boolean.FALSE;
		try {
			String[] tags = tag.split("->");
			for (String t : tags) {
				ObjectNode o = null;
				if (currentObj.isArray()) {
					for (final JsonNode objNode : currentObj.get(t)) {
						o=(ObjectNode)objNode;
						break;
					}
				} else {
					o = (ObjectNode)currentObj;
				}
				if (!o.has(t)) {
					break;
				}
				if (o.get(t).isArray()) {
					currentObjAry = (ArrayNode)o.get(t);
					isArray = Boolean.TRUE;
				} else {
					if (!o.get(t).isValueNode()) {
						currentObj =(ObjectNode) o.get(t);
						isArray = Boolean.FALSE;
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
			currentObj = null;
		}

		if (!isArray) {
			return getJsonObjectValue( currentObj,(ObjectNode) mObj, rootJson);
		}

		for (final JsonNode a : currentObjAry) {
	 
			ObjectNode co=(ObjectNode)a;
			ObjectNode item = JsonNodeFactory.instance.objectNode();

			String keyTag = mObj.get("keys").get(mObj.get("key").asText()).get("tag").asText();
			String value = getJsonStringValue(co, keyTag, mObj.get("keys").get(mObj.get("key").asText()), rootJson);
			yangObject.put(value, item);

			Iterator<String> keys = mObj.get("keys").fieldNames();
			while (keys.hasNext()) {
				String key = keys.next();
				JsonNode mo = mObj.get("keys").get(key);
				if (mo.get("type").asText().equals("string")) {
					String result = getJsonStringValue(co, mo.get("tag").asText(), mo, rootJson);    
					if (result != null) {
						item.put(key, result);
					}
				} else if (mo.get("type").asText().equals("string-list")) {
					String result = getJsonStringValue(co, mo.get("tag").asText(), mo, rootJson);
					ArrayNode ja =JsonNodeFactory.instance.arrayNode();
					item.put(key, ja);
					String[] stringList = result.split(",");
					for (String str : stringList) {
						ja.add(str);
					}			
				} else if (mo.get("type").asText().equals("int")) {
					Integer result = getJsonIntValue(co, mo.get("tag").asText(), mo, rootJson);   
					if (result != null) {
						item.put(key, result);
					}
				} else if (mo.get("type").asText().equals("grouping")) {
					ObjectNode groupJsonObj = getJsonObject(co, mo.get("tag").asText());
					JsonNode groupMObj = mo.get("grouping");
					ObjectNode newObj = JsonNodeFactory.instance.objectNode();
					item.put(key, newObj);

					ObjectNode saveRootObj = rootJson;
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

	@SuppressWarnings("deprecation")
	private ObjectNode getJsonObjectValue(ObjectNode jsonObj, ObjectNode mObj, ObjectNode rootJson) {
		ObjectNode yangObject = JsonNodeFactory.instance.objectNode();
		ObjectNode item = JsonNodeFactory.instance.objectNode();

		String keyTag = mObj.get("keys").get(mObj.get("key").asText()).get("tag").asText();
		String value = getJsonStringValue(jsonObj, keyTag, mObj.get("keys").get(mObj.get("key").asText()), rootJson);
		yangObject.put(value, item);

		Iterator<String> keys = mObj.get("keys").fieldNames();
		while (keys.hasNext()) {
			String key = keys.next();
			JsonNode mo = mObj.get("keys").get(key);
			String type=mo.get("type").asText();
			if (type.equals("string")) {
				String result = getJsonStringValue(jsonObj, mo.get("tag").asText(), mo, rootJson);
				if (result != null) {
					item.put(key, result);
				}
			} else if (type.equals("string-list")) {
				String result = getJsonStringValue(jsonObj, mObj.get("tag").asText(), mObj, rootJson);
				ArrayNode ja = JsonNodeFactory.instance.arrayNode();
				item.put(key, ja);
				String[] stringList = result.split(",");
				for (String str : stringList) {
					ja.add(str);
				}			
			} else if (type.equals("int")) {
				Integer result = getJsonIntValue(jsonObj, mObj.get("tag").asText(), mObj, rootJson);
				if (result != null) {
					item.put(key, result);
				}
			} else if (type.equals("grouping")) {
				ObjectNode groupJsonObj = getJsonObject(jsonObj, mo.get("tag").asText());
				JsonNode groupMObj = mo.get("grouping");
				ObjectNode newObj = JsonNodeFactory.instance.objectNode();
				item.put(key, newObj);

				ObjectNode saveRootObj = rootJson;
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

	private void addToHashTable(String mapName, String mapList)
	{
	     String[] items = mapList.split(",");
	     Hashtable<String,String> table = new Hashtable<String, String>();
	     for (int i= 0;i< items.length;i++)
	     {
	    	 String[] keyVal = items[i].split("=");
	    	 table.put(keyVal[0], keyVal[1]);
	     }
	     hashTable.put(mapName, table);
	}
	
	private String lookUpHashTable(String hashName,String retValue)
	{
		Hashtable<String, String> table = hashTable.get(hashName);
		if (table.containsKey(retValue)){
			retValue =  table.get(retValue);
		}
		else{
			retValue = table.get("def");
		}
		return(retValue);
	}
}
