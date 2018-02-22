package com.rest.tests.api.frmw.testcase;

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
        HttpEntity http = response.getEntity();
        if (http != null) {

            this.document = EntityUtils.toString(http);
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
