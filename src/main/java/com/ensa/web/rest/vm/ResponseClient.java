package com.ensa.web.rest.vm;

public class ResponseClient {
    private String message;
    private String status;
    private String extras;

    public ResponseClient(String message, String status, String extras) {
        this.message = message;
        this.status = status;
        this.extras = extras;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getExtras() {
        return extras;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }
}
