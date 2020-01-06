package com.lp.springboot1.ipaddress;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class getRealIpAddress {

    public static String getIpAddress(HttpServletRequest request) {
        //通过了HTTP代理或者负载均衡服务器时才会添加该项,一般情况下，第一个ip为客户端真实ip，后面的为经过的代理服务器ip
        String ip = request.getHeader("x-forwarded-for");

        //经过apache http服务器的请求才会有，用apache http做代理时一般会加上Proxy-Client-IP请求头，
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        //而WL-Proxy-Client-IP是它的weblogic插件加上的头
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        //有些代理服务器会加上此请求头
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        //nginx代理一般会加上此请求头
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            return ip.split(",")[0];
        } else {
            return ip;
        }
    }

    public static void main(String[] args) {
        System.err.println("公网ipppppppppppp:" + getPublicIp());
    }

    public static String getPublicIp(){
        try{
            String path = "http://144.131.254.26:29103/corp/admin/loginpage.htm";
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("contentType", "UTF-8");
            conn.setConnectTimeout(5*1000);
            conn.setRequestMethod("GET");

            InputStream is = conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
            StringBuffer sb = new StringBuffer();
            String line= "";
            while((line = br.readLine())!= null){
                sb.append(line);
            }
            String str = sb.toString();
            String ipString1 = str.substring(str.indexOf("["));
            String ipString2 = ipString1.substring(ipString1.indexOf("[") + 1,
                    ipString1.lastIndexOf("]"));
            return ipString2;
        }catch (Exception e){
            System.out.println("获取公网IP连接超时");
            return "连接超时";
        }
    }
}
