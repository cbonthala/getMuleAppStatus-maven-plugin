What?
====================
This is like a small add-on to the mule-maven-plugin. This is a small custom maven plugin to get MuleSoft application deployment status when an app is deploying to CloudHub.

Why?
====================
Current mule-maven-plugin(until release 3.2.2 as of today) does not consider application status when a mule app is updating through ARM. So, the maven build can be successful even when the application failed to start in CloudHub which is not good. This maven plugin is like an extension to mule-maven-plugin to retrieve app status and determine the DevOps pipeline to be a success or failure.

Keep in mind that if you did not use this kind of setup/logic to retrieve app status after deploying mule apps through mule-maven-plugin, Sometimes your maven build may be successful but the application might fail to start.

How?
====================
Use this below maven command with necessary arguments as applicable.
```mvn getMuleAppStatus:getMuleAppStatus```
You can modify this as per your company need and name the maven plugin with your company name. If that is the case, you can do
```mvn <yourCompany>:getMuleAppStatus```
Remember the goal name is "getMuleAppStatus".