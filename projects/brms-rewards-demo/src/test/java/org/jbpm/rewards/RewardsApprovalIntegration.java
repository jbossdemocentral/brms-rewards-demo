package org.jbpm.rewards;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.drools.KnowledgeBase;
import org.drools.SystemEventListenerFactory;
import org.drools.builder.ResourceType;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.jbpm.process.workitem.wsht.SyncWSHumanTaskHandler;
import org.jbpm.task.AccessType;
import org.jbpm.task.TaskService;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.ContentData;
import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.TaskClientHandler.TaskSummaryResponseHandler;
import org.jbpm.task.service.hornetq.CommandBasedHornetQWSHumanTaskHandler;
import org.jbpm.task.service.hornetq.HornetQTaskClientConnector;
import org.jbpm.task.service.hornetq.HornetQTaskClientHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskOperationResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskSummaryResponseHandler;
import org.jbpm.test.JbpmJUnitTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This is a sample file to integration test the Extended Rewards Approval process.
 * 
 * Note: you must have BRMS server running as this process test 
 *       uses a running HornetQ messaging system for its 
 *       integration test.
 */
public class RewardsApprovalIntegration extends JbpmJUnitTestCase {

	private static TaskClient client;	
	private static StatefulKnowledgeSession ksession;
	private static TaskService taskService;
	private static Map<String, Object> params;
	private static ProcessInstance processInstance;
	private static BlockingTaskSummaryResponseHandler taskSummaryResponseHandler;
	private static BlockingTaskOperationResponseHandler responseHandler;
 
	@BeforeClass
    public static void setUpOnce() throws Exception {
    	// nothing yet.
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

		ksession = createKnowledgeSession("rewardsapproval.bpmn2");
		taskService = getTaskService(ksession);
		
		// setup client to connect to human task service.
		client = new TaskClient(new HornetQTaskClientConnector("taskClient" + UUID.randomUUID(), 
				new HornetQTaskClientHandler(SystemEventListenerFactory.getSystemEventListener())));
		client.connect("127.0.0.1", 5153);
	
		// register human task work item.
		taskSummaryResponseHandler = new BlockingTaskSummaryResponseHandler();
		responseHandler = new BlockingTaskOperationResponseHandler();
						
		// register other work items
		ksession.getWorkItemManager().registerWorkItemHandler("Log", new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email", new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());
		
		// initialize process parameters.
		params = new HashMap<String, Object>();		
		params.put("employee", "erics");
		params.put("reason", "Amazing demos for JBoss World!");
    		
		
    }

    @Override
    @After
    public void tearDown() throws Exception {
    	client.disconnect();
    	super.tearDown();
    }

    @Test
    public void rewardApprovedIntegrationTest() {
 
 	  	// Use the local files.
 	  	final Map<String, ResourceType> resources = new HashMap<String, ResourceType>();
 	  	resources.put("rewardsapproval.bpmn2", ResourceType.BPMN2);
       	
 	  	// start a new process instance        
 	  	processInstance = ksession.startProcess("org.jbpm.approval.rewards", params);
 	  	
 	  	// Check whether the process instance has completed successfully.
 	  	assertProcessInstanceActive(processInstance.getId(), ksession);
 	  	System.out.println("Process is active.");
 	  	assertNodeExists(processInstance, "Approve Reward");
 	  	System.out.println("Process had node we are looking for.");
 	  	assertNodeTriggered(processInstance.getId(), "Start", "Approve Reward");
 	  	System.out.println("Process has triggerd node we expect it to do.");
 	  	
 	  	// execute task by Mary from HR.
 		List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("mary", new ArrayList<String>(), "en-UK");
		TaskSummary task = list.get(0);
		taskService.claim(task.getId(), "mary", new ArrayList<String>());
		taskService.start(task.getId(), "mary");
		
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put("Explanation", "Great work");
		taskParams.put("Outcome", "Approved");
		
		// Serialized and inserted.
		ContentData content = new ContentData();
		content.setAccessType(AccessType.Inline);
		content.setContent(getByteArrayFromObject(taskParams));
		
		// add results of task.
		taskService.complete(task.getId(), "mary", content);

		// test for completion and in correct end node.
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
		assertNodeTriggered(processInstance.getId(), "End Approved");
 	  	 	  	
 	  	System.out.println("Finished test.");
    }
    
	/**
	 * Converts an object to a serialized byte array.
	 * 
	 * @param obj Object to be converted.
	 * @return byte[] Serialized array representing the object.
	 */
	public static byte[] getByteArrayFromObject(Object obj) {
	    byte[] result = null;
	       
	    try {
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        ObjectOutputStream oos = new ObjectOutputStream(baos);
	        oos.writeObject(obj);
	        oos.flush();
	        oos.close();
	        baos.close();
	        result = baos.toByteArray();
	    } catch (IOException ioEx) {
	        Logger.getLogger("UtilityMethods").error("Error converting object to byteArray", ioEx);
	    }
	        
	    return result;
	}

}

