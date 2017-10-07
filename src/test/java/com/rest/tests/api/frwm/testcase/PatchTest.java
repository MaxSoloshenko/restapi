package com.rest.tests.api.frwm.testcase;

import org.apache.http.client.methods.HttpPatch;

/**
 * Created by msolosh on 3/26/2016.
 */
public class PatchTest extends Testcase {

    public PatchTest() {
        super();
        setMETHOD(HttpPatch.METHOD_NAME);
    }
}
