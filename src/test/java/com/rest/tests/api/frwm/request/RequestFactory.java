package com.rest.tests.api.frwm.request;

import com.rest.tests.api.frwm.testcase.TC;
import com.rest.tests.api.frwm.testcase.Testcase;
import org.apache.http.client.methods.*;

/**
 * Created by msolosh on 3/26/2016.
 */
public abstract class RequestFactory implements IRequest{

    public static IRequest getRequest(TC testcase){

        switch (testcase.getMethod())
        {
            case HttpPost.METHOD_NAME:
                return new Post(testcase);
            case HttpPut.METHOD_NAME:
                return new Put(testcase);
            case HttpDelete.METHOD_NAME:
                return new Delete(testcase);
            case HttpGet.METHOD_NAME:
                return new Get(testcase);
            case HttpPatch.METHOD_NAME:
                return new Patch(testcase);
            default:
                System.out.println("Unknown Method: " + testcase.getMethod() + " for Test case: " + testcase.getName());
        }

        return null;
    }
}
