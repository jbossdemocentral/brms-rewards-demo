brms-rewards-demo: BRMS Rewards Demo
====================================
Author: Eric D. Schabell
Level: Beginner
Technologies: BRMS, JBPM
Summary: Demonstrates the use of Human Task integration with BPM
Prerequisites: 
Target Product: BRMS
Source: <https://github.com/rafabene/brms-rewards-demo>

What is it?
-----------

This quickstart shows how to use BRMS to evaluate a Rewards Approval.

It will use two BPMN2 processes (a single version and an extended version) that has a Human Task Integration. The Human Task allows the Rewards to be approved or rejected.


System requirements
-------------------

All you need to build this project is Java 6.0 (Java SDK 1.6) or better, Maven 3.0 or better.

The application this project produces is designed to be run on BRMS 5.3.1 and JBoss EAP 6.1

Configure your environment
--------------------------

- Acccess <https://access.redhat.com/jbossnetwork/restricted/listSoftware.html>
- Download BRMS Platform
  1. Under JBoss Enterprise Platforms, select the BRMS Platform product.
  2. Select version 5.3.1 in the Version field.
  3. Download JBoss BRMS 5.3.1 Deployable for EAP 6 (Please note that this is the deployable distribution, not the standalone one.)
  4. Now copy brms-p-5.3.1.GA-deployable-ee6.zip, to the brms-customer-evaluation-demo's installs folder. 
  5. Ensure that this file is executable by running:

        $ chmod +x <path-to-project>/installs/brms-p-5.3.1.GA-deployableee6.zip
  
- Download EAP 6 Platform:
  1. Under JBoss Enterprise Platforms, select the Application Platform product.
  2. Select version 6.1.0.Beta in the Version field.
  3. Download JBoss Aplication Platform 6.1.0.
  4. Now copy jboss-eap-6.1.0.zip, to the brms-rewards-demo's installs folder. 
  5. Ensure that this file is executable by running:

        $ chmod +x <path-to-project>/installs/jboss-eap-6.1.0.zip

- Lastly, from the brms-rewards-demo folder, run the init.sh script:

        $ ./init.sh
  

Build and Run the Quickstart
----------------------------

_NOTE: The following build command assumes you have configured your Environment._

1. Open a command line and navigate to the root directory of this quickstart (<repo_root>/projects/brms-rewards-demo).
2. Type this command to build and runt the tests:

        mvn clean test 

Investigate the Console Output
------------------------------

### Maven

Maven prints summary of performed tests into the console:

    -------------------------------------------------------
     T E S T S
    -------------------------------------------------------
    Running org.jbpm.rewards.RewardsApprovalExtendedTest
    Executing work item WorkItem 2 [name=Log, state=0, processInstanceId=1, parameters{Message=Reward for erics: award is Approved, explanation is (Great work)., TaskName=Log}]
    Executing work item WorkItem 3 [name=Log, state=0, processInstanceId=1, parameters{Message=Reward congrats for erics: Approved (Amazing demos for JBoss World!) added to personel file., TaskName=Log}]
    Executing work item WorkItem 4 [name=Email, state=0, processInstanceId=1, parameters{Body=Congratulations!, Subject=You received a reward!, To=erics@info.com, TaskName=Email, From=rewards@info.com}]
    Executing work item WorkItem 2 [name=Log, state=0, processInstanceId=1, parameters{Message=Reward for erics: award is Rejected, explanation is (Too complicated for me)., TaskName=Log}]
    Executing work item WorkItem 3 [name=Log, state=0, processInstanceId=1, parameters{Message=Rejected Reward for erics: Rejected (Amazing demos for JBoss World!) added to personel file., TaskName=Log}]
    Executing work item WorkItem 4 [name=Email, state=0, processInstanceId=1, parameters{Body=Bummer..., Subject=You're reward got rejected!, To=erics@info.com, TaskName=Email, From=rewards@info.com}]
    Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 7.731 sec
    
    Running org.jbpm.rewards.RewardsApprovalTest
    Executing work item WorkItem 2 [name=Log, state=0, processInstanceId=1, parameters{Message=Reward for erics: Outcome is Approved, Reason given is (Amazing demos for JBoss World!)., TaskName=Log}]
    Executing work item WorkItem 3 [name=Email, state=0, processInstanceId=1, parameters{Body=Congratulations!, Subject=You received a reward!, To=erics@info.com, TaskName=Email, From=rewards@info.com}]
    Executing work item WorkItem 2 [name=Log, state=0, processInstanceId=1, parameters{Message=Reward for erics: Outcome is Rejected, Reason given is (Amazing demos for JBoss World!)., TaskName=Log}]
    Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 2.286 sec
    
    Results :
    
    Tests run: 4, Failures: 0, Errors: 0, Skipped: 0

