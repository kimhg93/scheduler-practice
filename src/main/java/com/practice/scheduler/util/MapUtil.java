package com.practice.scheduler.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class MapUtil {

    public Map<String, Object> jsonToMap(String json) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> resultMap = objectMapper.readValue(json, HashMap.class);
        return resultMap;
    }

    public String mapToJson(Map<String, Object> map) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(map);
            return json;
        } catch (JsonProcessingException e){
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
