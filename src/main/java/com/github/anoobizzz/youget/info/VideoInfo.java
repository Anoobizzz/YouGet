package com.github.anoobizzz.youget.info;


import com.github.anoobizzz.youget.domain.stream.StreamInfo;

public abstract class VideoInfo {
    private StreamInfo streamInfo;

    public StreamInfo getStreamInfo() {
        return streamInfo;
    }

    public void setStreamInfo(StreamInfo streamInfo) {
        this.streamInfo = streamInfo;
    }

    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}