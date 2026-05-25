package org.javaup.ai.ai.function.dto;

import lombok.Data;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * @description: 电影搜索 dto
 */
@Data
public class MovieSearchFunctionDto {

    @ToolParam(required = false, description = "电影所在城市名称，例如：北京、上海、嘉兴")
    private String cityName;

    @ToolParam(required = false, description = "城市或地区 id，已知数字 id 时填写")
    private Long areaId;

    @ToolParam(required = false, description = "电影名称、别名、导演或演员关键词")
    private String content;

    @ToolParam(required = false, description = "电影名称")
    private String movieName;

    @ToolParam(required = false, description = "电影演员")
    private String actor;

    @ToolParam(required = false, description = "电影导演")
    private String director;

    @ToolParam(required = false, description = "上映状态：1 预售，2 热映，3 下映")
    private Integer releaseStatus;

    @ToolParam(required = false, description = "页码，默认 1")
    private Integer pageNumber;

    @ToolParam(required = false, description = "每页数量，默认 10")
    private Integer pageSize;
}
