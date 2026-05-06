package com.glimmer.shopping.shoppingmall.util;

import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 排序工具类
 * 支持中英文混合排序，纯数字排在最后
 */
public class SortUtils {

    private static final Pattern PURE_NUMERIC_PATTERN = Pattern.compile("^[0-9]+$");
    private static final Pattern LETTER_PATTERN = Pattern.compile("[a-zA-Z\\u4e00-\\u9fa5]");

    /**
     * 按名称首字母排序（支持中英文混合，纯数字排最后）
     *
     * @param list 待排序的列表
     * @param nameExtractor 名称提取函数
     * @param <T> 元素类型
     * @return 排序后的列表
     */
    public static <T> List<T> sortByName(List<T> list, NameExtractor<T> nameExtractor) {
        list.sort(Comparator.comparing(t -> {
            String name = nameExtractor.extract(t);
            return getSortKey(name);
        }));
        return list;
    }

    /**
     * 获取排序键
     *
     * @param name 名称
     * @return 排序键
     */
    private static String getSortKey(String name) {
        if (name == null || name.isEmpty()) {
            return "~";
        }

        // 纯数字排在最后
        if (PURE_NUMERIC_PATTERN.matcher(name).matches()) {
            return "~" + name;
        }

        // 找到第一个字母或中文
        java.util.regex.Matcher matcher = LETTER_PATTERN.matcher(name);
        if (matcher.find()) {
            String firstLetter = matcher.group(0).toUpperCase();
            // 中文和英文混合排序
            return firstLetter + name;
        }

        return name;
    }

    /**
     * 名称提取接口
     */
    @FunctionalInterface
    public interface NameExtractor<T> {
        String extract(T item);
    }
}