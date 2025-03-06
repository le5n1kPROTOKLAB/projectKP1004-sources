package com.example.caracapp;

public class CanIdReceivedEvent {
    private String id;

    public CanIdReceivedEvent(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
