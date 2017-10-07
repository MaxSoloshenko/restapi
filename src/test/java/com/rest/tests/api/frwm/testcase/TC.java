package com.rest.tests.api.frwm.testcase;

import com.rest.tests.api.frwm.testcase.Expectations.Expectation;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by msolosh on 3/25/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TC implements ITestCase{


    public String Name = "";
    public String Method = "";
    public String URL = "";
//    public List<Expectation> Expectations = new ArrayList<>();
    public JSONObject[] Expectations = new JSONObject[]{};
    public Object Body = null;
    public JSONObject  Params = new JSONObject();
    public JSONObject  Boundary = new JSONObject();
    public String[] Tags = new String[]{};
    public long Timeout = 0;
    public long Loop = 1;
    public long LoopTimeout = 0;
    public String SourceFile = "";
    public Boolean Enabled = true;
    public HashMap<?, ?> Headers;

    public void setSourceFile(String sourceFile) {
        SourceFile = sourceFile;
    }

    public String getSourceFile() {
        if (SourceFile.equalsIgnoreCase(""))
            return "#{SourceFile}";
        return SourceFile;
    }
    //
//    public HashMap<?, ?> getHeaders() {
//        return Headers;
//    }
//
//    public void setHeaders(HashMap<?, ?> Headers) {
//        this.Headers = Headers;
//    }
//
    public boolean getEnabled() {
        return Enabled;
    }
//
//    public void setType(TestcaseType type) {
//        Type = type;
//    }
//
    public long getLoop() {
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

    public String[] getTags() {
        return Tags;
    }

    public JSONObject getBOUNDARY() {
        return Boundary;
    }

    public void setBOUNDARY(JSONObject BOUNDARY) {
        this.Boundary = BOUNDARY;
    }

    public JSONObject getPARAMS() {
        return Params;
    }

    public void setPARAMS(JSONObject  PARAMS) {
        this.Params = PARAMS;
    }

    public Object getBody() {
        return Body;
    }

    @Override
    public JSONObject[] getExpectations() {
        return Expectations;
    }

    public void setBODY(Object BODY) {
        this.Body = BODY;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getMethod() {
        return Method;
    }
//
    public void setMethod(String METHOD) {
        this.Method = METHOD;
    }

    public String getUrl() {
        return URL;
    }

    public void setUrl(String URL) {
        this.URL = URL;
    }

//    @Override
//    public JSONObject[] getExpectations() {
//        return Expectations;
//    }
//
//    public void setExpectations(JSONObject[] Expectations) {
//        this.Expectations = Expectations;
//    }
}
