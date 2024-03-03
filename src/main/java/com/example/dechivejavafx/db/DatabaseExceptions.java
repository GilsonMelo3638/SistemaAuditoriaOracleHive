package com.example.dechivejavafx.db;

public class DatabaseExceptions {

    public static class DbException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public DbException(String msg) {
            super(msg);
        }
    }

    public static void handleException(String message, Exception e) {
        System.err.println(message + ": " + e.getMessage());
        e.printStackTrace();
        System.exit(1);
    }
}
