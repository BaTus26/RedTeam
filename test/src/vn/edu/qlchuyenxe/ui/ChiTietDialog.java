package vn.edu.qlchuyenxe.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import vn.edu.qlchuyenxe.model.ChiTietChuyenXe;
import vn.edu.qlchuyenxe.model.SanPham;
import vn.edu.qlchuyenxe.service.BusinessException;
import vn.edu.qlchuyenxe.service.QuanLyChuyenXeService;
import vn.edu.qlchuyenxe.ui.component.BannerPanel;
import vn.edu.qlchuyenxe.ui.component.Dialogs;
import vn.edu.qlchuyenxe.ui.component.ValidatedTextField;
import vn.edu.qlchuyenxe.util.Formats;
import vn.edu.qlchuyenxe.util.Validator;

/** Cửa sổ Thêm / Sửa một Sản phẩm đã tải lên Xe đang chọn. */
public class ChiTietDialog extends JDialog {

    private final QuanLyChuyenXeService service;
    private final ChiTietChuyenXe goc;

    private final JComboBox<SanPham> cboSanPham = new JComboBox<>();
    private final JTextField txtTenSP = new JTextField(18);
    private final JTextField txtDonGia = new JTextField(18);
    private final ValidatedTextField txtNgayChay = ValidatedTextField.pattern(
            18, "[0-9/]*", 10, Validator::isDate, "Ngày chạy phải có dạng dd/MM/yyyy!");
    private final ValidatedTextField txtSoLuong = ValidatedTextField.digits(
            18, 9, Validator::isPositiveNumber, "Số lượng phải là số nguyên dương!");
    private final JTextField txtGhiChu = new JTextField(18);

    private boolean saved;

    public ChiTietDialog(java.awt.Frame owner, QuanLyChuyenXeService service, ChiTietChuyenXe goc) {
        super(owner, goc == null ? "Thêm sản phẩm lên xe" : "Sửa sản phẩm đã tải", true);
        this.service = service;
        this.goc = goc;
        buildUi();
        napDuLieu();
        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    public boolean isSaved() {
        return saved;
    }

    private void buildUi() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(UiTheme.FORM_BG);
        content.add(new BannerPanel(goc == null ? "THÊM SẢN PHẨM LÊN XE" : "SỬA SẢN PHẨM ĐÃ TẢI"),
                BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UiTheme.FORM_BG);
        form.setBorder(BorderFactory.createEmptyBorder(14, 20, 10, 20));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 6, 5, 6);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;

        addRow(form, gc, 0, "Mã sản phẩm:", cboSanPham);
        addRow(form, gc, 1, "Tên sản phẩm:", txtTenSP);
        addRow(form, gc, 2, "Đơn giá:", txtDonGia);
        addRow(form, gc, 3, "Ngày chạy:", txtNgayChay);
        addRow(form, gc, 4, "Số lượng:", txtSoLuong);
        addRow(form, gc, 5, "Ghi chú:", txtGhiChu);

        txtTenSP.setEditable(false);
        txtDonGia.setEditable(false);
        txtDonGia.setHorizontalAlignment(SwingConstants.RIGHT);
        txtTenSP.setFont(UiTheme.FIELD_FONT);
        txtDonGia.setFont(UiTheme.FIELD_FONT);
        txtGhiChu.setFont(UiTheme.FIELD_FONT);
        cboSanPham.setFont(UiTheme.FIELD_FONT);
        cboSanPham.addActionListener(e -> hienThiSanPhamDangChon());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttons.setBackground(UiTheme.FORM_BG);
        buttons.add(button("Lưu", e -> luu()));
        buttons.add(button("Hủy", e -> huy()));

        content.add(form, BorderLayout.CENTER);
        content.add(buttons, BorderLayout.SOUTH);
        setContentPane(content);
    }

    private void addRow(JPanel form, GridBagConstraints gc, int row, String caption, java.awt.Component field) {
        gc.gridx = 0;
        gc.gridy = row;
        gc.weightx = 0;
        JLabel label = new JLabel(caption);
        label.setFont(UiTheme.LABEL_FONT);
        label.setForeground(UiTheme.LABEL);
        form.add(label, gc);
        gc.gridx = 1;
        gc.weightx = 1;
        form.add(field, gc);
    }

    private JButton button(String text, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(UiTheme.BUTTON_FONT);
        button.setPreferredSize(new Dimension(96, 32));
        button.addActionListener(listener);
        return button;
    }

    private void napDuLieu() {
        for (SanPham sp : service.getDanhSachSanPham()) {
            cboSanPham.addItem(sp);
        }
        if (goc != null) {
            selectSanPham(goc.getMaSP());
            txtNgayChay.setText(Formats.date(goc.getNgayChay()));
            txtSoLuong.setText(String.valueOf(goc.getSoLuong()));
            txtGhiChu.setText(goc.getGhiChu() == null ? "" : goc.getGhiChu());
        } else {
            txtNgayChay.setText(Formats.date(LocalDate.now()));
            txtSoLuong.setText("1");
        }
        hienThiSanPhamDangChon();
    }

    private void selectSanPham(String maSP) {
        for (int i = 0; i < cboSanPham.getItemCount(); i++) {
            if (cboSanPham.getItemAt(i).getMaSP().equalsIgnoreCase(maSP)) {
                cboSanPham.setSelectedIndex(i);
                return;
            }
        }
    }

    private void hienThiSanPhamDangChon() {
        SanPham sp = (SanPham) cboSanPham.getSelectedItem();
        txtTenSP.setText(sp == null ? "" : sp.getTenSP());
        txtDonGia.setText(sp == null ? "" : Formats.money(sp.getDonGia()));
    }

    private void luu() {
        SanPham sp = (SanPham) cboSanPham.getSelectedItem();
        if (sp == null) {
            Dialogs.error(this, "Chưa chọn Mã sản phẩm!");
            return;
        }
        if (!Dialogs.confirm(this, "Bạn có chắc chắn lưu dữ liệu không?")) {
            return;
        }
        try {
            LocalDate ngay = Formats.parseDate(txtNgayChay.getText());
            if (goc == null) {
                service.themChiTiet(sp.getMaSP(), ngay, txtSoLuong.getText(), ghiChu());
            } else {
                service.suaChiTiet(goc, sp.getMaSP(), ngay, txtSoLuong.getText(), ghiChu());
            }
            saved = true;
            dispose();
        } catch (BusinessException ex) {
            Dialogs.error(this, ex.getMessage());
        } catch (RuntimeException ex) {
            Dialogs.error(this, "Không lưu được dữ liệu: " + ex.getMessage());
        }
    }

    private String ghiChu() {
        String text = txtGhiChu.getText();
        return text == null || text.trim().isEmpty() ? null : text.trim();
    }

    private void huy() {
        if (Dialogs.confirm(this, "Bạn có chắc chắn hủy thao tác không?")) {
            dispose();
        }
    }
}
