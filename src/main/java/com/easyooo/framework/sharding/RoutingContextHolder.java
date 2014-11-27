package com.easyooo.framework.sharding;

/**
 * 路由上下文绑定在当前线程变量
 *
 * @author Killer
 */
public final class RoutingContextHolder {

	private static final ThreadLocal<RoutingContext> contextHolder = 
			new ThreadLocal<RoutingContext>();

    public static void setRoutingContext(RoutingContext context) {    
        contextHolder.set(context);    
    }    
        
    public static RoutingContext getRoutingContext() {    
        return contextHolder.get();    
    }    
        
    public static void clearRoutingContext() {    
        contextHolder.remove();    
    } 
}
