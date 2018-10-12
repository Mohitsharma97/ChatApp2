package com.example.mohitsharma.chatapp;

public class Conv {
    private boolean seen;
    private long timestamp;

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Conv(boolean seen, long timestamp) {

        this.seen = seen;
        this.timestamp = timestamp;
    }

    public Conv(){}

}
