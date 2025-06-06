package com.lonely.dramatracker.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.lonely.dramatracker.R;
import com.lonely.dramatracker.models.RecordItem;
import com.lonely.dramatracker.services.AppwriteWrapper;

import java.util.ArrayList;
import java.util.List;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder> {
    private static final String TAG = "RecordAdapter";
    private List<RecordItem> items = new ArrayList<>();
    private boolean isGridMode = true;

    public RecordAdapter(boolean isGridMode) {
        this.isGridMode = isGridMode;
    }

    public void setGridMode(boolean isGridMode) {
        this.isGridMode = isGridMode;
    }

    public void setItems(List<RecordItem> items) {
        this.items = items != null ? items : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = isGridMode ? R.layout.item_record : R.layout.item_record_list;
        View view = LayoutInflater.from(parent.getContext())
                .inflate(layoutId, parent, false);
        return new ViewHolder(view, isGridMode);
    }

    @Override
    public int getItemViewType(int position) {
        // 返回不同的视图类型，确保在布局模式改变时强制重建ViewHolder
        return isGridMode ? 1 : 2;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecordItem item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "RecordAdapter";
        ImageView ivPoster;
        TextView tvTitle;
        TextView tvSubtitle;
        SwitchMaterial switchWatched;
        // 列表模式下的额外字段
        TextView tvRating, tvType, tvYear, tvDuration;
        // 评分图标
        ImageView ivRatingIcon;
        LinearLayout layoutRating;
        boolean isGridMode;

        ViewHolder(View itemView, boolean isGridMode) {
            super(itemView);
            this.isGridMode = isGridMode;
            ivPoster = itemView.findViewById(R.id.iv_poster);
            
            if (isGridMode) {
                // 网格模式(item_record.xml)
                tvTitle = itemView.findViewById(R.id.tv_title_original);
                tvRating = itemView.findViewById(R.id.tv_rating);
                tvYear = itemView.findViewById(R.id.tv_year);
                switchWatched = itemView.findViewById(R.id.switch_watched);
                layoutRating = itemView.findViewById(R.id.layout_rating);
                ivRatingIcon = itemView.findViewById(R.id.iv_rating_icon);
            } else {
                // 列表模式(item_record_list.xml)
                tvTitle = itemView.findViewById(R.id.tv_title);
                tvRating = itemView.findViewById(R.id.tv_rating);
                tvType = itemView.findViewById(R.id.tv_type);
                tvYear = itemView.findViewById(R.id.tv_year);
                tvDuration = itemView.findViewById(R.id.tv_duration);
                switchWatched = itemView.findViewById(R.id.switch_watched);
                layoutRating = itemView.findViewById(R.id.layout_rating);
                ivRatingIcon = itemView.findViewById(R.id.iv_rating_icon);
            }
        }

        void bind(RecordItem item) {
            // 设置标题 - 根据不同媒体类型选择标题
            if (tvTitle != null) {
                String title;
                if (isGridMode) {
                    // 网格模式：优先使用原始标题(title_origin)，若为空则使用中文标题(title_zh)
                    String originalTitle = item.getSubtitle();
                    if (originalTitle != null && !originalTitle.trim().isEmpty()) {
                        title = originalTitle; // 优先使用原始标题
                    } else {
                        title = item.getTitle(); // 原始标题为空时使用中文标题
                    }
                } else {
                    // 列表模式始终使用中文标题
                    title = item.getTitle();
                }
                tvTitle.setText(title != null ? title : "");
            }
            
            // 使用Glide加载海报图片
            if (ivPoster != null) {
                Glide.with(itemView.getContext())
                        .load(item.getPosterUrl())
                        .placeholder(R.drawable.placeholder_poster)
                        .error(R.drawable.placeholder_poster)
                        .into(ivPoster);
            }
            
            // 设置评分和对应图标
            if (tvRating != null && item.getRating() != null) {
                tvRating.setText(item.getRating());
                
                // 根据媒体类型和评分格式显示对应图标和颜色
                if (ivRatingIcon != null) {
                    String mediaType = item.getMediaType();
                    String rating = item.getRating();
                    
                    if ("anime".equals(mediaType)) {
                        // 动漫类型统一使用Bangumi图标和颜色
                        ivRatingIcon.setImageResource(R.drawable.ic_bangumi);
                        tvRating.setTextColor(itemView.getContext().getResources().getColor(R.color.bangumi_pink));
                    } else {
                        // 电影或电视剧类型
                        if (rating.contains(" / ")) {
                            // 有多个评分时,使用豆瓣图标(因为豆瓣评分在前)
                            ivRatingIcon.setImageResource(R.drawable.ic_douban_green);
                            tvRating.setTextColor(itemView.getContext().getResources().getColor(R.color.douban_green));
                        } else if (rating.matches(".*\\d+\\.?\\d*/10.*")) {
                            // IMDB格式: x/10
                            ivRatingIcon.setImageResource(R.drawable.ic_tmdb_16);
                            tvRating.setTextColor(itemView.getContext().getResources().getColor(R.color.tmdb_green));
                        } else {
                            // 其他情况使用豆瓣图标
                            ivRatingIcon.setImageResource(R.drawable.ic_douban_green);
                            tvRating.setTextColor(itemView.getContext().getResources().getColor(R.color.douban_green));
                        }
                    }
                }
                
                // 显示评分布局
                if (layoutRating != null) {
                    layoutRating.setVisibility(View.VISIBLE);
                }
            } else {
                // 无评分时隐藏评分布局
                if (layoutRating != null) {
                    layoutRating.setVisibility(View.GONE);
                }
            }
            
            // 设置年份/日期 - 根据视图类型选择不同格式
            if (tvYear != null && item.getYear() != null) {
                String dateText;
                if (isGridMode) {
                    // 网格视图只显示年份
                    if (item.getYear().length() >= 4) {
                        dateText = item.getYear().substring(0, 4);
                    } else {
                        dateText = item.getYear();
                    }
                } else {
                    // 列表视图显示完整日期
                    dateText = item.getYear();
                }
                tvYear.setText(dateText);
            }
            
            // 在列表模式下设置额外信息
            if (!isGridMode) {
                // 设置媒体类型
                if (tvType != null && item.getMediaType() != null) {
                    String displayType = "";
                    switch (item.getMediaType()) {
                        case "movie": displayType = "电影"; break;
                        case "tv": displayType = "电视剧"; break;
                        case "anime": displayType = "动漫"; break;
                        default: displayType = item.getMediaType();
                    }
                    tvType.setText(displayType);
                }
                
                // 设置时长
                if (tvDuration != null) {
                    String duration = item.getDuration();
                    if (duration != null && !duration.isEmpty()) {
                        // 格式化时长显示
                        String formattedDuration = formatDuration(duration, item.getMediaType());
                        tvDuration.setText(formattedDuration);
                    } else {
                        tvDuration.setText("");
                    }
                }
            }
            
            // 设置观看状态开关
            if (switchWatched != null) {
                // 设置当前状态
                switchWatched.setChecked(item.isWatched());
                
                // 设置开关状态变化监听器
                switchWatched.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    // 避免重复触发
                    if (isChecked == item.isWatched()) return;
                    
                    // 更新本地状态
                    item.setWatched(isChecked);
                    
                    // 获取当前用户ID
                    String userId = AppwriteWrapper.getCurrentUserId();
                    String mediaId = item.getMediaId();
                    
                    Log.d(TAG, "更新观看状态: userId=" + userId + ", mediaId=" + mediaId + ", status=" + isChecked);
                    
                    // 调用API更新观看状态
                    AppwriteWrapper.updateWatchStatus(userId, mediaId, isChecked, 
                        () -> {
                            // 成功回调
                            Log.d(TAG, "观看状态更新成功");
                        }, 
                        () -> {
                            // 失败回调，回滚UI状态
                            Log.e(TAG, "观看状态更新失败");
                            item.setWatched(!isChecked);
                            switchWatched.setChecked(!isChecked);
                            
                            // 显示错误提示
                            Toast.makeText(itemView.getContext(), 
                                "更新观看状态失败，请重试", Toast.LENGTH_SHORT).show();
                        });
                });
            }
        }
        
        /**
         * 格式化时长显示
         * @param duration 原始时长数据
         * @param mediaType 媒体类型
         * @return 格式化后的时长文本
         */
        private String formatDuration(String duration, String mediaType) {
            if (duration == null || duration.isEmpty()) {
                return "";
            }
            
            try {
                // 根据媒体类型选择格式
                if ("anime".equals(mediaType) || "tv".equals(mediaType)) {
                    // 动漫/电视剧，显示为"xx集"
                    return duration + "集";
                } else {
                    // 电影，尝试转换为小时+分钟格式
                    int minutes = Integer.parseInt(duration.trim());
                    if (minutes >= 60) {
                        int hours = minutes / 60;
                        int remainingMinutes = minutes % 60;
                        return hours + "时" + (remainingMinutes > 0 ? remainingMinutes + "分" : "");
                    } else {
                        return minutes + "分钟";
                    }
                }
            } catch (NumberFormatException e) {
                // 如果无法解析为数字，直接返回原始值
                return duration;
            }
        }
    }
} 