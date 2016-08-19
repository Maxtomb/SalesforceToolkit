package com.maxtomb.com.maxtomb.salesforce.tool;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.maxtomb.salesforce.tool.client.HttpClient;
import com.sforce.soap.enterprise.Connector;
import com.sforce.soap.enterprise.DeleteResult;
import com.sforce.soap.enterprise.EnterpriseConnection;
import com.sforce.soap.enterprise.Error;
import com.sforce.soap.enterprise.QueryResult;
import com.sforce.soap.enterprise.SaveResult;
import com.sforce.soap.enterprise.sobject.Account;
import com.sforce.soap.enterprise.sobject.Contact;
import com.sforce.soap.enterprise.sobject.SObject;
import com.sforce.ws.ConnectorConfig;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class SalesforceSchronizerTool {

	static final String USERNAME = "toiklaun@gmail.com";
	static final String PASSWORD = "xiaohei@0jSixZaFJ5NfJk6XQeWGF8QZ6";
	static EnterpriseConnection connection;

	public static void main(String[] args) {

		ConnectorConfig config = new ConnectorConfig();
		config.setUsername(USERNAME);
		config.setPassword(PASSWORD);
//		config.setTraceMessage(true);
		try {
			connection = Connector.newConnection(config);
			System.out.println("Auth EndPoint: " + config.getAuthEndpoint());
			System.out.println("Service EndPoint: " + config.getServiceEndpoint());
			System.out.println("Username: " + config.getUsername());
			System.out.println("SessionId: " + config.getSessionId());
			
			//定义一个map 管理所有得sobject name 和 记录数
			Map<String,List<Map<Object,Object>>> resultDataMap = new HashMap<String,List<Map<Object,Object>>>();
			//发http请求 获取json 数据
			String jsonDataString  = HttpClient.getJsonData();
			JSONObject jobj = JSONObject.fromObject(jsonDataString);
			Object[] nameSet = jobj.keySet().toArray();
		
			for(int i=0;i<nameSet.length;i++){
				//一个map代表一条记录，所有的记录添加到一个list中去
				List<Map<Object,Object>> recordList = new ArrayList<Map<Object,Object>>();
				System.out.println(nameSet[i]);
				JSONArray jobjArr = jobj.getJSONArray(nameSet[i].toString());
				
				//这个it是遍历某一个表里的所有的record
				Iterator<JSONObject> it = jobjArr.iterator();
				while(it.hasNext()){
					//创建一个map管理所有的字段和值
					Map<Object,Object> fieldMap = new HashMap<Object,Object>();
					JSONObject jsonObj = it.next();
					Iterator entryIt = jsonObj.entrySet().iterator();
					while(entryIt.hasNext()){
						Map.Entry entry = (Map.Entry) entryIt.next(); 
						//遍历所有的字段和值 并添加到管理map 中去
						fieldMap.put(entry.getKey(),entry.getValue());
					}
					//将每条记录的map 存入管理list中去
					recordList.add(fieldMap);
				}
				
				resultDataMap.put(nameSet[i].toString(), recordList);
			}
			//Map的key 存的是 table 的名字 也就是sobject 的名字
			//Map的value 存的是 记录列表 
			Iterator iter = resultDataMap.entrySet().iterator();
			while(iter.hasNext()){
				Map.Entry<String, List<Map<Object,Object>>> entry = (Entry<String, List<Map<Object, Object>>>) iter.next();
				createSObject(entry.getKey().toString(),entry.getValue());
			}
			
//			createSObject();
			// updateAccounts();
			// deleteAccounts();

		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}

	// queries and displays the 5 newest contacts
	private static void queryContacts() {

		try {

			// query for the 5 newest contacts
			QueryResult queryResults = connection.query("SELECT Id, FirstName, LastName, Account.Name "
					+ "FROM Contact WHERE AccountId != NULL ORDER BY CreatedDate DESC LIMIT 5");
			if (queryResults.getSize() > 0) {
				for (int i = 0; i < queryResults.getRecords().length; i++) {
					// cast the SObject to a strongly-typed Contact
					Contact c = (Contact) queryResults.getRecords()[i];
					System.out.println("Id: " + c.getId() + " - Name: " + c.getFirstName() + " " + c.getLastName()
							+ " - Account: " + c.getAccount().getName());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static SObject setupSObject(Class<?> sobj, SObject instance,Map<Object, Object> fieldNameAndValueMap)
			throws Exception {
		for (Entry<Object, Object> fieldNameAndValue : fieldNameAndValueMap.entrySet()) {
			Method m = sobj.getMethod("set" + fieldNameAndValue.getKey(), String.class);
			m.invoke(instance, fieldNameAndValue.getValue());
		}
		return instance;
	}

	private static void createSObject(String sObjectName, List<Map<Object, Object>> recordsList) throws Exception {
		String classNamePrefix = "com.sforce.soap.enterprise.sobject.";
		String classFullName = classNamePrefix + sObjectName;
		System.out.println("去找class:"+classFullName);
		Class<?> sobj = Class.forName(classFullName);
		List<SObject> records = new ArrayList<SObject>();
		for (Map<Object, Object> recordFieldNameAndValueMap : recordsList) {
			SObject sobjInstance = (SObject) sobj.newInstance();
			SObject sobject = setupSObject(sobj,sobjInstance, recordFieldNameAndValueMap);
			records.add(sobject);
		}
		SObject[] recordsArray = (SObject[]) records.toArray(new SObject[records.size()]);
		SaveResult[] saveResults = connection.create(recordsArray);
		for (int j = 0; j < saveResults.length; j++) {
			if (saveResults[j].isSuccess()) {
				System.out.println(j + ". Successfully created record - Id: " + saveResults[j].getId());
			} else {
				Error[] errors = saveResults[j].getErrors();
				for (int x = 0; x < errors.length; x++) {
					System.out.println("ERROR creating record: " + errors[x].getMessage());
				}
			}
		}

	}

	private static void createSObject(String sObjectName) throws Exception {
		Map<Object, Object> valueMap1 = new HashMap<Object, Object>();
		valueMap1.put("Name", "测试ccc");
		valueMap1.put("TestGreeting__cloth_style__c", "测试款式ccc");
		valueMap1.put("TestGreeting__cloth_color__c", "测试颜色ccc");
		Map<Object, Object> valueMap2 = new HashMap<Object, Object>();
		valueMap2.put("Name", "测试ddd");
		valueMap2.put("TestGreeting__cloth_style__c", "测试款式ddd");
		valueMap2.put("TestGreeting__cloth_color__c", "测试颜色ddd");
		List<Map<Object, Object>> recordsList = new ArrayList<Map<Object, Object>>();
		recordsList.add(valueMap1);
		recordsList.add(valueMap2);
		createSObject(sObjectName, recordsList);
	}

	// updates the 5 newly created Accounts
	private static void updateAccounts() {

		System.out.println("Update the 5 new test Accounts...");
		Account[] records = new Account[5];

		try {

			QueryResult queryResults = connection
					.query("SELECT Id, Name FROM Account ORDER BY " + "CreatedDate DESC LIMIT 5");
			if (queryResults.getSize() > 0) {
				for (int i = 0; i < queryResults.getRecords().length; i++) {
					// cast the SObject to a strongly-typed Account
					Account a = (Account) queryResults.getRecords()[i];
					System.out.println("Updating Id: " + a.getId() + " - Name: " + a.getName());
					// modify the name of the Account
					a.setName(a.getName() + " -- UPDATED");
					records[i] = a;
				}
			}

			// update the records in Salesforce.com
			SaveResult[] saveResults = connection.update(records);

			// check the returned results for any errors
			for (int i = 0; i < saveResults.length; i++) {
				if (saveResults[i].isSuccess()) {
					System.out.println(i + ". Successfully updated record - Id: " + saveResults[i].getId());
				} else {
					Error[] errors = saveResults[i].getErrors();
					for (int j = 0; j < errors.length; j++) {
						System.out.println("ERROR updating record: " + errors[j].getMessage());
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// delete the 5 newly created Account
	private static void deleteAccounts() {

		System.out.println("Deleting the 5 new test Accounts...");
		String[] ids = new String[5];

		try {

			QueryResult queryResults = connection
					.query("SELECT Id, Name FROM Account ORDER BY " + "CreatedDate DESC LIMIT 5");
			if (queryResults.getSize() > 0) {
				for (int i = 0; i < queryResults.getRecords().length; i++) {
					// cast the SObject to a strongly-typed Account
					Account a = (Account) queryResults.getRecords()[i];
					// add the Account Id to the array to be deleted
					ids[i] = a.getId();
					System.out.println("Deleting Id: " + a.getId() + " - Name: " + a.getName());
				}
			}

			// delete the records in Salesforce.com by passing an array of Ids
			DeleteResult[] deleteResults = connection.delete(ids);

			// check the results for any errors
			for (int i = 0; i < deleteResults.length; i++) {
				if (deleteResults[i].isSuccess()) {
					System.out.println(i + ". Successfully deleted record - Id: " + deleteResults[i].getId());
				} else {
					Error[] errors = deleteResults[i].getErrors();
					for (int j = 0; j < errors.length; j++) {
						System.out.println("ERROR deleting record: " + errors[j].getMessage());
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
