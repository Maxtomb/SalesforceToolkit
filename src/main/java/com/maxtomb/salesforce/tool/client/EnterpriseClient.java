package com.maxtomb.salesforce.tool.client;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.sforce.soap.enterprise.DeleteResult;
import com.sforce.soap.enterprise.EnterpriseConnection;
import com.sforce.soap.enterprise.Error;
import com.sforce.soap.enterprise.QueryResult;
import com.sforce.soap.enterprise.SaveResult;
import com.sforce.soap.enterprise.sobject.SObject;
/**
 * 
 * @author Jerry.Ling
 * @email toiklaun@gmail.com
 * 
 */
public class EnterpriseClient {
	
	public EnterpriseConnection connection;
	
	public EnterpriseClient(EnterpriseConnection connection){
		this.connection = connection;
	}
	
	public QueryResult querySObject(String soql) throws Exception {
		QueryResult queryResults = connection.query(soql);
		return queryResults;
	}
	/**
	 * 增加一个sobject记录 
	 * @param sObjectName 制定sobject得名字
	 * @param recordsList 制定要插入记录得集合
	 * @throws Exception
	 */
	public void createSObject(String sObjectName, List<Map<Object, Object>> recordsList) throws Exception {
		String classNamePrefix = "com.sforce.soap.enterprise.sobject.";
		String classFullName = classNamePrefix + sObjectName;
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
	/**
	 * 利用反射动态调用set+字段名 来注入字段得值
	 * @param sobj 指定sobject的名字
	 * @param instance 指定sobject 实例
	 * @param fieldNameAndValueMap 字段和值的对应map
	 * @return
	 * @throws Exception
	 */
	private SObject setupSObject(Class<?> sobj, SObject instance,Map<Object, Object> fieldNameAndValueMap)
			throws Exception {
		for (Entry<Object, Object> fieldNameAndValue : fieldNameAndValueMap.entrySet()) {
			Method m = sobj.getMethod("set" + fieldNameAndValue.getKey(), String.class);
			m.invoke(instance, fieldNameAndValue.getValue());
		}
		return instance;
	}
	/**
	 * 更新指定的某一个记录
	 * @param soql 查询出一条记录
	 * @param sObjectName 指定sObject的名字
	 * @param fieldMap 要更新字段的名字和值的map
	 * @throws Exception
	 */
	public void updateSObject(String soql, String sObjectName, Map<Object, Object> fieldMap) throws Exception {
		String classNamePrefix = "com.sforce.soap.enterprise.sobject.";
		String classFullName = classNamePrefix + sObjectName;
		Class<?> sobj = Class.forName(classFullName);
		SObject[] records = new SObject[1];
		QueryResult queryResults = connection.query(soql);
		if (queryResults.getSize() > 0) {
			SObject rec = (SObject) sobj.cast(queryResults.getRecords()[0]);
			SObject readyObj = setupSObject(sobj, rec, fieldMap);
			records[0] = readyObj;
		}
		SaveResult[] saveResults = connection.update(records);
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
	}
	/**
	 * 删掉查询出的多个记录
	 * @param soql 提供一个sql语句查询多条或一条记录
	 * @param sObjectName 指定sobject 名字
	 * @throws Exception
	 */
	public String deleteSObject(String soql,String sObjectName) throws Exception{
		String classNamePrefix = "com.sforce.soap.enterprise.sobject.";
		String classFullName = classNamePrefix + sObjectName;
		Class<?> sobj = Class.forName(classFullName);
		String result = "";
		try {
			QueryResult queryResults = connection.query(soql);
			String[] ids = new String[queryResults.getSize()];
			if (queryResults.getSize() > 0) {
				for (int i = 0; i < queryResults.getRecords().length; i++) {
					Method m = sobj.getMethod("getId");
					ids[i]  = (String) m.invoke(sobj.cast(queryResults.getRecords()[i]));
				}
			}
			DeleteResult[] deleteResults = connection.delete(ids);
			for (int i = 0; i < deleteResults.length; i++) {
				if (deleteResults[i].isSuccess()) {
					System.out.println(i + ". Successfully deleted record - Id: " + deleteResults[i].getId());
					result = i + ". Successfully deleted record - Id: " + deleteResults[i].getId();
				} else {
					Error[] errors = deleteResults[i].getErrors();
					for (int j = 0; j < errors.length; j++) {
						System.out.println("ERROR deleting record: " + errors[j].getMessage());
						result = "ERROR deleting record: " + errors[j].getMessage();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
}
