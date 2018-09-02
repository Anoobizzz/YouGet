package com.github.anoobizzz.youget.info;

import com.github.anoobizzz.youget.source.youtube.YouTubeParser;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class ParserResolver {
    private static final List<YouGetParser> parsers = new LinkedList<YouGetParser>() {
        {
            add(new YouTubeParser());
        }
    };

    public static YouGetParser resolveParser(URL url) {
        for (YouGetParser parser : parsers) {
            if (parser.probe(url)) {
                return parser;
            }
        }

        throw new RuntimeException("Unsupported web site");
    }
}
