package com.lp.springboot1.netty;


import io.netty.bootstrap.ServerBootstrap;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class nettyServer1 {
    public static void main(String[] args) {
        ServerBootstrap bootstrap = new ServerBootstrap();

        ExecutorService boss = Executors.newCachedThreadPool();
        ExecutorService worker = Executors.newCachedThreadPool();


    }
}
