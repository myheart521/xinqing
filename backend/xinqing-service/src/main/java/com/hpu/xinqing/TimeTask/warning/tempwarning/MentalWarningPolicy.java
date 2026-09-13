package com.hpu.xinqing.TimeTask.warning.tempwarning;

import com.hpu.xinqingpojo.DTO.ActiveAnalyseDTO;
import com.hpu.xinqingpojo.DTO.BlogAnalyseDTO;
import com.hpu.xinqingpojo.DTO.ChatWithAIAnalyseDTO;
import com.hpu.xinqingpojo.DTO.CommentAnalyseDTO;
import com.hpu.xinqingpojo.DTO.PsychologyWarningPredictionDTO;
import com.hpu.xinqingpojo.DTO.TempEmotionUserDataDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class MentalWarningPolicy {

    public int mapAlertLevel(PsychologyWarningPredictionDTO prediction) {
        int riskLevel = prediction == null || prediction.getRiskLevel() == null ? 0 : prediction.getRiskLevel();
        BigDecimal riskConfidence = prediction == null ? null : prediction.getRiskConfidence();

        if (riskLevel <= 0) {
            return 3;
        }
        if (riskLevel == 1) {
            return compareConfidence(riskConfidence, 0.55) >= 0 ? 0 : 3;
        }
        if (riskLevel == 2) {
            return compareConfidence(riskConfidence, 0.55) >= 0 ? 1 : 0;
        }
        return 2;
    }

    public boolean shouldNotifyTeacher(int alertLevel) {
        return alertLevel == 1 || alertLevel == 2;
    }

    public int scoreDelta(int alertLevel) {
        return switch (alertLevel) {
            case 0 -> -5;
            case 1 -> -10;
            case 2 -> -20;
            case 3 -> 10;
            default -> 0;
        };
    }

    public boolean shouldPersistStudentAlert(int alertLevel) {
        return alertLevel >= 0 && alertLevel <= 2;
    }

    public boolean isResolvedAutomatically(int alertLevel) {
        return alertLevel == 3;
    }

    public boolean isAbnormal(PsychologyWarningPredictionDTO prediction) {
        return prediction != null && prediction.getRiskLevel() != null && prediction.getRiskLevel() > 0;
    }

    public Long selectFeatureBlogId(TempEmotionUserDataDTO source) {
        BlogAnalyseDTO blog = first(source == null ? null : source.getBlogAnalyseList());
        return blog == null ? null : blog.getId();
    }

    public Long selectFeatureActiveId(TempEmotionUserDataDTO source) {
        ActiveAnalyseDTO active = first(source == null ? null : source.getActiveAnalyseList());
        return active == null ? null : active.getId();
    }

    public String buildRelatedContent(TempEmotionUserDataDTO source) {
        List<String> fragments = new ArrayList<>();

        BlogAnalyseDTO blog = first(source == null ? null : source.getBlogAnalyseList());
        if (blog != null) {
            fragments.add("博客:" + safeJoin(blog.getTitle(), blog.getContent()));
        }

        ActiveAnalyseDTO active = first(source == null ? null : source.getActiveAnalyseList());
        if (active != null) {
            fragments.add("活动:" + safeJoin(active.getTitle(), active.getContent()));
        }

        CommentAnalyseDTO comment = first(source == null ? null : source.getNoteAnalyseList());
        if (comment != null) {
            fragments.add("评论:" + safeText(comment.getContent()));
        }

        ChatWithAIAnalyseDTO chat = first(source == null ? null : source.getChatWithAIAnalyseList());
        if (chat != null) {
            fragments.add("对话:" + safeText(chat.getContent()));
        }

        if (fragments.isEmpty()) {
            return "暂无可用的近期行为文本";
        }
        return truncate(String.join(" | ", fragments), 240);
    }

    public String buildRecentBehavior(PsychologyWarningPredictionDTO prediction, TempEmotionUserDataDTO source) {
        int alertLevel = mapAlertLevel(prediction);
        String emotion = safeText(prediction == null ? null : prediction.getEmotionLabel());
        String evidence = buildRelatedContent(source);
        return switch (alertLevel) {
            case 0 -> truncate("出现轻度" + emotion + "和压力信号，" + evidence, 180);
            case 1 -> truncate("持续出现" + emotion + "、睡眠受影响或回避交流迹象，" + evidence, 180);
            case 2 -> truncate("存在明显高风险信号，" + evidence, 180);
            default -> "整体状态稳定，暂无明显预警信号";
        };
    }

    public String buildSuggestion(PsychologyWarningPredictionDTO prediction, int alertLevel) {
        String emotion = safeText(prediction == null ? null : prediction.getEmotionLabel());
        String suggestion = switch (alertLevel) {
            case 0 -> "建议持续关注，保持沟通，必要时安排简短谈话。";
            case 1 -> "建议及时联系辅导员或心理老师，持续跟进一周。";
            case 2 -> "建议立即人工介入，尽快联系辅导员、家长或专业心理支持。";
            default -> "保持当前状态，继续关注即可。";
        };
        if (alertLevel == 3 || emotion.isBlank()) {
            return suggestion;
        }
        return suggestion + " 重点关注" + emotion + "相关信号。";
    }

    public Integer resolveHandlingStatus(int alertLevel) {
        return isResolvedAutomatically(alertLevel) ? 1 : 0;
    }

    public Integer resolveHandleWay(int alertLevel) {
        return shouldNotifyTeacher(alertLevel) ? 1 : 3;
    }

    public Boolean resolveIsResolve(int alertLevel) {
        return isResolvedAutomatically(alertLevel);
    }

    private int compareConfidence(BigDecimal confidence, double threshold) {
        if (confidence == null) {
            return -1;
        }
        return confidence.compareTo(BigDecimal.valueOf(threshold));
    }

    private <T> T first(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    private String safeJoin(String first, String second) {
        String joined = safeText(first) + " " + safeText(second);
        return truncate(joined.trim(), 120);
    }

    private String safeText(String text) {
        return text == null ? "" : text.trim();
    }

    private String truncate(String text, int limit) {
        String value = safeText(text);
        if (value.length() <= limit) {
            return value;
        }
        return value.substring(0, limit) + "...";
    }
}
