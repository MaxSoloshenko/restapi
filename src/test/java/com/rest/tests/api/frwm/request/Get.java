package com.rest.tests.api.frwm.request;

import com.rest.tests.api.frwm.testcase.Testcase;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONObject;
import com.rest.tests.api.frwm.request.Tools;

import java.io.IOException;
import java.util.Iterator;

/**
 * Created by msolosh on 3/25/2016.
 */
public class Get implements IRequest{

    Testcase test;

    public Get(Testcase testcase){
        this.test = testcase;
    }

    @Override
    public HttpResponse sendRequest()  {
        HttpResponse res = null;
        try {
            HttpClient httpclient = HttpClientBuilder.create().useSystemProperties().build();
            HttpGet getRequest = new HttpGet(test.getURL());

            getRequest = (HttpGet) Tools.setHeaders(getRequest, test.getHeaders());

            if (test.getPARAMS() != null) {

                JSONObject params = test.getPARAMS();

                Iterator iter = params.keySet().iterator();
                while (iter.hasNext()){
                    String key = (String)iter.next();

                    getRequest.addHeader(key, (String)params.get(key));
                }
            }

            res = httpclient.execute(getRequest);

            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }
}
