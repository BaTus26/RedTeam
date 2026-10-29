package vn.edu.qlchuyenxe.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;

import javax.swing.UIManager;

/** Bảng màu và phông chữ bám theo giao diện mẫu của đề bài. */
public final class UiTheme {

    public static final Color BANNER = new Color(37, 49, 140);
    public static final Color BANNER_TEXT = Color.WHITE;
    public static final Color LABEL = new Color(23, 54, 125);
    public static final Color ACCENT_RED = new Color(192, 0, 0);
    public static final Color GRID_HEADER = new Color(226, 230, 240);
    public static final Color GRID_LINE = new Color(170, 180, 200);
    public static final Color ROW_ALT = new Color(244, 247, 252);
    public static final Color SELECTION = new Color(146, 197, 234);
    public static final Color FORM_BG = Color.WHITE;

    public static final Color REPORT_HEADER = new Color(61, 174, 74);
    public static final Color REPORT_BORDER = new Color(192, 0, 0);
    public static final Color REPORT_VALUE = new Color(0, 51, 204);

    private static final String FAMILY = pickFamily();

    public static final Font BANNER_TITLE = new Font(FAMILY, Font.BOLD, 30);
    public static final Font BANNER_SUBTITLE = new Font(FAMILY, Font.BOLD | Font.ITALIC, 15);
    public static final Font LABEL_FONT = new Font(FAMILY, Font.BOLD, 15);
    public static final Font FIELD_FONT = new Font(FAMILY, Font.BOLD, 14);
    public static final Font TABLE_FONT = new Font(FAMILY, Font.PLAIN, 14);
    public static final Font TABLE_HEADER_FONT = new Font(FAMILY, Font.PLAIN, 14);
    public static final Font BUTTON_FONT = new Font(FAMILY, Font.BOLD, 13);
    public static final Font REPORT_TITLE = new Font(FAMILY, Font.BOLD, 28);

    private UiTheme() {
    }

    private static String pickFamily() {
        String[] available = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        for (String candidate : new String[] {"Times New Roman", "Liberation Serif", "DejaVu Serif"}) {
            if (Arrays.asList(available).contains(candidate)) {
                return candidate;
            }
        }
        return Font.SERIF;
    }

    public static void install() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ignored) {
            // Giao diện mặc định vẫn dùng được.
        }
        UIManager.put("OptionPane.messageFont", LABEL_FONT.deriveFont(Font.PLAIN));
        UIManager.put("OptionPane.buttonFont", BUTTON_FONT);
        UIManager.put("ToolTip.font", TABLE_FONT);
    }
}
