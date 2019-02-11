package com.rest.tests.api.frmw.settings;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.commons.lang3.ArrayUtils;
import org.ini4j.Profile;
import org.ini4j.Wini;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.*;

/**
 * Created by msolosh on 1/25/2016.
 */
@SuppressWarnings("ALL")
public class Settings {

    Wini resource;
    ClassLoader classLoader = getClass().getClassLoader();
    List<String> response_headers = new ArrayList<String>();

    private static final String properties = "Settings/APIUrls.properties";
    private static final String headers = "Settings/logging.frmw";
    private static final String variables = "Variables/variables.json";
    private String[] tags = null;

    public Settings() throws IOException {

        String path = null;
        try {
            if (classLoader.getResource(properties) == null)
            {
                System.out.println(properties + " does not exist.");
                System.exit(1);
            }
            path = URLDecoder.decode(new File(classLoader.getResource(properties).getFile()).getAbsolutePath(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        File file = new File(path);

        this.resource = new Wini(file);

        String tags = System.getenv("JAVELIN_TEST_TAGS");

        if (tags != null && tags != "" && !tags.equalsIgnoreCase("all"))
        {
            this.tags = tags.split(",");
        }

        try {
            path = URLDecoder.decode(new File(classLoader.getResource(headers).getFile()).getAbsolutePath(), "utf-8");

            BufferedReader br = new BufferedReader(new FileReader(path));
            try {
                String line = br.readLine();

                ArrayList<String> list = new ArrayList<String>();
                while (line != null) {

                    list.add(line);
                    line = br.readLine();
                }
                response_headers = list;
            } finally {
                br.close();
            }
        } catch (Exception e) {
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

        if (System.getenv("JAVELIN_API_" + key) != null)
            return System.getenv("JAVELIN_API_" + key);

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
        return response_headers;
    }

    public HashMap<String, String> getTestcaseSettings(String section) throws IOException {

        String path = URLDecoder.decode(new File(classLoader.getResource("Settings/testcase.default").getFile()).getAbsolutePath(), "utf-8");
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

            if (classLoader.getResource("Variables") == null)
            {
                System.out.println("Variables folder is missed in resources!");
                System.exit(1);
            }
            String folder = classLoader.getResource("Variables").getPath();
            File directory = new File(folder);
            HashMap<String, String> fin = new HashMap<>();

            for (final File fileEntry : directory.listFiles()) {

                String path = fileEntry.getAbsolutePath();
                JSONParser par = new JSONParser();
                Object obj = par.parse(new FileReader(path));
                JSONObject jsonObject = (JSONObject)obj;

                Variables vars = mapper.readValue(jsonObject.toJSONString(), Variables.class);
                HashMap<String, String> var = new HashMap<>(vars.getVariables());

                for (String key : var.keySet()) {
                    String value = Tools.generateVariables((String)var.get(key));
                    fin.put(key.toLowerCase(), value);
                }
            }

            return fin;

        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Something wrong with reading variable files in folder Variables.");
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.out.println("Something wrong with reading variable files in folder Variables.");
            e.printStackTrace();
        }
        return null;
    }
}
