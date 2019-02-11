package com.rest.tests.api.frmw.request;

import com.rest.tests.api.frmw.settings.Tools;
import com.rest.tests.api.frmw.testcase.TC;
import net.minidev.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.util.Iterator;

/**
 * Created by msolosh on 3/25/2016.
 */
public class Patch implements IRequest {

    TC test;

    public Patch(TC testcase){
        this.test = testcase;
    }


    @Override
    public HttpResponse sendRequest(){

        HttpPatch put = new HttpPatch(test.getUrl());
        HttpResponse res = null;

        try {
            RequestConfig.Builder requestConfig = RequestConfig.custom();
            requestConfig = requestConfig.setConnectTimeout(30 * 1000);
            requestConfig = requestConfig.setConnectionRequestTimeout(30 * 1000);

            HttpClientBuilder builder = HttpClientBuilder.create();
            builder.setDefaultRequestConfig(requestConfig.build());
            HttpClient httpclient = builder.build();


            put.setHeader("Accept", "application/json");
            put.setHeader("Content-Type", "application/json");
            if (!test.getPARAMS().toJSONString().equalsIgnoreCase("{}")) {

                JSONObject params = test.getPARAMS();

                Iterator iter = params.keySet().iterator();
                while (iter.hasNext()){
                    String key = (String)iter.next();

                    put.addHeader(key, (String)params.get(key));
                }
            }

            if (test.getBody() != null)
            {
                StringEntity entity = new StringEntity(test.getBody().toString());
                put.setEntity(entity);
            }

            put = (HttpPatch) Tools.setHeaderHitId(put);
            res = httpclient.execute(put);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;
    }
}
