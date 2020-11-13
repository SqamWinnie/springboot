package com.sample.conn.http;

import com.sample.conn.http.dto.Ajson;
import com.sample.conn.http.dto.Car;
import com.sample.conn.http.dto.Person;
import com.sample.conn.http.utils.HttpUtils;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 调用 url 接口得到返回数据
 *
 * @author winnie
 * @date 2020/10/28
 */
public class HttpTest {

    private static final Logger logger = LoggerFactory.getLogger(HttpTest.class);

    public static void main(String[] args) throws IOException {
        String get = "http://localhost:8080/get/param?param=123";
        String postParam = "http://localhost:8080/post/param";
        String postDemo = "http://localhost:8080/post/demo";
        // httpGet1(get);
        // httpGet2(get);
        // httpPostParam(postParam);
        // httpPostDemo(postDemo);
        // connGet(get);
        // connPostParam(postParam);
        // connPostDemo(postDemo);
    }

    /**
     * HttpClient : Get 调用接口并解析成对象（方法一）
     *
     * @throws IOException I/O 异常
     */
    private static void httpGet1(String url) throws IOException {
        // 1. 调用接口获取字符串
        String getString = HttpUtils.sendGet(url);
        convert(getString);
    }

    /**
     * HttpClient : Get 调用接口并解析成对象（方法二）
     *
     * @throws IOException I/O 异常
     */
    private static void httpGet2(String url) throws IOException {
        // 1. 创建HttpClient对象
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        // 2. 创建HttpGet对象
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = null;
        try {
            // 3. 执行GET请求
            response = httpClient.execute(httpGet);
            logger.info("============================> 请求状态：\n" + response.getStatusLine());
            // 4. 获取响应实体
            HttpEntity entity = response.getEntity();
            // 5. 处理响应实体
            if (entity != null) {
                logger.info("============================> 长度：\n" + entity.getContentLength());
                logger.info("============================> 调用接口获取到的内容：\n" + EntityUtils.toString(entity));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 6. 释放资源
            try {
                if (response != null) {
                    response.close();
                }
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * HttpClient : Post 调用接口 @RequestParam 接收
     *
     * @param url 接口地址
     * @throws IOException I/O 异常
     */
    private static void httpPostParam(String url) throws IOException {
        Map<String, String> param = new HashMap<>();
        param.put("username","艾米一");
        param.put("password","123456");
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        String postString = HttpUtils.sendPost(url, null, param, headers);
        logger.info(postString);
    }

    /**
     * HttpClient : Post 调用接口 @RequestBody 接收
     *
     * @param url 接口地址
     * @throws IOException I/O 异常
     */
    private static void httpPostDemo(String url) throws IOException {
        String body = "{\"username\":\"艾米一\",\"password\":\"123456\"}";
        Map<String, String> param = new HashMap<>();
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        String postString = HttpUtils.sendPost(url, body, param, headers);
        logger.info(postString);
    }

    /**
     * connection : Get 调用接口并解析
     *
     * @param urlText url
     */
    private static void connGet(String urlText) {
        try {
            // 1. 得到访问地址的URL
            URL url = new URL(urlText);
            // 2. 得到网络访问对象java.net.HttpURLConnection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            /* 3. 设置请求参数（过期时间，输入、输出流、访问方式），以流的形式进行连接 */
            // 设置是否向HttpURLConnection输出
            connection.setDoOutput(false);
            // 设置是否从httpUrlConnection读入
            connection.setDoInput(true);
            // 设置请求方式
            connection.setRequestMethod("GET");
            // 设置是否使用缓存
            connection.setUseCaches(true);
            // 设置此 HttpURLConnection 实例是否应该自动执行 HTTP 重定向
            connection.setInstanceFollowRedirects(true);
            // 设置超时时间
            connection.setConnectTimeout(3000);
            // 连接（此时未发送请求）
            connection.connect();
            // 4. 发送请求，得到响应状态码的返回值 responseCode
            int code = connection.getResponseCode();
            // 5. 如果返回值正常，数据在网络中是以流的形式得到服务端返回的数据
            StringBuilder msg = new StringBuilder();
            // 正常响应
            if (code == 200) {
                // 从流中读取响应信息
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = null;
                // 循环从流中读取
                while ((line = reader.readLine()) != null) {
                    msg.append(line).append("\n");
                }
                // 关闭流
                reader.close();
            }
            // 6. 断开连接，释放资源
            connection.disconnect();

            // 显示响应结果
            System.out.println(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * connection : Post 调用接口并解析  @RequestParam 接收
     *
     * @param urlText url
     */
    private static void connPostParam(String urlText) {
        try {
            // 1. 获取访问地址 URL
            URL url = new URL(urlText);
            // 2. 创建HttpURLConnection对象
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            /* 3. 设置请求参数等 */
            // 请求方式
            connection.setRequestMethod("POST");
            // 超时时间
            connection.setConnectTimeout(3000);
            // 设置是否输出
            connection.setDoOutput(true);
            // 设置是否读入
            connection.setDoInput(true);
            // 设置是否使用缓存
            connection.setUseCaches(false);
            // 设置此 HttpURLConnection 实例是否应该自动执行 HTTP 重定向
            connection.setInstanceFollowRedirects(true);
            // 设置使用标准编码格式编码参数的名-值对（适用于 @RequestParam 的 get/post 请求）
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            // 连接
            connection.connect();
            /* 4. 处理输入输出 */
            // 写入参数到请求中
            String params = "username=15211&password=123456";
            OutputStream out = connection.getOutputStream();
            out.write(params.getBytes());
            out.flush();
            out.close();
            // 从连接中读取响应信息
            StringBuilder msg = new StringBuilder();
            int code = connection.getResponseCode();
            if (code == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    msg.append(line).append("\n");
                }
                reader.close();
            }
            // 5. 断开连接
            connection.disconnect();
            // 处理结果
            logger.info("========================> \n" + String.valueOf(msg));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * connection : Post 调用接口并解析  @RequestBody 接收
     *
     * @param urlText url
     */
    private static void connPostDemo(String urlText) {
        try {
            // 1. 获取访问地址 URL
            URL url = new URL(urlText);
            // 2. 创建HttpURLConnection对象
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            /* 3. 设置请求参数等 */
            // 请求方式
            connection.setRequestMethod("POST");
            // 超时时间
            connection.setConnectTimeout(3000);
            // 设置是否输出
            connection.setDoOutput(true);
            // 设置是否读入
            connection.setDoInput(true);
            // 设置是否使用缓存
            connection.setUseCaches(false);
            // 设置此 HttpURLConnection 实例是否应该自动执行 HTTP 重定向
            connection.setInstanceFollowRedirects(true);
            // 向 header 中添加信息（适用于 @RequestBody 的 post 请求）
            connection.setRequestProperty("Content-Type", "application/json");

            // 连接
            connection.connect();
            /* 4. 处理输入输出 */
            // 写入参数到请求中
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8);
            String params = "{\"username\":\"艾米一\",\"password\":\"connPostDemo\"}";
            out.append(params);
            out.flush();
            out.close();
            // 从连接中读取响应信息
            StringBuilder msg = new StringBuilder();
            if (connection.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                String line;
                while ((line = reader.readLine()) != null) {
                    msg.append(line).append("\n");
                }
                reader.close();
            }
            // 5. 断开连接
            connection.disconnect();
            // 处理结果
            logger.info("========================> 获取的结果：\n" + msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将 json 字符串转化成对象
     * String str = "{persons:[{name:'Amy',id:1},{name:'Bob',id:2}], cars:[{name:'car1',id:1},{name:'car2',id:2}]}";
     */
    private static void convert(String str){
        // 1. 将字符串转化成 JSON 对象
        JSONObject jsonObject = JSONObject.fromObject(str);
        // 2. 将 JSON 对象转化成 Java 对象
        Map<String, Object> map = new HashMap<>();
        map.put("persons", Person.class);
        map.put("cars", Car.class);
        Ajson json = (Ajson) JSONObject.toBean(jsonObject, Ajson.class, map);
        logger.info("========================> \n" + json.toString());
    }


    private static void httpConn() {
        URL url = null;
        try {
            url = new URL("https://oauth.fangcloud.com/oauth/token");

            // 将url 以 open方法返回的urlConnection 连接强转为HttpURLConnection连接
            // (标识一个url所引用的远程对象连接)
            // 此时cnnection只是为一个连接对象,待连接中
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // 设置连接输出流为true,默认false (post 请求是以流的方式隐式的传递参数)
            connection.setDoOutput(true);
            // 设置连接输入流为true
            connection.setDoInput(true);
            // 设置请求方式为post
            connection.setRequestMethod("POST");
            // post请求缓存设为false
            connection.setUseCaches(false);
            // 设置该HttpURLConnection实例是否自动执行重定向
            connection.setInstanceFollowRedirects(true);
            String client_id = "fb8897c4-fc15-4a59-93cd-e4b1a3a7edbe";
            String client_secret = "1ab88f21-dcc6-4b61-938d-5d1376678194";
            String authString = client_id + ":" + client_secret;
            byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
            String authStringEnc = new String(authEncBytes);
            connection.setRequestProperty("Authorization", "Basic " + authStringEnc);
            // 建立连接
            connection.connect();
            // 创建输入输出流,用于往连接里面输出携带的参数,(输出内容为?后面的内容)
            DataOutputStream dataout = new DataOutputStream(connection.getOutputStream());
            // 格式 parm = aaa=111&bbb=222&ccc=333&ddd=444
            String parm = "grant_type=refresh_token&refresh_token=" + "18881b0e-ed04-4041-aac9-1e179176f815";
            // System.out.println("传递参数："+parm);
            // 将参数输出到连接
            dataout.writeBytes(parm);
            // 输出完成后刷新并关闭流
            dataout.flush();
            dataout.close(); // 重要且易忽略步骤 (关闭流,切记!)
            // 连接发起请求,处理服务器响应 (从连接获取到输入流并包装为bufferedReader)
            BufferedReader bf = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String line;
            // 用来存储响应数据
            StringBuilder sb = new StringBuilder();

            // 循环读取流,若不到结尾处
            while ((line = bf.readLine()) != null) {
                sb.append(line).append(System.getProperty("line.separator"));
            }
            // 重要且易忽略步骤 (关闭流,切记!)
            bf.close();
            // 销毁连接
            connection.disconnect();

            JSONObject jasonObject2 = JSONObject.fromObject(sb.toString());
            String identify = "freshlinks@cn.lvmh-pc.com";
            String accessToken = (jasonObject2).get("access_token").toString();
            String newRefreshToken = (jasonObject2).get("refresh_token").toString();
            logger.info("==============================================================================");
            logger.info(identify);
            logger.info(accessToken);
            logger.info(newRefreshToken);
            logger.info(String.valueOf(jasonObject2));
            logger.info(String.valueOf(21599 / 1000));
            logger.info("==============================================================================");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void postMessage() throws IOException {
        // 把字符串转换为URL请求地址
        URL url = new URL("https://oauth.fangcloud.com/oauth/authorize?response_type=code&client_id=fb8897c4-fc15-4a59-93cd-e4b1a3a7edbe&redirect_uri=http://10.3.32.28:8081/core/lib/hap/fresh/link/fangcloud/gitcode");
        // 打开连接
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        // 连接会话
        connection.connect();
        // 获取输入流
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
        String line;
        // 用来存储响应数据
        StringBuilder sb = new StringBuilder();

        // 循环读取流,若不到结尾处
        while ((line = br.readLine()) != null) {
            sb.append(line).append(System.getProperty("line.separator"));
        }
        logger.info(String.valueOf(sb));
        br.close(); // 重要且易忽略步骤 (关闭流,切记!)
        connection.disconnect(); // 销毁连接
    }



}
