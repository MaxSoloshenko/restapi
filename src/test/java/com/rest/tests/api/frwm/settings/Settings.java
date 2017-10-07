package com.rest.tests.api.frwm.settings;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.rest.tests.api.frwm.testcase.TCSuite;
import org.apache.commons.lang3.ArrayUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.ini4j.Profile;
import org.ini4j.Wini;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URLDecoder;
import java.util.*;

/**
 * Created by msolosh on 1/25/2016.
 */
@SuppressWarnings("ALL")
public class Settings {

    Wini resource;
    ClassLoader classLoader = getClass().getClassLoader();

    private static final String properties = "Settings/APIUrls.properties";
    private static final String headers = "Settings/logging.frmw";
    private static final String variables = "Settings/variables.json";
    private String[] tags = null;

    Settings(String fileName) throws IOException {

        //Get file from resources folder

        File file = new File(classLoader.getResource(fileName).getFile());

        this.resource = new Wini(file);
    }

    public Settings() throws IOException {

        //Get file from resources folder
        String path = null;
        try {
            path = URLDecoder.decode(classLoader.getResource(properties).getFile().toString(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        File file = new File(path);

        this.resource = new Wini(file);

        String tags = System.getenv("REST_APP_TAGS");
        if (tags != null && tags != "" && !tags.equalsIgnoreCase("all"))
        {
            this.tags = tags.split(",");
        }
    }

    public String[] getTags()
    {
        return tags;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public String getKey(String section, String key){

        if (System.getenv("REST_APP_API_" + key) != null)
            return System.getenv("REST_APP_API_" + key);

        String url = resource.get(section, key);

        if (url != null)
            return url;
        else {
            return getDefaultKey(key);
        }
    }

    public String getDefaultKey(String key){

        return resource.get("base", key);
    }

    public Object[] getMandatoryFields(String fileName) throws IOException, ParseException {

        String fileAsString = null;
        try {
            fileAsString = new File(classLoader.getResource(fileName).getFile()).toString();
        } catch (Exception e) {
            throw new IOException(fileName + " not found.");
        }


        JSONParser parser = new JSONParser();
        JSONObject content = (JSONObject) parser.parse(new FileReader(fileAsString));

        return res(content);
    }

    private Object[] res(JSONObject content){

        Object[] objArr = null;
        Object[] obj = null;

        @SuppressWarnings("unchecked")
        Set<String> keySet = content.keySet();
        for (String key : keySet) {

            if (key.equals("required")){
                JSONArray value = (JSONArray)content.get(key);
                objArr = value.toArray();
                break;
            }
            else {
                try {
                    if (content.get(key) instanceof String){}
                    else if (content.get(key) instanceof JSONArray){
                        JSONArray jsonArray = (JSONArray) content.get(key);
                        for (Object ar : jsonArray){
                            obj = res((JSONObject) ar);
                        }
                    }
                    else if (content.get(key) instanceof JSONObject){
                        JSONObject jsonArray = (JSONObject) content.get(key);
                            obj = res((JSONObject)jsonArray);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        Object[] both = (Object[]) ArrayUtils.addAll(obj, objArr);
        return both;
    }

    public List<String> getHeadersForLogging() {
        try {
            String path = URLDecoder.decode(classLoader.getResource(headers).getFile().toString(), "utf-8");

            BufferedReader br = new BufferedReader(new FileReader(path));
            try {
                String line = br.readLine();

                ArrayList<String> list = new ArrayList<String>();
                while (line != null) {

                    list.add(line);
                    line = br.readLine();
                }
                return list;
            } finally {
                br.close();
            }
        } catch (IOException e) {
            System.out.println(headers + " not found in resource folder.");
            e.printStackTrace();
        }
        return null;
    }

    public HashMap<String, String> getTestcaseSettings(String section) throws IOException {

        String path = URLDecoder.decode(classLoader.getResource("Settings/testcase.default").getFile().toString(), "utf-8");
        File file = new File(path);

        Wini resource = new Wini(file);
        Profile.Section asd = resource.get(section);
        HashMap<String, String> list = new HashMap<>();
        for (String key : asd.childrenNames())
        {
            list.put(key, asd.get(key));
        }
        return list;
    }

    public Map<String, String> getGlobalVariables() {
        ObjectMapper mapper = new ObjectMapper();


        try {
            String vr = URLDecoder.decode(classLoader.getResource(variables).getFile().toString(), "utf-8");
            JSONParser par = new JSONParser();
            Object obj = par.parse(new FileReader(vr));
            JSONObject jsonObject = (JSONObject)obj;

            Variables vars = mapper.readValue(jsonObject.toJSONString(), Variables.class);
            HashMap<String, String> var = new HashMap<>(vars.getVariables());
            HashMap<String, String> fin = new HashMap<>();

            for (String key : var.keySet()) {
                String value = Tools.generateVariables((String)var.get(key));
                fin.put(key.toLowerCase(), value);
            }

            return fin;

        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Test case global variables file is not found: " + variables);
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
