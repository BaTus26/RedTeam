package vn.edu.qlchuyenxe.dao.memory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import vn.edu.qlchuyenxe.model.ChuyenXe;
import vn.edu.qlchuyenxe.model.SanPham;
import vn.edu.qlchuyenxe.model.Xe;

/**
 * Kho dữ liệu nằm trong bộ nhớ. Dùng khi không kết nối được Database Server:
 * đề bài yêu cầu sinh một dòng dữ liệu ảo để Cửa sổ vẫn hiển thị được.
 */
public class InMemoryDatabase {

    private final List<Xe> xeList = new ArrayList<>();
    private final List<SanPham> sanPhamList = new ArrayList<>();
    private final List<ChuyenXe> chuyenXeList = new ArrayList<>();

    public static InMemoryDatabase empty() {
        return new InMemoryDatabase();
    }

    /** Một dòng dữ liệu ảo đủ để nạp lên mọi control của Cửa sổ. */
    public static InMemoryDatabase withDummyRow() {
        InMemoryDatabase db = new InMemoryDatabase();
        db.sanPhamList.add(new SanPham("SP000", "(Dữ liệu ảo)", 0));
        db.xeList.add(new Xe("XE000", "(Dữ liệu ảo)", 0));
        db.chuyenXeList.add(new ChuyenXe("SP000", "XE000", LocalDate.now(), 1, "Không kết nối được CSDL"));
        return db;
    }

    public List<Xe> xeList() {
        return xeList;
    }

    public List<SanPham> sanPhamList() {
        return sanPhamList;
    }

    public List<ChuyenXe> chuyenXeList() {
        return chuyenXeList;
    }
}
