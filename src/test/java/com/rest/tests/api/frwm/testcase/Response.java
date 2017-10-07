package com.rest.tests.api.frwm.testcase;

import com.jayway.jsonpath.Configuration;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by msolosh on 10/6/17.
 */
public class Response {

    private Object document;
    private int status;

    public Response(HttpResponse response) throws IOException {
        String body = null;
        Object document = null;

        HttpEntity http = response.getEntity();
        if (http != null) {
            body = EntityUtils.toString(http);
            this.document = Configuration.defaultConfiguration().jsonProvider().parse(body);
        }

        this.status = response.getStatusLine().getStatusCode();
    }

    public int getStatus()
    {
        return status;
    }

    public Object getDocument()
    {
        return document;
    }
}
