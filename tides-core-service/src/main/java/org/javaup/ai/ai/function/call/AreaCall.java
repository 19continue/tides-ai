package org.javaup.ai.ai.function.call;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.javaup.ai.enums.BaseCode;
import org.javaup.ai.utils.StringUtil;
import org.javaup.ai.vo.AreaVo;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static org.javaup.ai.constants.TidesConstant.AREA_HOT_URL;
import static org.javaup.ai.constants.TidesConstant.AREA_MANAGE_LIST_URL;

@Component
public class AreaCall {

    private final Map<String, Long> areaCache = new ConcurrentHashMap<>(defaultAreaMap());

    public Long resolveAreaId(String cityName) {
        String normalizedCityName = normalizeAreaName(cityName);
        if (StringUtil.isEmpty(normalizedCityName)) {
            return null;
        }
        Long cachedAreaId = areaCache.get(normalizedCityName);
        if (Objects.nonNull(cachedAreaId)) {
            return cachedAreaId;
        }
        loadAreaList(AREA_HOT_URL);
        cachedAreaId = areaCache.get(normalizedCityName);
        if (Objects.nonNull(cachedAreaId)) {
            return cachedAreaId;
        }
        loadAreaList(AREA_MANAGE_LIST_URL);
        return areaCache.get(normalizedCityName);
    }

    private void loadAreaList(String url) {
        String result = HttpRequest.post(url)
                .header("no_verify", "true")
                .body("{}")
                .timeout(20000)
                .execute().body();
        JSONObject resultJson = JSON.parseObject(result);
        if (Objects.isNull(resultJson) || !Objects.equals(resultJson.getInteger("code"), BaseCode.SUCCESS.getCode())) {
            return;
        }
        JSONArray dataArray = resultJson.getJSONArray("data");
        if (Objects.isNull(dataArray)) {
            return;
        }
        List<AreaVo> areaVoList = dataArray.toJavaList(AreaVo.class);
        if (CollectionUtil.isEmpty(areaVoList)) {
            return;
        }
        for (final AreaVo areaVo : areaVoList) {
            if (Objects.nonNull(areaVo.getId()) && StringUtil.isNotEmpty(areaVo.getName())) {
                areaCache.putIfAbsent(normalizeAreaName(fixMojibake(areaVo.getName())), areaVo.getId());
            }
        }
    }

    private String normalizeAreaName(String areaName) {
        if (StringUtil.isEmpty(areaName)) {
            return "";
        }
        return areaName.trim()
                .replace("中国", "")
                .replace("市", "")
                .replace("省", "")
                .replace("特别行政区", "");
    }

    private String fixMojibake(String value) {
        if (StringUtil.isEmpty(value) || (!value.contains("å") && !value.contains("ä") && !value.contains("é"))) {
            return value;
        }
        return new String(value.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }

    private static Map<String, Long> defaultAreaMap() {
        Map<String, Long> areaMap = new HashMap<>();
        areaMap.put("全国", 1L);
        areaMap.put("北京", 2L);
        areaMap.put("上海", 25L);
        areaMap.put("天津", 27L);
        areaMap.put("重庆", 32L);
        areaMap.put("广州", 76L);
        areaMap.put("深圳", 77L);
        areaMap.put("成都", 322L);
        areaMap.put("杭州", 383L);
        areaMap.put("嘉兴", 385L);
        areaMap.put("香港", 33L);
        areaMap.put("澳门", 34L);
        return areaMap;
    }
}
