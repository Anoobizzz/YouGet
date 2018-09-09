package io.github.anoobizzz.youget.domain.stream;

public abstract class StreamInfo {
    private Quality.Container container;

    public StreamInfo(Quality.Container container) {
        this.container = container;
    }

    public Quality.Container getContainer() {
        return container;
    }

    public String toString() {
        return container.toString();
    }

    public abstract int ordinal();
}