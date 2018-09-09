package io.github.anoobizzz.youget.source.youtube;

import io.github.anoobizzz.youget.domain.DownloadHandler;
import io.github.anoobizzz.youget.exception.DownloadException;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

class SignatureDecrypter {
    private static final ConcurrentMap<String, String> playerCache = new ConcurrentHashMap<>();
    private String sig;
    private URI playerURI;

    SignatureDecrypter(String signature, URI playerURI) {
        this.sig = signature;
        this.playerURI = playerURI;
    }

    String decrypt() throws IOException {
        return playerURI == null ? decryptNoPlayerURI() : decryptHtml5();
    }

    // https://github.com/rg3/youtube-dl/blob/master/youtube_dl/extractor/youtube.py
    private String decryptNoPlayerURI() {
        switch (sig.length()) {
            case 93:
                return d(86, 29) + b(88) + d(28, 5);
            case 92:
                return b(25) + a(3, 25) + b(0) + a(26, 42) +
                        b(79) + a(43, 79) + b(91) + a(80, 83);
            case 91:
                return d(84, 27) + b(86) + d(26, 5);
            case 90:
                return b(25) + a(3, 25) + b(2) + a(26, 40) +
                        b(77) + a(41, 77) + b(89) + a(78, 81);
            case 89:
                return d(84, 78) + b(87) + d(77, 60) + b(0) +
                        d(59, 3);
            case 88:
                return a(7, 28) + b(87) + a(29, 45) + b(55) +
                        a(46, 55) + b(2) + a(56, 87) + b(28);
            case 87:
                return a(6, 27) + b(4) + a(28, 39) + b(27) +
                        a(40, 59) + b(2) + c();
            case 86:
                return d(80, 72) + b(16) + d(71, 39) + b(72) +
                        d(38, 16) + b(82) + d(15, 0);
            case 85:
                return a(3, 11) + b(0) + a(12, 55) + b(84) +
                        a(56, 84);
            case 84:
                return d(78, 70) + b(14) + d(69, 37) + b(70) +
                        d(36, 14) + b(80) + d(0, 14);
            case 83:
                return d(80, 63) + b(0) + d(62, 0) + b(63);
            case 82:
                return d(80, 37) + b(7) + d(36, 7) + b(0) +
                        d(6, 0) + b(37);
            case 81:
                return b(56) + d(79, 56) + b(41) + d(55, 41) +
                        b(80) + d(40, 34) + b(0) + d(33, 29) +
                        b(34) + d(28, 9) + b(29) + d(8, 0) + b(9);
            case 80:
                return a(1, 19) + b(0) + a(20, 68) + b(19) +
                        a(69, 80);
            case 79:
                return b(54) + d(77, 54) + b(39) + d(53, 39) +
                        b(78) + d(38, 34) + b(0) + d(33, 29) +
                        b(34) + d(28, 9) + b(29) + d(8, 0) + b(9);
        }

        throw new RuntimeException("Unable to decrypt signature, key length " + sig.length() + " not supported; retrying might work");
    }

    private String a(int b, int e) {
        return sig.substring(b, e);
    }

    private String b(int b) {
        return sig.substring(b, ++b);
    }

    private String c() {
        return a(60, sig.length());
    }

    private String d(int b, int e) {
        StringBuilder str = new StringBuilder();

        while (b != e) {
            str.append(sig.charAt(b));
            b--;
        }
        return str.toString();
    }

    private String decryptHtml5() throws IOException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");

        final String playerScript = getPlayerSource();
        final String decodeFuncName = getMainDecodeFunctionName(playerScript);
        final String decodeScript = extractDecodeFunctions(playerScript, decodeFuncName);

        try {
            engine.eval(decodeScript);
            Invocable inv = (Invocable) engine;
            return (String) inv.invokeFunction(decodeFuncName, sig);
        } catch (Exception e) {
            throw new RuntimeException("Unable to decrypt signature!");
        }
    }

    private String getPlayerSource() throws IOException {
        String url = playerCache.get(playerURI.toString());

        if (url == null) {
            try {
                String result = DownloadHandler.getHtml(playerURI.toURL());
                playerCache.put(playerURI.toString(), result);
                return result;
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
        return url;
    }

    private String getMainDecodeFunctionName(String playerJS) {
        Pattern decodeFunctionName = compile("(\\w+)\\s*=\\s*function\\((\\w+)\\)\\{\\s*\\2=\\s*\\2\\.split\\(\"\"\\)\\s*;");
        Matcher decodeFunctionNameMatch = decodeFunctionName.matcher(playerJS);
        if (decodeFunctionNameMatch.find()) {
            return decodeFunctionNameMatch.group(1);
        }
        throw new DownloadException("Failed to get main decoding function name!");
    }

    private String extractDecodeFunctions(String playerJS, String functionName) {
        StringBuilder decodeScript = new StringBuilder();
        Pattern decodeFunction = compile(String.format("(%s=function\\([a-zA-Z0-9$]+\\)\\{.*?\\})[,;]",
                Pattern.quote(functionName)), Pattern.DOTALL);
        Matcher decodeFunctionMatch = decodeFunction.matcher(playerJS);
        if (decodeFunctionMatch.find()) {
            decodeScript.append(decodeFunctionMatch.group(1)).append(';');
        } else {
            throw new RuntimeException("Unable to extractVideoInfo the main decode function!");
        }

        Pattern decodeFunctionHelperName = compile("\\);([a-zA-Z0-9]+)\\.");
        Matcher decodeFunctionHelperNameMatch = decodeFunctionHelperName.matcher(decodeScript.toString());
        if (decodeFunctionHelperNameMatch.find()) {
            final String decodeFuncHelperName = decodeFunctionHelperNameMatch.group(1);

            Pattern decodeFunctionHelper = compile(String.format("(var %s=\\{[a-zA-Z0-9]*:function\\(.*?\\};)",
                    Pattern.quote(decodeFuncHelperName)), Pattern.DOTALL);
            Matcher decodeFunctionHelperMatch = decodeFunctionHelper.matcher(playerJS);
            if (decodeFunctionHelperMatch.find()) {
                decodeScript.append(decodeFunctionHelperMatch.group(1));
            } else {
                throw new RuntimeException("Unable to extractVideoInfo the helper decode functions!");
            }

        } else {
            throw new RuntimeException("Unable to determine the name of the helper decode function!");
        }
        return decodeScript.toString();
    }
}