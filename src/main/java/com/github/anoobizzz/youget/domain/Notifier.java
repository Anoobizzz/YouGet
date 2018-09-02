package com.github.anoobizzz.youget.domain;

import com.github.anoobizzz.youget.info.StreamDownloadInfo;

public interface Notifier {
    void onQueued(StreamDownloadInfo info);

    void onDownloadStart(StreamDownloadInfo info, long size);

    void onDownloadProgressUpdate(StreamDownloadInfo info, long size, long downloaded);

    void onCompleted(StreamDownloadInfo info);

    void onCompleted(AbstractRequest request);

    void onError(Throwable e);
}
