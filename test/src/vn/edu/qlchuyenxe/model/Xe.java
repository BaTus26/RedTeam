package vn.edu.qlchuyenxe.model;

import java.util.Objects;

public class Xe {

    private String maXe;
    private String tenXe;
    private long giaTuyen;

    public Xe() {
    }

    public Xe(String maXe, String tenXe, long giaTuyen) {
        this.maXe = maXe;
        this.tenXe = tenXe;
        this.giaTuyen = giaTuyen;
    }

    public String getMaXe() {
        return maXe;
    }

    public void setMaXe(String maXe) {
        this.maXe = maXe;
    }

    public String getTenXe() {
        return tenXe;
    }

    public void setTenXe(String tenXe) {
        this.tenXe = tenXe;
    }

    public long getGiaTuyen() {
        return giaTuyen;
    }

    public void setGiaTuyen(long giaTuyen) {
        this.giaTuyen = giaTuyen;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Xe)) {
            return false;
        }
        return Objects.equals(maXe, ((Xe) o).maXe);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(maXe);
    }

    @Override
    public String toString() {
        return maXe;
    }
}
