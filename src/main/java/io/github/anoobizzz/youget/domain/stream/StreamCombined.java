package io.github.anoobizzz.youget.domain.stream;

public class StreamCombined extends StreamInfo {
    private Quality.VideoEncoding videoEncoding;
    private Quality.VideoQuality videoQuality;
    private Quality.AudioEncoding audioEncoding;
    private Quality.AudioQuality audioQuality;

    StreamCombined(Quality.Container container, Quality.VideoEncoding videoEncoding, Quality.VideoQuality videoQuality,
                   Quality.AudioEncoding audioEncoding, Quality.AudioQuality audioQuality) {
        super(container);
        this.videoEncoding = videoEncoding;
        this.videoQuality = videoQuality;
        this.audioEncoding = audioEncoding;
        this.audioQuality = audioQuality;
    }

    @Override
    public int ordinal() {
        return videoQuality.ordinal();
    }
}