package com.chuncongcong.framework.util;


import com.chuncongcong.framework.bean.Context;

public class ContextHolder {

    private static final ThreadLocal<Context> CONTEXT_HOLDER = InheritableThreadLocal.withInitial(Context::new);

    public static Context getContext() {
        return CONTEXT_HOLDER.get();
    }

    public static void clear() {
        CONTEXT_HOLDER.remove();
    }

    // 方便快捷访问方法
    public static Long getUserId() {
        return getContext().getUserId();
    }

    public static void setUserId(Long userId) {
        getContext().setUserId(userId);
    }
}

