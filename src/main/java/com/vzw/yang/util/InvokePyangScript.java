/*
Copyright Verizon Inc. 
Licensed under the terms of the Apache License 2.0 license.  See LICENSE file in project root for terms.
*/
package com.vzw.yang.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InvokePyangScript {
	private static final Logger logger = LogManager.getLogger(InvokePyangScript.class);

	private String pythonExecutable = "/usr/bin/python3";
	private String pythonWorkingDirectory = "/transformer";
	
	
	private Process process = null;
	private PrintWriter pw;
	BufferedInputStream bis;
	ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	
	public InvokePyangScript() {
		logger.info("InvokePyangScript: Entered");
		logger.info("InvokePyangScript: Exited");
	}
	
	public String convertJsonToYangOld(String topic, String jsonStr) {
		String jsonResponse = null;
		String response = null;
		
		try {		
			Process p = Runtime.getRuntime().exec(pythonExecutable + " jsonToYangParameter.py " + topic, null, new File(pythonWorkingDirectory));
			BufferedInputStream bis = new BufferedInputStream(p.getInputStream());
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			byte[] bytes = new byte[2048];
			while (true) {
				int len = bis.read(bytes);
				if (len <= 0) {
					break;
				}
				buffer.write(bytes, 0, len);
				//logger.info("Just read: " + buffer.toString());
				if (buffer.toString().contains("Enter Json")) {
					PrintWriter pw = new PrintWriter(p.getOutputStream());
					pw.println(jsonStr);
					pw.flush();
					buffer = new ByteArrayOutputStream();
				}
			}
			if (buffer.size() > 0) {
				jsonResponse = buffer.toString();
			}
			
			int index = jsonResponse.indexOf("YANG_RESPONSE=");
			if (index >= 0) {
				response = jsonResponse.substring(index + 14);
			}
			
			//logger.info("InvokePyangScript.convertJsonToYang: response: " + response);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return response;
	}
	
	public void startPython() throws Exception {
		
		//logger.info("InvokePyangScript:startPython: Entered:");
		process = Runtime.getRuntime().exec(pythonExecutable + " jsonToYangParameter.py ", null, new File(pythonWorkingDirectory));
		bis = new BufferedInputStream(process.getInputStream());
		buffer = new ByteArrayOutputStream();
		pw = new PrintWriter(process.getOutputStream());
		//logger.info("InvokePyangScript:startPython: Exited:");


	}
	
	public String convertJsonToYang(String topic, String jsonStr) {
		String jsonResponse = null;
		String response = null;
		
		//logger.info("InvokePyangScript:convertJsonToYang: Entered: " + process);

		try {	
			if (process == null) {
				startPython();
			}
			
			// send topic
			pw.println(topic);
			
			// send json str
			pw.println(jsonStr);
			pw.flush();
			
			// get output
			byte[] bytes = new byte[2048];
			while (true) {
				int len = bis.read(bytes);
				if (len <= 0) {
					break;
				}
				buffer.write(bytes, 0, len);
				//logger.info("Just read: " + buffer.toString());
				if (buffer.toString().contains("END PROCESSING")) {
					break;
				//	PrintWriter pw = new PrintWriter(process.getOutputStream());
				//	pw.println(jsonStr);
				//	pw.flush();
				//	buffer = new ByteArrayOutputStream();
				}
				//logger.info("InvokePyangScript.convertJsonToYang: stdout: " + buffer.toString());
			}
			if (buffer.size() > 0) {
				jsonResponse = buffer.toString();
			}
			
			int index = jsonResponse.indexOf("YANG_RESPONSE=");
			if (index >= 0) {
				response = jsonResponse.substring(index + 14);
			}
			buffer = new ByteArrayOutputStream();
			
			//logger.info("InvokePyangScript.convertJsonToYang: response: " + response);
		} catch(Exception e) {
			e.printStackTrace();
		}
		//logger.info("InvokePyangScript:convertJsonToYang: Exited: " + response);

		
		return response;
	}


}
