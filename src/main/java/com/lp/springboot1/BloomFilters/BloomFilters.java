package com.lp.springboot1.BloomFilters;

import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;

public class BloomFilters {

    private int arraySize;
    private int[] array;

    public BloomFilters(int arraySize) {
        this.arraySize = arraySize;
        array = new int[arraySize];
    }

    /**
     * 写入数据
     *
     * @param key
     */
    public void add(String key) {
        int first = hashcode_1(key);
        int second = hashcode_2(key);
        int third = hashcode_3(key);

        array[first % arraySize] = 1;
        array[second % arraySize] = 1;
        array[third % arraySize] = 1;
    }

    /**
     * 判断数据是否存在
     *
     * @param key
     * @return
     */
    public boolean check(String key) {
        int first = hashcode_1(key);
        int second = hashcode_1(key);
        int third = hashcode_1(key);

        int firstIndex = array[first % arraySize];
        if (firstIndex == 0) {
            return false;
        }

        int secondIndex = array[second % arraySize];
        if (secondIndex == 0) {
            return false;
        }

        int thirdIndex = array[third % arraySize];
        if (thirdIndex == 0) {
            return false;
        }

        return true;
    }

    /**
     * hash 算法1
     *
     * @param key
     * @return
     */
    private int hashcode_1(String key) {
        int hash = 0;
        for (int i = 0; i < key.length(); i++) {
            hash = 33 * hash + key.charAt(i);
        }

        return Math.abs(hash);
    }

    /**
     * hash 算法2
     *
     * @param key
     * @return
     */
    private int hashcode_2(String key) {
        final int p = 16777619;
        int hash = (int) 2166136261L;
        for (int i = 0; i < key.length(); i++) {
            hash = (hash ^ key.charAt(i)) * p;
        }
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;
        return Math.abs(hash);
    }

    /**
     * hash 算法3
     *
     * @param key
     * @return
     */
    private int hashcode_3(String key) {
        int hash = 0;
        for (int i = 0; i < key.length(); i++) {
            hash += key.charAt(i);
            hash += (hash << 10);
            hash ^= (hash >> 6);
        }
        hash += (hash << 3);
        hash ^= (hash >> 11);
        hash += (hash << 15);
        return Math.abs(hash);
    }


    public static void main(String[] args) {
        bloomFilterTest();
        guavaBloomFilter();

    }

    public static void bloomFilterTest() {
        long star = System.currentTimeMillis();
        BloomFilters bloomFilters = new BloomFilters(10000000);
        for (int i = 0; i < 10000000; i++) {
            bloomFilters.add(i + "");
        }
        System.out.println(bloomFilters.check(1 + ""));
        System.out.println(bloomFilters.check(2 + ""));
        System.out.println(bloomFilters.check(3 + ""));
        System.out.println(bloomFilters.check(999999 + ""));
        System.out.println(bloomFilters.check(400230340 + ""));
        long end = System.currentTimeMillis();
        System.out.println("执行时间：" + (end - star));
    }

    public static void guavaBloomFilter() {
        long star = System.currentTimeMillis();
        BloomFilter<String> filter = BloomFilter.create(new Funnel<String>() {
                                                            private static final long serialVersionUID = 1L;

                                                            @Override
                                                            public void funnel(String arg0, PrimitiveSink arg1) {
                                                                arg1.putString(arg0, Charsets.UTF_8);
                                                            }
                                                        },
                10000000,
                0.001);

        for (int i = 0; i < 10000000; i++) {
            filter.put(i +"");
        }
        System.out.println(filter.mightContain(1+""));
        System.out.println(filter.mightContain(2+""));
        System.out.println(filter.mightContain(3+""));
        System.out.println(filter.mightContain(999999+""));
        System.out.println(filter.mightContain(400230340+""));
        long end = System.currentTimeMillis();
        System.out.println("执行时间：" + (end - star));
    }

}
