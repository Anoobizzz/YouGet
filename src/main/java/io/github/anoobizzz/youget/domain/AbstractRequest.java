package io.github.anoobizzz.youget.domain;

import io.github.anoobizzz.youget.info.VideoInfo;

import java.net.URL;

public abstract class AbstractRequest {
    private URL link;
    private boolean downloadAudio;
    private boolean downloadVideo;
    private VideoInfo info;
    private Notifier notifier;

    public AbstractRequest(URL link, boolean downloadAudio, boolean downloadVideo, Notifier notifier) {
        this.link = link;
        this.downloadAudio = downloadAudio;
        this.downloadVideo = downloadVideo;
        this.notifier = notifier;
    }

    public Notifier getNotifier() {
        return notifier;
    }

    public URL getLink() {
        return link;
    }

    public boolean downloadAudio() {
        return downloadAudio;
    }

    public boolean downloadVideo() {
        return downloadVideo;
    }

    public VideoInfo getInfo() {
        return info;
    }

    public <T extends VideoInfo> T setInfo(T info) {
        this.info = info;
        return info;
    }
}