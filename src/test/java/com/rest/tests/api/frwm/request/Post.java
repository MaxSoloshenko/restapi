package com.rest.tests.api.frwm.request;

import com.rest.tests.api.frwm.testcase.TC;
import com.rest.tests.api.frwm.testcase.Testcase;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * Created by msolosh on 3/25/2016.
 */
public class Post implements IRequest {

    TC test;
    ClassLoader classLoader = getClass().getClassLoader();

    public Post(TC testcase){
        this.test = testcase;
    }

    @Override
    public HttpResponse sendRequest(){

        HttpPost post = new HttpPost(test.getUrl());
        HttpResponse res = null;

        try {
            RequestConfig.Builder requestConfig = RequestConfig.custom();
            requestConfig = requestConfig.setConnectTimeout(30 * 1000);
            requestConfig = requestConfig.setConnectionRequestTimeout(30 * 1000);

            HttpClientBuilder builder = HttpClientBuilder.create();
            builder.setDefaultRequestConfig(requestConfig.build());
            HttpClient httpclient = builder.build();

            if (!test.getPARAMS().toJSONString().equalsIgnoreCase("{}")) {

                JSONObject params = test.getPARAMS();

                Iterator iter = params.keySet().iterator();
                while (iter.hasNext()){
                    String key = (String)iter.next();
                    String value = (String)params.get(key);
                    post.addHeader(key, value);
                }
            }

            if (!test.getBOUNDARY().toJSONString().equalsIgnoreCase("{}")) {

                JSONObject boundary = test.getBOUNDARY();
                MultipartEntityBuilder  entity = MultipartEntityBuilder.create();
                entity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                entity.setCharset(Charset.defaultCharset());

                Iterator iter = boundary.keySet().iterator();
                while (iter.hasNext()){

                    String key = (String)iter.next();
                    String value = (String)boundary.get(key);


                    if (key.equals("filestream"))
                    {

                        String source = test.getSourceFile();
                        File file = new File(classLoader.getResource("SourceFiles/").getPath() + value);
                        String filename = file.getName();

                        if (!file.exists()) {
                            file = new File(classLoader.getResource("SourceFiles/").getPath() + source);
                        }
                        value = file.getAbsolutePath();

                        ContentType type;
                        if (value.endsWith(".pdf"))
                            type = ContentType.create("application/pdf");
                        else if (value.endsWith(".zip"))
                            type = ContentType.create("application/octet-stream");
                        else if ((value.endsWith(".doc")) || (value.endsWith(".dot")))
                            type = ContentType.create("application/msword");
                        else if (value.endsWith(".docx"))
                            type = ContentType.create("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
                        else if (value.endsWith(".gif"))
                            type = ContentType.create("image/gif");
                        else if ((value.endsWith(".pps")) || (value.endsWith(".ppt")))
                            type = ContentType.create("application/vnd.ms-powerpoint");
                        else if (value.endsWith(".pptm"))
                            type = ContentType.create("application/vnd.ms-powerpoint.presentation.macroEnabled.12");
                        else if (value.endsWith(".mp4"))
                            type = ContentType.create("video/mp4");
                        else
                            type = ContentType.create("application/octet-stream");

                        entity.addBinaryBody(key, new File(value), type, filename);
                    }
                    else
                    {
                        entity.addTextBody(key, value);
                    }
                }
                post.setEntity(entity.build());
            }
            else
            {
                post.setHeader("Content-Type", "application/json");
            }
            post.setHeader("Accept", "application/json, text/plain, */*");

            if (test.getBody() != null)
//            if (!test.getBody().toJSONString().equalsIgnoreCase("{}"))
            {
                StringEntity entity = new StringEntity(test.getBody().toString());
                post.setEntity(entity);
            }

            res = httpclient.execute(post);

        } catch (IOException e) {
            System.out.println("File not found.");
            PrintOut print = new PrintOut();
            e.printStackTrace();
        }

        return res;
    }
}
