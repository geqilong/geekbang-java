package org.geektimes.projects.user.context;

import org.geektimes.function.ThrowableFunction;
import org.geektimes.projects.user.function.ThrowableAction;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.naming.*;
import javax.servlet.ServletContext;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class ComponentContext {
    private final Logger logger = Logger.getLogger(getClass().getName());
    public static final String CONTEXT_NAME = ComponentContext.class.getName();
    private static final String COMPONENT_ENV_CONTEXT_NAME = "java:comp/env";
    private static ServletContext servletContext;
    // 假设一个 Tomcat JVM 进程，三个 Web Apps，会不会相互冲突？（不会冲突）
    // static 字段是 JVM 缓存吗？（是 ClassLoader 缓存）

    //    private static ApplicationContext applicationContext;

    //    public void setApplicationContext(ApplicationContext applicationContext){
    //        ComponentContext.applicationContext = applicationContext;
    //        WebApplicationContextUtils.getRootWebApplicationContext()
    //    }

    private Context context;
    private ClassLoader classLoader;
    private Map<String, Object> componentsMap = new LinkedHashMap<>();

    public static ComponentContext getInstance() {
        return (ComponentContext) servletContext.getAttribute(CONTEXT_NAME);
    }

    public void init(ServletContext servletContext) throws RuntimeException {
        ComponentContext.servletContext = servletContext;
        servletContext.setAttribute(CONTEXT_NAME, this);
        this.classLoader = servletContext.getClassLoader();
        initEnvContext();
        instantiateComponents();
        initializeComponents();
    }

    /**
     * 实例化组件
     */
    private void initEnvContext() {
        if (this.context != null) {
            return;
        }
        Context context = null;
        try {
            context = new InitialContext();
            this.context = (Context) context.lookup(COMPONENT_ENV_CONTEXT_NAME);
        } catch (NamingException e) {
            throw new RuntimeException();
        } finally {
            close(context);
        }
    }

    /**
     * 实例化组件
     */
    private void instantiateComponents() {
        //便利获取所有组件名称
        List<String> componentNames = listAllComponentNames();
        // 通过依赖查找，实例化对象（ Tomcat BeanFactory setter 方法的执行，仅支持简单类型）
        componentNames.forEach(new Consumer<String>() {
            @Override
            public void accept(String name) {
                componentsMap.put(name, lookupComponent(name));
            }
        });
    }

    /**
     * 初始化组件（支持 Java 标准 Commons Annotation 生命周期）
     * <ol>
     *  <li>注入阶段 - {@link Resource}</li>
     *  <li>初始阶段 - {@link PostConstruct}</li>
     *  <li>销毁阶段 - {@link PreDestroy}</li>
     * </ol>
     */
    private void initializeComponents() {
        componentsMap.values().forEach(new Consumer<Object>() {
            @Override
            public void accept(Object component) {
                Class<?> componentClass = component.getClass();
                // 注入阶段 - {@link Resource}
                injectComponents(component, componentClass);
                // 初始阶段 - {@link PostConstruct}
                processPostConstruct(component, componentClass);
            }
        });
    }

    /**
     * 注入 @{link Resource}
     *
     * @param component
     * @param componentClass
     */
    private void injectComponents(Object component, Class<?> componentClass) {
        Stream.of(componentClass.getDeclaredFields()).filter(new Predicate<Field>() {
            @Override
            public boolean test(Field field) {
                int mods = field.getModifiers();
                return !Modifier.isStatic(mods) && field.isAnnotationPresent(Resource.class);
            }
        }).forEach(new Consumer<Field>() {
            @Override
            public void accept(Field field) {
                Resource resource = field.getAnnotation(Resource.class);
                String resourceName = resource.name();
                Object injectedObject = lookupComponent(resourceName);
                field.setAccessible(true);
                //注入目标对象
                try {
                    field.set(component, injectedObject);
                } catch (IllegalAccessException e) {
                }
            }
        });
    }

    private void processPostConstruct(Object component, Class<?> componentClass) {
        Stream.of(componentClass.getMethods()).filter(new Predicate<Method>() {
            @Override
            public boolean test(Method method) {
                //非static、无参、标注PostConstruct的方法
                return !Modifier.isStatic(method.getModifiers()) && method.getParameterCount() == 0 && method.isAnnotationPresent(PostConstruct.class);
            }
        }).forEach(new Consumer<Method>() {
            @Override
            public void accept(Method method) {
                try {
                    method.invoke(component);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void processPreDestroy(Object component, Class<?> componentClass) {
        Stream.of(componentClass.getMethods()).filter(method -> {
            //非static、无参、标注PreDestroy的方法
            return !Modifier.isStatic(method.getModifiers()) && method.getParameterCount() == 0 && method.isAnnotationPresent(PreDestroy.class);
        }).forEach(method -> {
            try {
                method.invoke(component);
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            }
        });
    }

    protected <C> C lookupComponent(String name) {
        return executeInContext(context -> (C) context.lookup(name));
    }

    private List<String> listAllComponentNames() {
        return listComponentNames("/");
    }

    private List<String> listComponentNames(String name) {
        return executeInContext(context -> {
            NamingEnumeration<NameClassPair> ne = executeInContext(context, ctx -> ctx.list(name), true);
            //目录 Context
            //节点
            if (ne == null) { //当前JNDI名称下没有子节点
                return Collections.emptyList();
            }
            List<String> fullNames = new LinkedList<>();
            while (ne.hasMoreElements()) {
                NameClassPair element = ne.nextElement();
                String className = element.getClassName();
                Class<?> targetClass = classLoader.loadClass(className);
                if (Context.class.isAssignableFrom(targetClass)) {
                    // 如果当前名称是目录（Context 实现类）的话，递归查找
                    fullNames.addAll(listComponentNames(element.getName()));
                } else {
                    // 否则，当前名称绑定目标类型的话，添加该名称到集合中
                    String fullName = name.startsWith("/") ? element.getName() :
                                    name + "/" + element.getName();//获取相对路径
                    fullNames.add(fullName);
                }
            }
            return fullNames;
        });
    }

    /**
     * 在 Context 中执行，通过指定 ThrowableFunction 返回计算结果
     *
     * @param function ThrowableFunction
     * @param <R>      返回结果类型
     * @return 返回
     * @see ThrowableFunction#apply(Object)
     */
    protected <R> R executeInContext(ThrowableFunction<Context, R> function) {
        return executeInContext(this.context, function, true);
    }

    /**
     * 在 Context 中执行，通过指定 ThrowableFunction 返回计算结果
     *
     * @param function         ThrowableFunction
     * @param ignoredException 是否忽略异常
     * @param <R>              返回结果类型
     * @return 返回
     * @see ThrowableFunction#apply(Object)
     */
    private <R> R executeInContext(Context context, ThrowableFunction<Context, R> function, boolean ignoredException) {
        R result = null;
        try {
            result = ThrowableFunction.execute(context, function);
        } catch (Exception e) {
            if (ignoredException) {
                logger.warning("Error in ComponentContext#executeInContext->executing function: " + function+ e.getMessage());
            } else {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    public <C> C getComponent(String name) {
        return (C) componentsMap.get(name);
    }

    public void destroy() throws RuntimeException {
        componentsMap.values().forEach(new Consumer<Object>() {
            @Override
            public void accept(Object component) {
                Class<?> componentClass = component.getClass();
                // 销毁阶段 - {@link PreDestroy}
                processPreDestroy(component, componentClass);
            }
        });
        if (this.context != null) {
            try {
                context.close();
            } catch (NamingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void close(Context context) {
        if (context != null) {
            ThrowableAction.execute(() -> context.close());
        }
    }
}
