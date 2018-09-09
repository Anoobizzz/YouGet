package io.github.anoobizzz.youget;

import io.github.anoobizzz.youget.domain.AbstractRequest;
import io.github.anoobizzz.youget.domain.Client;
import io.github.anoobizzz.youget.domain.DownloadHandler;
import io.github.anoobizzz.youget.domain.Notifier;
import io.github.anoobizzz.youget.exception.InitializationException;
import io.github.anoobizzz.youget.info.ParserResolver;
import io.github.anoobizzz.youget.info.StreamDownloadInfo;
import io.github.anoobizzz.youget.stock.StockClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;

public class YouGet {
    private static final Logger LOG = LoggerFactory.getLogger(YouGet.class);
    private static final File DEFAULT_DIR = new File(System.getProperty("user.dir") + "/downloads");
    private static final Map<AbstractRequest, Future> GLOBAL_QUEUE = new Hashtable<>();
    private final ForkJoinPool threadPool;
    private final File targetDir;

    public YouGet() throws InitializationException {
        this(4, DEFAULT_DIR, new StockClient(4));
    }

    public YouGet(int parallelism) throws InitializationException {
        this(parallelism, DEFAULT_DIR, new StockClient(parallelism));
    }

    public YouGet(File targetDir) throws InitializationException {
        this(4, targetDir, new StockClient(4));
    }

    public YouGet(int parallelism, File targetDir) throws InitializationException {
        this(parallelism, targetDir, new StockClient(parallelism));
    }

    public YouGet(int parallelism, File targetDir, Client client) throws InitializationException {
        DownloadHandler.setClient(client);
        this.targetDir = targetDir;
        if (!this.targetDir.exists() && !this.targetDir.mkdir()) {
            throw new InitializationException("Failed to create instance of YouGet downloader: directory " +
                    this.targetDir.getAbsolutePath() + " doesn't exist and can't be created!");
        }
        this.threadPool = new ForkJoinPool(parallelism);
    }

    public void download(AbstractRequest request) {
        download(request, (downloadInfoList, notifier) -> {
            for (StreamDownloadInfo streamDownloadInfo : downloadInfoList) {
                DownloadHandler.download(streamDownloadInfo, notifier);
            }
        });
    }

    public void downloadAsync(AbstractRequest request) {
        GLOBAL_QUEUE.put(request, threadPool.submit(() -> download(request, (downloadInfoList, notifier) -> {
            List<Future> tasks = new LinkedList<>();
            for (StreamDownloadInfo streamDownloadInfo : downloadInfoList) {
                LOG.debug("Queueing download... URL: {}", streamDownloadInfo.getSource());
                tasks.add(DownloadHandler.downloadAsync(streamDownloadInfo, notifier));
            }

            for (Future task : tasks) {
                try {
                    task.get();
                } catch (InterruptedException | ExecutionException e) {
                    notifier.onError(e);
                    LOG.error("Failed download with error: {}", e.getMessage());
                }
            }
        })));
    }

    private void download(AbstractRequest request, BiConsumer<List<StreamDownloadInfo>, Notifier> downloader) {
        Notifier notifier = request.getNotifier();
        try {
            LOG.debug("Extracting encoding information...");
            final List<StreamDownloadInfo> streamDownloadInfoList =
                    ParserResolver.resolveParser(request.getLink()).extractVideoInfo(request);

            LOG.debug("Resolving download destination\\file name...");
            streamDownloadInfoList.stream().filter(downloadInfo -> downloadInfo.getTargetFile() == null)
                    .forEach(streamDownloadInfo -> resolveTargetFile(streamDownloadInfo, request.getInfo().getTitle()));

            LOG.debug("Processing download...");
            downloader.accept(streamDownloadInfoList, notifier);
            notifier.onCompleted(request);
            LOG.debug("Finished request...");
        } catch (Exception e) {
            notifier.onError(e);
            LOG.error("Failed download with error: {}", e.getMessage());
        }
    }

    public void cancelDownload(AbstractRequest request) {
        GLOBAL_QUEUE.get(request).cancel(true);
    }

    public void awaitCompletion(AbstractRequest request) throws ExecutionException, InterruptedException {
        GLOBAL_QUEUE.get(request).get();
    }

    private void resolveTargetFile(final StreamDownloadInfo downloadInfo, final String title) {
        String extension = downloadInfo.getExtension();
        downloadInfo.setTargetFile(new File(targetDir,
                resolveFileName(replaceInvalidChars(parseFileNameLength(title)), extension)));
    }

    private String resolveFileName(final String title, final String extension) {
        String fileName = title + extension;
        int i = 1;
        while (checkFilesInDirectory(fileName)) {
            fileName = title + "(" + i + ")" + extension;
            i++;
        }
        return fileName;
    }

    private boolean checkFilesInDirectory(final String fileName) {
        String[] filesInDirectory = targetDir.list();
        return filesInDirectory != null && Arrays.asList(filesInDirectory).contains(fileName);
    }

    private static String replaceInvalidChars(final String input) {
        return input.replaceAll("[^\\w\\d\\s\\-()_]", "");
    }

    private static String parseFileNameLength(final String fileName) {
        return fileName.length() > 252 ? fileName.substring(0, 252) : fileName;
    }
}