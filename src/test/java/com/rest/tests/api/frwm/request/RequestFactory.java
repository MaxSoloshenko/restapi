package com.rest.tests.api.frwm.request;

import com.rest.tests.api.frwm.testcase.Testcase;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;

/**
 * Created by msolosh on 3/26/2016.
 */
public abstract class RequestFactory implements IRequest{

    public static IRequest getRequest(Testcase testcase){

        switch (testcase.getMETHOD())
        {
            case HttpPost.METHOD_NAME:
                return new Post(testcase);
            case HttpPut.METHOD_NAME:
                return new Put(testcase);
            case HttpDelete.METHOD_NAME:
                return new Delete(testcase);
            case HttpGet.METHOD_NAME:
                return new Get(testcase);
            default:
                System.out.println("Unknown Method: " + testcase.getMETHOD() + " for Test case: " + testcase.getNAME());
        }

        return null;
    }
}
