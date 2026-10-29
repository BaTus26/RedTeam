package vn.edu.qlchuyenxe.dao.jdbc;

import java.util.List;

import vn.edu.qlchuyenxe.dao.XeDao;
import vn.edu.qlchuyenxe.db.ConnectionManager;
import vn.edu.qlchuyenxe.model.Xe;

public class JdbcXeDao extends JdbcSupport implements XeDao {

    public JdbcXeDao(ConnectionManager connectionManager) {
        super(connectionManager);
    }

    @Override
    public List<Xe> findAll() {
        return query("SELECT MaXe, TenXe, GiaTuyen FROM dbo.Xe ORDER BY MaXe",
                rs -> new Xe(rs.getString("MaXe"), rs.getString("TenXe"), rs.getLong("GiaTuyen")));
    }

    @Override
    public boolean exists(String maXe) {
        return existsBy("SELECT 1 FROM dbo.Xe WHERE MaXe = ?", maXe);
    }

    @Override
    public void insert(Xe xe) {
        execute("INSERT INTO dbo.Xe (MaXe, TenXe, GiaTuyen) VALUES (?, ?, ?)",
                xe.getMaXe(), xe.getTenXe(), xe.getGiaTuyen());
    }

    @Override
    public void update(Xe xe) {
        execute("UPDATE dbo.Xe SET TenXe = ?, GiaTuyen = ? WHERE MaXe = ?",
                xe.getTenXe(), xe.getGiaTuyen(), xe.getMaXe());
    }

    @Override
    public void delete(String maXe) {
        execute("DELETE FROM dbo.Xe WHERE MaXe = ?", maXe);
    }
}
