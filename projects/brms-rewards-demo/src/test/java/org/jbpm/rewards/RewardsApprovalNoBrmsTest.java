package org.jbpm.rewards;



import java.util.HashMap;
import java.util.Map;

import org.drools.KnowledgeBase;
import org.drools.builder.ResourceType;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.jbpm.test.JbpmJUnitTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;



/**
* This is a sample file to unit test the Extended Rewards Approval process.
*/

public class RewardsApprovalNoBrmsTest extends JbpmJUnitTestCase {


	public RewardsApprovalNoBrmsTest() {

		super(true);

	}

	@BeforeClass
	public static void setUpOnce() throws Exception {
		// nothing yet.
	}

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@Override
	@After
	public void tearDown() throws Exception {

	  //ksession.dispose();
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
 			e.printStackTrace();
 	  }

 	  StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		getTaskService(ksession);

		// register work items.
		ksession.getWorkItemManager().registerWorkItemHandler("Log", new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email", new SystemOutWorkItemHandler());

		// setup our input request for processing.
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("employee", "erics");
		params.put("reason", "Amazing demos for JBoss World");
		System.out.println("=================================================");
		System.out.println("=      Unit Test: Submit Employee Test Case        =");
		System.out.println("=================================================");
		
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

