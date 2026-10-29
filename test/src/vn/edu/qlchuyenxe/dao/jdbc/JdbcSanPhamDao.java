package vn.edu.qlchuyenxe.dao.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import vn.edu.qlchuyenxe.dao.SanPhamDao;
import vn.edu.qlchuyenxe.db.ConnectionManager;
import vn.edu.qlchuyenxe.model.SanPham;

public class JdbcSanPhamDao extends JdbcSupport implements SanPhamDao {

    private static final String SELECT = "SELECT MaSP, TenSP, DonGia FROM dbo.San_Pham";

    public JdbcSanPhamDao(ConnectionManager connectionManager) {
        super(connectionManager);
    }

    @Override
    public List<SanPham> findAll() {
        return query(SELECT + " ORDER BY MaSP", JdbcSanPhamDao::map);
    }

    @Override
    public SanPham findById(String maSP) {
        List<SanPham> rows = query(SELECT + " WHERE MaSP = ?", JdbcSanPhamDao::map, maSP);
        return rows.isEmpty() ? null : rows.get(0);
    }

    private static SanPham map(ResultSet rs) throws SQLException {
        return new SanPham(rs.getString("MaSP"), rs.getString("TenSP"), rs.getLong("DonGia"));
    }
}
