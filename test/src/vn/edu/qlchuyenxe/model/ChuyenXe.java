package vn.edu.qlchuyenxe.model;

import java.time.LocalDate;
import java.util.Objects;

public class ChuyenXe {

    private String maSP;
    private String maXe;
    private LocalDate ngayGio;
    private int soLuong;
    private String ghiChu;

    public ChuyenXe() {
    }

    public ChuyenXe(String maSP, String maXe, LocalDate ngayGio, int soLuong, String ghiChu) {
        this.maSP = maSP;
        this.maXe = maXe;
        this.ngayGio = ngayGio;
        this.soLuong = soLuong;
        this.ghiChu = ghiChu;
    }

    public String getMaSP() {
        return maSP;
    }

    public void setMaSP(String maSP) {
        this.maSP = maSP;
    }

    public String getMaXe() {
        return maXe;
    }

    public void setMaXe(String maXe) {
        this.maXe = maXe;
    }

    public LocalDate getNgayGio() {
        return ngayGio;
    }

    public void setNgayGio(LocalDate ngayGio) {
        this.ngayGio = ngayGio;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChuyenXe)) {
            return false;
        }
        ChuyenXe other = (ChuyenXe) o;
        return Objects.equals(maSP, other.maSP)
                && Objects.equals(maXe, other.maXe)
                && Objects.equals(ngayGio, other.ngayGio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maSP, maXe, ngayGio);
    }
}
