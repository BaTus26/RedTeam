package vn.edu.qlchuyenxe.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import vn.edu.qlchuyenxe.config.DatabaseConfig;

/**
 * Mở kết nối tới SQL Server và luôn cho phép kiểm tra trước việc kết nối
 * có thành công hay không (yêu cầu bắt buộc của đề bài).
 */
public class ConnectionManager {

    private static final String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    private final DatabaseConfig config;

    public ConnectionManager(DatabaseConfig config) {
        this.config = config;
    }

    public ConnectionManager() {
        this(DatabaseConfig.load());
    }

    public Connection open() throws SQLException {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException ex) {
            throw new SQLException("Không tìm thấy driver JDBC của SQL Server (" + DRIVER + ").", ex);
        }
        DriverManager.setLoginTimeout(config.getLoginTimeoutSeconds());
        return DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
    }

    /** Thử kết nối một lần; không ném ngoại lệ để Cửa sổ có thể chuyển sang dữ liệu ảo. */
    public ConnectionStatus check() {
        try (Connection connection = open()) {
            if (connection.isValid(config.getLoginTimeoutSeconds())) {
                return ConnectionStatus.connected("Đã kết nối CSDL QLChuyenXe.");
            }
            return ConnectionStatus.failed("Kết nối CSDL không hợp lệ.");
        } catch (SQLException ex) {
            return ConnectionStatus.failed(ex.getMessage());
        }
    }
}
