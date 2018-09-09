package io.github.anoobizzz.youget.stock;

import io.github.anoobizzz.youget.domain.AbstractRequest;
import io.github.anoobizzz.youget.domain.Notifier;
import io.github.anoobizzz.youget.info.StreamDownloadInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StockNotifier implements Notifier {
    private static final Logger LOG = LoggerFactory.getLogger(StockNotifier.class);

    @Override
    public void onQueued(StreamDownloadInfo info) {
        LOG.info("Download {} queued", info);
    }

    @Override
    public void onDownloadStart(StreamDownloadInfo info, long size) {
        LOG.info("Download {} download size: {}", info, size);
    }

    @Override
    public void onDownloadProgressUpdate(StreamDownloadInfo info, long size, long downloaded) {
        LOG.info("Download {} progress: {}%", info, (float) downloaded / size * 100);
    }

    @Override
    public void onCompleted(StreamDownloadInfo info) {
        LOG.info("Download {} completed", info);
    }

    @Override
    public void onCompleted(AbstractRequest request) {
        LOG.info("Request {} completed", request);
    }

    @Override
    public void onError(Throwable e) {
        LOG.error("Error occurred during request execution: {}", e);
    }
}
