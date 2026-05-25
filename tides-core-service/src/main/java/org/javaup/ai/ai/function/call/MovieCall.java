package org.javaup.ai.ai.function.call;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.javaup.ai.ai.function.dto.MovieDetailFunctionDto;
import org.javaup.ai.ai.function.dto.MovieScreeningFunctionDto;
import org.javaup.ai.ai.function.dto.MovieSearchFunctionDto;
import org.javaup.ai.enums.BaseCode;
import org.javaup.ai.utils.StringUtil;
import org.javaup.ai.vo.MovieDetailVo;
import org.javaup.ai.vo.MovieListVo;
import org.javaup.ai.vo.MoviePageDataVo;
import org.javaup.ai.vo.MovieScreeningVo;
import org.javaup.ai.vo.result.MovieDetailResultVo;
import org.javaup.ai.vo.result.MoviePageResultVo;
import org.javaup.ai.vo.result.MovieScreeningListResultVo;
import org.javaup.ai.vo.result.base.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.javaup.ai.constants.TidesConstant.MOVIE_DETAIL_URL;
import static org.javaup.ai.constants.TidesConstant.MOVIE_PAGE_URL;
import static org.javaup.ai.constants.TidesConstant.MOVIE_SCREENING_LIST_URL;

/**
 * @description: 电影服务调用
 */
@Component
public class MovieCall {

    @Autowired
    private AreaCall areaCall;

    public List<MovieListVo> page(MovieSearchFunctionDto movieSearchFunctionDto) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("pageNumber", Objects.requireNonNullElse(movieSearchFunctionDto.getPageNumber(), 1));
        requestBody.put("pageSize", Objects.requireNonNullElse(movieSearchFunctionDto.getPageSize(), 10));
        requestBody.put("areaId", resolveAreaId(movieSearchFunctionDto.getAreaId(), movieSearchFunctionDto.getCityName()));
        requestBody.put("content", buildContent(movieSearchFunctionDto));
        requestBody.put("releaseStatus", movieSearchFunctionDto.getReleaseStatus());

        String result = HttpRequest.post(MOVIE_PAGE_URL)
                .header("no_verify", "true")
                .body(JSON.toJSONString(requestBody))
                .timeout(20000)
                .execute().body();
        MoviePageResultVo moviePageResultVo = JSON.parseObject(result, MoviePageResultVo.class);
        checkResponse(moviePageResultVo, "调用潮声系统查询电影列表失败");
        MoviePageDataVo data = moviePageResultVo.getData();
        if (Objects.isNull(data) || CollectionUtil.isEmpty(data.getRecords())) {
            return Collections.emptyList();
        }
        return data.getRecords();
    }

    public MovieDetailVo detail(MovieDetailFunctionDto movieDetailFunctionDto) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("programId", movieDetailFunctionDto.getProgramId());
        requestBody.put("movieId", movieDetailFunctionDto.getMovieId());
        requestBody.put("areaId", resolveAreaId(movieDetailFunctionDto.getAreaId(), movieDetailFunctionDto.getCityName()));

        String result = HttpRequest.post(MOVIE_DETAIL_URL)
                .header("no_verify", "true")
                .body(JSON.toJSONString(requestBody))
                .timeout(20000)
                .execute().body();
        MovieDetailResultVo movieDetailResultVo = JSON.parseObject(result, MovieDetailResultVo.class);
        checkResponse(movieDetailResultVo, "调用潮声系统查询电影详情失败");
        return movieDetailResultVo.getData();
    }

    public List<MovieScreeningVo> screeningList(MovieScreeningFunctionDto movieScreeningFunctionDto) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("programId", movieScreeningFunctionDto.getProgramId());
        requestBody.put("cinemaId", movieScreeningFunctionDto.getCinemaId());
        requestBody.put("areaId", resolveAreaId(movieScreeningFunctionDto.getAreaId(), movieScreeningFunctionDto.getCityName()));
        requestBody.put("showDayTime", movieScreeningFunctionDto.getShowDayTime());

        String result = HttpRequest.post(MOVIE_SCREENING_LIST_URL)
                .header("no_verify", "true")
                .body(JSON.toJSONString(requestBody))
                .timeout(20000)
                .execute().body();
        MovieScreeningListResultVo movieScreeningListResultVo = JSON.parseObject(result, MovieScreeningListResultVo.class);
        checkResponse(movieScreeningListResultVo, "调用潮声系统查询电影放映场次失败");
        if (CollectionUtil.isEmpty(movieScreeningListResultVo.getData())) {
            return Collections.emptyList();
        }
        return movieScreeningListResultVo.getData();
    }

    private String buildContent(MovieSearchFunctionDto movieSearchFunctionDto) {
        if (StringUtil.isNotEmpty(movieSearchFunctionDto.getContent())) {
            return movieSearchFunctionDto.getContent();
        }
        if (StringUtil.isNotEmpty(movieSearchFunctionDto.getMovieName())) {
            return movieSearchFunctionDto.getMovieName();
        }
        if (StringUtil.isNotEmpty(movieSearchFunctionDto.getActor())) {
            return movieSearchFunctionDto.getActor();
        }
        if (StringUtil.isNotEmpty(movieSearchFunctionDto.getDirector())) {
            return movieSearchFunctionDto.getDirector();
        }
        return null;
    }

    private Long resolveAreaId(Long areaId, String cityName) {
        if (Objects.nonNull(areaId) || StringUtil.isEmpty(cityName)) {
            return areaId;
        }
        return areaCall.resolveAreaId(cityName);
    }

    private void checkResponse(ApiResponse apiResponse, String message) {
        if (Objects.isNull(apiResponse) || !Objects.equals(apiResponse.getCode(), BaseCode.SUCCESS.getCode())) {
            throw new RuntimeException(message);
        }
    }
}
