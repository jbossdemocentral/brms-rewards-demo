package org.jbpm.rewards;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.drools.KnowledgeBase;
import org.drools.SystemEventListenerFactory;
import org.drools.builder.ResourceType;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.hornetq.CommandBasedHornetQWSHumanTaskHandler;
import org.jbpm.task.service.hornetq.HornetQTaskClientConnector;
import org.jbpm.task.service.hornetq.HornetQTaskClientHandler;
import org.jbpm.test.JbpmJUnitTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * TODO: work in progress.
 * 
 * This is a sample file to integration test the Extended Rewards Approval process.
 * 
 * Note: you must have BRMS server running as this process test 
 *       uses a running HornetQ messaging system for its 
 *       integration test.
 */
public class RewardsApprovalExtendedIntegration extends JbpmJUnitTestCase {

	TaskClient client;

    @BeforeClass
    public static void setUpOnce() throws Exception {
    	// nothing yet.
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

		client = new TaskClient(new HornetQTaskClientConnector("taskClient" + UUID.randomUUID(), 
					new HornetQTaskClientHandler(SystemEventListenerFactory.getSystemEventListener())));
		client.connect("127.0.0.1", 5153);
    }

    @Override
    @After
    public void tearDown() throws Exception {
    	client.disconnect();
    	super.tearDown();
    }

    @Test
    public void submitEmployee() {
 	  	// load up the knowledge base
 	  	KnowledgeBase kbase = null;
 	  	
      // Use the local files.
      final Map<String, ResourceType> resources = new HashMap<String, ResourceType>();
      resources.put("rewardsapproval.bpmn2", ResourceType.BPMN2);
      
			try {
 	  		kbase = createKnowledgeBase(resources);
 	  	} catch (Exception e) {
 	  		// TODO Auto-generated catch block
 	  		e.printStackTrace();
 	  	}
 
 	  	StatefulKnowledgeSession ksession = createKnowledgeSession(kbase); 
 
 	  	// setup task client to use running BRMS server task client.
 	  	CommandBasedHornetQWSHumanTaskHandler handler = new CommandBasedHornetQWSHumanTaskHandler(ksession);
 	  	handler.setClient(client);
 
 	  	// register work items.
 	  	ksession.getWorkItemManager().registerWorkItemHandler("Log", new SystemOutWorkItemHandler());
 	  	ksession.getWorkItemManager().registerWorkItemHandler("Email", new SystemOutWorkItemHandler());
 	  	ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
       	
       	// setup our input request for processing.
 	  	final Map<String, Object> params = new HashMap<String, Object>();
 	  	params.put("employee", "erics");
 	  	params.put("reason", "Amazing demos for JBoss World");
 
 	  	// start a new process instance        
 	  	final ProcessInstance processInstance = ksession.startProcess("org.jbpm.approval.rewards", params);
 
 	  	// Check whether the process instance has completed successfully.
 	  	assertProcessInstanceActive(processInstance.getId(), ksession);
 	  	System.out.println("Process is active.");
 	  	assertNodeExists(processInstance, "Approve Reward");
 	  	System.out.println("Process had node we are looking for.");
 	  	assertNodeTriggered(processInstance.getId(), "Start", "Approve Reward");
 	  	System.out.println("Process has triggerd node we expect it to do.");
 	  	
 	  	System.out.println("Finished test.");
    }

}

