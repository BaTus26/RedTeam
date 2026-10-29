package vn.edu.qlchuyenxe.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import vn.edu.qlchuyenxe.dao.DataAccessException;
import vn.edu.qlchuyenxe.db.ConnectionManager;

/** Khung thực thi JDBC dùng chung: mở, đóng và chuyển SQLException sang lỗi nghiệp vụ. */
abstract class JdbcSupport {

    private final ConnectionManager connectionManager;

    JdbcSupport(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    interface RowMapper<T> {
        T map(ResultSet rs) throws SQLException;
    }

    <T> List<T> query(String sql, RowMapper<T> mapper, Object... params) {
        try (Connection connection = connectionManager.open();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bind(statement, params);
            try (ResultSet rs = statement.executeQuery()) {
                List<T> rows = new ArrayList<>();
                while (rs.next()) {
                    rows.add(mapper.map(rs));
                }
                return rows;
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Lỗi truy vấn CSDL: " + ex.getMessage(), ex);
        }
    }

    boolean existsBy(String sql, Object... params) {
        return !query(sql, rs -> Boolean.TRUE, params).isEmpty();
    }

    int execute(String sql, Object... params) {
        try (Connection connection = connectionManager.open();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bind(statement, params);
            return statement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("Lỗi cập nhật CSDL: " + ex.getMessage(), ex);
        }
    }

    private void bind(PreparedStatement statement, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            statement.setObject(i + 1, params[i]);
        }
    }
}
