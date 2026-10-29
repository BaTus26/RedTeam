package vn.edu.qlchuyenxe.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public final class Formats {

    public static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final DecimalFormat MONEY;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator('.');
        MONEY = new DecimalFormat("#,##0", symbols);
    }

    private Formats() {
    }

    public static String money(long value) {
        return MONEY.format(value);
    }

    public static String date(LocalDate value) {
        return value == null ? "" : DATE.format(value);
    }

    /** Trả về null khi chuỗi không phải ngày hợp lệ theo dd/MM/yyyy. */
    public static LocalDate parseDate(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(text.trim(), DATE);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    public static long parseLong(String text, long fallback) {
        try {
            return Long.parseLong(text.trim().replace(".", "").replace(",", ""));
        } catch (RuntimeException ex) {
            return fallback;
        }
    }
}
