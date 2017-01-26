package com.javelin.tests.api.request;

import com.javelin.tests.api.testcase.Testcase;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.Iterator;

/**
 * Created by msolosh on 3/25/2016.
 */
public class Delete implements IRequest {

    Testcase test;

    public Delete(Testcase testcase){
        this.test = testcase;
    }

    @Override
    public HttpResponse sendRequest(){

        HttpClient client;
        HttpDelete delete = new HttpDelete(test.getURL());
        HttpResponse res = null;

        try {
            client = HttpClientBuilder.create().build();

            delete.setHeader("Accept", "application/json");
            delete.setHeader("Content-Type", "application/json");
            if (test.getPARAMS() != null) {

                JSONObject params = test.getPARAMS();

                Iterator iter = params.keySet().iterator();
                while (iter.hasNext()){
                    String key = (String)iter.next();

                    delete.addHeader(key, (String)params.get(key));
                }
            }

            res = client.execute(delete);

        } catch (IOException e) {
            PrintOut print = new PrintOut();
            print.Print(delete);
            e.printStackTrace();
        }

        return res;
    }
}
