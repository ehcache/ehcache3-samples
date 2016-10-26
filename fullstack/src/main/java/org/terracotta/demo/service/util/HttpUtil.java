package org.terracotta.demo.service.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author Henri Tremblay
 */
public final class HttpUtil {

    private HttpUtil() {}

    public static String utf8Encode(String url) {
        return urlEncode(url, StandardCharsets.UTF_8);
    }

    public static String urlEncode(String url, Charset cs) {
        try {
            return URLEncoder.encode(url, cs.name());
        }
        catch(UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
