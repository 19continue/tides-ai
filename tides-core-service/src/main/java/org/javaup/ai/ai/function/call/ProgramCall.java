package org.javaup.ai.ai.function.call;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.javaup.ai.ai.function.dto.ProgramRecommendFunctionDto;
import org.javaup.ai.ai.function.dto.ProgramSearchFunctionDto;
import org.javaup.ai.dto.ProgramDetailDto;
import org.javaup.ai.enums.BaseCode;
import org.javaup.ai.utils.StringUtil;
import org.javaup.ai.vo.ProgramSearchVo;
import org.javaup.ai.vo.result.ProgramDetailResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.javaup.ai.constants.TidesConstant.PROGRAM_DETAIL_URL;
import static org.javaup.ai.constants.TidesConstant.PROGRAM_SEARCH_URL;

/**
 * @description: 节目服务调用
 */
@Component
public class ProgramCall {

    private static final int PAGE_NUMBER = 1;

    private static final int PAGE_SIZE = 10;

    private static final int ALL_TIME_TYPE = 0;

    private static final Map<String, Long> PARENT_CATEGORY_MAP = Map.ofEntries(
            Map.entry("演唱会", 1L),
            Map.entry("演出", 1L),
            Map.entry("话剧歌剧", 2L),
            Map.entry("话剧", 2L),
            Map.entry("歌剧", 2L),
            Map.entry("体育", 3L),
            Map.entry("儿童亲子", 4L),
            Map.entry("展览休闲", 5L),
            Map.entry("展览", 5L),
            Map.entry("音乐会", 6L),
            Map.entry("曲苑杂坛", 7L),
            Map.entry("舞蹈芭蕾", 8L),
            Map.entry("二次元", 9L),
            Map.entry("旅游展览", 10L),
            Map.entry("电影", 22L)
    );

    @Autowired
    private AreaCall areaCall;
    
    public List<ProgramSearchVo> recommendList(ProgramRecommendFunctionDto programRecommendFunctionDto){
        ProgramSearchFunctionDto programSearchFunctionDto = new ProgramSearchFunctionDto();
        programSearchFunctionDto.setCityName(programRecommendFunctionDto.getAreaName());
        programSearchFunctionDto.setProgramCategory(programRecommendFunctionDto.getProgramCategory());
        return search(programSearchFunctionDto);
    }

    public List<ProgramSearchVo> search(ProgramSearchFunctionDto programSearchFunctionDto){
        Long areaId = areaCall.resolveAreaId(programSearchFunctionDto.getCityName());
        Long parentProgramCategoryId = resolveParentProgramCategoryId(programSearchFunctionDto.getProgramCategory());
        String keyword = searchKeyword(programSearchFunctionDto);

        List<ProgramSearchVo> searchResult = searchByGateway(areaId, parentProgramCategoryId, keyword,
                programSearchFunctionDto.getShowTime());
        if (CollectionUtil.isNotEmpty(searchResult)) {
            return searchResult;
        }

        if (Objects.nonNull(areaId) && StringUtil.isNotEmpty(keyword)) {
            searchResult = searchByGateway(null, parentProgramCategoryId, keyword, programSearchFunctionDto.getShowTime());
            if (CollectionUtil.isNotEmpty(searchResult)) {
                return searchResult;
            }
        }

        if (Objects.nonNull(areaId)) {
            searchResult = searchByGateway(areaId, parentProgramCategoryId, null, programSearchFunctionDto.getShowTime());
            if (CollectionUtil.isNotEmpty(searchResult)) {
                return searchResult;
            }
        }

        if (StringUtil.isNotEmpty(programSearchFunctionDto.getCityName()) && StringUtil.isNotEmpty(keyword)) {
            searchResult = searchByGateway(null, parentProgramCategoryId,
                    programSearchFunctionDto.getCityName() + keyword, programSearchFunctionDto.getShowTime());
            if (CollectionUtil.isNotEmpty(searchResult)) {
                return searchResult;
            }
        }

        return searchByGateway(null, parentProgramCategoryId, keyword, programSearchFunctionDto.getShowTime());
    }

    public ProgramDetailResultVo detail(ProgramDetailDto programDetailDto) {
        String result = HttpRequest.post(PROGRAM_DETAIL_URL)
                .header("no_verify", "true")
                .body(JSON.toJSONString(programDetailDto))
                .timeout(20000)
                .execute().body();
        ProgramDetailResultVo programDetailResultVo = JSON.parseObject(result, ProgramDetailResultVo.class);
        if (!Objects.equals(programDetailResultVo.getCode(), BaseCode.SUCCESS.getCode())) {
            throw new RuntimeException("调用潮声系统查询节目失败");
        }
        sanitizeProgramDetail(programDetailResultVo.getData());
        return programDetailResultVo;
    }

    private List<ProgramSearchVo> searchByGateway(Long areaId, Long parentProgramCategoryId, String content,
                                                  Date showTime) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("pageNumber", PAGE_NUMBER);
        requestBody.put("pageSize", PAGE_SIZE);
        requestBody.put("timeType", ALL_TIME_TYPE);
        requestBody.put("type", 1);
        requestBody.put("areaId", areaId);
        requestBody.put("parentProgramCategoryId", parentProgramCategoryId);
        requestBody.put("content", content);
        if (Objects.nonNull(showTime)) {
            requestBody.put("startDateTime", showTime);
        }

        String result = HttpRequest.post(PROGRAM_SEARCH_URL)
                .header("no_verify", "true")
                .body(JSON.toJSONString(requestBody))
                .timeout(20000)
                .execute().body();
        JSONObject resultJson = JSON.parseObject(result);
        if (Objects.isNull(resultJson) || !Objects.equals(resultJson.getInteger("code"), BaseCode.SUCCESS.getCode())) {
            throw new RuntimeException("调用潮声系统搜索节目失败");
        }
        JSONObject dataJson = resultJson.getJSONObject("data");
        if (Objects.isNull(dataJson)) {
            return List.of();
        }
        JSONArray listJson = dataJson.getJSONArray("list");
        if (Objects.isNull(listJson)) {
            return List.of();
        }
        List<ProgramSearchVo> programSearchVoList = listJson.toJavaList(ProgramSearchVo.class);
        if (CollectionUtil.isEmpty(programSearchVoList)) {
            return List.of();
        }
        programSearchVoList.forEach(this::sanitizeProgramSearch);
        return new ArrayList<>(programSearchVoList);
    }

    private Long resolveParentProgramCategoryId(String programCategory) {
        if (StringUtil.isEmpty(programCategory)) {
            return null;
        }
        return PARENT_CATEGORY_MAP.entrySet().stream()
                .filter(entry -> programCategory.contains(entry.getKey()) || entry.getKey().contains(programCategory))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    private String searchKeyword(ProgramSearchFunctionDto programSearchFunctionDto) {
        if (StringUtil.isNotEmpty(programSearchFunctionDto.getKeyword())) {
            return programSearchFunctionDto.getKeyword();
        }
        if (StringUtil.isNotEmpty(programSearchFunctionDto.getActor())) {
            return programSearchFunctionDto.getActor();
        }
        return null;
    }

    private void sanitizeProgramSearch(ProgramSearchVo programSearchVo) {
        if (Objects.isNull(programSearchVo)) {
            return;
        }
        programSearchVo.setTitle(stripHighlight(programSearchVo.getTitle()));
        programSearchVo.setActor(stripHighlight(programSearchVo.getActor()));
        programSearchVo.setPlace(stripHighlight(programSearchVo.getPlace()));
        programSearchVo.setAreaName(stripHighlight(programSearchVo.getAreaName()));
        programSearchVo.setProgramCategoryName(stripHighlight(programSearchVo.getProgramCategoryName()));
        programSearchVo.setParentProgramCategoryName(stripHighlight(programSearchVo.getParentProgramCategoryName()));
    }

    private void sanitizeProgramDetail(org.javaup.ai.vo.ProgramDetailVo programDetailVo) {
        if (Objects.isNull(programDetailVo)) {
            return;
        }
        programDetailVo.setTitle(stripHighlight(programDetailVo.getTitle()));
        programDetailVo.setActor(stripHighlight(programDetailVo.getActor()));
        programDetailVo.setPlace(stripHighlight(programDetailVo.getPlace()));
        programDetailVo.setAreaName(stripHighlight(programDetailVo.getAreaName()));
        programDetailVo.setProgramCategoryName(stripHighlight(programDetailVo.getProgramCategoryName()));
        programDetailVo.setParentProgramCategoryName(stripHighlight(programDetailVo.getParentProgramCategoryName()));
    }

    private String stripHighlight(String value) {
        if (StringUtil.isEmpty(value)) {
            return value;
        }
        return value.replaceAll("<[^>]+>", "");
    }
}
