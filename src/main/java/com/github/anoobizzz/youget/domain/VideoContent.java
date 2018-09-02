package com.github.anoobizzz.youget.domain;

import com.github.anoobizzz.youget.domain.stream.StreamInfo;

import java.net.URL;

public class VideoContent {
    private StreamInfo stream;
    private URL url;

    public VideoContent(){
    }

    public VideoContent(StreamInfo stream, URL url) {
        this.stream = stream;
        this.url = url;
    }

    public StreamInfo getStream() {
        return stream;
    }

    public void setStream(StreamInfo stream) {
        this.stream = stream;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }
}
