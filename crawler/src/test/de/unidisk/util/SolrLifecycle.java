package de.unidisk.util;

import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.response.CoreAdminResponse;
import org.apache.solr.common.params.CoreAdminParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class SolrLifecycle  {

    static final String CONTAINER = "solr_test";
    static final String CORE = "unidisc";
    static final String PORT = "8983";

    static String runCommand(String cmd) throws IOException, InterruptedException {
        Runtime run = Runtime.getRuntime();
        Process pr = run.exec(cmd);
        pr.waitFor();
        String response = "";
        try (BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()))) {
            String line = "";
            while ((line = buf.readLine()) != null) {
                response+=line+"\n";
            }
        }
        return response;
    }

    static boolean containerRunning() throws IOException, InterruptedException {
        final String up = runCommand("docker ps");
        final String[] lines = up.split("\n");
        for(String line : lines){
            final List<String> parts = Arrays.stream(line.split(" ")).filter(v-> !v.equals("")).collect(Collectors.toList());
            final String container = parts.get(parts.size() - 1);
            if(container.equals(CONTAINER)){
                return true;
            }
        }
        return false;
    }


    static boolean isCoreAvailable() {
        HttpSolrClient solrClient=new HttpSolrClient.Builder("http://localhost:"+PORT+"/solr").build();

        System.out.println("Requesting core list");
        CoreAdminRequest request = new CoreAdminRequest();
        request.setAction(CoreAdminParams.CoreAdminAction.STATUS);

        try {
            CoreAdminResponse cores = request.process(solrClient);

            for (int i = 0; i < cores.getCoreStatus().size(); i++) {
                final String coreName = cores.getCoreStatus().getName(i);
                if(coreName.equals(CORE))
                    return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return false;
    }

    public static void setup() throws IOException, InterruptedException {
        if(containerRunning()){
            return;
        }

        removeContainer();
        String cmd = "docker run -d -p "+PORT+":8983 --name " + CONTAINER + " solr:8 solr-create -c " + CORE;
        runCommand(cmd);


        int attempts = 0;
        while(attempts < 5){
            final boolean coreAvailable = isCoreAvailable();
            if(coreAvailable)
                return;
            // takes some time for the Solr Server to finish setup
            final int sleep = attempts * 3000;
            System.out.println("Core not available, waiting for " + sleep);
            Thread.sleep(sleep);
            attempts++;
        }
    }

    private static void removeContainer() throws IOException, InterruptedException {
        runCommand("docker rm -fv "+ CONTAINER);
    }


    public static void clean() throws IOException {

        HttpSolrClient solr = new HttpSolrClient.Builder("http://localhost:"+PORT+"/solr/"+CORE+"/").build();
        try {
          solr.deleteByQuery("*:*");
          solr.commit();
          removeContainer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
