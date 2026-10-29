package vn.edu.qlchuyenxe.service;

import java.util.List;

import vn.edu.qlchuyenxe.model.ChiTietChuyenXe;

/** Giá trị của hai control Thống kê: số lượng sản phẩm và tổng thành tiền. */
public final class ThongKe {

    private final int soLuongSanPham;
    private final long tongThanhTien;

    public ThongKe(int soLuongSanPham, long tongThanhTien) {
        this.soLuongSanPham = soLuongSanPham;
        this.tongThanhTien = tongThanhTien;
    }

    public static ThongKe of(List<ChiTietChuyenXe> rows) {
        long tong = 0;
        for (ChiTietChuyenXe row : rows) {
            tong += row.getThanhTien();
        }
        return new ThongKe(rows.size(), tong);
    }

    public int getSoLuongSanPham() {
        return soLuongSanPham;
    }

    public long getTongThanhTien() {
        return tongThanhTien;
    }
}
