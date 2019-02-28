package com.itheima.utils;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import javax.swing.text.html.parser.Entity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Name: HttpClientUtil
 * User: zhaocq
 * Date: 2018/12/6 0006
 * Time: 17:10
 * Description: 爬取数据的工具类
 */
@Component
public class HttpClientUtil {

    private PoolingHttpClientConnectionManager pm;

    // 设置连接池
    public HttpClientUtil() {
        // 创建连接池对象
        pm = new PoolingHttpClientConnectionManager();
        // 设置最大连接数
        pm.setMaxTotal(50);
        // 设置每个主机的并最大发数
        pm.setDefaultMaxPerRoute(25);

    }

    /* *
     * @Author zhaocq
     * @Description //TODO 用来爬取Html页面，并转化为字符串返回
     * @Date 17:13 2018/12/6 0006
     * @Param []
     * @return java.lang.String
     **/
    public String getHtml(String url){
        // 创建HttpClient 对象
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(pm).build();
        // 创建请求对象
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse httpResponse = null;
        try {
            // 执行爬取
            httpResponse = httpClient.execute(httpGet);
            // 将结果转化为字符串返回
            if(httpResponse.getStatusLine().getStatusCode() == 200){
                String htmlStr = "";
                if(httpResponse.getEntity() != null){
                    htmlStr = EntityUtils.toString(httpResponse.getEntity(),"utf-8");
                }

                return htmlStr;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(httpResponse != null){
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }


    /* *
     * @Author zhaocq
     * @Description //TODO 爬取图片资源并且下载本地，最后将图片名称返回
     * @Date 17:22 2018/12/6 0006
     * @Param []
     * @return java.lang.String https:
     **/
    public String getImg(String imgUrl){
        // 创建HttpClient 对象
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(pm).build();
        // 创建请求对象
        if(!"https:".equals(imgUrl)){
            HttpGet httpGet = new HttpGet(imgUrl);
            CloseableHttpResponse httpResponse = null;
            try {
                // 执行爬取
                httpResponse = httpClient.execute(httpGet);
                // 将结果转化为字符串返回
                if(httpResponse.getStatusLine().getStatusCode() == 200){
                    String imgStr = "";
                    if(httpResponse.getEntity() != null){
                        // 图片资源处理
                        // 1. 获取图片后缀名
                        String endName = imgUrl.substring(imgUrl.lastIndexOf("."));
                        // 2. 生成图片的名称
                        imgStr = UUID.randomUUID().toString() + endName;
                        // 3.将图片下载到本地
                        OutputStream outputStream = new FileOutputStream(new File("C:\\Users\\65780\\Desktop" + imgStr));
                        // 4.将输入流写入到输出流
                        httpResponse.getEntity().writeTo(outputStream);
                    }

                    return imgStr;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(httpResponse != null){
                    try {
                        httpResponse.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
        return null;
    }
}
