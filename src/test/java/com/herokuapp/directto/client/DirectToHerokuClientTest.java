package com.herokuapp.directto.client;

import com.herokuapp.directto.client.models.Pipeline;
import org.junit.Test;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Ryan Brainard
 */
public class DirectToHerokuClientTest {

    private final String apiKey = System.getProperty("heroku.apiKey");
    private final String appName = System.getProperty("heroku.appName");
    private final String warFilePath = System.getProperty("heroku.warFile");
    private final DirectToHerokuClient client = new DirectToHerokuClient("http", "direct-to.herokuapp.com", 80, apiKey);
//    private final DirectToHerokuClient client = new DirectToHerokuClient("http", "localhost", 8080, apiKey);

    @Test
    public void testGetPipelineNames() throws Exception {
        final Collection<String> pipelineNames = client.getPipelineNames();
        assertTrue(pipelineNames.contains("war"));
        assertTrue(pipelineNames.contains("fatjar"));
    }

    @Test
    public void testGetPipeline() throws Exception {
        final Pipeline pipeline = client.getPipeline("war");
        assertEquals("war", pipeline.getName());
        assertEquals("POST", pipeline.getCall().getMethod());
        assertEquals("/direct/<your app>/war", pipeline.getCall().getUrl());
        assertEquals("Directly deploy a war that will be executed with tomcat runner", pipeline.getManifest().getDescription());
        assertEquals("The war file to be deployed alongside tomcat runner", pipeline.getManifest().getRequiredFileInfo().get("war"));
    }

    @Test
    public void testDeploy() throws Exception {
        final File warFile = new File(warFilePath);
        assertTrue("Precondition", warFile.exists());

        final Map<String, File> files = new HashMap<String, File>(1);
        files.put("war", warFile);

        assertEquals("success", client.deploy("war", appName, files).get("status"));
    }
}