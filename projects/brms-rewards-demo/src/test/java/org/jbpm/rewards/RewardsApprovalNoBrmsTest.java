package org.jbpm.rewards;



import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.drools.KnowledgeBase;
import org.drools.builder.ResourceType;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.jbpm.task.AccessType;
import org.jbpm.task.Status;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.ContentData;
import org.jbpm.task.service.TaskService;
import org.jbpm.task.service.TaskServiceSession;
import org.jbpm.task.service.local.LocalTaskService;
import org.jbpm.task.service.responsehandlers.BlockingTaskSummaryResponseHandler;
import org.jbpm.test.JbpmJUnitTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;



/**
* This is a sample file to unit test the Extended Rewards Approval process.
*/

public class RewardsApprovalNoBrmsTest extends JbpmJUnitTestCase {

	private static StatefulKnowledgeSession ksession;
	private static org.jbpm.task.TaskService taskService;
	private static Map<String, Object> params;
	private static ProcessInstance processInstance;

	public RewardsApprovalNoBrmsTest() {
		super(true);
	}

	@BeforeClass
	public static void setUpOnce() throws Exception {
		// nothing yet.
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}

	private void setupTestCase() {		
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

	    ksession = createKnowledgeSession(kbase);
		getTaskService(ksession);

		// register work items.
		ksession.getWorkItemManager().registerWorkItemHandler("Log", new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email", new SystemOutWorkItemHandler());

		params = new HashMap<String, Object>();
		// initialize process parameters.
		params.put("employee", "erics");
		params.put("reason", "Amazing demos for JBoss World!");		
		
		taskService = getTaskService(ksession);
	}

	
	
	@Test
	public void rewardApprovedTest() {
		setupTestCase();

		// start process.
		processInstance = ksession.startProcess("org.jbpm.approval.rewards", params);

		// execute task by Mary from HR.		
		List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("mary", new ArrayList<String>(), "en-UK");  // NP error here.
		TaskSummary task = list.get(0);
		
		LocalTaskService internalTaskService = (LocalTaskService) taskService;
		// do this the "normal" way instead of under the cover
		// ??? taskServiceSession.setTaskStatus(task.getId(), Status.Reserved);
		// ??? taskServiceSession.setTaskStatus(task.getId(), Status.InProgress);
		
		
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put("Explanation", "Great work");
		taskParams.put("Outcome", "Approved");
		
		// Serialized and inserted.
		ContentData content = new ContentData();
		content.setAccessType(AccessType.Inline);
		content.setContent(getByteArrayFromObject(taskParams));
		
		// add results of task.
		// do this the "normal" way instead of under the cover
		// ??? taskServiceSession.setTaskStatus(task.getId(), Status.Completed);

		// test for completion and in correct end node.
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
		assertNodeTriggered(processInstance.getId(), "End Approved");
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

