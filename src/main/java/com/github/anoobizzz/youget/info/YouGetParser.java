package com.github.anoobizzz.youget.info;

import com.github.anoobizzz.youget.domain.AbstractRequest;

import java.net.URL;
import java.util.List;

public abstract class YouGetParser {
    public abstract boolean probe(URL url);

    public abstract List<StreamDownloadInfo> extractVideoInfo(AbstractRequest videoInfo);
}
