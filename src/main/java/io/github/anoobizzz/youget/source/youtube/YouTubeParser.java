package io.github.anoobizzz.youget.source.youtube;

import io.github.anoobizzz.youget.domain.AbstractRequest;
import io.github.anoobizzz.youget.domain.ContentComparator;
import io.github.anoobizzz.youget.domain.Notifier;
import io.github.anoobizzz.youget.domain.VideoContent;
import io.github.anoobizzz.youget.domain.stream.StreamAudio;
import io.github.anoobizzz.youget.domain.stream.StreamCombined;
import io.github.anoobizzz.youget.domain.stream.StreamInfo;
import io.github.anoobizzz.youget.exception.AgeRestrictionException;
import io.github.anoobizzz.youget.exception.DownloadException;
import io.github.anoobizzz.youget.exception.EmbeddingDisabledException;
import io.github.anoobizzz.youget.exception.EmptyTitleException;
import io.github.anoobizzz.youget.exception.UnavailablePlayerException;
import io.github.anoobizzz.youget.exception.VideoDeletedException;
import io.github.anoobizzz.youget.info.StreamDownloadInfo;
import io.github.anoobizzz.youget.info.YouGetParser;
import io.github.anoobizzz.youget.domain.DownloadHandler;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import static io.github.anoobizzz.youget.domain.stream.Quality.getQualityByITag;
import static java.net.URLDecoder.decode;
import static java.util.Collections.singletonList;
import static java.util.regex.Pattern.compile;
import static org.apache.commons.lang3.CharEncoding.UTF_8;
import static org.apache.commons.lang3.StringEscapeUtils.unescapeHtml4;

public class YouTubeParser extends YouGetParser {
    private static final Logger LOG = LoggerFactory.getLogger(YouTubeParser.class);

    @Override
    public List<StreamDownloadInfo> extractVideoInfo(AbstractRequest request) {
        List<VideoContent> videos = extractContent(request.getLink(),
                request.setInfo(new YouTubeInfo()), request.getNotifier());

        if (videos.isEmpty()) {
            throw new DownloadException("Empty content list, nothing found");
        }

        List<VideoContent> audios = new LinkedList<>();

        LOG.debug("Filtering VideoContent streams...");
        for (Iterator<VideoContent> iterator = videos.iterator(); iterator.hasNext(); ) {
            VideoContent video = iterator.next();
            StreamInfo streamInfo = video.getStream();
            if (streamInfo == null || streamInfo instanceof StreamAudio) {
                iterator.remove();
            }
            if (streamInfo instanceof StreamAudio) {
                audios.add(video);
            }
        }

        videos.sort(ContentComparator.getInstance());
        audios.sort(ContentComparator.getInstance());

        List<StreamDownloadInfo> streamDownloadInfoList = new LinkedList<>();
        LOG.debug("Retrieving best encoding\\audio streams...");
        if (request.downloadVideo() && !videos.isEmpty()) {
            VideoContent videoContent = videos.get(0);
            StreamDownloadInfo videoStreamInfo = new StreamDownloadInfo(videoContent.getUrl(),
                    videoContent.getStream().getContainer().getExtension());
            if (videoContent.getStream() instanceof StreamCombined) {
                return singletonList(videoStreamInfo);
            }
            streamDownloadInfoList.add(videoStreamInfo);
        }

        if (request.downloadAudio() && !audios.isEmpty()) {
            VideoContent audio = audios.get(0);
            streamDownloadInfoList.add(new StreamDownloadInfo(audio.getUrl(),
                    audio.getStream().getContainer().getExtension()));
        }

        if (streamDownloadInfoList.isEmpty()) {
            throw new DownloadException("No request matching content to download");
        }
        return streamDownloadInfoList;
    }

    private List<VideoContent> extractContent(URL link, YouTubeInfo info, Notifier notifier) {
        try {
            LOG.debug("Attempting to extract from HTML info");
            return extractHtmlInfo(info, DownloadHandler.getHtml(link));
        } catch (Exception htmlExtraction) {
            notifier.onError(htmlExtraction);
            try {
                LOG.debug("Attempting to extract from embedded");
                return extractEmbedded(info, link);
            } catch (Exception embeddedExtraction) {
                notifier.onError(embeddedExtraction);
                throw new DownloadException("Failed to extract content from both Html and Embedded");
            }
        }
    }

    private static String extractId(URL url) {
        Matcher matcher = compile("youtube.com/watch?.*v=([^&]*)").matcher(url.toString());
        if (matcher.find()) {
            return matcher.group(1);
        }

        matcher = compile("youtube.com/v/([^&]*)").matcher(url.toString());
        if (matcher.find()) {
            return matcher.group(1);
        }

        throw new RuntimeException("unknown url");
    }

    private List<VideoContent> extractEmbedded(YouTubeInfo info, URL link)
            throws IOException {
        String id = extractId(link);

        info.setTitle(String.format("https://www.youtube.com/watch?v=%s", id));

        Map<String, String> map = getQueryMap(
                DownloadHandler.getHtml(new URL(String.format("https://www.youtube.com/get_video_info?authuser=0&video_id=%s&el=embedded", id))));

        if (map.get("status").equals("fail")) {
            if (map.get("errorcode").equals("150")) {
                throw new EmbeddingDisabledException();
            }
            if (map.get("errorcode").equals("100")) {
                throw new VideoDeletedException();
            }
            throw new DownloadException(decode(map.get("reason"), UTF_8));
        }

        info.setTitle(decode(map.get("title"), UTF_8));

        return extractUrlEncodedVideos(info, decode(map.get("url_encoded_fmt_stream_map"), UTF_8));
    }

    private static Map<String, String> getQueryMap(String qs) {
        try {
            HashMap<String, String> map = new HashMap<>();
            qs = qs.trim();
            String[] pairs = qs.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                map.put(decode(pair.substring(0, idx), UTF_8),
                        decode(pair.substring(idx + 1), UTF_8));
            }
            return map;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(qs, e);
        }
    }

    private List<VideoContent> extractHtmlInfo(YouTubeInfo info, String html) throws Exception {
        List<VideoContent> contentList = new ArrayList<>();

        if (compile("(verify_age)").matcher(html).find()) {
            throw new AgeRestrictionException();
        }

        if (compile("(unavailable-player)").matcher(html).find()) {
            throw new UnavailablePlayerException();
        }

        Matcher playerVersionMatch = compile("<script[\\s]*src=\"([^\"]*?player[-_].*?base\\.js)\"")
                .matcher(html);
        if (playerVersionMatch.find()) {
            info.setPlayerURI(new URI("https://www.youtube.com" + playerVersionMatch.group(1)));
        }

        Matcher urlEncodedMatch = compile("\"url_encoded_fmt_stream_map\":\"([^\"]*)\"")
                .matcher(html);
        if (urlEncodedMatch.find()) {
            contentList.addAll(extractUrlEncodedVideos(info, urlEncodedMatch.group(1)));
        }

        Matcher adaptiveFmtsMatcher = compile("\"adaptive_fmts\":\\s*\"([^\"]*)\"")
                .matcher(html);
        if (adaptiveFmtsMatcher.find()) {
            contentList.addAll(extractUrlEncodedVideos(info, adaptiveFmtsMatcher.group(1)));
        }

        Matcher titleMatch = compile("<meta name=\"title\" content=\"(.*?)\"").matcher(html);
        if (titleMatch.find()) {
            info.setTitle(unescapeHtml4(titleMatch.group(1)));
        }

        if (info.getTitle() == null) {
            throw new EmptyTitleException("Empty title");
        }

        return contentList;
    }

    private List<VideoContent> extractUrlEncodedVideos(YouTubeInfo info, String line) throws IOException {
        List<VideoContent> contentList = new ArrayList<>();

        for (String urlString : line.split(",")) {
            urlString = StringEscapeUtils.unescapeJava(urlString);

            String urlFull = decode(urlString, UTF_8);

            String url = null;
            Matcher linkMatch = compile("url=(.*?)(&|\\z)").matcher(urlString);
            if (linkMatch.find()) {
                url = decode(linkMatch.group(1), UTF_8);
            }

            String iTag = null;
            Matcher iTagMatch = compile("itag=(\\d+)").matcher(urlFull);
            if (iTagMatch.find()) {
                iTag = iTagMatch.group(1);
            }

            String signature = null;
            Matcher signatureMatch = compile("&signature=([^&,]*)").matcher(urlFull);
            if (signatureMatch.find()) {
                signature = signatureMatch.group(1);
            } else {
                signatureMatch = compile("sig=([^&,]*)").matcher(urlFull);
                if (signatureMatch.find()) {
                    signature = signatureMatch.group(1);
                } else {
                    signatureMatch = compile("[&,]s=([^&,]*)").matcher(urlFull);
                    if (signatureMatch.find()) {
                        signature = new SignatureDecrypter(signatureMatch.group(1), info.getPlayerURI()).decrypt();
                    }
                }
            }

            if (url != null && iTag != null && signature != null) {
                try {
                    url += "&signature=" + signature;
                    contentList.add(new VideoContent(getQualityByITag(iTag), new URL(url)));
                } catch (MalformedURLException e) {
                    LOG.info("Failed to from a URL: {}", url);
                }
            }
        }

        return contentList;
    }

    public boolean probe(URL url) {
        //TODO: Add youtu.be support
        return url.toString().contains("youtube.com");
    }
}