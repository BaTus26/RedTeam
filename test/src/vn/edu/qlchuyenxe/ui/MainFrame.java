package vn.edu.qlchuyenxe.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

import vn.edu.qlchuyenxe.model.ChiTietChuyenXe;
import vn.edu.qlchuyenxe.model.Xe;
import vn.edu.qlchuyenxe.service.BusinessException;
import vn.edu.qlchuyenxe.service.QuanLyChuyenXeService;
import vn.edu.qlchuyenxe.service.ThongKe;
import vn.edu.qlchuyenxe.ui.component.BannerPanel;
import vn.edu.qlchuyenxe.ui.component.Dialogs;
import vn.edu.qlchuyenxe.ui.component.NavIcon;
import vn.edu.qlchuyenxe.ui.component.ValidatedTextField;
import vn.edu.qlchuyenxe.util.Formats;
import vn.edu.qlchuyenxe.util.TxtExporter;
import vn.edu.qlchuyenxe.util.Validator;

/** Cửa sổ chính: Quản lý sản phẩm theo chuyến xe. */
public class MainFrame extends JFrame {

    private final QuanLyChuyenXeService service;

    private final ValidatedTextField txtMaXe = ValidatedTextField.pattern(
            18, "[A-Za-z0-9_]*", 10, Validator::isCode, "Mã xe không được để trống (tối đa 10 ký tự chữ/số)!");
    private final ValidatedTextField txtTenXe = new ValidatedTextField(
            18, Validator::isName, "Tên xe không được để trống (tối đa 50 ký tự)!");
    private final ValidatedTextField txtGiaTuyen = ValidatedTextField.digits(
            18, 15, Validator::isNonNegativeNumber, "Giá tuyến không được để trống và phải là số nguyên!");

    private final ChiTietTableModel tableModel = new ChiTietTableModel();
    private final JTable table = new JTable(tableModel);

    private final JLabel lblSoLuongSanPham = new JLabel("0");
    private final JTextField txtTongThanhTien = new JTextField(14);
    private final JTextField txtChiSo = new JTextField("0/0", 5);

    public MainFrame(QuanLyChuyenXeService service) {
        super("Quản lý sản phẩm theo chuyến xe");
        this.service = service;
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        buildUi();
        napDuLieuBanDau();
    }

    /* --------------------------------- Giao diện --------------------------------- */

    private void buildUi() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(UiTheme.FORM_BG);
        content.add(new BannerPanel("QUẢN LÝ SẢN PHẨM THEO CHUYẾN XE"), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 6));
        center.setBackground(UiTheme.FORM_BG);
        center.setBorder(BorderFactory.createEmptyBorder(10, 18, 6, 18));
        center.add(buildFormPanel(), BorderLayout.NORTH);
        center.add(buildTablePanel(), BorderLayout.CENTER);
        center.add(buildThongKePanel(), BorderLayout.SOUTH);

        content.add(center, BorderLayout.CENTER);
        content.add(buildButtonBar(), BorderLayout.SOUTH);

        setContentPane(content);
        setMinimumSize(new Dimension(980, 660));
        pack();
        setLocationRelativeTo(null);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                ketThuc();
            }
        });
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UiTheme.FORM_BG);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 6, 4, 6);
        gc.anchor = GridBagConstraints.WEST;

        gc.gridx = 0;
        gc.gridy = 0;
        panel.add(label("Mã xe:"), gc);
        gc.gridx = 1;
        panel.add(txtMaXe, gc);

        gc.gridx = 2;
        gc.weightx = 0.2;
        panel.add(Box.createHorizontalStrut(40), gc);
        gc.weightx = 0;

        gc.gridx = 3;
        panel.add(label("Giá tuyến"), gc);
        gc.gridx = 4;
        panel.add(txtGiaTuyen, gc);

        gc.gridx = 5;
        gc.weightx = 1;
        panel.add(Box.createHorizontalGlue(), gc);
        gc.weightx = 0;

        gc.gridx = 0;
        gc.gridy = 1;
        panel.add(label("Tên xe:"), gc);
        gc.gridx = 1;
        panel.add(txtTenXe, gc);

        return panel;
    }

    private JPanel buildTablePanel() {
        table.setFont(UiTheme.TABLE_FONT);
        table.setRowHeight(24);
        table.setGridColor(UiTheme.GRID_LINE);
        table.setShowGrid(true);
        table.setSelectionBackground(UiTheme.SELECTION);
        table.setSelectionForeground(Color.BLACK);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setFont(UiTheme.TABLE_HEADER_FONT);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setDefaultRenderer(new HeaderRenderer());
        table.setDefaultRenderer(Object.class, new ZebraRenderer());

        TableColumnModel columns = table.getColumnModel();
        int[] widths = {50, 120, 220, 120, 110, 90, 140};
        for (int i = 0; i < widths.length; i++) {
            columns.getColumn(i).setPreferredWidth(widths[i]);
        }

        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setBackground(UiTheme.FORM_BG);
        panel.add(buildChiTietToolbar(), BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(0, 280));
        scroll.getViewport().setBackground(Color.WHITE);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildChiTietToolbar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        bar.setBackground(UiTheme.FORM_BG);
        bar.add(smallButton("Thêm SP", e -> themChiTiet()));
        bar.add(smallButton("Sửa SP", e -> suaChiTiet()));
        bar.add(smallButton("Xóa SP", e -> xoaChiTiet()));
        return bar;
    }

    private JPanel buildThongKePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UiTheme.FORM_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        left.setBackground(UiTheme.FORM_BG);
        left.add(label("Số lượng sản phẩm:"));
        lblSoLuongSanPham.setFont(UiTheme.LABEL_FONT);
        lblSoLuongSanPham.setForeground(UiTheme.ACCENT_RED);
        left.add(lblSoLuongSanPham);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 2));
        right.setBackground(UiTheme.FORM_BG);
        right.add(label("Tổng thành tiền:"));
        txtTongThanhTien.setFont(UiTheme.FIELD_FONT);
        txtTongThanhTien.setEditable(false);
        txtTongThanhTien.setHorizontalAlignment(SwingConstants.RIGHT);
        right.add(txtTongThanhTien);

        panel.add(left, BorderLayout.WEST);
        panel.add(right, BorderLayout.EAST);
        return panel;
    }

    private JPanel buildButtonBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 10));
        bar.setBackground(UiTheme.FORM_BG);
        bar.setBorder(BorderFactory.createEmptyBorder(0, 10, 6, 10));

        bar.add(navButton(NavIcon.Kind.FIRST, "VeDau", "Về bản ghi đầu tiên", e -> dieuHuong(Huong.DAU)));
        bar.add(navButton(NavIcon.Kind.PREVIOUS, "VeTruoc", "Về trước bản ghi hiện tại",
                e -> dieuHuong(Huong.TRUOC)));

        txtChiSo.setFont(UiTheme.FIELD_FONT);
        txtChiSo.setHorizontalAlignment(SwingConstants.CENTER);
        txtChiSo.setToolTipText("Bản ghi hiện thời / tổng số bản ghi của table Xe");
        txtChiSo.addActionListener(e -> nhayDenChiSo());
        bar.add(txtChiSo);

        bar.add(navButton(NavIcon.Kind.NEXT, "VeSau", "Về sau bản ghi hiện tại", e -> dieuHuong(Huong.SAU)));
        bar.add(navButton(NavIcon.Kind.LAST, "VeCuoi", "Về bản ghi cuối cùng", e -> dieuHuong(Huong.CUOI)));

        bar.add(Box.createHorizontalStrut(24));
        bar.add(actionButton("Thêm Xe", e -> themXe()));
        bar.add(actionButton("Xóa Xe", e -> xoaXe()));
        bar.add(actionButton("Báo cáo", e -> moBaoCao()));
        bar.add(actionButton("Kết thúc", e -> ketThuc()));
        bar.add(actionButton("Ghi File", e -> ghiFile()));
        return bar;
    }

    private JLabel label(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UiTheme.LABEL_FONT);
        label.setForeground(UiTheme.LABEL);
        return label;
    }

    private JButton actionButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(UiTheme.BUTTON_FONT);
        button.setPreferredSize(new Dimension(96, 32));
        button.addActionListener(listener);
        return button;
    }

    private JButton navButton(NavIcon.Kind kind, String name, String tooltip, ActionListener listener) {
        JButton button = new JButton(new NavIcon(kind));
        button.setName(name);
        button.setPreferredSize(new Dimension(56, 32));
        button.setToolTipText(tooltip);
        button.addActionListener(listener);
        return button;
    }

    private JButton smallButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(UiTheme.BUTTON_FONT.deriveFont(12f));
        button.setPreferredSize(new Dimension(88, 26));
        button.addActionListener(listener);
        return button;
    }

    /* --------------------------------- Nạp dữ liệu -------------------------------- */

    private void napDuLieuBanDau() {
        service.napDuLieu();
        hienThiBanGhiHienThoi();
        // Hàng đợi EDT bảo đảm thông báo chỉ bật lên sau khi Cửa sổ đã hiện.
        SwingUtilities.invokeLater(() -> {
            if (!service.getConnectionStatus().isConnected()) {
                Dialogs.warn(this, "Kết nối CSDL không được!\n"
                        + "Chi tiết: " + service.getConnectionStatus().getMessage() + "\n"
                        + "Cửa sổ sẽ hiển thị một dòng dữ liệu ảo thay cho dữ liệu thật.");
            }
            txtMaXe.requestFocusInWindow();
        });
    }

    private void hienThiBanGhiHienThoi() {
        Xe xe = service.getXeHienThoi();
        // Khi table Xe rỗng, các ô buộc phải trống nên tạm ngừng kiểm tra lúc rời control.
        boolean coBanGhi = xe != null;
        for (ValidatedTextField field : new ValidatedTextField[] {txtMaXe, txtTenXe, txtGiaTuyen}) {
            field.setVerifyOnFocusLost(coBanGhi);
        }
        txtMaXe.setText(coBanGhi ? xe.getMaXe() : "");
        txtTenXe.setText(coBanGhi ? xe.getTenXe() : "");
        txtGiaTuyen.setText(coBanGhi ? String.valueOf(xe.getGiaTuyen()) : "");
        txtChiSo.setText(service.getChiSo());
        capNhatKhungChiTiet();
    }

    /** Cập nhật Khung hiển thị dữ liệu chi tiết và hai control Thống kê. */
    private void capNhatKhungChiTiet() {
        tableModel.setRows(service.getChiTietHienThoi());
        ThongKe thongKe = service.getThongKe();
        lblSoLuongSanPham.setText(String.valueOf(thongKe.getSoLuongSanPham()));
        txtTongThanhTien.setText(Formats.money(thongKe.getTongThanhTien()));
        if (tableModel.getRowCount() > 0) {
            table.setRowSelectionInterval(0, 0);
        }
    }

    /* --------------------------------- Điều hướng --------------------------------- */

    private enum Huong { DAU, TRUOC, SAU, CUOI }

    private void dieuHuong(Huong huong) {
        if (service.getTongSoBanGhi() == 0) {
            Dialogs.info(this, "Không có bản ghi Xe nào để di chuyển.");
            return;
        }
        if (!Dialogs.confirm(this, "Bạn có chắc chắn di chuyển bản ghi không?")) {
            return;
        }
        switch (huong) {
            case DAU:
                service.veDau();
                break;
            case TRUOC:
                service.veTruoc();
                break;
            case SAU:
                service.veSau();
                break;
            default:
                service.veCuoi();
                break;
        }
        hienThiBanGhiHienThoi();
    }

    private void nhayDenChiSo() {
        String text = txtChiSo.getText().trim();
        int slash = text.indexOf('/');
        String number = slash < 0 ? text : text.substring(0, slash);
        long viTri = Formats.parseLong(number, -1);
        if (viTri < 1) {
            txtChiSo.setText(service.getChiSo());
            Dialogs.error(this, "Chỉ số bản ghi không hợp lệ!");
            return;
        }
        service.diChuyenDen((int) viTri - 1);
        hienThiBanGhiHienThoi();
    }

    /* ------------------------------------ Xe -------------------------------------- */

    private void themXe() {
        List<ChiTietChuyenXe> dangHienThi = tableModel.getRows();
        if (!Dialogs.confirm(this, String.format(
                "Bạn có chắc chắn thêm Xe \"%s\" cùng %d sản phẩm đang hiển thị không?",
                txtMaXe.getTrimmedText(), dangHienThi.size()))) {
            return;
        }
        try {
            service.kiemTraRangBuocXe(txtMaXe.getText(), txtTenXe.getText(), txtGiaTuyen.getText());
            service.themXe(txtMaXe.getText(), txtTenXe.getText(), txtGiaTuyen.getText());
            for (ChiTietChuyenXe row : dangHienThi) {
                service.themChiTiet(row.getMaSP(), row.getNgayChay(),
                        String.valueOf(row.getSoLuong()), row.getGhiChu());
            }
            hienThiBanGhiHienThoi();
            Dialogs.info(this, "Đã thêm Xe thành công.");
        } catch (BusinessException ex) {
            Dialogs.error(this, ex.getMessage());
            if (QuanLyChuyenXeService.LOI_MA_XE_TON_TAI.equals(ex.getMessage())) {
                txtMaXe.selectAll();
                txtMaXe.requestFocusInWindow();
            }
        } catch (RuntimeException ex) {
            Dialogs.error(this, "Không lưu được dữ liệu: " + ex.getMessage());
        }
    }

    private void xoaXe() {
        Xe xe = service.getXeHienThoi();
        if (xe == null) {
            Dialogs.info(this, "Không có bản ghi Xe nào để xóa.");
            return;
        }
        if (!Dialogs.confirm(this, "Bạn có chắc chắn xóa Xe \"" + xe.getMaXe()
                + "\" cùng toàn bộ sản phẩm đã tải không?")) {
            return;
        }
        try {
            service.xoaXeHienThoi();
            hienThiBanGhiHienThoi();
            Dialogs.info(this, "Đã xóa Xe và chuyển sang bản ghi kế tiếp.");
        } catch (RuntimeException ex) {
            Dialogs.error(this, "Không xóa được: " + ex.getMessage());
        }
    }

    /* ------------------------- Khung hiển thị dữ liệu chi tiết --------------------- */

    private void themChiTiet() {
        if (service.getXeHienThoi() == null) {
            Dialogs.info(this, "Chưa có Xe nào được chọn trên Cửa sổ.");
            return;
        }
        if (!Dialogs.confirm(this, "Bạn có chắc chắn thêm Sản phẩm lên Xe hiện thời không?")) {
            return;
        }
        ChiTietDialog dialog = new ChiTietDialog(this, service, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            capNhatKhungChiTiet();
        }
    }

    private void suaChiTiet() {
        ChiTietChuyenXe row = tableModel.getRow(table.getSelectedRow());
        if (row == null) {
            Dialogs.info(this, "Chưa chọn dòng dữ liệu cần sửa.");
            return;
        }
        if (!Dialogs.confirm(this, "Bạn có chắc chắn sửa Sản phẩm \"" + row.getMaSP() + "\" không?")) {
            return;
        }
        ChiTietDialog dialog = new ChiTietDialog(this, service, row);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            capNhatKhungChiTiet();
        }
    }

    private void xoaChiTiet() {
        ChiTietChuyenXe row = tableModel.getRow(table.getSelectedRow());
        if (row == null) {
            Dialogs.info(this, "Chưa chọn dòng dữ liệu cần xóa.");
            return;
        }
        if (!Dialogs.confirm(this, "Bạn có chắc chắn xóa Sản phẩm \"" + row.getMaSP() + "\" không?")) {
            return;
        }
        try {
            service.xoaChiTiet(row);
            capNhatKhungChiTiet();
        } catch (RuntimeException ex) {
            Dialogs.error(this, "Không xóa được: " + ex.getMessage());
        }
    }

    /* ------------------------------ Báo cáo / Ghi file ----------------------------- */

    private void moBaoCao() {
        if (!Dialogs.confirm(this, "Bạn có chắc chắn mở Cửa sổ Báo cáo không?")) {
            return;
        }
        new BaoCaoDialog(this, service).setVisible(true);
    }

    private void ghiFile() {
        if (tableModel.getRowCount() == 0) {
            Dialogs.info(this, "Khung hiển thị dữ liệu chi tiết đang trống, không có gì để ghi.");
            return;
        }
        if (!Dialogs.confirm(this, "Bạn có chắc chắn ghi dữ liệu chi tiết ra file txt không?")) {
            return;
        }
        Xe xe = service.getXeHienThoi();
        String goiY = String.format("ChiTietChuyenXe_%s_%s.txt",
                xe == null ? "KhongXacDinh" : xe.getMaXe(),
                DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss").format(java.time.LocalDateTime.now()));

        JFileChooser chooser = new JFileChooser(Paths.get("").toAbsolutePath().toFile());
        chooser.setDialogTitle("Chọn nơi lưu file txt");
        chooser.setSelectedFile(new java.io.File(goiY));
        chooser.setFileFilter(new FileNameExtensionFilter("Tập tin văn bản (*.txt)", "txt"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        Path target = chooser.getSelectedFile().toPath();
        if (!target.getFileName().toString().toLowerCase().endsWith(".txt")) {
            target = target.resolveSibling(target.getFileName() + ".txt");
        }
        try {
            TxtExporter.export(target, xe, tableModel.getRows());
            Dialogs.info(this, "Đã ghi file:\n" + target.toAbsolutePath());
        } catch (IOException ex) {
            Dialogs.error(this, "Không ghi được file: " + ex.getMessage());
        }
    }

    private void ketThuc() {
        if (Dialogs.confirm(this, "Bạn có chắc chắn kết thúc chương trình không?")) {
            dispose();
            System.exit(0);
        }
    }

    /* ------------------------------------ Renderer --------------------------------- */

    private static final class HeaderRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setFont(UiTheme.TABLE_HEADER_FONT);
            setBackground(UiTheme.GRID_HEADER);
            setForeground(Color.BLACK);
            setOpaque(true);
            setHorizontalAlignment(SwingConstants.LEFT);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(1, 0, 1, 1, UiTheme.GRID_LINE),
                    BorderFactory.createEmptyBorder(2, 6, 2, 6)));
            return this;
        }
    }

    private static final class ZebraRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(alignmentFor(column));
            if (!isSelected) {
                c.setBackground(row % 2 == 0 ? Color.WHITE : UiTheme.ROW_ALT);
            }
            setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
            return c;
        }

        private int alignmentFor(int column) {
            switch (column) {
                case 0:
                case 4:
                case 5:
                    return SwingConstants.CENTER;
                case 3:
                case 6:
                    return SwingConstants.RIGHT;
                default:
                    return SwingConstants.LEFT;
            }
        }
    }
}
