package vn.edu.qlchuyenxe.dao;

import java.time.LocalDate;
import java.util.List;

import vn.edu.qlchuyenxe.model.ChiTietChuyenXe;
import vn.edu.qlchuyenxe.model.ChuyenXe;

public interface ChuyenXeDao {

    /** Danh sách sản phẩm đã tải trên một Xe, kèm Thành tiền. */
    List<ChiTietChuyenXe> findByXe(String maXe);

    /** Lọc báo cáo: mã xe và/hoặc ngày chạy, tham số null nghĩa là không lọc. */
    List<ChiTietChuyenXe> search(String maXe, LocalDate ngayChay);

    boolean exists(String maSP, String maXe, LocalDate ngayGio);

    void insert(ChuyenXe chuyenXe);

    /** Khóa chính gồm 3 cột nên bản ghi cũ được xác định bằng {@code key}. */
    void update(ChuyenXe key, ChuyenXe value);

    void delete(String maSP, String maXe, LocalDate ngayGio);

    void deleteByXe(String maXe);
}
