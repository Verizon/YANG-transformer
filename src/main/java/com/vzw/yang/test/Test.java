/*
Copyright Verizon Inc. 
Licensed under the terms of the Apache License 2.0 license.  See LICENSE file in project root for terms.
*/
package com.vzw.yang.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.vzw.yang.services.TransactionStatus;
import com.vzw.yang.services.TransformerDisplayCount;
import com.vzw.yang.services.TransformerStressTester;

public class Test {
	
	public static void sendRequest(String request) {
		try {

			URL url = new URL("http://localhost:8080/jtransformer/rest/yangApiService/jsonToYangJson");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Accept", "application/json");
			conn.setDoOutput(true);

			conn.getOutputStream().write(request.getBytes());

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			//System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				//System.out.println(output);
			}

			conn.disconnect();

		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}
	}

	public static void main(String args[]) {
		
        List<TransactionStatus> statuses = new ArrayList<TransactionStatus>();
        
        Thread thread = new Thread(new TransformerDisplayCount(statuses));
        thread.start();

        TransactionStatus status = new TransactionStatus();
    	status.setName("Stress Tester: ");
    	statuses.add(status);

        while (true) {
        	status.addCounter();
        	Test.sendRequest("{\"topic\": \"MSE_ALARMS_JUNIPER\", \"jsonStr\": { \"systemId\": \"MIAM2K01-BSYS-RE0\", \"componentId\": 65535, \"path\": \"sensor_1004:/junos/events/:/junos/events/:eventd\", \"sequenceNumber\": \"69910\", \"timestamp\": \"1564417305297\", \"kv\": [ { \"key\": \"__timestamp__\", \"uintValue\": \"1564420471913\" }, { \"key\": \"__junos_re_stream_creation_timestamp__\", \"uintValue\": \"1564417305297\" }, { \"key\": \"__junos_re_payload_get_timestamp__\", \"uintValue\": \"1564417305297\" }, { \"key\": \"__junos_re_event_timestamp__\", \"uintValue\": \"1564417305297\" }, { \"key\": \"__prefix__\", \"strValue\": \"/junos/events/event[id='PIC' and type='3' and facility='20']/\" }, { \"key\": \"timestamp/seconds\", \"uintValue\": \"1564417305\" }, { \"key\": \"timestamp/microseconds\", \"uintValue\": \"243533\" }, { \"key\": \"priority\", \"uintValue\": \"6\" }, { \"key\": \"pid\", \"uintValue\": \"0\" }, { \"key\": \"message\", \"strValue\": \"fpc11 tx lo pkt 0 : tx lo bytes 0 \" }, { \"key\": \"hostname\", \"strValue\": \"MIAM2K01-BSYS-RE0\" }, { \"key\": \"__prefix__\", \"strValue\": \"/junos/events/event[id='PIC' and type='3' and facility='20']/attributes[key='message']/\" }, { \"key\": \"value\", \"strValue\": \"fpc11 tx lo pkt 0 : tx lo bytes 0 \" }, { \"key\": \"__prefix__\", \"strValue\": \"/junos/events/event[id='PIC' and type='3' and facility='20']/\" }, { \"key\": \"logoptions\", \"intValue\": \"0\" } ] }, \"validate\": false}");
        }
        
		
	}

}
