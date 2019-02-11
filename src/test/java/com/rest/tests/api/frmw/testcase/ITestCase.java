package com.rest.tests.api.frmw.testcase;

import net.minidev.json.JSONObject;

/**
 * Created by msolosh on 3/26/2016.
 */
interface ITestCase {

    public String getMethod();
    public String getUrl();
    public Object getBody();
    public JSONObject[] getExpectations();

}
