package ru.isb.bot.utils;

public class PathFileUtils {

    private static final String COPY_POSTFIX = "(копия)";

    public static String copyFileName(String fileName) {
        String [] prefixAndPostfix = fileName.split("\\.");
        return prefixAndPostfix.length == 2 ? prefixAndPostfix[0] + COPY_POSTFIX + "." + prefixAndPostfix[1] : fileName + COPY_POSTFIX;
    }

    public static String replaceSpace(String fileName) {
        return fileName.replace(" ", "_");
    }

}
