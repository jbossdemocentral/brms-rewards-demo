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
 * Simple Rewards Process
 * 
 * Before launching this process start file, we assume you have the BRMS server started as this provides a task client for the
 * human task service.
 */
public class ProcessMain {

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
        // start a new process instance
        System.out.println("Starting Rewards process testing...");
        ksession.startProcess("org.jbpm.approval.rewards", params);
        System.out.println("Rewards process testing started and at first Human Task...");
    }

    private static KnowledgeBase readKnowledgeBase() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("rewardsapproval.bpmn2"), ResourceType.BPMN2);
        return kbuilder.newKnowledgeBase();
    }
}
