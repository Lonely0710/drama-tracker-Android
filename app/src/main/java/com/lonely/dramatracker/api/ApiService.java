package com.lonely.dramatracker.api;

import com.lonely.dramatracker.models.SearchResult;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ApiService {
    /**
     * 搜索剧集
     * @param keyword 搜索关键词
     * @param type 搜索类型
     * @param page 页码
     * @param limit 每页数量
     * @return 搜索结果列表的Future
     */
    CompletableFuture<List<SearchResult>> search(String keyword, String type, int page, int limit);
    
    /**
     * 获取搜索结果的总数量
     * @param keyword 搜索关键词
     * @param type 搜索类型
     * @return 结果总数的Future
     */
    CompletableFuture<Integer> getTotalCount(String keyword, String type);
}