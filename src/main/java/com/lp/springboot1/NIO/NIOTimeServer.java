package com.lp.springboot1.NIO;

public class NIOTimeServer {
    public static void main(String[] args) {
        int port = 8080;
        if(args != null && args.length >0){
            try{
                port = Integer.valueOf(args[0]);
            }catch (NumberFormatException e){

            }
        }
        //多路複用類，一个独立 的线程 ，负责轮 询多i路 复用器 Seletor ，可 以处理多个 客户端的并发接 入
        MultiplexerTimeServer timeServer = new MultiplexerTimeServer(port);
        new Thread(timeServer,"NIO-MultiplexerTimeServer-001").start();
    }
}
