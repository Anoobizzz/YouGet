package com.github.anoobizzz.youget.domain.stream;

import java.util.HashMap;
import java.util.Map;

public class Quality {
    public enum VideoQuality {
        p3072, p2304, p2160, p1440, p1080, p720, p520, p480, p360, p270, p240, p224, p144
    }

    public enum Container {
        FLV(".flv"), GP3(".gp3"), MP4(".mp4"), WEBM(".webm");
        private String extension;

        Container(String extension) {
            this.extension = extension;
        }

        public String getExtension() {
            return extension;
        }
    }

    public enum VideoEncoding {
        H263, H264, VP8, VP9, MP4
    }

    public enum AudioEncoding {
        AAC, VORBIS, OPUS, MP3
    }

    public enum AudioQuality {
        k256, k192, k160, k128, k96, k70, k64, k50, k48, k36, k24
    }

    private static final Map<Integer, StreamInfo> ITAG_MAP = new HashMap<Integer, StreamInfo>() {
        {
            put(120, new StreamCombined(Container.FLV, VideoEncoding.H264, VideoQuality.p720, AudioEncoding.AAC,
                    AudioQuality.k128));
            put(102, new StreamCombined(Container.WEBM, VideoEncoding.VP8, VideoQuality.p720, AudioEncoding.VORBIS,
                    AudioQuality.k192));
            put(101, new StreamCombined(Container.WEBM, VideoEncoding.VP8, VideoQuality.p360, AudioEncoding.VORBIS,
                    AudioQuality.k192)); // webm
            put(100, new StreamCombined(Container.WEBM, VideoEncoding.VP8, VideoQuality.p360, AudioEncoding.VORBIS,
                    AudioQuality.k128)); // webm
            put(85, new StreamCombined(Container.MP4, VideoEncoding.H264, VideoQuality.p1080, AudioEncoding.AAC,
                    AudioQuality.k192)); // mp4
            put(84, new StreamCombined(Container.MP4, VideoEncoding.H264, VideoQuality.p720, AudioEncoding.AAC,
                    AudioQuality.k192)); // mp4
            put(83, new StreamCombined(Container.MP4, VideoEncoding.H264, VideoQuality.p240, AudioEncoding.AAC,
                    AudioQuality.k96)); // mp4
            put(82, new StreamCombined(Container.MP4, VideoEncoding.H264, VideoQuality.p360, AudioEncoding.AAC,
                    AudioQuality.k96)); // mp4
            put(46, new StreamCombined(Container.WEBM, VideoEncoding.VP8, VideoQuality.p1080, AudioEncoding.VORBIS,
                    AudioQuality.k192)); // webm
            put(45, new StreamCombined(Container.WEBM, VideoEncoding.VP8, VideoQuality.p720, AudioEncoding.VORBIS,
                    AudioQuality.k192)); // webm
            put(44, new StreamCombined(Container.WEBM, VideoEncoding.VP8, VideoQuality.p480, AudioEncoding.VORBIS,
                    AudioQuality.k128)); // webm
            put(43, new StreamCombined(Container.WEBM, VideoEncoding.VP8, VideoQuality.p360, AudioEncoding.VORBIS,
                    AudioQuality.k128)); // webm
            put(38, new StreamCombined(Container.MP4, VideoEncoding.H264, VideoQuality.p3072, AudioEncoding.AAC,
                    AudioQuality.k192)); // mp4
            put(37, new StreamCombined(Container.MP4, VideoEncoding.H264, VideoQuality.p1080, AudioEncoding.AAC,
                    AudioQuality.k192)); // mp4
            put(36, new StreamCombined(Container.GP3, VideoEncoding.MP4, VideoQuality.p240, AudioEncoding.AAC,
                    AudioQuality.k36)); // 3gp
            put(35, new StreamCombined(Container.FLV, VideoEncoding.H264, VideoQuality.p480, AudioEncoding.AAC,
                    AudioQuality.k128)); // flv
            put(34, new StreamCombined(Container.FLV, VideoEncoding.H264, VideoQuality.p360, AudioEncoding.AAC,
                    AudioQuality.k128)); // flv
            put(22, new StreamCombined(Container.MP4, VideoEncoding.H264, VideoQuality.p720, AudioEncoding.AAC,
                    AudioQuality.k192)); // mp4
            put(18, new StreamCombined(Container.MP4, VideoEncoding.H264, VideoQuality.p360, AudioEncoding.AAC,
                    AudioQuality.k96)); // mp4
            put(17, new StreamCombined(Container.GP3, VideoEncoding.MP4, VideoQuality.p144, AudioEncoding.AAC,
                    AudioQuality.k24)); // 3gp
            put(6, new StreamCombined(Container.FLV, VideoEncoding.H263, VideoQuality.p270, AudioEncoding.MP3,
                    AudioQuality.k64)); // flv
            put(5, new StreamCombined(Container.FLV, VideoEncoding.H263, VideoQuality.p240, AudioEncoding.MP3,
                    AudioQuality.k64)); // flv

            put(133, new StreamVideo(Container.MP4, VideoEncoding.H264, VideoQuality.p240));
            put(134, new StreamVideo(Container.MP4, VideoEncoding.H264, VideoQuality.p360));
            put(135, new StreamVideo(Container.MP4, VideoEncoding.H264, VideoQuality.p480));
            put(136, new StreamVideo(Container.MP4, VideoEncoding.H264, VideoQuality.p720));
            put(137, new StreamVideo(Container.MP4, VideoEncoding.H264, VideoQuality.p1080));
            put(138, new StreamVideo(Container.MP4, VideoEncoding.H264, VideoQuality.p2160));
            put(160, new StreamVideo(Container.MP4, VideoEncoding.H264, VideoQuality.p144));
            put(242, new StreamVideo(Container.WEBM, VideoEncoding.VP9, VideoQuality.p240));
            put(243, new StreamVideo(Container.WEBM, VideoEncoding.VP9, VideoQuality.p360));
            put(244, new StreamVideo(Container.WEBM, VideoEncoding.VP9, VideoQuality.p480));
            put(247, new StreamVideo(Container.WEBM, VideoEncoding.VP9, VideoQuality.p720));
            put(248, new StreamVideo(Container.WEBM, VideoEncoding.VP9, VideoQuality.p1080));
            put(264, new StreamVideo(Container.MP4, VideoEncoding.H264, VideoQuality.p1440));
            put(271, new StreamVideo(Container.WEBM, VideoEncoding.VP9, VideoQuality.p1440));
            put(272, new StreamVideo(Container.WEBM, VideoEncoding.VP9, VideoQuality.p2160));
            put(278, new StreamVideo(Container.WEBM, VideoEncoding.VP9, VideoQuality.p144));
            put(298, new StreamVideo(Container.MP4, VideoEncoding.H264, VideoQuality.p720));
            put(299, new StreamVideo(Container.MP4, VideoEncoding.H264, VideoQuality.p1080));
            put(302, new StreamVideo(Container.WEBM, VideoEncoding.VP9, VideoQuality.p720));
            put(303, new StreamVideo(Container.WEBM, VideoEncoding.VP9, VideoQuality.p1080));

            put(139, new StreamAudio(Container.MP4, AudioEncoding.AAC, AudioQuality.k48));
            put(140, new StreamAudio(Container.MP4, AudioEncoding.AAC, AudioQuality.k128));
            put(141, new StreamAudio(Container.MP4, AudioEncoding.AAC, AudioQuality.k256));
            put(171, new StreamAudio(Container.WEBM, AudioEncoding.VORBIS, AudioQuality.k128));
            put(172, new StreamAudio(Container.WEBM, AudioEncoding.VORBIS, AudioQuality.k192));

            put(249, new StreamAudio(Container.WEBM, AudioEncoding.OPUS, AudioQuality.k50));
            put(250, new StreamAudio(Container.WEBM, AudioEncoding.OPUS, AudioQuality.k70));
            put(251, new StreamAudio(Container.WEBM, AudioEncoding.OPUS, AudioQuality.k160));
        }
    };

    public static StreamInfo getQualityByITag(String iTag) {
        return ITAG_MAP.get(Integer.parseInt(iTag));
    }
}