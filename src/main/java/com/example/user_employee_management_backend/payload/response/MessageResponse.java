// src/main/java/com/example/user_employee_management_backend/payload/response/MessageResponse.java (NEW)
package com.example.user_employee_management_backend.payload.response;

public class MessageResponse {
    private String message;

    public MessageResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
