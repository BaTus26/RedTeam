package vn.edu.qlchuyenxe.dao;

import java.util.List;

import vn.edu.qlchuyenxe.model.SanPham;

public interface SanPhamDao {

    List<SanPham> findAll();

    SanPham findById(String maSP);
}
