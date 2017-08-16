package com.bob.cock.job.utils;

import org.springframework.context.ApplicationContext;

public final class ContextUtils {

    private static volatile ApplicationContext ctx;
    
    public static void setApplicationContext(ApplicationContext context) {
        if (null == ctx) {
            ctx = context;
        }
    }
    
    public static Object getJobHandler(String handlerName) {
        return ctx.getBean(handlerName);
    }
}
