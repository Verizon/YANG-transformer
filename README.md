JSON to OpenConfig YANG JSON Transformer
=====================================================

This directory contains the Java spring-boot json to OpenConfig json transformer.   This software is used to help onboard equipent that is not yet fully OpenConfig compliant.   Using this software enables companies that want to talk OpenConfig Json to be able to utilize these equipment without having to change their code as no devices are added.

The jtransformer does require the yang-validator to be running for access to data mappings.   yang-validator can be found here: https://github.com/Verizon/YANG-validator.   When running jtransformer, first start the yang-validator following its intructions.    Then follow the jtransformer instructions below.   There are sample test scripts below as well.    Also added is a swagger UI for testing the validator.    Should the yang-validator need to be started on a separate port, you can inform jtransformer of this port and ip location in pyang.properties.


Build
-----
mvn package

Run
---
mvn spring-boot:run

Build and Run with Docker
-------------------------
docker build -t jtransformer .
docker run --rm -it -p 8088:8082 jtransformer

Access Docker Logs
------------------
docker ps
docker exec -it [value] bash

To test you can use SWAGGER
---------------------------
http://localhost:8082/swagger-ui.html


Test Transformations
--------------------
{"topic": "MSE_ALARMS_JUNIPER", "jsonStr": { "systemId": "MIAM2K01-BSYS-RE0", "componentId": 65535, "path": "sensor_1004:/junos/events/:/junos/events/:eventd", "sequenceNumber": "69910", "timestamp": "1564417305297", "kv": [ { "key": "__timestamp__", "uintValue": "1564420471913" }, { "key": "__junos_re_stream_creation_timestamp__", "uintValue": "1564417305297" }, { "key": "__junos_re_payload_get_timestamp__", "uintValue": "1564417305297" }, { "key": "__junos_re_event_timestamp__", "uintValue": "1564417305297" }, { "key": "__prefix__", "strValue": "/junos/events/event[id='PIC' and type='3' and facility='20']/" }, { "key": "timestamp/seconds", "uintValue": "1564417305" }, { "key": "timestamp/microseconds", "uintValue": "243533" }, { "key": "priority", "uintValue": "6" }, { "key": "pid", "uintValue": "0" }, { "key": "message", "strValue": "fpc11 tx lo pkt 0 : tx lo bytes 0 " }, { "key": "hostname", "strValue": "MIAM2K01-BSYS-RE0" }, { "key": "__prefix__", "strValue": "/junos/events/event[id='PIC' and type='3' and facility='20']/attributes[key='message']/" }, { "key": "value", "strValue": "fpc11 tx lo pkt 0 : tx lo bytes 0 " }, { "key": "__prefix__", "strValue": "/junos/events/event[id='PIC' and type='3' and facility='20']/" }, { "key": "logoptions", "intValue": "0" } ] }, "validate": false}


Test Validation
---------------
{"topic": "ENMV_SAMSUNG5G_CPEDATA", "jsonStr": {"perfdata": {"eventTime": "2019-03-23T02:50:00+00:00", "annotatedFamilyId": "CPE_RESOURCE", "neId": "100", "neType": "cpefama", "familyId": 601, "neVersion": "v_0_2_3_28", "indexData": {"0": {"indexId": 0, "indexName": "CPE ID", "indexValue": "20dbab03f5ec"}}, "payloadData": {"0": {"typeId": 0, "valueUnit": "%", "typeValue": "2.066667", "valueType": "float", "typeName": "ControlCpuUsage"}, "1": {"typeId": 1, "valueUnit": "%", "typeValue": "26.000000", "valueType": "float", "typeName": "MemoryUsage"}, "2": {"typeId": 2, "valueUnit": "%", "typeValue": "10.000000", "valueType": "float", "typeName": "DiskUsage"}, "3": {"typeId": 3, "valueUnit": "u00b0C", "typeValue": "27.000000", "valueType": "float", "typeName": "Temperature"}}}}}

