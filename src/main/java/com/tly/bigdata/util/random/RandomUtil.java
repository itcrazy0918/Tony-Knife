package com.tly.bigdata.util.random;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.gzyouai.hummingbird.common2.component.GameRuntimeException;
import com.gzyouai.hummingbird.common2.util.collection.CollectionUtil;

import io.netty.util.internal.ThreadLocalRandom;

public class RandomUtil {
    private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    
    /**
     * 计算是否中奖
     * @param base
     * @param rate
     * @return
     */
    public static boolean lucky (int base, int rate) {
        if (rate < 0 || rate > base || base <= 0) {
            throw new GameRuntimeException("lucky illegal param. rate=" + rate + ", base=" + base);
        }
        
        if (rate == 0) {
            return false;
        }        
        if (rate == base) {
            return true;
        }
        
        int luckyValue = randomInt(0, base);
        return luckyValue < rate;
    }
    
    /**
     * 权重 随机奖励
     * @param list
     * @return
     */
    public static <T extends WeightRandomItem> T weightRandom (List<T> list) {
        if (null == list) {
            throw new NullPointerException("weightRandom: list=" + list);
        }
        if (list.size() == 0) {
            return null;
        }
        
        // 计算累加 weight list
        int accumulationWeight = 0;        
        final int size = list.size();
        List<Integer> accumulationWeightList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            accumulationWeight += list.get(i).getWeight();
            accumulationWeightList.add(accumulationWeight);
        }
        
        if (accumulationWeight < 0) {
            throw new GameRuntimeException("weightRandom: illegal accumulationWeight=" + accumulationWeight);
        }
        
        // 随机计算
        if (accumulationWeight == 0) {
            return null;
        }
        
        final int lucky = RandomUtil.randomInt(0, accumulationWeight  + 1);
        for (int i = 0; i < size; i++) {
            if (lucky <= accumulationWeightList.get(i)) {
                return list.get(i);
            }
        }
        
        return null;
    }
    
    /**
     * 概率 随机奖励
     * @param list
     * @return
     */
    public static <T extends RateRandomItem> T rateRandom (List<T> list) {
        if (null == list) {
            throw new NullPointerException("rateRandom: list=" + list);
        }
        if (list.size() == 0) {
            return null;
        }
        
        // 计算累加概率 list
        int accumulationRate = 0;        
        final int size = list.size();
        List<Integer> accumulationRateList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            accumulationRate += list.get(i).getRate();
            accumulationRateList.add(accumulationRate);
        }
        
        final int UPPER_LIMIT = 10000;
        if (accumulationRate < 0 || accumulationRate > UPPER_LIMIT) {
            throw new GameRuntimeException("rateRandom: illegal accumulationRate=" + accumulationRate + ", UPPER_LIMIT=" + UPPER_LIMIT);
        }
        
        // 随机计算
        if (accumulationRate == 0) {
            return null;
        }
        
        final int lucky = RandomUtil.randomInt(0, UPPER_LIMIT  + 1);
        for (int i = 0; i < size; i++) {
            if (lucky <= accumulationRateList.get(i)) {
                return list.get(i);
            }
        }
        
        return null;
    }
    
    /**
     * 在 List 里面随机出一个元素出来
     * @param list
     * @return
     */
    public static <T> T randomList (List<T> list) {
        if (CollectionUtil.isNullOrEmpty(list)) {
            return null;
        }
        
        if (list.size() == 1) {
            return list.get(0);
        }
        
        int rnd = new Random().nextInt(list.size());
        return list.get(rnd);
    }
    
    /**
     * 在 [a, b) 之间随机出一个整数
     * @param a
     * @param b
     * @return
     */
    public static int randomInt (int a, int b) {
        if (a >= b) {
            throw new GameRuntimeException("randomInt illegal param. a=" + a + ", b=" + b);
        }
        
        int diff = b - a;
        int num = ThreadLocalRandom.current().nextInt(diff);
        return a + num;
    }
    
    /**
     * 随机长度为size的文本
     * @param size
     * @return
     */
    public static String randomText (int size) {
        if (size < 1) {
            throw new GameRuntimeException("randomText illegal param. size=" + size);
        }
        
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < size; i++) {
            int rnd = randomInt(0, LETTERS.length());
            char ch = LETTERS.charAt(rnd);
            text.append(ch);
        }
        
        return text.toString();
    }
    
    public static void main(String[] args) {
//        System.out.println( randomText(6) );
        
//        System.out.println( randomInt(0, 2) );
//        System.out.println( randomInt(2, 2) );
        
//        System.out.println( randomList(null) );
//        System.out.println( randomList(new ArrayList<>()) );
//        System.out.println( randomList(Lists.newArrayList(10,20)) );
        
//        System.out.println( rateRandom(null) );        
//        System.out.println( rateRandom(new ArrayList<RateRandomItem>()) );
//        System.out.println( rateRandom(Lists.newArrayList(
//                new RateRandomItem() {public int getRate() {return 0;}; public String toString () {return "1";}}
//                )) );
//        System.out.println( rateRandom(Lists.newArrayList(
//                new RateRandomItem() {public int getRate() {return 10000;}; public String toString () {return "1";}}
//                )) );
//        System.out.println( rateRandom(Lists.newArrayList(
//                new RateRandomItem() {public int getRate() {return 9000;}; public String toString () {return "1";}},
//                new RateRandomItem() {public int getRate() {return 9000;}; public String toString () {return "2";}}
//                )) );
//        System.out.println( rateRandom(Lists.newArrayList(
//                new RateRandomItem() {public int getRate() {return 9000;}; public String toString () {return "1";}},
//                new RateRandomItem() {public int getRate() {return 1000;}; public String toString () {return "2";}}
//                )) );
        
//        System.out.println( weightRandom(null) );
//        System.out.println( weightRandom(new ArrayList<WeightRandomItem>()) );
//        System.out.println( weightRandom(Lists.newArrayList(
//              new WeightRandomItem() {public int getWeight() {return 0;}; public String toString () {return "1";}}
//              )) );
//        System.out.println( weightRandom(Lists.newArrayList(
//                new WeightRandomItem() {public int getWeight() {return 9;}; public String toString () {return "1";}},
//                new WeightRandomItem() {public int getWeight() {return 1;}; public String toString () {return "2";}}
//                )) );
    }
}
