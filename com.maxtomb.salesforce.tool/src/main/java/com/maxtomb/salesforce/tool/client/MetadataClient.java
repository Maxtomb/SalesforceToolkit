package com.maxtomb.salesforce.tool.client;

import com.sforce.soap.metadata.*;
import com.sforce.soap.metadata.Error;
import com.sforce.ws.ConnectorConfig;

/**
 * Sample that logs in and creates a custom object through the metadata API
 */
public class MetadataClient {
    private MetadataConnection metadataConnection;
    static final String USERNAME = "toiklaun@gmail.com";
	static final String PASSWORD = "xiaohei@0jSixZaFJ5NfJk6XQeWGF8QZ6";
    // one second in milliseconds
    private static final long ONE_SECOND = 1000;

    public MetadataClient() {
    }

    public static void main(String[] args) throws Exception {
    	MetadataClient crudSample = new MetadataClient();
        crudSample.runCreate();
    }

    /**
     * Create a custom object. This method demonstrates usage of the
     * create() and checkStatus() calls.
     *
     * @param uniqueName Custom object name should be unique.
     */
    public void createCustomObjectSync(final String uniqueName, String desc, CustomField[] fieldArray) throws Exception {
        final String label = uniqueName;
        CustomObject co = new CustomObject();
        co.setFullName(uniqueName);
        co.setDeploymentStatus(DeploymentStatus.Deployed);
        co.setDescription(desc);
        co.setEnableActivities(true);
        co.setLabel(label);
        co.setPluralLabel(label + "s");
        co.setSharingModel(SharingModel.ReadWrite);

        // The name field appears in page layouts, related lists, and elsewhere.
        CustomField nf = new CustomField();
        nf.setType(FieldType.Text);
        nf.setDescription("The custom object identifier on page layouts, related lists etc");
        nf.setLabel(label);
        nf.setFullName("standard");
        co.setNameField(nf);
        
        co.setFields(fieldArray);
        
        SaveResult[] results = metadataConnection
                .createMetadata(new Metadata[] { co });

        for (SaveResult r : results) {
            if (r.isSuccess()) {
                System.out.println("Created component: " + r.getFullName());
            } else {
                System.out
                        .println("Errors were encountered while creating "
                                + r.getFullName());
                for (Error e : r.getErrors()) {
                    System.out.println("Error message: " + e.getMessage());
                    System.out.println("Status code: " + e.getStatusCode());
                }
            }
        }
    }

    private void runCreate() throws Exception {
    	ConnectorConfig config = new ConnectorConfig();
		config.setUsername(USERNAME);
		config.setPassword(PASSWORD);
//        metadataConnection = Connector.newConnection(config);
		metadataConnection = MetadataLoginUtil.login();
        
        CustomField cf1 = new CustomField();
        cf1.setType(FieldType.Text);
        cf1.setLabel("ABC1__c");
        cf1.setLength(20);
        cf1.setFullName("standard2__c");
        
        CustomField cf2 = new CustomField();
        cf2.setType(FieldType.Text);
        cf2.setLabel("ABC2__c");
        cf2.setLength(20);
        cf2.setFullName("standard2__c");
        
        // Custom objects and fields must have __c suffix in the full name.
        final String uniqueObjectName = "xiaohei6__c";
        createCustomObjectSync(uniqueObjectName,"test",new CustomField[]{cf1,cf2});
    }
}
