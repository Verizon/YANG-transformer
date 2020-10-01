package com.vzw.yang.transformer.ucs;


import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MappingDetail {
	private static final Logger logger = LogManager.getLogger(MappingDetail.class);
	private String topic;
	private String mappingFile;
	private boolean equipmentTopic;
	private JsonNode mappingObj;
	private  String rootPath="C:\\vendorC\\yang-validator\\yang-validator\\transformer\\";

	ObjectMapper mapper = new ObjectMapper();
	
	private static MappingDetail[] mappingDetails = {
			new MappingDetail("ENMV_SAMSUNG5G_CPEDATA", "mappings\\samsungCpePerfdataYangMap.json", false),
			new MappingDetail( "BNC-FILTERED-ALARMS",  "mappings\\bncFilteredAlarms.json" ,  false),
			new MappingDetail("UT_ALARMS_CIENA", "mappings\\UT_Ciena_AlarmMapper.json" ,  false),
			new MappingDetail("UT_ALARMS_CIENA_OBJ", "mappings\\UT_Ciena_AlarmMapperObject.json" ,  false),
			new MappingDetail("UT_ALARMS_CISCO", "mappings\\UT_Cisco_AlarmMapper.json" ,  false),
			new MappingDetail("UT_ALARMS_CISCO_CCS", "mappings\\UT_CiscoCCS_AlarmMapper.json" ,  false),
			new MappingDetail("UTS_EQUIPMENT", "mappings\\UTS_EquipmentMapper.json" ,  false),
			new MappingDetail("MSE_ALARMS_CISCO", "mappings\\MSE_Cisco_AlarmMapper.json" ,  false),
			new MappingDetail("UT_CISCO_STATE", "mappings\\UT_Cisco_StateMapper.json" ,  false),
			new MappingDetail("MSE_ALARMS_JUNIPER", "mappings\\MSE_Juniper_AlarmMapper.json" ,  false),
			new MappingDetail("UTS_EQUIPMENT_API", "mappings\\UTS_EquipmentMapper.json" ,  true),
			new MappingDetail("UTS_CIRCUIT_API", "mappings\\UTS_CircuitMapper.json" ,  false),
			new MappingDetail("MSE_PERF_JUNIPER", "mappings\\MSE_Juniper_PerfMapper.json" ,  false),
			new MappingDetail("EPNM_CONTROL_PLANE", "mappings\\EPNM_ControlPlaneMapper.json" ,  false),
			new MappingDetail("EPNM_CONTROL_PLANE_ACTIVE", "mappings\\EPNM_ControlPlaneMapperActiveAlarm.json" ,  false),
			new MappingDetail("SYSLOG_ALARMS", "mappings\\Syslog_AlarmMapper.json" ,  false)


	};

	public MappingDetail(String topic, JsonNode obj){
		this.topic = topic;
		this.mappingObj = obj;
	}
	public MappingDetail(String topic, String mappingFile, boolean equipmentTopic) {
		this.topic = topic;
		this.mappingFile = mappingFile;
		this.equipmentTopic = equipmentTopic;
		try {
			this.loadMappingDetails();
		} catch (  IOException e) {
			logger.error("JsonProcessingException : ",e);
		}  

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



	public JsonNode getMappingObj() {
		return mappingObj;
	}

	public void setMappingObj(JsonNode mappingObj) {
		this.mappingObj = mappingObj;
	}

	public static MappingDetail findMappingDetail(String topic) {
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


	public boolean loadMappingDetails() throws  IOException {
		boolean rc = false;

		MappingDetail md=this;
		logger.info("YangTransformer.loadMappingDetails: Entered");

		if (md.getMappingObj() != null) {
			// already loaded
			logger.info("YangTransformer.loadMappingDetails: mappings already loaded");

		}else {
			
			JsonNode rootNode=mapper.readTree(new File(rootPath+md.getMappingFile()));
			md.setMappingObj(rootNode);
			logger.info("YangTransformer.loadMappingDetails: mappings loaded");
			logger.info("YangTransformer.loadMappingDetails: Exited");
		}

		return rc;

	}




}
