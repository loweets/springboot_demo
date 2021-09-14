package com.wxcloudrun.demo.service;

import com.google.gson.Gson;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestController
public class DemoService {


    @RequestMapping("/")
    public Map sayHello(@RequestParam(name = "top") int top) throws Exception {
        System.out.println("top：" + top);
        if (top >= 10) {
            throw new Exception("top over 10");
        }

        CloseableHttpClient httpclient = HttpClients.createDefault();
        URI uri = new URIBuilder("https://i.news.qq.com/trpc.qqnews_web.kv_srv.kv_srv_http_proxy/list")
                .setParameter("sub_srv_id", "24hours")
                .setParameter("srv_id", "pc").setParameter("offset", "0").setParameter("limit", String.valueOf(top)).setParameter("strategy", "1")
                .setParameter("ext", "{\"pool\":[\"top\"],\"is_filter\":7,\"check_type\":true}")
                .build();

        HttpGet httpGet = new HttpGet(uri);

        CloseableHttpResponse response = null;
        String content = "";
        try {
            response = httpclient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 200) {
                content = EntityUtils.toString(response.getEntity(), "UTF-8");
                System.out.println("内容长度：" + content);
            }
        } finally {
            if (response != null) {
                response.close();
            }
            httpclient.close();
        }

        Gson gson = new Gson();
        Map map = gson.fromJson(content, Map.class);
        return map;
    }

    @RequestMapping("/auth")
    public Map auth(@RequestHeader Map<String, String> headers, @RequestHeader("x-wx-openid") String openid, @RequestHeader("x-wx-unionid") String unionid) throws Exception {
        Map res = new HashMap(){{
            put("headers", headers);
            put("openid", openid);
            put("unionid", unionid);
        }};
        return res;
    }
}
