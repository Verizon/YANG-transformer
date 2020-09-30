package com.vzw.yang.data;

public class JsonToYangJsonRequest {

	private String topic;
	private String jsonStr;
	
	public JsonToYangJsonRequest() {
		
	}
	
	public JsonToYangJsonRequest(String topic, String jsonStr) {
		this.topic = topic;
		this.jsonStr = jsonStr;
	}
	
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public String getJsonStr() {
		return jsonStr;
	}
	public void setJsonStr(String jsonStr) {
		this.jsonStr = jsonStr;
	}

}
