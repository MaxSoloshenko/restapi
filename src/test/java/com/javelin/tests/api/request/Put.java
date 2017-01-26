package com.javelin.tests.api.request;

import com.javelin.tests.api.testcase.Testcase;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.Iterator;

/**
 * Created by msolosh on 3/25/2016.
 */
public class Put implements IRequest {

    Testcase test;

    public Put(Testcase testcase){
        this.test = testcase;
    }


    @Override
    public HttpResponse sendRequest(){

        HttpPut put = new HttpPut(test.getURL());
        HttpResponse res = null;

        try {
            HttpClient client = HttpClientBuilder.create().build();


            put.setHeader("Accept", "application/json");
            put.setHeader("Content-Type", "application/json");
            if (test.getPARAMS() != null) {

                JSONObject params = test.getPARAMS();

                Iterator iter = params.keySet().iterator();
                while (iter.hasNext()){
                    String key = (String)iter.next();

                    put.addHeader(key, (String)params.get(key));
                }
            }

            if (test.getBODY() != null)
            {
                StringEntity entity = new StringEntity(test.getBODY());
                put.setEntity(entity);
            }

            res = client.execute(put);

        } catch (IOException e) {
            PrintOut print = new PrintOut();
            print.Print(put);
            e.printStackTrace();
        }

        return res;
    }
}
