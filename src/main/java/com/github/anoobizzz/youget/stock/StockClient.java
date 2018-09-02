package com.github.anoobizzz.youget.stock;

import com.github.anoobizzz.youget.domain.Client;
import com.github.anoobizzz.youget.domain.Notifier;
import com.github.anoobizzz.youget.info.StreamDownloadInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

public class StockClient implements Client {
    private static final Logger LOG = LoggerFactory.getLogger(Client.class);
    private final ForkJoinPool threadPool;

    public StockClient(int parallelism) {
        this.threadPool = new ForkJoinPool(parallelism);
    }

    @Override
    public String getHtml(URL url) throws IOException {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                builder.append(line);
            }
        }
        return builder.toString();
    }

    @Override
    public void download(final StreamDownloadInfo downloadInfo, final Notifier notifier) {
        URL url = downloadInfo.getSource();
        File targetFile = downloadInfo.getTargetFile();
        try (FileOutputStream out = new FileOutputStream(targetFile)) {
            URLConnection connection = url.openConnection();
            long downloadSize = connection.getContentLengthLong();
            notifier.onDownloadStart(downloadInfo, downloadSize);
            ReadableByteChannel channel = Channels.newChannel(connection.getInputStream());
            long step = downloadSize / 10;
            long position = 0;
            while (position <= downloadSize) {
                notifier.onDownloadProgressUpdate(downloadInfo, downloadSize, position);
                out.getChannel().transferFrom(channel, position, step);
                position += step;
            }
            notifier.onCompleted(downloadInfo);
        } catch (Exception e) {
            LOG.debug("Download failed with error: {}", e.getMessage());
            if (targetFile.delete()) {
                LOG.debug("Deleting failed download file: {}", targetFile.getName());
            }
            notifier.onError(e);
        }
    }

    @Override
    public Future downloadAsync(StreamDownloadInfo downloadInfo, Notifier notifier) {
        notifier.onQueued(downloadInfo);
        return threadPool.submit(() -> download(downloadInfo, notifier));
    }
}
