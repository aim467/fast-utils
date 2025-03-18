package org.fast.srt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SrtParser {
    /**
     * 解析SRT文件，返回字幕对象列表
     * @param filePath SRT文件路径
     * @return List<Subtitle> 字幕列表
     * @throws IOException 如果文件读取或格式错误
     */
    public static List<Subtitle> parseSrtFile(String filePath) throws IOException {
        List<Subtitle> subtitles = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue; // 跳过空行
                }

                // 解析序号
                int index;
                try {
                    index = Integer.parseInt(line);
                } catch (NumberFormatException e) {
                    throw new IOException("无效的字幕序号: " + line);
                }

                // 解析时间戳
                line = reader.readLine();
                if (line == null || !line.contains(" --> ")) {
                    throw new IOException("无效的时间戳格式，序号: " + index);
                }
                String[] times = line.trim().split(" --> ");
                if (times.length != 2) {
                    throw new IOException("时间戳格式错误，序号: " + index);
                }

                // 解析字幕文本
                StringBuilder textBuilder = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) {
                        break; // 遇到空行，表示字幕块结束
                    }
                    textBuilder.append(line).append("\n");
                }
                String text = textBuilder.toString().trim();
                if (text.isEmpty()) {
                    throw new IOException("字幕文本为空，序号: " + index);
                }

                // 创建字幕对象并添加到列表
                Subtitle subtitle = new Subtitle(index, times[0], times[1], text);
                subtitles.add(subtitle);
            }
        }

        return subtitles;
    }
}