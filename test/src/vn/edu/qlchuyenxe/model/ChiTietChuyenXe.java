package vn.edu.qlchuyenxe.model;

import java.time.LocalDate;

/**
 * Dòng dữ liệu của Khung hiển thị chi tiết: Chuyen_Xe kết với San_Pham,
 * kèm cột dẫn xuất Thành tiền = Đơn giá * Số lượng.
 */
public class ChiTietChuyenXe {

    private String maSP;
    private String tenSP;
    private long donGia;
    private LocalDate ngayChay;
    private int soLuong;
    private String ghiChu;

    public ChiTietChuyenXe() {
    }

    public ChiTietChuyenXe(String maSP, String tenSP, long donGia,
                           LocalDate ngayChay, int soLuong, String ghiChu) {
        this.maSP = maSP;
        this.tenSP = tenSP;
        this.donGia = donGia;
        this.ngayChay = ngayChay;
        this.soLuong = soLuong;
        this.ghiChu = ghiChu;
    }

    public long getThanhTien() {
        return donGia * soLuong;
    }

    public String getMaSP() {
        return maSP;
    }

    public void setMaSP(String maSP) {
        this.maSP = maSP;
    }

    public String getTenSP() {
        return tenSP;
    }

    public void setTenSP(String tenSP) {
        this.tenSP = tenSP;
    }

    public long getDonGia() {
        return donGia;
    }

    public void setDonGia(long donGia) {
        this.donGia = donGia;
    }

    public LocalDate getNgayChay() {
        return ngayChay;
    }

    public void setNgayChay(LocalDate ngayChay) {
        this.ngayChay = ngayChay;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }
}
