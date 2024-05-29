package util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPGetRequest {
    public static synchronized String get(String url, String args) {
        try {
            // 创建 URL 对象
            URL urlPass = new URL(url + "?" + args);

            // 创建 HttpURLConnection 对象
            HttpURLConnection connection = (HttpURLConnection) urlPass.openConnection();

            // 设置请求方式为 GET
            connection.setRequestMethod("GET");

            // 获取响应码
            int responseCode = connection.getResponseCode();
//            System.out.println("Response Code: " + responseCode);

            // 读取响应内容
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // 打印响应内容
//            System.out.println("Response Body: " + response.toString());

            // 关闭连接
            connection.disconnect();
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
