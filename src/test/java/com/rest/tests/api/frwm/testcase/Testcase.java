package com.rest.tests.api.frwm.testcase;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by msolosh on 3/25/2016.
 */
abstract public class Testcase implements ITestCase{


    private String NAME;
    private String METHOD;
    private String URL;
    private ArrayList<JSONObject> EXPECTATIONS;
    private String BODY = null;
    private JSONObject  PARAMS = null;
    private JSONObject  BOUNDARY = null;
    private JSONArray  TAGS = null;
    private long Timeout = 0;
    private int Loop = 1;
    private long LoopTimeout = 0;
    private TestcaseType Type;
    private HashMap<?, ?> Headers;

    public HashMap<?, ?> getHeaders() {
        return Headers;
    }

    public void setHeaders(HashMap<?, ?> Headers) {
        this.Headers = Headers;
    }

    public TestcaseType getType() {
        return Type;
    }

    public void setType(TestcaseType type) {
        Type = type;
    }

    public int getLoop() {
        return Loop;
    }

    public void setLoop(int loop) {
        Loop = loop;
    }

    public long getLoopTimeout() {
        return LoopTimeout;
    }

    public void setLoopTimeout(long loopTimeout) {
        LoopTimeout = loopTimeout;
    }

    public long getTimeout() {
        return Timeout;
    }

    public void setTimeout(long timeout) {
        Timeout = timeout*1000;
    }

    public JSONArray getTAGS() {
        return TAGS;
    }

    public void setTAGS(JSONArray TAGS) {
        this.TAGS = TAGS;
    }

    public JSONObject getBOUNDARY() {
        return BOUNDARY;
    }

    public void setBOUNDARY(JSONObject BOUNDARY) {
        this.BOUNDARY = BOUNDARY;
    }

    public JSONObject getPARAMS() {
        return PARAMS;
    }

    public void setPARAMS(JSONObject  PARAMS) {
        this.PARAMS = PARAMS;
    }

    public String getBODY() {
        return BODY;
    }
    public void setBODY(String BODY) {
        this.BODY = BODY;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getMETHOD() {
        return METHOD;
    }

    public void setMETHOD(String METHOD) {
        this.METHOD = METHOD;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    @Override
    public ArrayList<JSONObject> getEXPECTATION() {
        return EXPECTATIONS;
    }

    public void setEXPECTATIONS(ArrayList<JSONObject> EXPECTATIONS) {
        this.EXPECTATIONS = EXPECTATIONS;
    }
}
