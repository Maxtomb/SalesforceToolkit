package com.maxtomb.salesforce.tool.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class CustomizedJsonParser {
	public Map<String,List<Map<Object,Object>>> getDataMap(String jsonDataString){
		JSONObject jobj = JSONObject.fromObject(jsonDataString);
		Object[] nameSet = jobj.keySet().toArray();
		Map<String,List<Map<Object,Object>>> resultDataMap = new HashMap<String,List<Map<Object,Object>>>();
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
		return resultDataMap;
	}
}
