package com.github.anoobizzz.youget.domain.stream;

public class StreamVideo extends StreamInfo {
    private Quality.VideoEncoding encoding;
    private Quality.VideoQuality videoQuality;

    public StreamVideo(Quality.Container container, Quality.VideoEncoding videoEncoding, Quality.VideoQuality videoQuality) {
        super(container);
        this.videoQuality = videoQuality;
        this.encoding = videoEncoding;
    }

    @Override
    public int ordinal() {
        return videoQuality.ordinal();
    }
}