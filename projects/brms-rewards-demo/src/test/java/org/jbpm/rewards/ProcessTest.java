package org.jbpm.rewards;

import java.util.HashMap;
import java.util.Map;

import org.drools.KnowledgeBase;
import org.drools.SystemEventListenerFactory;
import org.drools.builder.ResourceType;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.jbpm.process.workitem.wsht.AsyncWSHumanTaskHandler;
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
 * This is a sample file to test a process.
 */
public class ProcessTest extends JbpmJUnitTestCase {
    private static final boolean USE_RESOURCES_FROM_GUVNOR = false;
    private static final String GUVNOR_URL = "http://localhost:8080/jboss-brms";
    private static final String GUVNOR_USER_NAME = "admin";
    private static final String GUVNOR_PASSWORD = "admin";
    private static final String[] GUVNOR_PACKAGES = { "mortgages" };

    private static final String LOCAL_PROCESS_NAME = "rewardsapproval.bpmn2";
    private static final String LOCAL_PROCESS_NAME_EXTENDED = "rewardsapprovalextended.bpmn2";

    private static StatefulKnowledgeSession ksession;

    @BeforeClass
    public static void setUpOnce() throws Exception {
    	// nothing yet.
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

//        // Set up the knowledge session with the process and handlers.
//        KnowledgeBase kbase = null;
//        if (USE_RESOURCES_FROM_GUVNOR) {
//            kbase = createKnowledgeBaseGuvnor(false, GUVNOR_URL, GUVNOR_USER_NAME, GUVNOR_PASSWORD, GUVNOR_PACKAGES);
//        } else {
//            // Use the local files.
//            final Map<String, ResourceType> resources = new HashMap<String, ResourceType>();
//            resources.put(LOCAL_PROCESS_NAME, ResourceType.BPMN2);
//            kbase = createKnowledgeBase(resources);
//        }
//		// setup task client to use running BRMS server task client.
//		TaskClient client = new TaskClient(new HornetQTaskClientConnector("taskClient",
//                new HornetQTaskClientHandler(SystemEventListenerFactory.getSystemEventListener())));
//        AsyncWSHumanTaskHandler handler = new AsyncWSHumanTaskHandler(client, ksession);
//        handler.setConnection("127.0.0.1", 5446);
//        
//        ksession = createKnowledgeSession(kbase);
//        ksession.getWorkItemManager().registerWorkItemHandler("Log", new SystemOutWorkItemHandler());
//		ksession.getWorkItemManager().registerWorkItemHandler("Email", new SystemOutWorkItemHandler());
//		ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

		// setup task client to use running BRMS server task client.
		TaskClient client = new TaskClient(new HornetQTaskClientConnector("taskClient",
                new HornetQTaskClientHandler(SystemEventListenerFactory.getSystemEventListener())));
        client.connect("127.0.0.1", 5153);
        CommandBasedHornetQWSHumanTaskHandler handler = new CommandBasedHornetQWSHumanTaskHandler(ksession);
    	handler.setClient(client);
    	handler.connect();

    	// register work items.
		ksession.getWorkItemManager().registerWorkItemHandler("Log", new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email", new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
    	
    	// setup our input request for processing.
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("employee", "erics");
		params.put("reason", "Amazing demos for JBoss World");
		
        System.out.println("=================================================");
        System.out.println("= Starting Process Submit Employee Test Case.   =");
        System.out.println("=================================================");
		// start a new process instance        
        final ProcessInstance processInstance = ksession.startProcess("org.jbpm.approval.rewards", params);

        // Check whether the process instance has completed successfully.
        assertProcessInstanceActive(processInstance.getId(), ksession);
        assertNodeExists(processInstance, "Approve Reward");
        assertNodeTriggered(processInstance.getId(), "Start", "Approve Reward");
    }

}
