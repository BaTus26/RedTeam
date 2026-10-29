package vn.edu.qlchuyenxe.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import vn.edu.qlchuyenxe.model.ChiTietChuyenXe;
import vn.edu.qlchuyenxe.model.Xe;
import vn.edu.qlchuyenxe.ui.component.BannerPanel;
import vn.edu.qlchuyenxe.ui.component.Dialogs;
import vn.edu.qlchuyenxe.util.Formats;

/** Cửa sổ kết quả: THỐNG KÊ SẢN PHẨM THEO XE. */
public class BaoCaoFrame extends JFrame {

    private static final String NGUOI_LAP = "Mai Thế Tôn";

    private final Xe xe;
    private final List<ChiTietChuyenXe> rows;

    public BaoCaoFrame(Xe xe, List<ChiTietChuyenXe> rows) {
        super("Thống kê sản phẩm theo xe");
        this.xe = xe;
        this.rows = rows;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        buildUi();
        setMinimumSize(new Dimension(820, 560));
        pack();
        setLocationRelativeTo(null);
    }

    private void buildUi() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);
        content.add(new BannerPanel("THỐNG KÊ SẢN PHẨM THEO XE"), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 10));
        body.setBackground(Color.WHITE);
        body.setBorder(BorderFactory.createEmptyBorder(14, 24, 10, 24));
        body.add(buildHeader(), BorderLayout.NORTH);
        body.add(buildTable(), BorderLayout.CENTER);
        body.add(buildFooter(), BorderLayout.SOUTH);

        content.add(body, BorderLayout.CENTER);
        content.add(buildButtons(), BorderLayout.SOUTH);
        setContentPane(content);
    }

    private JPanel buildHeader() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(3, 4, 3, 4);
        gc.anchor = GridBagConstraints.WEST;

        gc.gridx = 0;
        gc.gridy = 0;
        panel.add(caption("Mã xe:"), gc);
        gc.gridx = 1;
        panel.add(value(xe == null ? "(Tất cả)" : xe.getMaXe()), gc);

        gc.gridx = 2;
        gc.weightx = 1;
        panel.add(new JLabel(), gc);
        gc.weightx = 0;

        gc.gridx = 3;
        panel.add(caption("Tên xe:"), gc);
        gc.gridx = 4;
        panel.add(value(xe == null ? "(Tất cả)" : xe.getTenXe()), gc);

        gc.gridx = 0;
        gc.gridy = 1;
        panel.add(caption("Giá tuyến:"), gc);
        gc.gridx = 1;
        panel.add(value(xe == null ? "" : Formats.money(xe.getGiaTuyen())), gc);
        return panel;
    }

    private JScrollPane buildTable() {
        JTable table = new JTable(new BaoCaoTableModel(rows));
        table.setFont(UiTheme.TABLE_FONT);
        table.setRowHeight(24);
        table.setGridColor(UiTheme.REPORT_BORDER);
        table.setShowGrid(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        table.setDefaultRenderer(Object.class, new ReportCellRenderer());

        JTableHeader header = table.getTableHeader();
        header.setFont(UiTheme.LABEL_FONT.deriveFont(14f));
        header.setBackground(UiTheme.REPORT_HEADER);
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);
        header.setDefaultRenderer(new ReportHeaderRenderer());

        TableColumnModel columns = table.getColumnModel();
        int[] widths = {60, 150, 130, 100, 240};
        for (int i = 0; i < widths.length; i++) {
            columns.getColumn(i).setPreferredWidth(widths[i]);
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(0, 300));
        scroll.setBorder(BorderFactory.createLineBorder(UiTheme.REPORT_BORDER));
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    private JPanel buildFooter() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        left.setBackground(Color.WHITE);
        left.add(caption("Số lượng sản phẩm"));
        left.add(value(String.valueOf(rows.size())));

        JPanel right = new JPanel();
        right.setBackground(Color.WHITE);
        right.setLayout(new javax.swing.BoxLayout(right, javax.swing.BoxLayout.Y_AXIS));
        LocalDate today = LocalDate.now();
        right.add(centered(String.format("TP.HCM, ngày %d tháng %d năm %d",
                today.getDayOfMonth(), today.getMonthValue(), today.getYear())));
        right.add(centered("Người lập"));
        right.add(javax.swing.Box.createVerticalStrut(34));
        right.add(centered(NGUOI_LAP));
        right.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 40));

        panel.add(left, BorderLayout.WEST);
        panel.add(right, BorderLayout.EAST);
        return panel;
    }

    private JPanel buildButtons() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bar.setBackground(Color.WHITE);
        JButton btnDong = new JButton("Đóng");
        btnDong.setFont(UiTheme.BUTTON_FONT);
        btnDong.setPreferredSize(new Dimension(96, 32));
        btnDong.addActionListener(e -> {
            if (Dialogs.confirm(this, "Bạn có chắc chắn đóng Cửa sổ báo cáo không?")) {
                dispose();
            }
        });
        bar.add(btnDong);
        return bar;
    }

    private JLabel caption(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UiTheme.LABEL_FONT);
        label.setForeground(Color.BLACK);
        return label;
    }

    private JLabel value(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UiTheme.LABEL_FONT);
        label.setForeground(UiTheme.REPORT_VALUE);
        label.setPreferredSize(new Dimension(220, 22));
        label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
        return label;
    }

    private JLabel centered(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(UiTheme.LABEL_FONT);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private static final class ReportHeaderRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setFont(UiTheme.LABEL_FONT.deriveFont(14f));
            setBackground(UiTheme.REPORT_HEADER);
            setForeground(Color.WHITE);
            setOpaque(true);
            setHorizontalAlignment(SwingConstants.CENTER);
            setBorder(BorderFactory.createLineBorder(UiTheme.REPORT_BORDER));
            return this;
        }
    }

    private static final class ReportCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(column == 4 ? SwingConstants.LEFT : SwingConstants.CENTER);
            if (!isSelected) {
                c.setBackground(Color.WHITE);
            }
            setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
            return c;
        }
    }
}
