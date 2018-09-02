package com.github.anoobizzz.youget.domain.stream;

public class StreamAudio extends StreamInfo {
    private Quality.AudioEncoding videoEncoding;
    private Quality.AudioQuality audioQuality;

    StreamAudio(Quality.Container container, Quality.AudioEncoding videoEncoding, Quality.AudioQuality audioQuality) {
        super(container);
        this.videoEncoding = videoEncoding;
        this.audioQuality = audioQuality;
    }

    @Override
    public int ordinal() {
        return audioQuality.ordinal();
    }
}