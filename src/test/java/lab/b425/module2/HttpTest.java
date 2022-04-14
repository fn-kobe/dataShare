package lab.b425.module2;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.util.Timeout;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class HttpTest {
    private static Object paramsObj;

    /*public static void sendPost(String url) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("url");
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(Timeout.ofSeconds(2))
                .setConnectionRequestTimeout(Timeout.ofSeconds(2))
                .setResponseTimeout(Timeout.ofSeconds(2))
                .build();
        httpPost.setConfig(requestConfig);
        httpPost.setVersion(HttpVersion.HTTP_2); // 支持http2
        httpPost.setHeader("Content-Type", "application/json");
        // 支持多种entity参数，字节数组，流，文件等等
        // 此处使用restful的"application/json"，所以传递json字符串
        httpPost.setEntity(new StringEntity(JSON.toJSONString(paramsObj)));
        try (CloseableHttpResponse res = httpClient.execute(httpPost)) {
            if (res.getCode() == HttpStatus.SC_OK) {
                HttpEntity entity = res.getEntity();
                println(EntityUtils.toString(entity));
            } else {
                System.err.println("请求失败，状态码：" + res.getCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 释放连接资源
            httpPost.reset();
        }
    }*/


    public static void sendGet(String url) {

        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpGet httpGet = new HttpGet(url);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(Timeout.ofSeconds(2))
                .setConnectionRequestTimeout(Timeout.ofSeconds(2))
                .setResponseTimeout(Timeout.ofSeconds(2))
                .build();
        httpGet.setConfig(requestConfig);
        httpGet.setVersion(HttpVersion.HTTP_1_1); // 支持http2
        httpGet.setHeader("Content-Type", "application/json");
        // 支持多种entity参数，字节数组，流，文件等等
        // 此处使用restful的"application/json"，所以传递json字符串

        try (CloseableHttpResponse res = httpClient.execute(httpGet)) {
            if (res.getCode() == HttpStatus.SC_OK) {
                HttpEntity entity = res.getEntity();
                String s = EntityUtils.toString(entity);
                System.out.println("·····················");
                JSONObject jsonObject = JSONObject.parseObject(s);
                System.out.println(jsonObject.toJSONString());
                Map<Object, Object> map = JSON.parseObject(s, Map.class);
                System.out.println(map.get("code"));
                System.out.println(map.get("message"));

            } else {
                System.err.println("请求失败，状态码：" + res.getCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 释放连接资源
            httpGet.reset();
        }
    }

    public static void sendPost(String url) {

        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost(url);

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(Timeout.ofSeconds(2))
                .setConnectionRequestTimeout(Timeout.ofSeconds(2))
                .setResponseTimeout(Timeout.ofSeconds(2))
                .build();
        httpPost.setConfig(requestConfig);
        httpPost.setVersion(HttpVersion.HTTP_1_1); // 支持http2
        httpPost.setHeader("Content-Type", "application/json");

        // 支持多种entity参数，字节数组，流，文件等等
        // 此处使用restful的"application/json"，所以传递json字符串
//        Map<Object, String> payloadMap = new HashMap<>();
//        payloadMap.put("id", "test03");
//        payloadMap.put("info", "测试节点");
//        payloadMap.put("name", "03");
//        payloadMap.put("password", "112233");
//        payloadMap.put("peerId", "01#a");

        String str = "{\"id\":\"test001\",\"info\":\"testPeer\",\"name\":\"03\",\"password\":\"112233\",\"peerId\":\"01#a\"}";
        httpPost.setEntity(new StringEntity(str));
        try (CloseableHttpResponse res = httpClient.execute(httpPost)) {
            if (res.getCode() == HttpStatus.SC_OK) {
                HttpEntity entity = res.getEntity();
                String s = EntityUtils.toString(entity);
                System.out.println("·····················");
                JSONObject jsonObject = JSONObject.parseObject(s);
                System.out.println(jsonObject.toJSONString());
                Map<Object, Object> map = JSON.parseObject(s, Map.class);
                System.out.println(map.get("code"));
                System.out.println(map.get("message"));

            } else {
                System.err.println("请求失败，状态码：" + res.getCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 释放连接资源
            httpPost.reset();
        }
    }

    public static void sendPost2(String url, Map<Object, Object> payloads) {
        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> nvps = new ArrayList<>();
        for (Object key : payloads.keySet()) {
            nvps.add(new BasicNameValuePair(Objects.toString(key), Objects.toString(payloads.get(key))));
        }

        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nvps, StandardCharsets.UTF_8);
        httpPost.setEntity(entity);
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try (CloseableHttpResponse response2 = httpclient.execute(httpPost)) {
            System.out.println(response2.getCode() + " " + response2.getReasonPhrase());
            HttpEntity entity2 = response2.getEntity();
            // do something useful with the response body
            String s = EntityUtils.toString(entity2);
            System.out.println("·····················");
            JSONObject jsonObject = JSONObject.parseObject(s);
            System.out.println(jsonObject.toJSONString());
            Map<Object, Object> map = JSON.parseObject(s, Map.class);

            // and ensure it is fully consumed
            EntityUtils.consume(entity2);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        } finally {
            // 释放连接资源
            httpPost.reset();
        }
    }

    public static void main(String[] args) {
//        String url = "http://localhost:8080/user/logout";
//        sendGet(url);
        Map<Object, Object> payloadMap = new HashMap<>();
        payloadMap.put("id", "test06");
        payloadMap.put("info", "测试节点");
        payloadMap.put("name", "06");
        payloadMap.put("password", "112233");
        payloadMap.put("peerId", "01#a");
        String url = "http://localhost:8080/user";
//        sendPost2(url,payloadMap);

        Map<Object, Object> payloadMap2 = new HashMap<>();
        payloadMap2.put("id", "test01");
        payloadMap2.put("password", "123");
        String url2 = "http://localhost:8080/user/login";
//        sendPost2(url2,payloadMap);
        Map<Object, Object> payloadMap3 = new HashMap<>();
        payloadMap3.put("carId", "CAR0");
        String url3 = "http://192.168.1.163:8080/fabricProject_war_exploded/queryCar";
//        sendPost2(url3,payloadMap3);

        sendGet("http://192.168.1.163:8080/fabricProject_war_exploded/queryAllMaterials");




//        Map<Object, Object> map = new HashMap<>();
//        map.put("id", "test03");
//        map.put("info", "测试节点");
//        map.put("name", "03");
//        map.put("password", "112233");
//        map.put("peerId", "01#a");
//        System.out.println(JSON.toJSONString(map));
//        String str = "{\"id\":\"test03\",\"info\":\"测试节点\",\"name\":\"03\",\"password\":\"112233\",\"peerId\":\"01#a\"}";
//        System.out.println(str);


    }


}
