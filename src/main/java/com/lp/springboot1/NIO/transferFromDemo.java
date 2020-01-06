package com.lp.springboot1.NIO;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

public class transferFromDemo {
    public static void main(String[] args) throws IOException {
        RandomAccessFile fromFile = new RandomAccessFile("D:\\fromFile.txt", "rw");
        FileChannel fromChannel = fromFile.getChannel();

        RandomAccessFile toFile = new RandomAccessFile("D:\\toFile.txt", "rw");
        FileChannel toChannel = toFile.getChannel();

        long position = 0;
        long count = fromChannel.size();
        //toChannel.transferFrom(fromChannel,position,count);
        fromChannel.transferTo(position,count,toChannel);
    }

}
