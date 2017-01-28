package com.rest.tests.api.frwm.testcase;

import org.apache.http.client.methods.HttpDelete;

/**
 * Created by msolosh on 3/26/2016.
 */
public class DeleteTest extends Testcase {

    public DeleteTest() {
        super();
        setMETHOD(HttpDelete.METHOD_NAME);
    }
}
