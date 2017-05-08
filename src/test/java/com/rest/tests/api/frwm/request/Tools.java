package com.rest.tests.api.frwm.request;

import com.rest.tests.api.frwm.testcase.Testcase;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
