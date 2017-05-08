package com.rest.tests.api.frwm.settings;

import com.rest.tests.api.frwm.rest.Settings;
import com.rest.tests.api.frwm.testcase.Testcase;
import com.rest.tests.api.frwm.testcase.TestcaseFactory;
import com.rest.tests.api.frwm.testcase.TestcaseType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.ini4j.Profile;
import org.ini4j.Wini;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLDecoder;
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
    private HashMap<String, String> commonHeaders;
    private boolean setUp;

    public TestParser(String filename, boolean setup) {
        this.setUp = setup;
        TestParser(filename);
        try {
            Settings sett = new Settings();
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    private void TestParser(String filename){
        this.file = filename;
        String[] names = file.split("\\\\");

        if (!this.setUp)
        {
            String tags = System.getenv("REST_APP_TAGS");
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

    public void TestParser(String filename, boolean setup) {
        this.setUp = setup;
        TestParser(filename);
    }

    public Testcase parseTestCases(JSONObject test) throws Exception {

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

        if (test.get("Headers") != null || commonHeaders != null)
        {
            JSONObject headers = (JSONObject) test.get("Headers");
            HashMap<?, ?> heads = updateHeaders(headers);
            testcase.setHeaders(heads);
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

    public ArrayList<Testcase> parseSuite() throws Exception {
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

            if (jsonObject.get("Headers") != null)
            {
                JSONObject commonHeaders = (JSONObject) jsonObject.get("Headers");

                this.commonHeaders = updateHeaders(commonHeaders);
            }

            JSONArray tests = (JSONArray)jsonObject.get("SetUp");

            testcases = parseTestCases(tests, url, TestcaseType.SETUP);

            tests = (JSONArray)jsonObject.get("Tests");

            testcases.addAll(parseTestCases(tests, url, TestcaseType.TEST));

            tests = (JSONArray)jsonObject.get("TearDown");

            testcases.addAll(parseTestCases(tests, url, TestcaseType.TEARDOWN));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return testcases;
    }

    private ArrayList<Testcase> parseTestCases(JSONArray tests, String url, TestcaseType type){

        ArrayList<Testcase> testcases = new ArrayList<Testcase>();

        if (tests != null) {

            try
            {
                Settings api = new Settings();
                this.commonHeaders = api.getTestcaseSettings("headers");
            }
            catch(Exception e)
            {
                System.out.println("There is no Settings/headers.frmw file.");
            }

            Iterator<JSONObject> iterator = tests.iterator();
            while(iterator.hasNext())
            {
                JSONObject test = (JSONObject)iterator.next();

                String nameTc = (String)test.get("Name");
                System.out.print("- " + nameTc);
                try {
                    Testcase tcs = parseTestCases(test);
                    if (tcs != null) {

                        switch (type){
                            case SETUP: tcs.setType(TestcaseType.SETUP); break;
                            case TEARDOWN: tcs.setType(TestcaseType.TEARDOWN); break;
                            case TEST: tcs.setType(TestcaseType.TEST); break;
                        }

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

        return testcases;
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

    public HashMap<String, String> getVariablesTests() throws Exception{

        String[] names = file.split("\\\\");
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

    private HashMap<String,String> updateHeaders(JSONObject headers)
    {
        HashMap<String, String> list;
        commonHeaders = ((list = new HashMap<>()) == null) ? list : commonHeaders;

        if (headers != null)
        {
            for (Object key : headers.keySet()) {
                String keyStr = (String)key;
                String keyvalue = (String)headers.get(keyStr);
                list.put(keyStr, keyvalue);
            }
        }
        return list;
    }
}
