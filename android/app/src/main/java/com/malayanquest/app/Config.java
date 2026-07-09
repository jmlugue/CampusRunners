package com.malayanquest.app;

public final class Config {
    private Config() {
    }

    // Android emulator -> local XAMPP Apache.
    // For a real phone, replace 10.0.2.2 with your computer IPv4 address.
    public static final String API_BASE_URL = "http://10.0.2.2:8080/IT140P-MP-MalayanQuest/backend/api/";
}
