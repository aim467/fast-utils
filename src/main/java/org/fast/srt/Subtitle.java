package org.fast.srt;

public class Subtitle {
    private int index;         // 字幕序号
    private String startTimeStr; // 开始时间（原始格式）
    private String endTimeStr;   // 结束时间（原始格式）
    private long startTimeMs;    // 开始时间（毫秒）
    private long endTimeMs;      // 结束时间（毫秒）
    private long durationMs;     // 持续时间（毫秒）
    private String text;         // 字幕文本

    // 构造函数
    public Subtitle(int index, String startTimeStr, String endTimeStr, String text) throws IllegalArgumentException {
        this.index = index;
        this.startTimeStr = startTimeStr;
        this.endTimeStr = endTimeStr;
        this.startTimeMs = parseTime(startTimeStr);
        this.endTimeMs = parseTime(endTimeStr);
        if (endTimeMs < startTimeMs) {
            throw new IllegalArgumentException("endTime 必须大于或等于 startTime");
        }
        this.durationMs = endTimeMs - startTimeMs;
        this.text = text;
    }

    // Getter 方法
    public String getStartTimeStr() {
        return startTimeStr;
    }

    public String getEndTimeStr() {
        return endTimeStr;
    }

    public String getDurationStr() {
        return formatTime(durationMs);
    }

    // 将时间字符串转换为毫秒数
    private static long parseTime(String timeStr) {
        try {
            String[] parts = timeStr.split(",");
            if (parts.length != 2) {
                throw new IllegalArgumentException("时间格式错误: " + timeStr);
            }
            String[] hms = parts[0].split(":");
            if (hms.length != 3) {
                throw new IllegalArgumentException("时间格式错误: " + timeStr);
            }
            int hours = Integer.parseInt(hms[0]);
            int minutes = Integer.parseInt(hms[1]);
            int seconds = Integer.parseInt(hms[2]);
            int milliseconds = Integer.parseInt(parts[1]);
            return (hours * 3600L + minutes * 60L + seconds) * 1000L + milliseconds;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("时间格式错误: " + timeStr);
        }
    }

    // 将毫秒数转换为时间字符串
    private static String formatTime(long milliseconds) {
        long hours = milliseconds / 3600000;
        long minutes = (milliseconds % 3600000) / 60000;
        long seconds = (milliseconds % 60000) / 1000;
        long millis = milliseconds % 1000;
        return String.format("%02d:%02d:%02d,%03d", hours, minutes, seconds, millis);
    }

    // 重写 toString 方法
    @Override
    public String toString() {
        return index + "\n" +
                startTimeStr + " --> " + endTimeStr + "\n" +
                "Duration: " + getDurationStr() + "\n" +
                text;
    }
}