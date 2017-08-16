package com.bob.cock.job.utils;

import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.bob.cock.job.JobParameter;

public final class ParameterUtils {
    
    public static String toJson(JobParameter parameter) {
        return JSON.toJSONString(parameter.getAllParameters(), SerializerFeature.WriteNullStringAsEmpty);
    }
    
    public static JobParameter parse(String jsonText) {
        Map<String, String> mappedParameters = JSON.parseObject(jsonText, new TypeReference<Map<String, String>>(){});
        JobParameter parameter = new JobParameter();
        parameter.addAllParameters(mappedParameters);
        return parameter;
    }
    
    private ParameterUtils() {
    }
}
