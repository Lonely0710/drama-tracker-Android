package com.lonely.dramatracker.services;

import com.lonely.dramatracker.models.MediaInfo;
import com.lonely.dramatracker.models.SearchResult;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface SearchService {
    interface SearchCallback {
        void onSearchComplete(List<SearchResult> results);
    }
    
    interface JsonSearchCallback {
        void onSearchComplete(String jsonResults);
    }
    
    // 回调接口用于获取总项目数
    interface TotalItemsCallback {
        void onTotalItemsResult(int count);
    }
    
    // 添加 page 和 limit 参数
    void search(String keyword, String type, int page, int limit, SearchCallback callback);
    // 添加 page 和 limit 参数
    void search(String keyword, String type, int page, int limit, JsonSearchCallback callback);
    
    // 获取总项目数的方法
    void getTotalItems(String keyword, String type, TotalItemsCallback callback);
    
    void addToCollection(SearchResult result);
    void addToCollection(SearchResult result, Runnable onSuccess);
    void addToCollection(SearchResult result, Runnable onSuccess, Runnable onFailure);
    void removeFromCollection(SearchResult result);
    void removeFromCollection(SearchResult result, Runnable onSuccess);
    void removeFromCollection(SearchResult result, Runnable onSuccess, Runnable onFailure);

    /**
     * 获取媒体详细信息
     * @param sourceType 来源类型 (douban/imdb/bgm)
     * @param sourceId 源站ID
     * @return 媒体详细信息
     */
    CompletableFuture<MediaInfo> getMediaInfo(String sourceType, String sourceId);

    /**
     * 检查媒体是否已被收藏
     * @param mediaId 媒体ID
     * @return 是否已收藏
     */
    CompletableFuture<Boolean> isCollected(String mediaId);

    /**
     * 收藏媒体
     * @param mediaId 媒体ID
     * @return 是否收藏成功
     */
    CompletableFuture<Boolean> collect(String mediaId);

    /**
     * 取消收藏
     * @param mediaId 媒体ID
     * @return 是否取消成功
     */
    CompletableFuture<Boolean> uncollect(String mediaId);
} 