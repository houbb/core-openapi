package io.coreplatform.openapi.gateway;

public class RequestContextHolder {

    private static final ThreadLocal<RequestContext> CONTEXT = new ThreadLocal<>();

    public static void init(RequestContext context) {
        CONTEXT.set(context);
    }

    public static RequestContext current() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}