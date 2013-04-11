package org.jbpm.rewards;

import java.util.HashMap;
import java.util.Map;

import org.drools.KnowledgeBase;
import org.drools.SystemEventListenerFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.jbpm.process.workitem.wsht.AsyncWSHumanTaskHandler;
import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.hornetq.HornetQTaskClientConnector;
import org.jbpm.task.service.hornetq.HornetQTaskClientHandler;

/**
 * Before launching this process start file, we assume you have the BRMS server 
 * started as this provides a task client for the human task service.
 */
public class ProcessMainExtended {
	
	public static final void main(String[] args) throws Exception {
		// load up the knowledge base
		KnowledgeBase kbase = readKnowledgeBase();
		StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

		// setup task client to use running BRMS server task client.
		TaskClient client = new TaskClient(new HornetQTaskClientConnector("taskClient",
                new HornetQTaskClientHandler(SystemEventListenerFactory.getSystemEventListener())));
        AsyncWSHumanTaskHandler handler = new AsyncWSHumanTaskHandler(client, ksession);
        handler.setConnection("127.0.0.1", 5153);
		
		// register work items.
		ksession.getWorkItemManager().registerWorkItemHandler("Log", new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email", new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
		
		// setup our input request for processing.
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("employee", "erics");
		params.put("reason", "Amazing demos for JBoss World");
		
		// start a new process instance
		ksession.startProcess("org.jbpm.approval.rewards.extended", params);		
	}

	private static KnowledgeBase readKnowledgeBase() throws Exception {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource("rewardsapprovalextended.bpmn2"), ResourceType.BPMN2);
		return kbuilder.newKnowledgeBase();
	}

	/**
	 * Attempt at local task server... complicated.
	 */
//	private static void setupTaskClient(StatefulKnowledgeSession ksession) {
//	    TaskServer server = new HornetQTaskServer(taskService, 5446);
//        Thread thread = new Thread(server);
//        thread.start();
//        // Waiting for the HornetQTask Server to come up".
//        while (!server.isRunning()) {
//
//            try {
//				Thread.sleep(50);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//        }
//        client = new TaskClient(new HornetQTaskClientConnector("task client",
//                new HornetQTaskClientHandler(SystemEventListenerFactory.getSystemEventListener())));
//        handler = new AsyncWSHumanTaskHandler(client, ksession);
//        handler.setConnection("127.0.0.1", 5446);
//	}
}