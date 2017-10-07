package com.rest.tests.api.frwm.testcase;

import org.apache.http.client.methods.*;

/**
 * Created by msolosh on 3/26/2016.
 */
public class TestcaseFactory {

    public static Testcase generate(String method){

        switch(method){
            case HttpPost.METHOD_NAME:
                return new PostTest();
            case HttpPut.METHOD_NAME:
                return new PutTest();
            case HttpDelete.METHOD_NAME:
                return new DeleteTest();
            case HttpGet.METHOD_NAME:
                return new GetTest();
            case HttpPatch.METHOD_NAME:
                return new PatchTest();
            default:
                System.out.println("Unknown Method: " + method);
                break;
        }
        return null;
    }
}
