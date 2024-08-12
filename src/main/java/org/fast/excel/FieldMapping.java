package org.fast.excel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字段映射
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldMapping {


    /**
     * 枚举映射map
     * @return
     */
    String enumMap() default "";

    /**
     * 枚举类导入、导出在excel中的分隔符号
     * @return
     */
    String spiteChar() default ",";

    /**
     * key和value的映射方式
     * 例如：
     * key=value,key=value...
     * key=value|key=value
     * @return
     */
    String mapSplit() default ",";

    /**
     * 映射的连接方式
     * 例如：
     * key=value
     * key|value
     * @return
     */
    String mapConcat() default "=";

    /**
     * 单选 or 多选
     * @return
     */
    boolean single() default true;
}