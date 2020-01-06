package com.lp.springboot1.NIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class MultiplexerTimeServer implements Runnable {

    private Selector selector;
    private ServerSocketChannel serverChannel;
    private volatile boolean stop;

    //初始化多路复用器、绑定监听端口
    public MultiplexerTimeServer(int port) {
        try {
            //创建多路复用器
            selector = Selector.open();
            //用于监听客户端的连接，它是有客户端连接的父管道
            serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);
            //绑定 监听听端口，设置连接为非阻塞模式
            serverChannel.socket().bind(new InetSocketAddress(port), 1024);
            //将 ServerSocketChannel注册到线程的多路复用器 Selector上 ，监 听ACCEPT 事件
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("The time server is start in port : " + port);

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void stop() {
        this.stop = true;
    }

    @Override
    public void run() {
        while (!stop) {
            try {
                //selector 每隔 1s 都被唤醒一次
                selector.select(1000);
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iter = selectedKeys.iterator();
                SelectionKey key = null;
                while (iter.hasNext()) {
                    key = iter.next();
                    iter.remove();
                    try {
                        handleInput(key);
                    } catch (Exception e) {
                        if (key != null) {
                            key.cancel();
                            if (key.channel() != null) {
                                key.channel().close();
                            }
                        }
                    }
                }
            } catch (Exception t) {
                t.printStackTrace();
            }
        }
        if (selector != null) {
            try {
                selector.close();
            } catch (IOException t) {
                t.printStackTrace();
            }
        }
    }

    private void handleInput(SelectionKey key) throws IOException {
        if (key.isValid()) {
            // 处理新接入的请求消息
            if (key.isAcceptable()) {
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                //接收客户端的连接请求并创 建 SocketChannel 实例
                //accept后相当于完成了tcp的3次握手，物理链路建立
                SocketChannel sc = ssc.accept();
                //异步非阻塞
                sc.configureBlocking(false);
                sc.register(selector, SelectionKey.OP_READ);
            }
            if (key.isReadable()) {
                //读取客户端请求
                SocketChannel sc = (SocketChannel) key.channel();
                //开辟一个1K 的缓冲 区
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                //调用SocketChannel 的 read 方法读 取请求码流
                int readBytes = sc.read(readBuffer);
                //读到 了字节 ，对字 节进行编解码
                if (readBytes > 0) {
                    //将 缓冲区 当前的 limit 设置 为 position,position设置 为 0，用于后续对 缓冲区的读取操作
                    readBuffer.flip();
                    //根据缓冲区可读的字节个数创建字节数组
                    byte[] bytes = new byte[readBuffer.remaining()];
                    //将缓冲区可读的字节数组复制到新创建的字节数组中
                    readBuffer.get(bytes);
                    String body = new String(bytes, "UTF-8");
                    System.out.println("thie time server receive order: " + body);
                    String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new java.util.Date(
                            System.currentTimeMillis()).toString()
                            : "BAD ORDER";
                    doWrite(sc, currentTime);
                    //链路 已经关闭 ，需要关 闭 SocketChannel，释放资源
                } else if(readBytes<0){
                    key.cancel();
                    sc.close();
                    //没有读 取到字节 ，属 于正常场景 ，忽略
                } else {
                }
            }
        }

    }

    private void doWrite(SocketChannel channel,String response) throws IOException {
        if(response != null && response.trim().length()>0){
            //将字 符 串编 码成字 节数组
            byte[] bytes = response.getBytes();
            //根 据 字节数组的容量创建 ByteBuffer
            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
            //调 用ByteBuffer的 put 操作将字节数组复制到缓 冲 区中
            writeBuffer.put(bytes);
            //对 缓冲区j进 行 flip 操作
            writeBuffer.flip();
            //将缓冲 区中的字节数组发送 出去
            channel.write(writeBuffer);
        }
    }
}
