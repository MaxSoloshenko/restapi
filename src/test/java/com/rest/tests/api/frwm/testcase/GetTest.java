package com.rest.tests.api.frwm.testcase;

import org.apache.http.client.methods.HttpGet;

/**
 * Created by msolosh on 3/26/2016.
 */
public class GetTest extends Testcase {

    public GetTest() {
        super();
        setMETHOD(HttpGet.METHOD_NAME);
    }
}
