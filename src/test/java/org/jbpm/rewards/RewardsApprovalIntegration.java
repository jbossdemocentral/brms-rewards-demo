/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.rewards;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.log4j.Logger;
import org.drools.KnowledgeBase;
import org.drools.SystemEventListenerFactory;
import org.drools.builder.ResourceType;
import org.drools.runtime.Environment;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.jbpm.task.AccessType;
import org.jbpm.task.Group;
import org.jbpm.task.User;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.ContentData;
import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.TaskService;
import org.jbpm.task.service.TaskServiceSession;
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
 * TODO: work in progress.
 * 
 * This is a sample file to integration test the Extended Rewards Approval process.
 * 
 * Note: you must have BRMS server running as this process test uses a running HornetQ messaging system for its integration
 * test.
 */
public class RewardsApprovalIntegration extends JbpmJUnitTestCase {

    private TaskClient client;
    private StatefulKnowledgeSession ksession;
    private static TaskService taskService;
    private static TaskServiceSession taskSession;
    private Map<String, Object> params;
    private BlockingTaskSummaryResponseHandler taskSummaryResponseHandler;
    private BlockingTaskOperationResponseHandler responseHandler;
    private CommandBasedHornetQWSHumanTaskHandler handler;
    private KnowledgeBase kbase;
    private Map<String, ResourceType> resources;

    private static EntityManagerFactory emf;
    private Environment env;
    private int sessionId;

    @BeforeClass
    public static void setUpOnce() throws Exception {
        // nothing yet.
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        // // create the entity manager factory and register it in the environment
        // emf = Persistence.createEntityManagerFactory( "org.jbpm.persistence.jpa" );
        // Environment env = KnowledgeBaseFactory.newEnvironment();
        // env.set( EnvironmentName.ENTITY_MANAGER_FACTORY, emf );
        //
        // // create a new knowledge session that uses JPA to store the runtime state
        // ksession = JPAKnowledgeService.newStatefulKnowledgeSession( kbase, null, env );
        // sessionId = ksession.getId();

        // load up the knowledge base
        kbase = null;

        // Use the local files.
        resources = new HashMap<String, ResourceType>();
        resources.put("rewardsapproval.bpmn2", ResourceType.BPMN2);

        try {
            kbase = createKnowledgeBase(resources);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        ksession = createKnowledgeSession(kbase);

        // ksession = createKnowledgeSession("rewardsapproval.bpmn2");

        // setup client to connect to human task service.
        client = new TaskClient(new HornetQTaskClientConnector("taskClient" + UUID.randomUUID(),
            new HornetQTaskClientHandler(SystemEventListenerFactory.getSystemEventListener())));
        client.connect("127.0.0.1", 5153);

        // setup task client to use running BRMS server task client.
        handler = new CommandBasedHornetQWSHumanTaskHandler(ksession);
        handler.setClient(client);

        // setup task users and groups.
        setupUsers();

        // register other work items
        // ksession.getWorkItemManager().registerWorkItemHandler("Log", new SystemOutWorkItemHandler());
        // ksession.getWorkItemManager().registerWorkItemHandler("Email", new SystemOutWorkItemHandler());
        ksession.getWorkItemManager().registerWorkItemHandler("Log", handler);
        ksession.getWorkItemManager().registerWorkItemHandler("Email", handler);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);

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

        // start a new process instance
        ProcessInstance processInstance = ksession.startProcess("org.jbpm.approval.rewards", params);

        // Check whether the process instance has completed successfully.
        assertProcessInstanceActive(processInstance.getId(), ksession);
        System.out.println("Process is active.");
        assertNodeExists(processInstance, "Approve Reward");
        System.out.println("Process had node we are looking for.");
        assertNodeTriggered(processInstance.getId(), "Start", "Approve Reward");
        System.out.println("Process has triggerd node we expect it to do.");

        // execute task by Mary from HR.
        BlockingTaskSummaryResponseHandler taskSummaryResponseHandler = new BlockingTaskSummaryResponseHandler();
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();

        // find our tasks for user.
        client.getTasksAssignedAsPotentialOwner("mary", "en-UK", taskSummaryResponseHandler);
        List<TaskSummary> tasks = taskSummaryResponseHandler.getResults();

        // claim and get started on task.
        System.out.println("Size of task list: " + tasks.size());
        client.claim(tasks.get(0).getId(), "mary", responseHandler);
        responseHandler.waitTillDone(1000);

        client.start(tasks.get(0).getId(), "mary", responseHandler);
        responseHandler.waitTillDone(1000);

        // Add task data to response.
        Map<String, Object> taskParams = new HashMap<String, Object>();
        taskParams.put("Explanation", "Great work");
        taskParams.put("Outcome", "Approved");

        // Serialized and inserted.
        ContentData content = new ContentData();
        content.setAccessType(AccessType.Inline);
        content.setContent(getByteArrayFromObject(taskParams));

        client.complete(tasks.get(0).getId(), "mary", content, responseHandler);
        responseHandler.waitTillDone(1000);

        // List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("mary", new ArrayList<String>(), "en-UK");
        // TaskSummary task = list.get(0);
        // taskService.claim(task.getId(), "mary", new ArrayList<String>());
        // taskService.start(task.getId(), "mary");
        //
        // Map<String, Object> taskParams = new HashMap<String, Object>();
        // taskParams.put("Explanation", "Great work");
        // taskParams.put("Outcome", "Approved");
        //
        // // Serialized and inserted.
        // ContentData content = new ContentData();
        // content.setAccessType(AccessType.Inline);
        // content.setContent(getByteArrayFromObject(taskParams));
        //
        // // add results of task.
        // taskService.complete(task.getId(), "mary", content);

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
    private static byte[] getByteArrayFromObject(Object obj) {
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

    /**
     * Sets up users in task service for tests.
     */
    private static void setupUsers() {

        emf = Persistence.createEntityManagerFactory("org.jbpm.task");
        taskService = new TaskService(emf, SystemEventListenerFactory.getSystemEventListener());
        taskSession = taskService.createSession();

        // now register new user and group for test.
        taskSession.addUser(new User("Administrator"));
        taskSession.addUser(new User("mary"));
        taskSession.addGroup(new Group("HR"));
    }
}
