package xyz.needpainkiller.core.helper;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

@UtilityClass
@Slf4j
public class CalcHelper {
    public static final String PERCENT_FORMAT_STR = "#.##";
    public static final DecimalFormat percentageFormatter = new DecimalFormat(PERCENT_FORMAT_STR);


    public static String calcPercentageStr(int numerator, int denominator) {
        double result = calcPercentage(numerator, denominator);
        if (Double.isNaN(result)) {
            return PERCENT_FORMAT_STR;
        }
        try {
            return percentageFormatter.format(result);
        } catch (RuntimeException e) {
            return String.valueOf(result);
        }
    }

    public static double calcPercentageFixed(long numerator, long denominator) {
        double result = calcPercentage(numerator, denominator);
        if (Double.isNaN(result)) {
            return 0;
        }
        try {
            BigDecimal percentage = BigDecimal.valueOf(result);
            return percentage.setScale(2, RoundingMode.CEILING).doubleValue();
        } catch (RuntimeException e) {
            return result;
        }
    }

    public static double calcPercentage(double numerator, double denominator) {
        try {
            return (numerator / denominator) * 100;
        } catch (RuntimeException e) {
            return Double.NaN;
        }
    }
}
