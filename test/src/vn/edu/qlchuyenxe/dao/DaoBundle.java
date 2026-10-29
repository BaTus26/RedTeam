package vn.edu.qlchuyenxe.dao;

/** Gom ba DAO để tầng dịch vụ hoán đổi giữa nguồn JDBC và nguồn dữ liệu ảo. */
public final class DaoBundle {

    private final XeDao xeDao;
    private final SanPhamDao sanPhamDao;
    private final ChuyenXeDao chuyenXeDao;

    public DaoBundle(XeDao xeDao, SanPhamDao sanPhamDao, ChuyenXeDao chuyenXeDao) {
        this.xeDao = xeDao;
        this.sanPhamDao = sanPhamDao;
        this.chuyenXeDao = chuyenXeDao;
    }

    public XeDao xe() {
        return xeDao;
    }

    public SanPhamDao sanPham() {
        return sanPhamDao;
    }

    public ChuyenXeDao chuyenXe() {
        return chuyenXeDao;
    }
}
