package com.rest.tests.api.frmw.testcase;

import org.apache.http.Header;
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
    private Header[] headers;
    private String type;

    public Response(HttpResponse response) throws IOException {
        this.headers = response.getAllHeaders();
        this.status = response.getStatusLine().getStatusCode();
        HttpEntity http = response.getEntity();
        if (http != null)
        {
            this.document = EntityUtils.toString(http);
            if (http.getContentType() != null) {
                type = http.getContentType().toString();
            }
        }
    }

    public int getStatus()
    {
        return status;
    }

    public Object getDocument()
    {
        return document;
    }

    public String getContenType()
    {
        return type;
    }

    public Header[] getHeaders()
    {
        return headers;
    }
}
