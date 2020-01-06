package com.lp.springboot1.NIO;
import	java.nio.ByteBuffer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NIOTimeClientHandle  implements Runnable{
    private String host;
    private int port;
    private Selector selector;
    private SocketChannel socketChannel;
    private volatile boolean stop;

    public NIOTimeClientHandle(String host,int port){
        this.host = host == null? "127.0.0.1":host;
        this.port = port;
        try{
            selector = Selector.open();
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);

        }catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    @Override
    public void run() {
        try{
            doConnect();
        }catch (IOException e){
            e.printStackTrace();
            System.exit(1);
        }

        while(!stop){
            try{
                selector.select(1000);
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectedKeys.iterator();
                SelectionKey key = null;
                while(iterator.hasNext()){
                    key = iterator.next();
                    iterator.remove();
                    try{
                        handleInput(key);
                    }catch (Exception e){
                        if(key != null){
                            key.cancel();
                            if(key.channel() != null){
                                key.channel().close();
                            }
                        }
                    }
                }
            }catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        if(selector != null){
            try{
                selector.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }

    }

    private void handleInput(SelectionKey key) throws IOException {
        if(key.isValid()){
            SocketChannel socketChannel = (SocketChannel) key.channel();
            //处于连接状态
            if(key.isConnectable()){
                //对连接结果进行判断,如果返回值为true，说明客户端连接成功
                if(socketChannel.finishConnect()){
                    //将SocketChannel注册到多路复用器上，注册SelectionKey.OP_READ操作位
                    socketChannel.register(selector,SelectionKey.OP_READ);
                    doWrite(socketChannel);
                }else{
                    System.exit(1);
                }
            }
        }
        //如果客户端接收到了服务端的应答消息，则SocketChannel是可读的
        if(key.isReadable()){
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            //进行异步读取操作
            int readBytes = socketChannel.read(byteBuffer);
            if(readBytes > 0){
                byteBuffer.flip();
                byte[] bytes = new byte[byteBuffer.remaining()];
                byteBuffer.get(bytes);
                String body = new String(bytes,"utf-8");
                System.out.println("now is : "+body);
                this.stop = true;
            }else if (readBytes < 0) {
                // 对端链路关闭
                key.cancel();
                socketChannel.close();
            } else {
            }
        }
    }
    private void doConnect() throws IOException {
        // 如果直接连接成功，则注册到多路复用器上，发送请求消息，读应答
        if(socketChannel.connect(new InetSocketAddress(host,port))){
            socketChannel.register(selector, SelectionKey.OP_READ);
            doWrite(socketChannel);
        }else{
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
        }
    }

    private void doWrite(SocketChannel sc) throws IOException{
        byte[] req = "QUERY TIME ORDER".getBytes();
        ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);
        writeBuffer.put(req);
        writeBuffer.flip();
        sc.write(writeBuffer);
        if (!writeBuffer.hasRemaining()) {
            System.out.println("Send order 2 server succeed.");
        }
    }
}
