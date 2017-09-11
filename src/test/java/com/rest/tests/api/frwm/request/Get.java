package com.rest.tests.api.frwm.request;

import com.rest.tests.api.frwm.testcase.Testcase;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.json.simple.JSONObject;
import com.rest.tests.api.frwm.request.Tools;

import java.io.IOException;
import java.net.URLDecoder;
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
            RequestConfig.Builder requestConfig = RequestConfig.custom();
            requestConfig = requestConfig.setConnectTimeout(30 * 1000);
            requestConfig = requestConfig.setConnectionRequestTimeout(30 * 1000);

            HttpClient httpclient = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();


            HttpGet getRequest = new HttpGet(URLDecoder.decode(test.getURL().replace("\\/", "/").replace("\\",""), "UTF-8"));

            getRequest.setHeader("Accept", "application/json");
            getRequest.setHeader("Content-Type", "application/json");
            if (test.getPARAMS() != null) {

                JSONObject params = test.getPARAMS();

                Iterator iter = params.keySet().iterator();
                while (iter.hasNext()){
                    String key = (String)iter.next();

                    getRequest.addHeader(key, (String)params.get(key));
                }
            }

            res = httpclient.execute(getRequest);

//            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }
}
