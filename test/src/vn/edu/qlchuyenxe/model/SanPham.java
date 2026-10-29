package vn.edu.qlchuyenxe.model;

import java.util.Objects;

public class SanPham {

    private String maSP;
    private String tenSP;
    private long donGia;

    public SanPham() {
    }

    public SanPham(String maSP, String tenSP, long donGia) {
        this.maSP = maSP;
        this.tenSP = tenSP;
        this.donGia = donGia;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SanPham)) {
            return false;
        }
        return Objects.equals(maSP, ((SanPham) o).maSP);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(maSP);
    }

    @Override
    public String toString() {
        return maSP + " - " + tenSP;
    }
}
