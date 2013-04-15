package org.jbpm.rewards;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.drools.KnowledgeBase;
import org.drools.SystemEventListenerFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.hornetq.CommandBasedHornetQWSHumanTaskHandler;
import org.jbpm.task.service.hornetq.HornetQTaskClientConnector;
import org.jbpm.task.service.hornetq.HornetQTaskClientHandler;

/**
 * Rewards Extended Process
 * 
 * Before launching this process start file, we assume you have the BRMS server 
 * started as this provides a task client for the human task service.
 */
public class ProcessMainExtended {
	
	
	public static final void main(String[] args) throws Exception {
		// load up the knowledge base
		KnowledgeBase kbase = readKnowledgeBase();
		StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

		// setup task client to use running BRMS server task client.	        
        TaskClient client = new TaskClient(new HornetQTaskClientConnector("taskClient" + UUID.randomUUID(), 
				new HornetQTaskClientHandler(SystemEventListenerFactory.getSystemEventListener())));
        client.connect("127.0.0.1", 5153);
        
        // setup task client to use running BRMS server task client.
 	  	CommandBasedHornetQWSHumanTaskHandler handler = new CommandBasedHornetQWSHumanTaskHandler(ksession);
 	  	handler.setClient(client);
        
		// register work items.
		ksession.getWorkItemManager().registerWorkItemHandler("Log", new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email", new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
		
		// setup our input request for processing.
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("employee", "erics");
		params.put("reason", "Amazing demos for JBoss World");
		
		// start a new process instance
		System.out.println("Starting Rewards process testing...");
		ksession.startProcess("org.jbpm.approval.rewards.extended", params);
		System.out.println("Rewards process testing started and at first Human Task...");		

	}

	private static KnowledgeBase readKnowledgeBase() throws Exception {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource("rewardsapprovalextended.bpmn2"), ResourceType.BPMN2);
		return kbuilder.newKnowledgeBase();
	}

}