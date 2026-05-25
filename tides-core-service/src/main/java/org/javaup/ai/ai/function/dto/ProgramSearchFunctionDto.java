package org.javaup.ai.ai.function.dto;

import lombok.Data;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.Date;

/**
 * @description: 节目搜索 dto
 */
@Data
public class ProgramSearchFunctionDto {

    @ToolParam(required = false, description = "节目演出城市")
    private String cityName;

    @ToolParam(required = false, description = "节目艺人、演员、主演或主创")
    private String actor;

    @ToolParam(required = false, description = "节目、演唱会、电影名称或标题关键词")
    private String keyword;

    @ToolParam(required = false, description = "节目大类，例如：演唱会、电影、话剧歌剧、体育")
    private String programCategory;

    @ToolParam(required = false, description = "节目演出时间")
    private Date showTime;
}
