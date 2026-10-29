package vn.edu.qlchuyenxe.ui.component;

import java.awt.Component;

import javax.swing.JOptionPane;

/**
 * Hộp thoại dùng chung. Mọi nút lệnh trên các Cửa sổ đều hỏi xác nhận
 * Yes/No trước khi thực thi theo yêu cầu của đề bài.
 */
public final class Dialogs {

    private static final String TITLE = "Quản lý sản phẩm theo chuyến xe";
    private static final Object[] YES_NO = {"Yes", "No"};

    private Dialogs() {
    }

    public static boolean confirm(Component parent, String message) {
        int answer = JOptionPane.showOptionDialog(parent, message, TITLE,
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, YES_NO, YES_NO[0]);
        return answer == JOptionPane.YES_OPTION;
    }

    public static void info(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, TITLE, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void warn(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, TITLE, JOptionPane.WARNING_MESSAGE);
    }

    public static void error(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, TITLE, JOptionPane.ERROR_MESSAGE);
    }
}
