package com.rest.tests.api.frwm.testcase;

import org.apache.http.client.methods.HttpPost;

/**
 * Created by msolosh on 3/26/2016.
 */
public class PostTest extends Testcase {

    public PostTest() {
        super();
        setMETHOD(HttpPost.METHOD_NAME);
    }
}
