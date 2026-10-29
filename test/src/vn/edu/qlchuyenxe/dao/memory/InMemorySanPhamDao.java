package vn.edu.qlchuyenxe.dao.memory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import vn.edu.qlchuyenxe.dao.SanPhamDao;
import vn.edu.qlchuyenxe.model.SanPham;

public class InMemorySanPhamDao implements SanPhamDao {

    private final InMemoryDatabase db;

    public InMemorySanPhamDao(InMemoryDatabase db) {
        this.db = db;
    }

    @Override
    public List<SanPham> findAll() {
        List<SanPham> copy = new ArrayList<>(db.sanPhamList());
        copy.sort(Comparator.comparing(SanPham::getMaSP));
        return copy;
    }

    @Override
    public SanPham findById(String maSP) {
        return db.sanPhamList().stream()
                .filter(sp -> sp.getMaSP().equalsIgnoreCase(maSP))
                .findFirst()
                .orElse(null);
    }
}
