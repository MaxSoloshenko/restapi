package com.rest.tests.api.frmw.settings;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.rest.tests.api.frmw.testcase.TC;
import com.rest.tests.api.frmw.testcase.TCSuite;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.testng.Assert;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by msolosh on 3/25/2016.
 */
public class TestParser {

    private String file;
    private TCSuite tcsuite;

    public TestParser(String filename) throws Exception {
        this.file = filename;
        ObjectMapper mapper = new ObjectMapper();

        try {
            JSONParser par = new JSONParser();
            Object obj = par.parse(new FileReader(filename));
            JSONObject jsonObject = (JSONObject)obj;
            Set<String> items = new HashSet<>(Arrays.asList("SetUp", "Tests"));
            Iterator<String> iterator = items.iterator();
            while(iterator.hasNext()) {

                String item = iterator.next();

                if (jsonObject.get(item) != null)
                {
                    JSONArray res = new JSONArray();
                    JSONArray arr = (JSONArray) jsonObject.get(item);
                    for (Object oj : arr)
                    {
                        JSONObject test = (JSONObject)oj;
                        if (test.get("Template") != null)
                        {
                            TreeMap content = new TreeMap<>();

                            for (Object e : test.entrySet()) {
                                Map.Entry entry = (Map.Entry) e;
                                content.put(String.valueOf(entry.getKey()), entry.getValue());
                            }
                            content.remove("Template");

                            JSONObject templ = (JSONObject)test.get("Template");
                            JSONObject body = getTemplate(templ);
                            test.remove("Template");
                            test.putAll(body);

                            for (Object e : content.entrySet()) {
                                Map.Entry entry = (Map.Entry) e;
                                test.put(String.valueOf(entry.getKey()), entry.getValue());
                            }

                        }
                        res.add(test);
                    }
                    jsonObject.remove(item);
                    jsonObject.put(item, res);
                }
            }

            tcsuite = mapper.readValue(jsonObject.toJSONString(), TCSuite.class);
            tcsuite.setFile(filename);

        } catch (JsonGenerationException e) {
            e.printStackTrace();
        }
    }

    private JSONObject getTemplate(JSONObject template) throws Exception {

        try
        {

            Path filePath = Paths.get(new File(getClass().getClassLoader().getResource("Templates").getFile()).getAbsolutePath(), (String)template.get("source"));

            if (!new File(filePath.toString()).exists()) {
                try {
                    throw new IOException("File not found " + filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            JSONParser par = new JSONParser();
            Object obj = par.parse(new FileReader(filePath.toString()));

            String body = obj.toString();

            if (template.keySet().size() > 1) {
                Set keysItr = template.keySet();
                Iterator<String> ir = keysItr.iterator();
                while (ir.hasNext()) {
                    String key = ir.next().toString();
                    Object value = template.get(key);
                    if (key.startsWith("$.")) {
                        DocumentContext doc = JsonPath.parse(body);
                        try {
                            doc.read(key);
                            doc.set(key, value);
                        } catch (PathNotFoundException e) {
                            doc.put(key.substring(0, key.lastIndexOf(".")), key.substring(key.lastIndexOf(".") + 1), value);
                        }
                        body = new Gson().toJson(doc.read("$"));
                    } else {
                        if (value instanceof Boolean) {
                            body = body.replace("\"${" + key + "}\"", value.toString());
                        } else if (value instanceof String) {
                            body = body.replace("${" + key + "}", value.toString());
                        }
                    }
                }
            }

            Assert.assertNotNull(body, "Template is not found: " + (String) template.get("source"));

            obj = par.parse(body);
            return (JSONObject) obj;
        }
        catch (PathNotFoundException path) {
            throw new Exception(path.getMessage() + " in source " + (String)template.get("source"));
        }
        catch (Exception e) {
            throw new Exception("File cannot be parsed because of Template missed file: " + (String)template.get("source"));
        }
    }

    public HashMap<String, String> getTSVariables()
    {
        HashMap<String, String> var = tcsuite.getVariables();
        HashMap<String, String> newvar = new HashMap<>();
        for (String key : var.keySet()) {
            String value = Tools.generateVariables((String)var.get(key));
            newvar.put(key.toLowerCase(), value);
            Tools.writeToFile(file, key.toLowerCase() + ":" + value + "\n");
        }
        tcsuite.setVariables(newvar);
        return newvar;
    }

    public void writeTestListIntoFile()
    {
        String log;
        JSONObject[] tests = tcsuite.getSetUp();
        log = Tools.printFixLineString("SetUp", "=");
        if (tests.length > 0)
            Tools.writeToFile(file, log + "\n");
        for (JSONObject set : tests)
        {
            Tools.writeToFile(file, (String)set.get("Name") + "\n");
        }
        tests = tcsuite.getTests();
        log = Tools.printFixLineString("Tests", "=");
        if (tests.length > 0)
            Tools.writeToFile(file, log + "\n");
        for (JSONObject set : tests)
        {
            Tools.writeToFile(file, (String)set.get("Name") + "\n");
        }
        tests = tcsuite.getTearDown();
        log = Tools.printFixLineString("TearDown", "=");
        if (tests.length > 0)
            Tools.writeToFile(file, log + "\n");
        for (JSONObject set : tests)
        {
            Tools.writeToFile(file, (String)set.get("Name") + "\n");
        }
    }
    public TCSuite getTcsuite()
    {
        return tcsuite;
    }

    public TC parseTest(JSONObject test) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        TC tc = mapper.readValue(test.toJSONString(), TC.class);
        tc.setName(tcsuite.getFile() + ":" + tc.getName());

        if (!tc.getUrl().startsWith("http"))
        {
            tc.setUrl(getServiceURL(tcsuite.getMicroservice()) + tc.getUrl());
        }
        return tc;
    }

    public String getServiceURL(String serviceName) throws IOException {
        Settings api = new Settings();
        String url = api.getKey(serviceName, "URL") +
                ":" +
                api.getKey(serviceName, "PORT") +
                api.getKey(serviceName, "MAPPING");

        return url;
    }
}
