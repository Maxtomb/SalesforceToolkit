package com.maxtomb.com.maxtomb.salesforce.tool;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.maxtomb.salesforce.tool.client.EnterpriseClient;
import com.sforce.soap.enterprise.Connector;
import com.sforce.soap.enterprise.EnterpriseConnection;
import com.sforce.soap.enterprise.QueryResult;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;


public class EnterpriseClientTest {

	static final String USERNAME = "toiklaun@gmail.com";
	static final String PASSWORD = "xiaohei@0jSixZaFJ5NfJk6XQeWGF8QZ6";
	public EnterpriseConnection connection;
	public EnterpriseClient client;

	@Before
	public void setup() {
		ConnectorConfig config = new ConnectorConfig();
		config.setUsername(USERNAME);
		config.setPassword(PASSWORD);
		try {
			connection = Connector.newConnection(config);
			client = new EnterpriseClient(connection);
		} catch (ConnectionException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testCreateSObject() throws Exception {
		Map<Object, Object> valueMap1 = new HashMap<Object, Object>();
		valueMap1.put("Name", "测试1");
		valueMap1.put("TestGreeting__cloth_style__c", "测试款式1");
		valueMap1.put("TestGreeting__cloth_color__c", "测试颜色1");
		Map<Object, Object> valueMap2 = new HashMap<Object, Object>();
		valueMap2.put("Name", "测试2");
		valueMap2.put("TestGreeting__cloth_style__c", "测试款式2");
		valueMap2.put("TestGreeting__cloth_color__c", "测试颜色2");
		Map<Object, Object> valueMap3 = new HashMap<Object, Object>();
		valueMap3.put("Name", "测试3");
		valueMap3.put("TestGreeting__cloth_style__c", "测试款式3");
		valueMap3.put("TestGreeting__cloth_color__c", "测试颜色3");
		Map<Object, Object> valueMap4 = new HashMap<Object, Object>();
		valueMap4.put("Name", "测试4");
		valueMap4.put("TestGreeting__cloth_style__c", "测试款式4");
		valueMap4.put("TestGreeting__cloth_color__c", "测试颜色4");
		Map<Object, Object> valueMap5 = new HashMap<Object, Object>();
		valueMap5.put("Name", "测试5");
		valueMap5.put("TestGreeting__cloth_style__c", "测试款式5");
		valueMap5.put("TestGreeting__cloth_color__c", "测试颜色5");
		List<Map<Object, Object>> recordsList = new ArrayList<Map<Object, Object>>();
		recordsList.add(valueMap1);
		recordsList.add(valueMap2);
		recordsList.add(valueMap3);
		recordsList.add(valueMap4);
		recordsList.add(valueMap5);
		client.createSObject("TestGreeting__Cloth__c",recordsList);
	}

	@Test
	public void testQuerySObject() throws Exception {
		String soql = "SELECT Id, Name FROM TestGreeting__Cloth__c ORDER BY " + "CreatedDate DESC LIMIT 5";
		QueryResult result = client.querySObject(soql);
//		assertEquals(5, result.getRecords().length);
	}
	
	@Test
	public void testCreateASObject() throws Exception {
		Map<Object, Object> valueMap1 = new HashMap<Object, Object>();
		valueMap1.put("Name", "Test");
		valueMap1.put("TestGreeting__cloth_style__c", "test1");
		valueMap1.put("TestGreeting__cloth_color__c", "test1");
		valueMap1.put("TestGreeting__LeadRef__c", "00Q900000110V8d");
		List<Map<Object, Object>> recordsList = new ArrayList<Map<Object, Object>>();
		recordsList.add(valueMap1);
		client.createSObject("TestGreeting__Cloth__c",recordsList);
	}
	
	@Test
	public void testUpdateSObject() throws Exception{
		Map<Object, Object> valueMap = new HashMap<Object, Object>();
		valueMap.put("Name", "名字更新了");
		valueMap.put("TestGreeting__cloth_style__c", "款式更新了");
		valueMap.put("TestGreeting__cloth_color__c", "颜色更新了");
		String soql = "SELECT Id, Name FROM TestGreeting__Cloth__c ORDER BY " + "CreatedDate DESC LIMIT 1";
		client.updateSObject(soql, "TestGreeting__Cloth__c", valueMap);
	}
	
	@Test
	public void testDeleteSObject() {
		String soql = "SELECT Id, Name FROM TestGreeting__Cloth__c ORDER BY " + "CreatedDate DESC LIMIT 5";
		try {
			client.deleteSObject(soql, "TestGreeting__Cloth__c");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
