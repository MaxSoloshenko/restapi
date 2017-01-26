package com.javelin.tests.api.settings;

import com.javelin.tests.api.rest.Settings;
import com.javelin.tests.api.testcase.Testcase;
import com.javelin.tests.api.testcase.TestcaseFactory;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by msolosh on 3/25/2016.
 */
public class TestParser {

    private String file;
    private String name;
    private ArrayList<String> tags = new ArrayList<String>();
    private JSONArray commonTags;
    private boolean setUp;

    public TestParser(String filename) {
        TestParser(filename);
    }

    public TestParser(String filename, boolean setup) {
        this.setUp = setup;
        TestParser(filename);
    }

    private void TestParser(String filename){
        this.file = filename;

        if (!this.setUp)
        {
            String tags = System.getenv("JAVELIN_TEST_TAGS");
            if (tags != null && tags != "" && !tags.equalsIgnoreCase("all"))
            {
                for (String tag : tags.split(","))
                {
                    if (!tag.equals(""))
                    {
                        this.tags.add(tag);
                    }
                }
            }
        }
        this.name = filename;

    }

    public Testcase parseTestCase(JSONObject test) throws Exception {

        ArrayList<JSONObject> expectations = new ArrayList<JSONObject>();

        if (test.get("Enabled") != null && !(boolean) test.get("Enabled")){
            return null;
        }
        Testcase testcase = TestcaseFactory.generate((String) test.get("Method"));
        testcase.setNAME((String) test.get("Name"));
        testcase.setMETHOD((String) test.get("Method"));
        testcase.setURL((String) test.get("URL"));

        if (test.get("Body") != null) {
            if ((test.get("Body")) instanceof JSONArray)
            {
                JSONArray body = (JSONArray)test.get("Body");
                testcase.setBODY(body.toJSONString());
            }
            else if ((test.get("Body")) instanceof JSONObject)
            {
                JSONObject body = (JSONObject)test.get("Body");
                testcase.setBODY(body.toJSONString());
            }
        }

        if (test.get("Params") != null)
        {
            testcase.setPARAMS((JSONObject) test.get("Params"));
        }

        if (test.get("Boundary") != null)
        {
            testcase.setBOUNDARY((JSONObject) test.get("Boundary"));
        }

        if (test.get("Tags") != null)
        {
            testcase.setTAGS((JSONArray)test.get("Tags"));
        }

        if (test.get("Timeout") != null) {
            testcase.setTimeout((long)test.get("Timeout"));
        }

        if (test.get("Loop") != null) {
            Long vale = (long)test.get("Loop");
            int value = vale.intValue();
            testcase.setLoop(value);
        }

        if (test.get("LoopTimeout") != null) {
            testcase.setLoopTimeout((long)test.get("LoopTimeout"));
        }

        if (test.get("Expectations") != null)
        {
            JSONArray expects = (JSONArray)test.get("Expectations");
            Iterator<JSONObject> iterator = expects.iterator();

            while (iterator.hasNext()) {
                JSONObject expectObj = (JSONObject) iterator.next();
                expectations.add(expectObj);
            }
        }
        testcase.setEXPECTATIONS(expectations);

        return testcase;
    }

    private ArrayList<String> readTestSuiteFile(String filename) throws FileNotFoundException {

        ArrayList<String> testcases = new ArrayList<String>();
        String line;
        try (
                InputStream fis = new FileInputStream(filename);
                InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
                BufferedReader br = new BufferedReader(isr);
        ) {
            while ((line = br.readLine()) != null) {
                testcases.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return testcases;
    }

    public String getServiceURL(String serviceName) throws IOException {
        Settings api = new Settings();
        String url = api.getKey(serviceName, "URL") +
                ":" +
                api.getKey(serviceName, "PORT") +
                api.getKey(serviceName, "MAPPING");

        return url;
    }

    private boolean checkTags(Testcase tc){

        if (tc.getTAGS() != null)
        {
            JSONArray tag = tc.getTAGS();
            for (int i = 0; i < tag.size(); i++)
            {
                if (this.tags.contains((String)tag.get(i)))
                {
                    return true;
                }
            }
        }

        if (commonTags != null) {
            for (int i = 0; i < commonTags.size(); i++) {
                if (this.tags.contains((String) commonTags.get(i))) {
                    return true;
                }
            }
        }

        return false;
    }

    private ArrayList<Testcase> parseJsonSuite() throws Exception {
        ArrayList<Testcase> testcases = new ArrayList<Testcase>();
        JSONParser par = new JSONParser();
        String url;

        try {
            System.out.println("Parse file " + file);
            System.out.println("Test cases:");
            Object obj = par.parse(new FileReader(file));
            JSONObject jsonObject = (JSONObject)obj;

            String serviceName = (String)jsonObject.get("Microservice");
            url = getServiceURL(serviceName);

            commonTags = (JSONArray)jsonObject.get("Tags");

            JSONArray tests = (JSONArray)jsonObject.get("Tests");
            if (tests != null) {
                Iterator<JSONObject> iterator = tests.iterator();
                while(iterator.hasNext())
                {
                    JSONObject test = (JSONObject)iterator.next();

                    String nameTc = (String)test.get("Name");
                    System.out.print("- " + nameTc);
                    try {
                        Testcase tcs = parseTestCase(test);
                        if (tcs != null) {
                            tcs.setNAME(name + ":" + tcs.getNAME());
                            if (!tcs.getURL().startsWith("http"))
                                tcs.setURL(url + tcs.getURL());

                            if ((this.tags.size() == 0) ||
                                    (checkTags(tcs)) || setUp)
                            {
                                testcases.add(tcs);
                            }

                            System.out.println(" - OK");
                        }
                        else
                        {
                            System.out.println(" - DISABLED");
                        }
                    } catch (Exception e) {
                        System.out.println(" - SKIPPED");
                        System.out.println(e.getMessage());
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return testcases;
    }

    public ArrayList<Testcase> parseSuite() throws Exception {
        if (name.endsWith(".json"))
            return parseJsonSuite();
        return null;
    }

    private MultipartEntityBuilder readToMap(JSONArray params) throws Exception {
        MultipartEntityBuilder  reqEntity = MultipartEntityBuilder.create();
        reqEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        Iterator<JSONObject> iterator = params.iterator();
        while(iterator.hasNext())
        {
            JSONObject param = (JSONObject)iterator.next();
            String type = (String)param.get("type");
            if (type.equals("string"))
            {
                String key = (String)param.get("key");
                String value = (String)param.get("value");
                reqEntity.addPart(key, new StringBody(value));
            }
            else if (type.equals("file"))
            {
                String key = (String)param.get("key");
                String value = (String)param.get("value");
                String filename = new Settings().getClassLoader().getResource("UploadFiles").getPath() + "/" + value;
                File file = new File(filename);
                FileBody uploadFilePart = new FileBody(file);
                reqEntity.addPart(key, uploadFilePart);
            }
            else {
                System.out.println(
                        String.format(">>>>> Unknown type of Params: '%s'. " +
                                "This param is going to be skipped.\n" +
                                "Known types of params: string, file.", type));
            }
        }
        return reqEntity;
    }

    private static JSONObject convertToJSONobj(String line){

        String[] params = line.split(",");
        JSONObject obj = new JSONObject();
        for (int k =0; k < params.length; k++)
        {
            obj.put(params[k].split(":")[0], params[k].split(":")[1]);
        }

        return obj;
    }

    private static MultipartEntityBuilder readToMap(String params) throws Exception {
        MultipartEntityBuilder  reqEntity = MultipartEntityBuilder.create();
        reqEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        String pars[] = params.split(",");
        String key;
        String value;
        for (int k = 0; k < pars.length; k++)
        {
            key = pars[k].split(":")[0];
            value = pars[k].split(":")[1];
            if (value.startsWith("\"")) {
                value = value.substring(1, value.length() - 1);
                key = key.substring(1, key.length() - 1);
                reqEntity.addPart(key, new StringBody(value));
            }
            else if (value.startsWith("FILE"))
            {
                String filename = new Settings().getClassLoader().getResource("UploadFiles").getPath() + "/"
                        + value.substring(5, value.length() - 1);
                File file = new File(filename);
                FileBody uploadFilePart = new FileBody(file);
                key = key.substring(1, key.length() - 1);
                reqEntity.addPart(key, uploadFilePart);
            }
        }

        return reqEntity;
    }

    private HashMap<String, String> getVariablesFromPlainTests() throws FileNotFoundException {
        ArrayList<String> tests = readTestSuiteFile(file);
        HashMap<String, String> fileMap = new HashMap<String, String>();
        for (String test : tests) // read each line in file
        {
            if (test.startsWith("*"))
            {
                String value = test.substring(1).split("=")[1];
                value = Tools.generateVariables(value);
                fileMap.put(test.substring(1).split("=")[0],
                        value);
            }
        }
        return fileMap;
    }

    public HashMap<String, String> getVariablesTests() throws Exception{

        String[] names = file.split("\\\\");
        String name = names[names.length - 1];
        if (name.endsWith(".json"))
        {
            return getVariablesFromJsonTests();
        }
        else if (name.endsWith(".suite")) {
            return getVariablesFromPlainTests();
        }
        return null;
    }

    private HashMap<String, String> getVariablesFromJsonTests() throws Exception {
        HashMap<String, String> fileMap = null;
        JSONParser par = new JSONParser();

        Object obj = par.parse(new FileReader(file));
        JSONObject jsonObject = (JSONObject)obj;

        fileMap = new HashMap<String, String>();
        if (jsonObject.get("Variables") != null)
        {
            System.out.println("VARIABLES:");
            JSONObject variables = (JSONObject)jsonObject.get("Variables");

            Set<String> keys = variables.keySet();
            Iterator<String> as = keys.iterator();
            while (as.hasNext())
            {
                String key = as.next().toString();
                String value = (String)variables.get(key);
                value = Tools.generateVariables(value);
                System.out.println(key + ": " + value);
                fileMap.put(key, value);
            }
        }

        return fileMap;
    }
}
