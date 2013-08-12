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

This quickstart shows how to use BRMS to evaluate and determine whether approve or reject an employee reward request. This demo includes Human Task Integration, meaning the process waits for human task intervention to approve or reject the award before it continues the workflow.

This demo includes two `BPMN2` processes: 

1. Simple process - In this version, the award request is logged and then waits for approval or rejection. If the award is approved, an email is sent to the employee, and the process ends. If the award is rejected, the process ends without notification.
2. Extended process - In this version, the award request is logged and then waits for approval or rejection. If the award is approved, the information is filed in the employee personnel file, a congratulations email is sent to the employee, and the process ends. If the award is rejected, the rejection is noted in the employee file, a rejection is emailed to the employee, and the process ends.


Configure and Run the Quickstart
-------------------

This quickstart has more complex setup and configuration requirements than many of the other quickstarts. Please see the `Quick Start Guide` located in the `docs/` folder for complete instructions on how to configrre and run this quickstart. The file is provided in both PDF and ODT formats.

The following is a brief summary of the steps you will take to configure and run the quickstart. _Note: These steps are not meant to replace the complete instructions contained in the `docs/Quick Start Guide.odt` or `docs/Quick Start Guide.pdf` files!_

1. Download the following from the JBoss Customer Portal at <https://access.redhat.com/jbossnetwork/restricted/listSoftware.html> into the quickstart `installs/` directory:
    * BRMS (brms-p-5.3.1.GA-deployable-ee6.zip)	
    * EAP (jboss-eap-6.1.0.zip)
2. Run `init.sh` to install EAP 6 and deploy BRMS. Verify the output and make sure the command completes successfully.
3. Configure JBoss Developer Studio (JBDS).
    * Install the SOA tools.
    * Add the BRMS platform server runtime.
    * Import the project.
4. Run `mvn clean install` on the project to ensure it builds successfully.
5. Start the JBoss EAP server.
6. Login to JBoss BRMS at <http://localhost:8080/jboss-brms>.
7. Import the project repository `repository-export.zip` file from the `support/` directory.
8. Build and deploy project in BRM.
9. Login to BRMS Central at <http://localhost:8080/business-central>.
10. Start the process and view the JBoss EAP logs for results.

_Note: Windows users should see `support/windows/README` for installation procedures._

Supporting Articles
-------------------

[Rewards Demo Updated with EAP 6.1.0] (http://www.schabell.org/2013/05/jboss-brms-demo-eap-6.1.0.html)

[Rewards Demo Updated with EAP 6.1.0.Beta] (http://www.schabell.org/2013/04/red-hat-jboss-brms-rewards-demo-updated.html)

[Rewards Demo Updated with EAP 6.0.0] (http://www.schabell.org/2012/06/jboss-enterprise-brms-bpm-human-tasks.html)

[BPM Human Tasks Made Simple in the Rewards Approval Demo with video] (http://www.schabell.org/2012/06/jboss-enterprise-brms-bpm-human-tasks.html)

[From Zero to Testing, Setting Up the Rewards Demo in IDE with video] (http://www.schabell.org/2012/06/from-zero-to-testing-setting-up-jboss.html)

[How to setup SOA Tools in BRMS Example for JBoss Dev Studio 7] (http://www.schabell.org/2013/04/jboss-developer-studio-7-how-to-setup.html)

[How to setup SOA Tools in BRMS Example for JBoss Dev Studio 6] (http://www.schabell.org/2013/04/jboss-developer-studio-6-how-to-setup.html)

[How to setup SOA Tools in BRMS Example for JBoss Dev Studio 5] (http://www.schabell.org/2012/05/jboss-developer-studio-5-how-to-setup.html)

[How to add Eclipse BPMN2 Modeller project to JBoss Dev Studio 5] (http://www.schabell.org/2013/01/jbds-bpmn2-modeler-howto-install.html)

[Demo now available with Windows installation scripts] (http://www.schabell.org/2013/04/jboss-brms-demos-available-windows.html)


Released versions
-----------------

See the tagged releases for the following versions of the product:

- v1.9 is BRMS 5.3.1 deployable, JBDS 7.0.0.Beta1, running on JBoss EAP 6.1.

- v1.8 is BRMS 5.3.1 deployable, running on JBoss EAP 6.1.

- v1.7 is BRMS 5.3.1 deployable, running on JBoss EAP 6.1.0.Beta.

- v1.6 demo project Mavenized.

- v1.5 has Windows installation scripts. 

- v1.4 is updated patched designer war to fix end-of-line removal in code editor.

- v1.3 is BRMS 5.3.1 deployable, running on JBoss EAP 6, cleaner logging.

- v1.2 is BRMS 5.3.1 deployable, running on JBoss EAP 6.

- v1.0 is BRMS 5.3.0 standalone, running on JBoss EAP 5.


