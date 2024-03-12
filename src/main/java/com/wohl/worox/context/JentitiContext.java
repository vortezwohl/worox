package com.wohl.worox.context;

import org.jentiti.context.EntityContext;

public class JentitiContext {
    private static EntityContext entityContext = new EntityContext();
    public static Object get(Class clazz) {
        return entityContext.get(clazz);
    }
    public static Object get(String name) {
        return entityContext.get(name);
    }
}
