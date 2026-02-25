package com.glowxq.core.util;

import cn.hutool.core.util.EscapeUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/8/20
 */
public class HtmlUtil {

    public static final String NBSP = "&nbsp;";

    public static final String AMP = "&amp;";

    public static final String QUOTE = "&quot;";

    public static final String APOS = "&apos;";

    public static final String LT = "&lt;";

    public static final String GT = "&gt;";

    public static final String RE_HTML_MARK = "(<[^<]*?>)|(<[\\s]*?/[^<]*?>)|(<[^<]*?/[\\s]*?>)";

    public static final String RE_SCRIPT = "<[\\s]*?script[^>]*?>.*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";

    private static final char[][] TEXT = new char[256][];

    static {
        for (int i = 0; i < 256; ++i) {
            TEXT[i] = new char[]{(char) i};
        }

        TEXT[39] = "&#039;".toCharArray();
        TEXT[34] = "&quot;".toCharArray();
        TEXT[38] = "&amp;".toCharArray();
        TEXT[60] = "&lt;".toCharArray();
        TEXT[62] = "&gt;".toCharArray();
        TEXT[160] = "&nbsp;".toCharArray();
    }

    public static String escape(String text) {
        return encode(text);
    }

    public static String unescape(String htmlStr) {
        return StrUtil.isBlank(htmlStr) ? htmlStr : EscapeUtil.unescapeHtml4(htmlStr);
    }

    public static String cleanHtmlTag(String content) {
        return content.replaceAll("(<[^<]*?>)|(<[\\s]*?/[^<]*?>)|(<[^<]*?/[\\s]*?>)", "");
    }

    public static String removeHtmlTag(String content, String... tagNames) {
        return removeHtmlTag(content, true, tagNames);
    }

    public static String unwrapHtmlTag(String content, String... tagNames) {
        return removeHtmlTag(content, false, tagNames);
    }

    public static String removeHtmlTag(String content, boolean withTagContent, String... tagNames) {
        for (String tagName : tagNames) {
            if (!StrUtil.isBlank(tagName)) {
                tagName = tagName.trim();
                String regex;
                if (withTagContent) {
                    regex = StrUtil.format("(?i)<{}(\\s+[^>]*?)?/?>(.*?</{}>)?", new Object[]{tagName, tagName});
                }
                else {
                    regex = StrUtil.format("(?i)<{}(\\s+[^>]*?)?/?>|</?{}>", new Object[]{tagName, tagName});
                }

                content = ReUtil.delAll(regex, content);
            }
        }

        return content;
    }

    public static String removeHtmlAttr(String content, String... attrs) {
        for (String attr : attrs) {
            String regex = StrUtil.format("(?i)(\\s*{}\\s*=\\s*)(([\"][^\"]+?[\"]\\s*)|([^>]+?\\s+(?=>))|([^>]+?(?=\\s|>)))", new Object[]{attr});
            content = content.replaceAll(regex, "");
        }

        return content;
    }

    public static String removeAllHtmlAttr(String content, String... tagNames) {
        for (String tagName : tagNames) {
            String regex = StrUtil.format("(?i)<{}[^>]*?>", new Object[]{tagName});
            content = content.replaceAll(regex, StrUtil.format("<{}>", new Object[]{tagName}));
        }

        return content;
    }

    private static String encode(String text) {
        int len;
        if (text != null && (len = text.length()) != 0) {
            StringBuilder buffer = new StringBuilder(len + (len >> 2));

            for (int i = 0; i < len; ++i) {
                char c = text.charAt(i);
                if (c < 256) {
                    buffer.append(TEXT[c]);
                }
                else {
                    buffer.append(c);
                }
            }

            return buffer.toString();
        }
        else {
            return "";
        }
    }
}
