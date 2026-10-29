package vn.edu.qlchuyenxe.db;

/** Kết quả kiểm tra kết nối Database Server. */
public final class ConnectionStatus {

    private final boolean connected;
    private final String message;

    private ConnectionStatus(boolean connected, String message) {
        this.connected = connected;
        this.message = message;
    }

    public static ConnectionStatus connected(String message) {
        return new ConnectionStatus(true, message);
    }

    public static ConnectionStatus failed(String message) {
        return new ConnectionStatus(false, message);
    }

    public boolean isConnected() {
        return connected;
    }

    public String getMessage() {
        return message;
    }
}
