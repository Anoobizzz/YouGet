package io.github.anoobizzz.youget.stock;

import io.github.anoobizzz.youget.domain.AbstractRequest;

import java.net.URL;

public class StockRequest extends AbstractRequest {
    public StockRequest(URL link, boolean downloadAudio, boolean downloadVideo) {
        super(link, downloadAudio, downloadVideo, new StockNotifier());
    }
}
