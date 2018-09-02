package com.github.anoobizzz.youget.domain;

import java.util.Comparator;

public class ContentComparator implements Comparator<VideoContent> {
    private static final ContentComparator instance = new ContentComparator();

    public static ContentComparator getInstance() {
        return instance;
    }

    @Override
    public int compare(VideoContent content1, VideoContent content2) {
        Integer i1 = content1.getStream().ordinal();
        Integer i2 = content2.getStream().ordinal();
        return i1.compareTo(i2);
    }
}
