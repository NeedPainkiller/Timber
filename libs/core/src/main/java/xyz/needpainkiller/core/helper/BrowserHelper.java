package xyz.needpainkiller.core.helper;


import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@UtilityClass
public class BrowserHelper {

    public static String getBrowser(HttpServletRequest req) {
        String userAgent = req.getHeader("User-Agent");
        if (userAgent.contains("MSIE")
                || userAgent.contains("Trident") //IE11
                || userAgent.contains("Edge")) {
            return "MSIE";
        } else if (userAgent.contains("Chrome")) {
            return "Chrome";
        } else if (userAgent.contains("Opera")) {
            return "Opera";
        } else if (userAgent.contains("Safari")) {
            return "Safari";
        } else if (userAgent.contains("Firefox")) {
            return "Firefox";
        } else {
            return null;
        }
    }

    public static String getEncodeFileName(HttpServletRequest req, String originalFileName) {
        String userAgent = req.getHeader("User-Agent");
        String fileName = null;
        if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
            fileName = URLEncoder.encode(originalFileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        } else {
            fileName = new String(originalFileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        }
        return fileName;
    }


}
