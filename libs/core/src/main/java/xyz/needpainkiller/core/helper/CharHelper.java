package xyz.needpainkiller.core.helper;

import org.apache.logging.log4j.util.Strings;

import java.util.Arrays;

public class CharHelper {
    public static boolean isEnglish(char ch) {
        return (ch >= (int) 'A' && ch <= (int) 'Z')
                || (ch >= (int) 'a' && ch <= (int) 'z');
    }

    public static boolean isKorean(char ch) {
        return ch >= Integer.parseInt("AC00", 16)
                && ch <= Integer.parseInt("D7A3", 16);
    }

    public static boolean isNumber(char ch) {
        return ch >= (int) '0' && ch <= (int) '9';
    }

    public static boolean isSpecial(char ch) {
        return (ch >= (int) '!' && ch <= (int) '/') // !"#$%&'()*+,-./
                || (ch >= (int) ':' && ch <= (int) '@') //:;<=>?@
                || (ch >= (int) '[' && ch <= (int) '`') //[\]^_`
                || (ch >= (int) '{' && ch <= (int) '~'); //{|}~
    }

    public static boolean isStringsBlank(String... source) {
        return Arrays.stream(source).anyMatch(Strings::isBlank);
    }

    public static String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }
}
