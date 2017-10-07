package com.rest.tests.api.frwm.settings;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.rest.tests.api.frwm.testcase.*;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.codehaus.jackson.map.ObjectMapper;
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
//@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = As.PROPERTY, property = "@class")
public class TestParser {

    private String file;
    private ArrayList<String> tags = new ArrayList<String>();
    private HashMap<String, String> commonHeaders;
    private boolean setUp;
    private TCSuite tcsuite;

    public TestParser(String filename)
    {
        this.file = filename;
        ObjectMapper mapper = new ObjectMapper();

        try {
            JSONParser par = new JSONParser();
            Object obj = par.parse(new FileReader(filename));
            JSONObject jsonObject = (JSONObject)obj;

            tcsuite = mapper.readValue(jsonObject.toJSONString(), TCSuite.class);
            tcsuite.setFile(filename);

        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, String> getTSVariables()
    {
        HashMap<String, String> var = tcsuite.getVariables();
        for (String key : var.keySet()) {
            String value = Tools.generateVariables((String)var.get(key));
            var.put(key, value);
            Tools.writeToFile(file, key + ":" + value + "\n");
        }
        tcsuite.setVariables(var);
        return var;
    }

    public TCSuite getTcsuite()
    {
        return tcsuite;
    }

    public TC parseTest(JSONObject test)
    {
        ObjectMapper mapper = new ObjectMapper();

        try {
//            System.out.println(test.toJSONString());
            TC tc = mapper.readValue(test.toJSONString(), TC.class);
            tc.setName(tcsuite.getFile() + ":" + tc.getName());

            if (!tc.getUrl().startsWith("http"))
            {
                tc.setUrl(getServiceURL(tcsuite.getMicroservice()) + tc.getUrl());
            }
            return tc;

        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

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
//        String[] names = file.split("\\\\");

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
//        this.name = filename;
    }


    public String getServiceURL(String serviceName) throws IOException {
        Settings api = new Settings();
        String url = api.getKey(serviceName, "URL") +
                ":" +
                api.getKey(serviceName, "PORT") +
                api.getKey(serviceName, "MAPPING");

        return url;
    }


    public HashMap<String, String> getVariablesTests() throws Exception{

//        String[] names = file.split("\\\\");
        HashMap<String, String> fileMap = null;
        JSONParser par = new JSONParser();

        Object obj = par.parse(new FileReader(file));
        JSONObject jsonObject = (JSONObject)obj;

        fileMap = new HashMap<String, String>();
        if (jsonObject.get("Variables") != null)
        {
//            System.out.println("VARIABLES:");
            JSONObject variables = (JSONObject)jsonObject.get("Variables");

            Set<String> keys = variables.keySet();
            Iterator<String> as = keys.iterator();
            while (as.hasNext())
            {
                String key = as.next().toString();
                String value = (String)variables.get(key);
                value = Tools.generateVariables(value);
//                System.out.println(key + ": " + value);
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
