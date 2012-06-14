package org.jbpm.rewards;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.jboss.logging.Logger;
import org.jboss.serial.io.JBossObjectOutputStream;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.jbpm.task.AccessType;
import org.jbpm.task.TaskService;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.ContentData;
import org.jbpm.test.JbpmJUnitTestCase;
import org.junit.Test;

/**
 * This is a sample file to launch a process.
 */
public class RewardsApprovalTest extends JbpmJUnitTestCase {

	public RewardsApprovalTest() {
		super(true);
	}

	@Test
	public void rewardApprovedTest() {
		StatefulKnowledgeSession ksession = createKnowledgeSession("rewardsapproval.bpmn2");
		TaskService taskService = getTaskService(ksession);
	
		ksession.getWorkItemManager().registerWorkItemHandler("Log", new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email", new SystemOutWorkItemHandler());

		Map<String, Object> params = new HashMap<String, Object>();
		// initialize process parameters.
		params.put("employee", "erics");
		params.put("reason", "Amazing demos for JBoss World!");
		
		ProcessInstance processInstance = ksession.startProcess("org.jbpm.approval.rewards", params);

		// execute task by Mary from HR.
		List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("mary", new ArrayList<String>(), "en-UK");
		TaskSummary task = list.get(0);
		taskService.claim(task.getId(), "mary", new ArrayList<String>());
		taskService.start(task.getId(), "mary");
		
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put("explanation", "Great work");
		taskParams.put("outcome", "Approved");
		
		// Serialized and inserted.
		ContentData content = new ContentData();
		content.setAccessType(AccessType.Inline);
		content.setContent(getByteArrayFromObject(taskParams));
		
		// add results of task.
		taskService.complete(task.getId(), "mary", content);
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
		ksession.dispose();
	}
	
	@Test
	public void rewardRejectedTest() {
		StatefulKnowledgeSession ksession = createKnowledgeSession("rewardsapproval.bpmn2");
		TaskService taskService = getTaskService(ksession);
	
		ksession.getWorkItemManager().registerWorkItemHandler("Log", new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email", new SystemOutWorkItemHandler());

		Map<String, Object> params = new HashMap<String, Object>();
		// initialize process parameters.
		params.put("employee", "erics");
		params.put("reason", "Cool demo for JBoss World!");
		
		ProcessInstance processInstance = ksession.startProcess("org.jbpm.approval.rewards", params);

		// execute task by John from HR.
		List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("john", new ArrayList<String>(), "en-UK");
		TaskSummary task = list.get(0);
		taskService.claim(task.getId(), "john", new ArrayList<String>());
		taskService.start(task.getId(), "john");
		
		Map<String, Object> taskParams = new HashMap<String, Object>();
		taskParams.put("explanation", "Too complicated for me");
		taskParams.put("outcome", "Rejected");
		
		// Serialized and inserted.
		ContentData content = new ContentData();
		content.setAccessType(AccessType.Inline);
		content.setContent(getByteArrayFromObject(taskParams));
		
		// add results of task.
		taskService.complete(task.getId(), "john", content);
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
		ksession.dispose();
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
	        ObjectOutputStream oos = new JBossObjectOutputStream(baos);
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