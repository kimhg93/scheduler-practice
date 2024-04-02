package com.practice.scheduler.model;

import com.practice.scheduler.util.MapUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RequestDto {

    private Long id;
    private String classNm;
    private String jobId;
    private String cron;
    private String paramString;
    private String dupYn;
    private Object parameter;
    private String apiUrl;
    private String method;
    private String apiYn;
    private String callback;


    public String getDupYn() {
        return dupYn == null ? "N" : dupYn;
    }

    public String getApiYn() {
        return apiYn == null ? "N" : apiYn;
    }

    public Object getParameter() {
        String str = paramString;
        Map<String, Object> map = new HashMap<>();
        try {
            map = new MapUtil().jsonToMap(str);
        } catch (Exception e) {
            map.put("param", str);
        }
        return map;
    }

}
