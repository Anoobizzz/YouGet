package io.github.anoobizzz.youget.domain;

import io.github.anoobizzz.youget.info.StreamDownloadInfo;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Future;

public class DownloadHandler {
    private static Client client;

    public static void setClient(Client client) {
        DownloadHandler.client = client;
    }

    public static String getHtml(URL url) throws IOException {
        return client.getHtml(url);
    }

    public static void download(StreamDownloadInfo downloadInfo, Notifier notifier) {
        client.download(downloadInfo, notifier);
    }

    public static Future downloadAsync(StreamDownloadInfo downloadInfo, Notifier notifier) {
        return client.downloadAsync(downloadInfo, notifier);
    }
}