package vn.edu.qlchuyenxe.dao.memory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import vn.edu.qlchuyenxe.dao.XeDao;
import vn.edu.qlchuyenxe.model.Xe;

public class InMemoryXeDao implements XeDao {

    private final InMemoryDatabase db;

    public InMemoryXeDao(InMemoryDatabase db) {
        this.db = db;
    }

    @Override
    public List<Xe> findAll() {
        List<Xe> copy = new ArrayList<>(db.xeList());
        copy.sort(Comparator.comparing(Xe::getMaXe));
        return copy;
    }

    @Override
    public boolean exists(String maXe) {
        return db.xeList().stream().anyMatch(xe -> xe.getMaXe().equalsIgnoreCase(maXe));
    }

    @Override
    public void insert(Xe xe) {
        db.xeList().add(xe);
    }

    @Override
    public void update(Xe xe) {
        for (int i = 0; i < db.xeList().size(); i++) {
            if (db.xeList().get(i).getMaXe().equalsIgnoreCase(xe.getMaXe())) {
                db.xeList().set(i, xe);
                return;
            }
        }
    }

    @Override
    public void delete(String maXe) {
        db.xeList().removeIf(xe -> xe.getMaXe().equalsIgnoreCase(maXe));
    }
}
