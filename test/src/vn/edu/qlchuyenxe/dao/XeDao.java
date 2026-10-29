package vn.edu.qlchuyenxe.dao;

import java.util.List;

import vn.edu.qlchuyenxe.model.Xe;

public interface XeDao {

    List<Xe> findAll();

    boolean exists(String maXe);

    void insert(Xe xe);

    void update(Xe xe);

    void delete(String maXe);
}
