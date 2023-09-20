package ru.isb.bot.utils;

public class StringUtils {

    public static String replaceHTML(String text) {
        return text.replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("!", "&#33;")
                .replaceFirst("```", "<pre>")
                .replaceFirst("```", "</pre>");
    }
}
