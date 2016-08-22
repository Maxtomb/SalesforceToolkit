package com.maxtomb.salesforce.tool.job;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import com.maxtomb.salesforce.tool.client.CustomizedJsonParser;
import com.maxtomb.salesforce.tool.client.EnterpriseClient;
import com.maxtomb.salesforce.tool.client.HttpClient;
import com.sforce.soap.enterprise.Connector;
import com.sforce.soap.enterprise.EnterpriseConnection;
import com.sforce.ws.ConnectorConfig;

public class SyncJob implements Job {

    public void execute(JobExecutionContext ctx) throws JobExecutionException{
    	
    	String jsonUrl = ctx.getJobDetail().getJobDataMap().getString("jsonurl");
    	String account = ctx.getJobDetail().getJobDataMap().getString("account");;
    	String password = ctx.getJobDetail().getJobDataMap().getString("password");;
    	String token = ctx.getJobDetail().getJobDataMap().getString("token");;
		try {
			String jsonDataString = new HttpClient().getJsonData(jsonUrl);
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
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
		

}
