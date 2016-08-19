package com.maxtomb.salesforce.tool.client;

import com.sforce.soap.enterprise.EnterpriseConnection;
import com.sforce.soap.enterprise.LoginResult;
import com.sforce.soap.metadata.MetadataConnection;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

public class MetadataLoginUtil {
	public static MetadataConnection login() throws ConnectionException {
		// This is only a sample. Hard coding passwords in source files is a bad practice.
        final String USERNAME = "toiklaun@gmail.com";
        final String PASSWORD = "xiaohei@0jSixZaFJ5NfJk6XQeWGF8QZ6"; 
        final String URL = "https://login.salesforce.com/services/Soap/c/37.0";
//        final String URL = "https://login.salesforce.com/services/Soap/m/37.0";
        final LoginResult loginResult = loginToSalesforce(USERNAME, PASSWORD, URL);
        return createMetadataConnection(loginResult);
    }

    private static MetadataConnection createMetadataConnection(
            final LoginResult loginResult) throws ConnectionException {
        final ConnectorConfig config = new ConnectorConfig();
        config.setServiceEndpoint(loginResult.getMetadataServerUrl());
        config.setSessionId(loginResult.getSessionId());
        return new MetadataConnection(config);
    }

    private static LoginResult loginToSalesforce(
            final String username,
            final String password,
            final String loginUrl) throws ConnectionException {
        final ConnectorConfig config = new ConnectorConfig();
        config.setAuthEndpoint(loginUrl);
        config.setServiceEndpoint(loginUrl);
        config.setManualLogin(true);
        return (new EnterpriseConnection(config)).login(username, password);
    }
}
