package vn.edu.qlchuyenxe.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Tham số kết nối CSDL, nạp từ db.properties (nếu có), ngược lại dùng mặc định.
 */
public final class DatabaseConfig {

    private static final String FILE_NAME = "db.properties";

    private final String url;
    private final String user;
    private final String password;
    private final int loginTimeoutSeconds;

    private DatabaseConfig(Properties props) {
        String host = props.getProperty("db.host", "localhost");
        String port = props.getProperty("db.port", "1433");
        String name = props.getProperty("db.name", "QLChuyenXe");
        this.url = props.getProperty("db.url", String.format(
                "jdbc:sqlserver://%s:%s;databaseName=%s;encrypt=true;trustServerCertificate=true;loginTimeout=%s",
                host, port, name, props.getProperty("db.loginTimeout", "3")));
        this.user = props.getProperty("db.user", "sa");
        this.password = props.getProperty("db.password", "12345");
        this.loginTimeoutSeconds = Integer.parseInt(props.getProperty("db.loginTimeout", "3"));
    }

    public static DatabaseConfig from(Properties props) {
        return new DatabaseConfig(props);
    }

    public static DatabaseConfig load() {
        Properties props = new Properties();
        Path external = Paths.get(FILE_NAME);
        if (Files.isReadable(external)) {
            try (InputStream in = Files.newInputStream(external)) {
                props.load(in);
            } catch (IOException ignored) {
                // Thiếu file cấu hình không phải lỗi chặn: dùng giá trị mặc định.
            }
        } else {
            try (InputStream in = DatabaseConfig.class.getResourceAsStream("/" + FILE_NAME)) {
                if (in != null) {
                    props.load(in);
                }
            } catch (IOException ignored) {
                // Như trên.
            }
        }
        return new DatabaseConfig(props);
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public int getLoginTimeoutSeconds() {
        return loginTimeoutSeconds;
    }
}
