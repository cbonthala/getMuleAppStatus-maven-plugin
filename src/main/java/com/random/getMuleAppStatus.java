package com.random;


import com.google.gson.*;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;


@Mojo(name = "getMuleAppStatus", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class getMuleAppStatus
        extends AbstractMojo {
    @Parameter(property = "applicationName", required = true)
    private String applicationName;

    @Parameter(property = "username", required = true)
    private String username;

    @Parameter(property = "password", required = true)
    private String password;

    @Parameter(property = "environment", required = true)
    private String environment;

    public void execute()
            throws MojoExecutionException {

        String envId = "";
        if (environment.equals("DEVELOPMENT")) {
            envId="";
        }
        if (environment.equals("STAGING")) {
            envId="";
        }
        if (environment.equals("PRODUCTION")) {
            envId="";
        }
        String onUrl = "https://anypoint.mulesoft.com/cloudhub/api/v2/applications/" + applicationName;
        String anypointCredentials = username + ":" + password;
        String basicAuth = DatatypeConverter.printBase64Binary(anypointCredentials.getBytes());

        try {
            while (true) {
                getLog().info("Waiting 15 Seconds");
                Thread.sleep(15000);
                getLog().info("Calling "+onUrl);
                URL url = new URL(onUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("X-ANYPNT-ENV-ID", envId);
                conn.setRequestProperty("Authorization", "Basic " + basicAuth);

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                StringBuilder response = new StringBuilder();
                String currentLine;

                while ((currentLine = in.readLine()) != null)
                    response.append(currentLine);

                in.close();
                JsonObject responseJson = new JsonParser().parse(response.toString()).getAsJsonObject();
                //System.out.println(responseJson);
                String appStatus = responseJson.get("status").getAsString();
                String updatingAppStatusE = responseJson.get("deploymentUpdateStatus") != null ? responseJson.get("deploymentUpdateStatus").getAsString() : null;

                if ((appStatus.equals("STARTED")) && (updatingAppStatusE == null)) {
                    getLog().info("------------------------------------------------------------------------");
                    getLog().info("APPLICATION STARTED");
                    getLog().info("------------------------------------------------------------------------");
                    break;
                } else if ((!appStatus.equals("STARTED")) && (!appStatus.equals("DEPLOYING"))) {
                    getLog().error("Mule application failed to deploy/start");
                    throw new MojoExecutionException("Mule application failed to deploy/start");
                } else if ((appStatus.equals("STARTED")) && (updatingAppStatusE != null) && (!updatingAppStatusE.equals("DEPLOYING"))) {
                    getLog().error("Mule application failed to deploy/start");
                    throw new MojoExecutionException("Mule application failed to deploy/start");
                }

                getLog().info("Still waiting for the application to start");

            }
        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage());
        }

    }
}