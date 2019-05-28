package com.example.hyunju.notification_collector.models;

public class ChangeGlobalStateEvent {
    private boolean isMultiMode = false;

    public ChangeGlobalStateEvent(boolean isMultiMode) {
        this.isMultiMode = isMultiMode;
    }

    public boolean isMultiMode() {
        return isMultiMode;
    }

    public void setMultiMode(boolean multiMode) {
        isMultiMode = multiMode;
    }
}
