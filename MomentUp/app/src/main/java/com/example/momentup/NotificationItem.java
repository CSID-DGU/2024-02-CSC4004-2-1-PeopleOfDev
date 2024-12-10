package com.example.momentup;

public class NotificationItem {
    private String title;
    private String message;
    private String time;
    private int iconResId;

    public NotificationItem(String title, String message, String time, int iconResId) {
        this.title = title;
        this.message = message;
        this.time = time;
        this.iconResId = iconResId;
    }

    // Getters
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getTime() { return time; }
    public int getIconResId() { return iconResId; }
}