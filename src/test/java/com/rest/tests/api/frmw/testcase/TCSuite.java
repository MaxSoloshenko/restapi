package com.rest.tests.api.frmw.testcase;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.json.simple.JSONObject;

import java.util.HashMap;

/**
 * Created by msolosh on 3/25/2016.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class TCSuite {

    public String Microservice = "";
    public String[] Tags = new String[]{};
    public HashMap<String, String> Variables = new HashMap<String, String>();
    public JSONObject[] SetUp = new JSONObject[]{};
    public JSONObject[]  TearDown = new JSONObject[]{};
    public JSONObject[]  Tests = new JSONObject[]{};
    public String  File = "";


    public String[] getTags() {
        return Tags;
    }
    public JSONObject[] getTests()
    {
        return Tests;
    }
    public JSONObject[] getSetUp()
    {
        return SetUp;
    }
    public JSONObject[] getTearDown()
    {
        return TearDown;
    }
    public String getMicroservice() {
        return Microservice;
    }
    public void setFile(String file)
    {
        this.File = file;
    }
    public String getFile()
    {
        return this.File;
    }
    public HashMap<String, String> getVariables() {
        return Variables;
    }
    public void setVariables(HashMap<String, String> variables) {
        if (variables.size() > 0)
            Variables = variables;
    }
}
