package xyz.needpainkiller.core.helper;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class Inets {
    private static final long p3_256 = 256L * 256L * 256L;
    private static final long p2_256 = 256L * 256L;

    public static Long aton(String ip) {
        if (ip == null) return null;
        List<Integer> vals = Arrays.stream(ip.split("\\."))
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .toList();
        if (vals.isEmpty()) return null;
        if (vals.size() == 1) return vals.get(0).longValue();
        if (vals.size() == 2) return vals.get(0) * p3_256 + vals.get(1);
        if (vals.size() == 3) return vals.get(0) * p3_256 + vals.get(1) * p2_256 + vals.get(2);
        else return vals.get(0) * p3_256 + vals.get(1) * p2_256 + vals.get(2) * 256L + vals.get(3);
    }

    public static String ntoa(Long num) {
        if (num == null) return null;
        long d = num % 256;
        long c = num / 256 % 256;
        long b = num / (p2_256) % 256;
        long a = num / (p3_256) % 256;
        return String.format("%s.%s.%s.%s", a, b, c, d);
    }
}