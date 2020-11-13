package com.sample.conn.http.utils;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Administrator on 2017/12/18.
 */
public class HttpUtils {
    private static Logger log = LoggerFactory.getLogger(HttpUtils.class);

    private static HttpClient httpClient = new HttpClient(new MultiThreadedHttpConnectionManager());

    public static String sendGet(String url) throws IOException {
        return sendGet(url, null);
    }

    public static String sendGet(String url, Map<String, String> parameters)
            throws IOException {
        if (url == null) {
            throw new IOException("URL不能为空");
        }

        GetMethod gMethod = createGetMethod(url, parameters);

        return executeMethod(gMethod);
    }

    public static String sendPost(String url, Map<String, String> parameters)
            throws IOException {
        return sendPost(url, parameters, false);
    }

    public static String sendPost(String url, Map<String, String> parameters,
                           boolean putInHead) throws IOException {
        return sendPost(url, null, parameters, putInHead);
    }

    public String sendPost(String url, String body) throws IOException {
        return sendPost(url, body, null, null);
    }

    public static String sendPost(String url, String body,
                           Map<String, String> parameters, boolean putInHead)
            throws IOException {
        return sendPost(url, body, putInHead ? null : parameters, putInHead ? parameters : null);
    }

    public static String sendPost(String url, String body,
                           Map<String, String> parameters, Map<String, String> headers)
            throws IOException {
        if (url == null) {
            throw new IOException("URL不能为空");
        }

        PostMethod pMethod = createPostMethod(url, body, parameters, headers);

        return executeMethod(pMethod);
    }

    public static String executeMethod(HttpMethod method) throws IOException {
        HttpClientParams params = new HttpClientParams();
        params.setConnectionManagerTimeout(50000);
        params.setSoTimeout(50000);

        httpClient.setParams(params);
        log.info ("The request url is '" + method.getURI() + "'.");
        BufferedReader read = null;
        int responseCode;
        try {
            responseCode = httpClient.executeMethod(method);
            log.info ("The response code is '{}'", responseCode);
            if (responseCode == HttpStatus.SC_OK) {
                StringBuffer result = new StringBuffer();
                read = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream(), "UTF-8"));
                String temp = null;
                while ((temp = read.readLine()) != null) {
                    result.append(temp).append("\r\n");
                }

                if (read != null) {
                    read.close();
                }

                return result.toString().trim();
            }
        } catch (IOException e) {
            System.out.println("The request url is '" + method.getURI() + "'.");
            throw e;
        } finally {

            method.releaseConnection();

        }

        return null;
    }

    public String executeMethod2(HttpMethod method, int second) throws IOException {
        HttpConnectionManagerParams httpConnectionManagerParams = httpClient.getHttpConnectionManager().getParams();
        httpConnectionManagerParams.setConnectionTimeout(second * 5000);
        httpConnectionManagerParams.setSoTimeout(second * 5000);

        httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");

        log.info ("The request url is '" + method.getURI() + "'.");

        int responseCode;
        try {
            responseCode = httpClient.executeMethod(method);
            log.info ("The response code is '{}'", responseCode);
            if (responseCode == HttpStatus.SC_OK) {
                StringBuffer result = new StringBuffer();
                BufferedReader read = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream(), "UTF-8"));
                String temp = null;
                while ((temp = read.readLine()) != null) {
                    result.append(temp).append("\r\n");
                }
                read.close();
                return result.toString().trim();
            }
        } catch (IOException e) {
            System.out.println("The request url is '" + method.getURI() + "'.");
            throw e;
        } finally {
            method.releaseConnection();
        }

        return null;
    }

    private static PutMethod createPutMethod(String url, String body, Map<String, String> headers){
        PutMethod pMethod = new PutMethod(url);
        String contentType = "text/xml;charset=utf-8";
        if (body != null) {
            try {
                RequestEntity rEntity = new StringRequestEntity(body, contentType, "utf-8");
                pMethod.setRequestEntity(rEntity);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }


        if (headers != null && headers.size() != 0) {
            Iterator<String> it = headers.keySet().iterator();
            String name = null;
            while (it.hasNext()) {
                name = it.next();
                pMethod.setRequestHeader(name, headers.get(name));
            }
        }

        if (pMethod.getRequestHeader("Content-Type") == null) {
            pMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        }

        return pMethod;
    }



    private static GetMethod createGetMethod(String url, Map<String, String> parameters){
        GetMethod gMethod = new GetMethod(url);
        gMethod.setRequestHeader("Content-Type", "text/xml;charset=utf-8");

        NameValuePair[] nvps = convert(parameters);
        if (nvps != null) {
            gMethod.setQueryString(nvps);
        }
        return gMethod;
    }

    private static PostMethod createPostMethod(String url, String body, Map<String, String> parameters, Map<String, String> headers){
        PostMethod pMethod = new UTF8PostMethod(url);
        String contentType = "text/xml;charset=utf-8";

        if (body != null) {
            try {
                RequestEntity rEntity = new StringRequestEntity(body, contentType, "utf-8");
                pMethod.setRequestEntity(rEntity);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        if (parameters != null && parameters.size() != 0) {
            Iterator<String> it = parameters.keySet().iterator();
            String name = null;
            while (it.hasNext()) {
                name = it.next();
                pMethod.setParameter(name, parameters.get(name));
            }
        }

        if (headers != null && headers.size() != 0) {
            Iterator<String> it = headers.keySet().iterator();
            String name = null;
            while (it.hasNext()) {
                name = it.next();
                pMethod.setRequestHeader(name, headers.get(name));
            }
        }

        if (pMethod.getRequestHeader("Content-Type") == null) {
            pMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        }

        return pMethod;
    }

    // Inner class for UTF-8 support
    public static class UTF8PostMethod extends PostMethod {

        public UTF8PostMethod(String url){
            super(url);
        }

        @Override
        public String getRequestCharSet(){
            // return super.getRequestCharSet();
            return "UTF-8";
        }
    }
    /**
     * 把集合转成数组
     */
    public static NameValuePair[] convert(Map<String, String> values) {
        NameValuePair[] nvps = null;
        if (values != null && values.size() != 0) {
            Iterator<String> it = values.keySet().iterator();
            nvps = new NameValuePair[values.size()];
            String name = null;
            int i = 0;
            while (it.hasNext()) {
                name = it.next();
                nvps[i] = new NameValuePair(name, values.get(name));
                i++;
            }
        }
        return nvps;
    }

}
