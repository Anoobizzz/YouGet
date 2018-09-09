package io.github.anoobizzz.youget.info;

import java.io.File;
import java.net.URL;

public class StreamDownloadInfo {
    private File targetFile;
    private URL source;
    private String extension;

    public StreamDownloadInfo(URL source, String extension) {
        this.source = source;
        this.extension = extension;
    }

    public void setTargetFile(File file) {
        targetFile = file;
    }

    public File getTargetFile() {
        return targetFile;
    }

    public URL getSource() {
        return source;
    }

    public void setSource(URL source) {
        this.source = source;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
}
