package com.javelin.tests.api.testcase;

import org.json.simple.JSONObject;

import java.util.ArrayList;

/**
 * Created by msolosh on 3/26/2016.
 */
interface ITestCase {

    public String getMETHOD();
    public String getURL();
    public String getBODY();
    public ArrayList<JSONObject> getEXPECTATION();

}
