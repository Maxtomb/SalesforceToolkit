package com.maxtomb.salesforce.tool.controller;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.quartz.JobDataMap;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.maxtomb.salesforce.tool.client.CustomizedJsonParser;
import com.maxtomb.salesforce.tool.client.EnterpriseClient;
import com.maxtomb.salesforce.tool.client.HttpClient;
import com.maxtomb.salesforce.tool.job.QuartzManager;
import com.maxtomb.salesforce.tool.job.SyncJob;
import com.sforce.soap.enterprise.Connector;
import com.sforce.soap.enterprise.EnterpriseConnection;
import com.sforce.ws.ConnectorConfig;

@EnableAutoConfiguration
@RestController
@RequestMapping("/salesforce")
public class SalesforceController {
	
    @RequestMapping(value="/tool/sync/records",method=RequestMethod.POST)
    public Map<String, List<Map<Object, Object>>> syncUpRecords(
    		@RequestParam(value="account") String account,
    		@RequestParam(value="password") String password,
    		@RequestParam(value="token") String token,
    		@RequestParam(value="jsonurl") String jsonurl
    		) throws Exception{
    	
    	String jsonDataString = new HttpClient().getJsonData(jsonurl);
    	CustomizedJsonParser parser = new CustomizedJsonParser();
    	Map<String, List<Map<Object, Object>>> resultDataMap = parser.getDataMap(jsonDataString);
    	ConnectorConfig config = new ConnectorConfig();
		config.setUsername(account);
		config.setPassword(password+token);
		EnterpriseConnection connection = Connector.newConnection(config);
		EnterpriseClient client = new EnterpriseClient(connection);
		Iterator iter = resultDataMap.entrySet().iterator();
		while(iter.hasNext()){
			Map.Entry<String, List<Map<Object,Object>>> entry = (Entry<String, List<Map<Object, Object>>>) iter.next();
			client.createSObject(entry.getKey().toString(),entry.getValue());
		}
    	return resultDataMap;
    }
    
    @RequestMapping(value="/tool/sync/metadata",method=RequestMethod.POST)
    public Map<String, List<Map<Object, Object>>> syncUpMetadata(
    		@RequestParam(value="account") String account,
    		@RequestParam(value="password") String password,
    		@RequestParam(value="token") String token,
    		@RequestParam(value="jsonurl") String jsonurl
    		) throws Exception{
    	
    	String jsonDataString = new HttpClient().getJsonData(jsonurl);
    	CustomizedJsonParser parser = new CustomizedJsonParser();
    	Map<String, List<Map<Object, Object>>> resultDataMap = parser.getDataMap(jsonDataString);
    	ConnectorConfig config = new ConnectorConfig();
		config.setUsername(account);
		config.setPassword(password+token);
		EnterpriseConnection connection = Connector.newConnection(config);
		EnterpriseClient client = new EnterpriseClient(connection);
		Iterator iter = resultDataMap.entrySet().iterator();
		while(iter.hasNext()){
			Map.Entry<String, List<Map<Object,Object>>> entry = (Entry<String, List<Map<Object, Object>>>) iter.next();
			client.createSObject(entry.getKey().toString(),entry.getValue());
		}
    	return resultDataMap;
    }
    
    @RequestMapping(value="/tool/sync/job",method=RequestMethod.POST)
    public String addJob(
    		@RequestParam(value="account") String account,
    		@RequestParam(value="password") String password,
    		@RequestParam(value="token") String token,
    		@RequestParam(value="jsonurl") String jsonurl,
    		@RequestParam(value="jobname") String jobname,
    		@RequestParam(value="time") String time
    		) throws Exception{
    	
    	SyncJob job = new SyncJob();
    	JobDataMap jobMap=new JobDataMap();
    	jobMap.put("jsonurl", jsonurl);
    	jobMap.put("account", account);
    	jobMap.put("password", password);
    	jobMap.put("token", token);
    	 try {  
             QuartzManager.addJob(jobname,job,time,jobMap);   
         }  catch (Exception e) {  
             e.printStackTrace();  
         }  
    	return "Job:"+jobname+",Time:"+time+", 开始工作。";
    }
    
    @RequestMapping(value="/tool/sync/job",method=RequestMethod.GET)
    public List listJob() throws Exception{
    	return QuartzManager.listAllJob();
    }
    
    @RequestMapping(value="/tool/sync/sobject/records",method=RequestMethod.POST)
    public String deleteSObject(
    		@RequestParam(value="account") String account,
    		@RequestParam(value="password") String password,
    		@RequestParam(value="token") String token,
    		@RequestParam(value="sobjectname") String sobjectname
    		) throws Exception{
    	
    	String soql = "SELECT id,name from "+sobjectname;
    	ConnectorConfig config = new ConnectorConfig();
		config.setUsername(account);
		config.setPassword(password+token);
		EnterpriseConnection connection = Connector.newConnection(config);
		EnterpriseClient client = new EnterpriseClient(connection);
		return client.deleteSObject(soql, sobjectname);
    }
    
    public static void main(String[] args) {
        SpringApplication.run(SalesforceController.class);
    }

}

