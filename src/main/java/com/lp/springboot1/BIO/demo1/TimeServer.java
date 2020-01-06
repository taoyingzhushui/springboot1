package com.lp.springboot1.BIO.demo1;
import java.io.IOException;
import	java.net.Socket;
import	java.net.ServerSocket;

public class TimeServer {
    public static void main(String[] args) throws IOException {
        int port=8080;
        if(args != null && args.length>0){
            try {
                //设置监听端口
                port = Integer.valueOf(args[0]);
            }catch (NumberFormatException e){

            }
        }
        ServerSocket server = null;

        try{
            server = new ServerSocket(port);//服务器监听
            System.out.println("The time server is start in port : " + port);
            Socket socket = null;
            while (true){
                //监听客户端连接
                socket = server.accept();
                new Thread(new TimeServerHandler(socket)).start();
            }
        }finally {
            if(server != null){
                System.out.println("The time server close");
                server.close();
                server = null;
            }
        }
    }
}
