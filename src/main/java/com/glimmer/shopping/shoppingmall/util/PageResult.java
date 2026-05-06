package com.glimmer.shopping.shoppingmall.util;

import lombok.Data;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Data
public class PageResult<T> {
    private List<T> records;
    private List<T> list;
    private Long total;
    private Integer page;
    private Integer size;
    private Integer pages;
    private Boolean hasNext;
    private Boolean hasPrevious;

    private static final Pattern PURE_NUMERIC_PATTERN = Pattern.compile("^[0-9]+$");
    private static final Pattern LETTER_PATTERN = Pattern.compile("[a-zA-Z\\u4e00-\\u9fa5]");
    private static final Collator CHINESE_COLLATOR = Collator.getInstance(Locale.CHINA);

    public PageResult() {}

    public PageResult(List<T> records, Long total, Integer page, Integer size) {
        this.records = records;
        this.list = records;
        this.total = total;
        this.page = page;
        this.size = size;
        this.pages = (int) Math.ceil((double) total / size);
        this.hasNext = page < pages;
        this.hasPrevious = page > 1;
    }

    public static <T> PageResult<T> of(List<T> records, Long total, Integer page, Integer size) {
        return new PageResult<>(records, total, page, size);
    }

    public static <T> PageResult<T> of(List<T> records, Integer page, Integer size) {
        return new PageResult<>(records, (long) records.size(), page, size);
    }

    /**
     * 按名称首字母排序（支持中英文混合，纯数字排最后）
     * 使用中文拼音排序，确保同首字母的中文按正确的拼音顺序排列
     *
     * @param nameExtractor 名称提取函数
     * @return 排序后的 PageResult
     */
    public PageResult<T> sortByName(NameExtractor<T> nameExtractor) {
        if (this.records != null && !this.records.isEmpty()) {
            List<T> sortedList = new ArrayList<>(this.records);
            sortedList.sort((a, b) -> {
                String nameA = nameExtractor.extract(a);
                String nameB = nameExtractor.extract(b);
                
                String sortKeyA = getSortKey(nameA);
                String sortKeyB = getSortKey(nameB);
                
                String firstCharA = sortKeyA.substring(0, 1);
                String firstCharB = sortKeyB.substring(0, 1);
                
                int firstCharCompare = firstCharA.compareTo(firstCharB);
                if (firstCharCompare != 0) {
                    return firstCharCompare;
                }
                
                return CHINESE_COLLATOR.compare(nameA, nameB);
            });
            this.records = sortedList;
            this.list = sortedList;
        }
        return this;
    }

    /**
     * 获取排序键
     */
    private String getSortKey(String name) {
        if (name == null || name.isEmpty()) {
            return "~";
        }

        if (PURE_NUMERIC_PATTERN.matcher(name).matches()) {
            return "~" + name;
        }

        java.util.regex.Matcher matcher = LETTER_PATTERN.matcher(name);
        if (matcher.find()) {
            String firstLetter = matcher.group(0).toUpperCase();
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