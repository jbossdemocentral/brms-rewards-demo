package org.jbpm.rewards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
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
	public void testConstrain1() {
		StatefulKnowledgeSession ksession = createKnowledgeSession("rewardsapproval.bpmn2");
		TaskService taskService = getTaskService(ksession);
	
		ksession.getWorkItemManager().registerWorkItemHandler("Log", new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email", new SystemOutWorkItemHandler());

		Map<String, Object> params = new HashMap<String, Object>();
		// initialize process parameters.
		params.put("employee", "erics");
		params.put("reason", "Amazing demos for JBoss World!");
		
		ProcessInstance processInstance = ksession.startProcess("org.jbpm.approval.rewards", params);

		// execute task.
		List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("mary", new ArrayList<String>(), "en-UK");
		TaskSummary task = list.get(0);
		taskService.claim(task.getId(), "mary", new ArrayList<String>());
		taskService.start(task.getId(), "mary");
		ContentData results = new ContentData();
		// add results of task.
		taskService.complete(task.getId(), "mary", results);
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
//		ksession.dispose();
	}
	
}