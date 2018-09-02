package com.github.anoobizzz.youget.domain;

import com.github.anoobizzz.youget.info.StreamDownloadInfo;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Future;

public interface Client {
    String getHtml(URL url) throws IOException;

    void download(StreamDownloadInfo downloadInfo, Notifier notifier);

    Future downloadAsync(StreamDownloadInfo downloadInfo, Notifier notifier);
}