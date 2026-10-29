package vn.edu.qlchuyenxe.ui.component;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import vn.edu.qlchuyenxe.ui.UiTheme;

/** Dải tiêu đề xanh đậm: dòng "Bến xe Miền Tây" nhỏ ở trên, tên Cửa sổ ở giữa. */
public class BannerPanel extends JPanel {

    private static final int HEIGHT = 86;
    private static final int SIDE_PADDING = 28;

    private final JLabel heading;

    public BannerPanel(String title) {
        super(new BorderLayout());
        setBackground(UiTheme.BANNER);
        setBorder(BorderFactory.createEmptyBorder(4, 14, 10, 14));

        JLabel subtitle = new JLabel("Bến xe Miền Tây");
        subtitle.setFont(UiTheme.BANNER_SUBTITLE);
        subtitle.setForeground(UiTheme.BANNER_TEXT);

        heading = new JLabel(title, SwingConstants.CENTER);
        heading.setFont(UiTheme.BANNER_TITLE);
        heading.setForeground(UiTheme.BANNER_TEXT);

        add(subtitle, BorderLayout.NORTH);
        add(heading, BorderLayout.CENTER);
    }

    /** Dải tiêu đề phải đủ rộng cho tên Cửa sổ, nếu không pack() sẽ cắt chữ. */
    @Override
    public Dimension getPreferredSize() {
        int titleWidth = getFontMetrics(heading.getFont()).stringWidth(heading.getText());
        return new Dimension(titleWidth + SIDE_PADDING * 2, HEIGHT);
    }
}
