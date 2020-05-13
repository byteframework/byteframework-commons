package com.byteframework.commons.util;

import java.util.Random;

public class RandomUtils {

    private static Random random = new Random();


    public static int randomInt(int min, int max) {
        if (min > max) return 0;
        if (min == max) return min;
        if (max <= 1) return 1;
        return random.nextInt(max) % (max - min + 1) + min;
    }

    public static double randomDouble(double min, double max) {
        int base = 100000;
        int ri = randomInt(new Double(base * min).intValue(), new Double(base * max).intValue());
        return ri * 1.0 / base;
    }


    public static String generateMac() {
        StringBuffer sb = new StringBuffer();
        for (int i = 1; i <= 6; i++) {
            sb.append(Integer.toHexString(RandomUtils.randomInt(0, 256)).toUpperCase());
        }
        return sb.toString();
    }


    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            System.out.println(randomInt(1, 3));
        }
    }
}
