package vn.edu.qlchuyenxe.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import vn.edu.qlchuyenxe.model.ChiTietChuyenXe;
import vn.edu.qlchuyenxe.model.Xe;
import vn.edu.qlchuyenxe.service.QuanLyChuyenXeService;
import vn.edu.qlchuyenxe.ui.component.BannerPanel;
import vn.edu.qlchuyenxe.ui.component.Dialogs;
import vn.edu.qlchuyenxe.util.Formats;

/** Cửa sổ nhập tiêu chí lọc báo cáo: Mã xe và/hoặc Ngày chạy. */
public class BaoCaoDialog extends JDialog {

    private static final String TAT_CA = "(Tất cả)";

    private final QuanLyChuyenXeService service;
    private final JComboBox<String> cboMaXe = new JComboBox<>();
    private final JTextField txtNgayChay = new JTextField(16);

    public BaoCaoDialog(java.awt.Frame owner, QuanLyChuyenXeService service) {
        super(owner, "Báo cáo thống kê sản phẩm theo xe", true);
        this.service = service;
        buildUi();
        napTieuChi();
        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    private void buildUi() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(UiTheme.FORM_BG);
        content.add(new BannerPanel("TIÊU CHÍ LỌC BÁO CÁO"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UiTheme.FORM_BG);
        form.setBorder(BorderFactory.createEmptyBorder(16, 24, 12, 24));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;

        gc.gridx = 0;
        gc.gridy = 0;
        form.add(label("Mã xe:"), gc);
        gc.gridx = 1;
        gc.weightx = 1;
        cboMaXe.setFont(UiTheme.FIELD_FONT);
        form.add(cboMaXe, gc);

        gc.gridx = 0;
        gc.gridy = 1;
        gc.weightx = 0;
        form.add(label("Ngày chạy:"), gc);
        gc.gridx = 1;
        gc.weightx = 1;
        txtNgayChay.setFont(UiTheme.FIELD_FONT);
        txtNgayChay.setToolTipText("Định dạng dd/MM/yyyy, để trống nghĩa là không lọc theo ngày");
        form.add(txtNgayChay, gc);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttons.setBackground(UiTheme.FORM_BG);
        JButton btnThucHien = new JButton("Thực hiện báo cáo");
        btnThucHien.setFont(UiTheme.BUTTON_FONT);
        btnThucHien.setPreferredSize(new Dimension(176, 32));
        btnThucHien.addActionListener(e -> thucHienBaoCao());
        buttons.add(btnThucHien);

        JButton btnDong = new JButton("Đóng");
        btnDong.setFont(UiTheme.BUTTON_FONT);
        btnDong.setPreferredSize(new Dimension(96, 32));
        btnDong.addActionListener(e -> dong());
        buttons.add(btnDong);

        content.add(form, BorderLayout.CENTER);
        content.add(buttons, BorderLayout.SOUTH);
        setContentPane(content);
    }

    private JLabel label(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UiTheme.LABEL_FONT);
        label.setForeground(UiTheme.LABEL);
        return label;
    }

    private void napTieuChi() {
        cboMaXe.addItem(TAT_CA);
        for (Xe xe : service.getDanhSachXe()) {
            cboMaXe.addItem(xe.getMaXe());
        }
        Xe hienThoi = service.getXeHienThoi();
        if (hienThoi != null) {
            cboMaXe.setSelectedItem(hienThoi.getMaXe());
        }
    }

    private void thucHienBaoCao() {
        String ngayText = txtNgayChay.getText() == null ? "" : txtNgayChay.getText().trim();
        LocalDate ngayChay = null;
        if (!ngayText.isEmpty()) {
            ngayChay = Formats.parseDate(ngayText);
            if (ngayChay == null) {
                Dialogs.error(this, "Ngày chạy phải có dạng dd/MM/yyyy!");
                txtNgayChay.requestFocusInWindow();
                return;
            }
        }
        if (!Dialogs.confirm(this, "Bạn có chắc chắn phát sinh báo cáo không?")) {
            return;
        }
        Object selected = cboMaXe.getSelectedItem();
        String maXe = TAT_CA.equals(selected) ? null : String.valueOf(selected);

        List<ChiTietChuyenXe> rows = service.locBaoCao(maXe, ngayChay);
        if (rows.isEmpty()) {
            Dialogs.info(this, "Không có dữ liệu phù hợp với tiêu chí đã chọn.");
            return;
        }
        new BaoCaoFrame(maXe == null ? null : service.timXe(maXe), rows).setVisible(true);
    }

    private void dong() {
        if (Dialogs.confirm(this, "Bạn có chắc chắn đóng Cửa sổ Báo cáo không?")) {
            dispose();
        }
    }
}
