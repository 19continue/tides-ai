package org.javaup.ai.vo;

import lombok.Data;

import java.util.List;

@Data
public class MoviePageDataVo {

    private List<MovieListVo> records;

    private Long total;

    private Long size;

    private Long current;

    private Long pages;
}
