package vn.edu.qlchuyenxe.service;

/** Lỗi nghiệp vụ có thông điệp hiển thị thẳng cho người dùng. */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
