JBoss BPM Rewards Demo Quickstart Guide
=======================================

Demo based on JBoss BRMS products.


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


Setup and Configuration
-----------------------

See Quick Start Guide in project as ODT and PDF for details on installation. For those that can't wait:

- see README in 'installs' directory

- add products 

- run 'init.sh' & read output

- read Quick Start Guide

- setup JBDS for project import, add JBoss EAP server

- import projects

- run 'mvn clean install' on project to build

- start JBoss EAP server

- login to BRM (http://localhost:8080/jboss-brms)

- import repository-export from support dir

- build and deploy project in BRM

- login to Business Central (http://localhost:8080/business-central)

- start process, process human tasks via forms provided, view JBoss EAP logs for results

Windows users see support/windows/README for installation.


Released versions
-----------------

See the tagged releases for the following versions of the product:

- v1.8 is BRMS 5.3.1 deployable, running on JBoss EAP 6.1.

- v1.7 is BRMS 5.3.1 deployable, running on JBoss EAP 6.1.0.Beta.

- v1.6 demo project Mavenized.

- v1.5 has Windows installation scripts. 

- v1.4 is updated patched designer war to fix end-of-line removal in code editor.

- v1.3 is BRMS 5.3.1 deployable, running on JBoss EAP 6, cleaner logging.

- v1.2 is BRMS 5.3.1 deployable, running on JBoss EAP 6.

- v1.0 is BRMS 5.3.0 standalone, running on JBoss EAP 5.

