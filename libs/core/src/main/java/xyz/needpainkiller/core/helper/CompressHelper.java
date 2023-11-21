package xyz.needpainkiller.core.helper;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

@Slf4j
public class CompressHelper {
    private static final String UTF8 = "UTF-8";
    private static final String EMPTY = "";

    /**
     * String 객체를 압축하여 String 으로 리턴한다.
     *
     * @param string
     * @return
     */
    public synchronized static String compressString(String string) {
        try {
            byte[] bytes = compress(string);
            return byteToString(bytes);
        } catch (RuntimeException | IOException e) {
            log.error("compress error : {}", e.getMessage());
            return string;
        }
    }

    /**
     * 압축된 스트링을 복귀시켜서 String 으로 리턴한다.
     *
     * @param compressed
     * @return
     */
    public synchronized static String decompressString(String compressed) {
        try {
            byte[] bytes = hexToByteArray(compressed);
            return decompress(bytes);
        } catch (RuntimeException e) {
            return compressed;
        }
    }

    private static String byteToString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        try {
            for (byte b : bytes) {
                sb.append(String.format("%02X", b));
            }
        } catch (RuntimeException e) {
            log.error("byteToString error : {}", e.getMessage());
            return null;
        }

        return sb.toString();
    }

    private static byte[] compress(String text) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (OutputStream out = new DeflaterOutputStream(baos)) {
            out.write(text.getBytes(StandardCharsets.UTF_8));
            return baos.toByteArray();
        }
    }


    private static String decompress(byte[] bytes) {

        InputStream in = new InflaterInputStream(new ByteArrayInputStream(bytes));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = in.read(buffer)) > 0)
                baos.write(buffer, 0, len);
            return baos.toString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("decompress error : {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 16진 문자열을 byte 배열로 변환한다.
     */
    private static byte[] hexToByteArray(String hex) {
        if (hex == null || hex.length() % 2 != 0) {
            return new byte[]{};
        }

        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < hex.length(); i += 2) {
            byte value = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
            bytes[(int) Math.floor(i / 2)] = value;
        }
        return bytes;
    }

}
