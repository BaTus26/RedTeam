package vn.edu.qlchuyenxe.util;

/** Các ràng buộc dữ liệu dùng chung cho mọi Cửa sổ. */
public final class Validator {

    private Validator() {
    }

    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean isCode(String value) {
        return !isBlank(value) && value.trim().matches("[A-Za-z0-9_]{1,10}");
    }

    public static boolean isName(String value) {
        return !isBlank(value) && value.trim().length() <= 50;
    }

    public static boolean isNonNegativeNumber(String value) {
        if (isBlank(value)) {
            return false;
        }
        String digits = value.trim();
        if (!digits.matches("[0-9]{1,15}")) {
            return false;
        }
        return Long.parseLong(digits) >= 0;
    }

    public static boolean isPositiveNumber(String value) {
        return isNonNegativeNumber(value) && Long.parseLong(value.trim()) > 0;
    }

    public static boolean isDate(String value) {
        return Formats.parseDate(value) != null;
    }
}
