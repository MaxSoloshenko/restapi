package com.rest.tests.api.frwm.testcase;

import com.rest.tests.api.frwm.testcase.Expectations.Expectation;
import org.json.simple.JSONObject;

import java.util.List;

/**
 * Created by msolosh on 3/26/2016.
 */
interface ITestCase {

    public String getMethod();
    public String getUrl();
    public Object getBody();
//    public List<Expectation> getExpectations();
    public JSONObject[] getExpectations();

}
