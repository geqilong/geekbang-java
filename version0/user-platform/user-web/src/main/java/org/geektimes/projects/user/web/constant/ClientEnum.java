package org.geektimes.projects.user.web.constant;

public enum ClientEnum {
    CLIENT_ID("0d627daf8a040d288da49eb9cff916417bd8d5fcf240835088d941d9244d667d"),
    CLIENT_SECRET("69fbd934ac3121821f8d12fd8f226b5efd8dc51b7902be5d2aa6a11fd7ffdc6f"),
    REDIRECT_URL("http://localhost:8080/hello/world");
    private String value;
    ClientEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
