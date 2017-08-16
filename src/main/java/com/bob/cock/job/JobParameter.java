package com.bob.cock.job;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JobParameter {
    
    private Map<String, String> parameters = new ConcurrentHashMap<>();
    
    public void addParameter(String name, String value) {
        this.parameters.put(name, value);
    }
    
    public void addAllParameters(Map<String, String> parameters) {
        this.parameters.putAll(parameters);
    }
    
    public String getParameter(String name) {
        return this.parameters.get(name);
    }
    
    public Map<String, String> getAllParameters() {
        return parameters;
    }
}
