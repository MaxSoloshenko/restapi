package com.javelin.tests.api.request;

import org.apache.http.client.methods.HttpRequestBase;

/**
 * Created by msolosh on 3/28/2016.
 */
public class PrintOut {

    public static void Print(HttpRequestBase request){

        System.out.println("Request method: " + request.getMethod());
        System.out.println("Request path:   " + request.getURI());
        System.out.println("Protocol:       " + request.getRequestLine().getProtocolVersion().getProtocol());

        if (request.headerIterator().hasNext())
        {
            System.out.println("Header: " + request.headerIterator().nextHeader());
        }
    }
}
