package vn.edu.qlchuyenxe.dao.memory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import vn.edu.qlchuyenxe.dao.ChuyenXeDao;
import vn.edu.qlchuyenxe.model.ChiTietChuyenXe;
import vn.edu.qlchuyenxe.model.ChuyenXe;
import vn.edu.qlchuyenxe.model.SanPham;

public class InMemoryChuyenXeDao implements ChuyenXeDao {

    private final InMemoryDatabase db;

    public InMemoryChuyenXeDao(InMemoryDatabase db) {
        this.db = db;
    }

    @Override
    public List<ChiTietChuyenXe> findByXe(String maXe) {
        return search(maXe, null);
    }

    @Override
    public List<ChiTietChuyenXe> search(String maXe, LocalDate ngayChay) {
        List<ChiTietChuyenXe> rows = new ArrayList<>();
        for (ChuyenXe cx : db.chuyenXeList()) {
            if (maXe != null && !maXe.trim().isEmpty() && !cx.getMaXe().equalsIgnoreCase(maXe.trim())) {
                continue;
            }
            if (ngayChay != null && !ngayChay.equals(cx.getNgayGio())) {
                continue;
            }
            rows.add(toDetail(cx));
        }
        rows.sort(Comparator.comparing(ChiTietChuyenXe::getNgayChay)
                .thenComparing(ChiTietChuyenXe::getMaSP));
        return rows;
    }

    @Override
    public boolean exists(String maSP, String maXe, LocalDate ngayGio) {
        return indexOf(maSP, maXe, ngayGio) >= 0;
    }

    @Override
    public void insert(ChuyenXe chuyenXe) {
        db.chuyenXeList().add(chuyenXe);
    }

    @Override
    public void update(ChuyenXe key, ChuyenXe value) {
        int index = indexOf(key.getMaSP(), key.getMaXe(), key.getNgayGio());
        if (index >= 0) {
            db.chuyenXeList().set(index, value);
        }
    }

    @Override
    public void delete(String maSP, String maXe, LocalDate ngayGio) {
        int index = indexOf(maSP, maXe, ngayGio);
        if (index >= 0) {
            db.chuyenXeList().remove(index);
        }
    }

    @Override
    public void deleteByXe(String maXe) {
        db.chuyenXeList().removeIf(cx -> cx.getMaXe().equalsIgnoreCase(maXe));
    }

    private int indexOf(String maSP, String maXe, LocalDate ngayGio) {
        for (int i = 0; i < db.chuyenXeList().size(); i++) {
            ChuyenXe cx = db.chuyenXeList().get(i);
            if (cx.getMaSP().equalsIgnoreCase(maSP)
                    && cx.getMaXe().equalsIgnoreCase(maXe)
                    && cx.getNgayGio().equals(ngayGio)) {
                return i;
            }
        }
        return -1;
    }

    private ChiTietChuyenXe toDetail(ChuyenXe cx) {
        SanPham sp = db.sanPhamList().stream()
                .filter(p -> p.getMaSP().equalsIgnoreCase(cx.getMaSP()))
                .findFirst()
                .orElse(new SanPham(cx.getMaSP(), "", 0));
        return new ChiTietChuyenXe(sp.getMaSP(), sp.getTenSP(), sp.getDonGia(),
                cx.getNgayGio(), cx.getSoLuong(), cx.getGhiChu());
    }
}
