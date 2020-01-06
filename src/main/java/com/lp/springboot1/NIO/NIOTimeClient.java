package com.lp.springboot1.NIO;

public class NIOTimeClient {
    public static void main(String[] args) {
        int port = 8080;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                // 采用默认值
            }
        }
        //创建 TimeClientHandle 线程来处理异步连接和读写操 作
        new Thread(new NIOTimeClientHandle("127.0.0.1",port),"TimeClient-001").start();
    }
}
