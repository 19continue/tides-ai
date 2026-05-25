package org.javaup.ai.ai.function.dto;

import lombok.Data;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * @description: 电影详情 dto
 */
@Data
public class MovieDetailFunctionDto {

    @ToolParam(required = true, description = "电影关联的节目 id")
    private Long programId;

    @ToolParam(required = false, description = "电影 id")
    private Long movieId;

    @ToolParam(required = false, description = "电影所在城市名称，例如：北京、上海、嘉兴")
    private String cityName;

    @ToolParam(required = false, description = "城市或地区 id，已知数字 id 时填写")
    private Long areaId;
}
