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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.jbpm.process.workitem.wsht.SyncWSHumanTaskHandler;
import org.jbpm.task.AccessType;
import org.jbpm.task.TaskService;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.ContentData;
import org.jbpm.test.JbpmJUnitTestCase;
import org.junit.Test;

/**
 * This is a sample file to launch a process.
 */
public class RewardsApprovalExtendedTest extends JbpmJUnitTestCase {

    private static StatefulKnowledgeSession ksession;
    private static TaskService taskService;
    private static Map<String, Object> params;
    private static ProcessInstance processInstance;

    public RewardsApprovalExtendedTest() {
        super(true);
    }

    private void setupTestCase() {
        ksession = createKnowledgeSession("rewardsapprovalextended.bpmn2");
        taskService = getTaskService(ksession);

        // register human task work item.
        SyncWSHumanTaskHandler humanTaskHandler = new SyncWSHumanTaskHandler(
            taskService, ksession);
        humanTaskHandler.setLocal(true);
        humanTaskHandler.connect();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", humanTaskHandler);

        // register other work items
        ksession.getWorkItemManager().registerWorkItemHandler("Log", new SystemOutWorkItemHandler());
        ksession.getWorkItemManager().registerWorkItemHandler("Email", new SystemOutWorkItemHandler());

        params = new HashMap<String, Object>();
        // initialize process parameters.
        params.put("employee", "erics");
        params.put("reason", "Amazing demos for JBoss World!");

    }

    @Test
    public void rewardApprovedExtendedTest() {
        setupTestCase();

        // start process.
        processInstance = ksession.startProcess("org.jbpm.approval.rewards.extended", params);

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
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
        assertNodeTriggered(processInstance.getId(), "End Approved");
        ksession.dispose();
    }

    @Test
    public void rewardRejectedExtendedTest() {
        setupTestCase();

        // start process.
        processInstance = ksession.startProcess("org.jbpm.approval.rewards.extended", params);

        // execute task by John from HR.
        List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("john", new ArrayList<String>(), "en-UK");
        TaskSummary task = list.get(0);
        taskService.claim(task.getId(), "john", new ArrayList<String>());
        taskService.start(task.getId(), "john");

        Map<String, Object> taskParams = new HashMap<String, Object>();
        taskParams.put("Explanation", "Too complicated for me");
        taskParams.put("Outcome", "Rejected");

        // Serialized and inserted.
        ContentData content = new ContentData();
        content.setAccessType(AccessType.Inline);
        content.setContent(getByteArrayFromObject(taskParams));

        // add results of task.
        taskService.complete(task.getId(), "john", content);

        // test for completion and in correct end node.
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
        assertNodeTriggered(processInstance.getId(), "End Rejected");
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