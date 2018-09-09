package io.github.anoobizzz.youget.source.youtube;

import io.github.anoobizzz.youget.info.VideoInfo;

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