package com.rest.tests.api.frmw.request;

import org.apache.http.client.methods.HttpRequestBase;

import java.util.HashMap;

/**
 * Created by msolosh on 3/25/2016.
 */
public class Tools{

    public static HttpRequestBase setHeaders(HttpRequestBase request, HashMap<?, ?> headers){

        for (Object key : headers.keySet()) {
            request.setHeader((String)key, (String)headers.get((String)key));
        }

        return request;
    }
}
