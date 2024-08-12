package org.fast.excel;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.alibaba.excel.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 针对 EasyExcel 的 Converter 实现通用导入导出转换器
 *
 */
public class CommonConverter implements Converter<Object> {
    /**
     * 枚举列表
     */
    private Map<String, String> enumMap = new HashMap<>();


    /**
     * excel转换后的类型
     *
     * @return
     */
    @Override
    public Class<?> supportJavaTypeKey() {
        return Object.class;
    }

    /**
     * excel中的数据类型，默认都为字符串
     *
     * @return
     */
    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    /**
     * 导入转换
     * @param cellData            当前单元格对象
     * @param contentProperty     当前单元格属性
     * @param globalConfiguration 全局配置
     * @return
     */
    @Override
    public Object convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        String cellMsg = cellData.getStringValue();
        Field field = contentProperty.getField();
        FieldMapping fieldMapping = field.getAnnotation(FieldMapping.class);
        if (fieldMapping == null) {
            return null;
        }
        String enumStr = fieldMapping.enumMap();
        getEnumMap(enumStr, true, fieldMapping.mapSplit(), fieldMapping.mapConcat());
        boolean single = fieldMapping.single();
        // 如果是单选，默认Java属性为Integer
        if (single) {
            String res = enumMap.get(cellMsg);
            return StringUtils.isNotBlank(res) ? Integer.valueOf(res) : null;
        } else {
            String spiteChar = fieldMapping.spiteChar();
            // 多选枚举，默认Java属性为字符串，格式为 key1,key2,key3
            List<String> strStr = Arrays.asList(cellMsg.split(spiteChar)).stream().map(s -> String.valueOf(enumMap.get(s))).collect(Collectors.toList());
            return String.join(spiteChar, strStr);
        }
    }

    /**
     * 导出转化
     * @param value               当前值
     * @param contentProperty     当前单元格属性
     * @param globalConfiguration 全局配置
     * @return
     */
    @Override
    public WriteCellData<?> convertToExcelData(Object value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        Field field = contentProperty.getField();
        FieldMapping fieldMapping = field.getAnnotation(FieldMapping.class);
        if (fieldMapping == null) {
            return new WriteCellData<>();
        }
        // 解析枚举字符串
        String enumStr = fieldMapping.enumMap();
        getEnumMap(enumStr, false, fieldMapping.mapSplit(), fieldMapping.mapConcat());
        // 是否为单选
        boolean single = fieldMapping.single();
        // 如果是单选，默认Java属性为Integer
        if (single) {
            return new WriteCellData<>(enumMap.getOrDefault(String.valueOf(value), ""));
        } else {
            // 多选分隔符
            String spiteChar = fieldMapping.spiteChar();
            List<String> strStr = Arrays.asList(String.valueOf(value).split(spiteChar)).stream().map(s -> String.valueOf(enumMap.get(s))).collect(Collectors.toList());
            String str = String.join(spiteChar, strStr);
            return new WriteCellData<>(str);
        }
    }

    /**
     * 根据注解配置的枚举映射字符串进行解析到map中
     * @param mapStr 映射字符串
     * @param mapSplit key和value映射的字符串
     * @param mapConcat 映射之间拼接的字符串
     * @param readOrWrite 读或写
     */
    private void getEnumMap(String mapStr, boolean readOrWrite, String mapSplit, String mapConcat) {
        String[] enumS = mapStr.split(mapSplit);
        for (String anEnum : enumS) {
            String[] data = anEnum.split(mapConcat);
            if (readOrWrite) {
                enumMap.put(data[1], data[0]);
            } else {
                enumMap.put(data[0], data[1]);
            }
        }
    }
}