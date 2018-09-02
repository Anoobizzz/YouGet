package com.github.anoobizzz.youget.source.youtube;

import com.github.anoobizzz.youget.info.VideoInfo;
import com.github.anoobizzz.youget.domain.stream.StreamInfo;

import java.net.URI;

public class YouTubeInfo extends VideoInfo {
    private URI playerURI;

    public URI getPlayerURI() {
        return playerURI;
    }

    public void setPlayerURI(URI playerURI) {
        this.playerURI = playerURI;
    }
}