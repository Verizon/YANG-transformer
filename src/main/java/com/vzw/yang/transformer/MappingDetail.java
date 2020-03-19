/*
Copyright Verizon Inc. 
Licensed under the terms of the Apache License 2.0 license.  See LICENSE file in project root for terms.
*/
package com.vzw.yang.transformer;

import org.json.JSONObject;

public class MappingDetail {
	
	private String topic;
	private String mappingFile;
	private boolean equipmentTopic;
	private JSONObject mappingObj;
	
	/*
	private static MappingDetail[] mappingDetails = {
			new MappingDetail("ENMV_SAMSUNG5G_CPEDATA", "samsungCpePerfdataYangMap.json", false),
			new MappingDetail( "BNC-FILTERED-ALARMS",  "bncFilteredAlarms.json" ,  false),
			new MappingDetail("UT_ALARMS_CIENA", "UT_Ciena_AlarmMapper.json" ,  false),
			new MappingDetail("UT_ALARMS_CIENA_OBJ", "UT_Ciena_AlarmMapperObject.json" ,  false),
			new MappingDetail("UT_ALARMS_CISCO", "UT_Cisco_AlarmMapper.json" ,  false),
			new MappingDetail("UT_ALARMS_CISCO_CCS", "UT_CiscoCCS_AlarmMapper.json" ,  false),
			new MappingDetail("UTS_EQUIPMENT", "UTS_EquipmentMapper.json" ,  false),
			new MappingDetail("MSE_ALARMS_CISCO", "MSE_Cisco_AlarmMapper.json" ,  false),
			new MappingDetail("MSE_ALARMS_JUNIPER", "MSE_Juniper_AlarmMapper.json" ,  false),
			new MappingDetail("UTS_EQUIPMENT_API", "UTS_EquipmentMapper.json" ,  true),
			new MappingDetail("UTS_CIRCUIT_API", "UTS_CircuitMapper.json" ,  false)
	};
	*/
	
	public MappingDetail(String topic, String mappingFile, boolean equipmentTopic) {
		this.topic = topic;
		this.mappingFile = mappingFile;
		this.equipmentTopic = equipmentTopic;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getMappingFile() {
		return mappingFile;
	}

	public void setMappingFile(String mappingFile) {
		this.mappingFile = mappingFile;
	}

	public boolean isEquipmentTopic() {
		return equipmentTopic;
	}

	public void setEquipmentTopic(boolean equipmentTopic) {
		this.equipmentTopic = equipmentTopic;
	}
	
	
	
	public JSONObject getMappingObj() {
		return mappingObj;
	}

	public void setMappingObj(JSONObject mappingObj) {
		this.mappingObj = mappingObj;
	}

}
