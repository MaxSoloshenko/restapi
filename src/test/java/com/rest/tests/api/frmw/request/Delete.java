package com.rest.tests.api.frmw.request;

import com.rest.tests.api.frmw.testcase.TC;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.Iterator;

/**
 * Created by msolosh on 3/25/2016.
 */
public class Delete implements IRequest {

    TC test;

    public Delete(TC testcase){
        this.test = testcase;
    }

    @Override
    public HttpResponse sendRequest(){

        HttpDelete delete = new HttpDelete(test.getUrl());
        HttpResponse res = null;

        try {
            RequestConfig.Builder requestConfig = RequestConfig.custom();
            requestConfig = requestConfig.setConnectTimeout(30 * 1000);
            requestConfig = requestConfig.setConnectionRequestTimeout(30 * 1000);

            HttpClientBuilder builder = HttpClientBuilder.create();
            builder.setDefaultRequestConfig(requestConfig.build());
            HttpClient httpclient = builder.build();

            delete.setHeader("Accept", "application/json");
            delete.setHeader("Content-Type", "application/json");
            if (!test.getPARAMS().toJSONString().equalsIgnoreCase("{}")) {

                JSONObject params = test.getPARAMS();

                Iterator iter = params.keySet().iterator();
                while (iter.hasNext()){
                    String key = (String)iter.next();

                    delete.addHeader(key, (String)params.get(key));
                }
            }

            res = httpclient.execute(delete);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;
    }
}
