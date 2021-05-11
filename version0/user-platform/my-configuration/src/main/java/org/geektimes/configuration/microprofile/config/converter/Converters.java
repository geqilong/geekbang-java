package org.geektimes.configuration.microprofile.config.converter;

import org.eclipse.microprofile.config.spi.Converter;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;


public class Converters implements Iterable<Converter> {
    public static final int DEFAULT_PRIORITY = 100;
    private final Map<Class<?>, PriorityQueue<PrioritizedConverter>> typedConverters = new HashMap<>();
    private ClassLoader classloader;
    private boolean addedDefaultConverters = false;
    private boolean addedDiscoveredConverters = false;

    public Converters() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public Converters(ClassLoader classloader) {
        this.classloader = classloader;
    }

    public void setClassLoader(ClassLoader classloader) {
        this.classloader = classloader;
    }

    public void addDefaultConverters() {
        if (addedDefaultConverters) {
            return;
        }
        addConverters(ServiceLoader.load(Converter.class, classloader));
        addedDefaultConverters = true;
    }

    public void addDiscoveredConverters() {
        if (addedDiscoveredConverters) {
            return;
        }
        addConverters(ServiceLoader.load(Converter.class, classloader));
        addedDiscoveredConverters = true;
    }

    private void addConverters(Iterable<Converter> converters) {
        converters.forEach(this::addConverter);
    }

    private void addConverter(Converter converter) {
        addConverter(converter, DEFAULT_PRIORITY);
    }

    public void addConverters(Class<? extends Converter>... converterClasses) {
        for (int i = 0; i < converterClasses.length; i++) {
            addConverter(newInstance(converterClasses[i]));
        }
    }

    public void addConverters(Converter... converters) {
        addConverters(Arrays.asList(converters));
    }

    public List<Converter> getConverters(Class<?> convertedType) {
        PriorityQueue<PrioritizedConverter> prioritizedConverters = typedConverters.get(convertedType);
        if (prioritizedConverters == null || prioritizedConverters.isEmpty()) {
            return Collections.emptyList();
        }
        List<Converter> converters = new LinkedList<>();
        for (PrioritizedConverter prioritizedConverter : prioritizedConverters) {
            converters.add(prioritizedConverter);
        }
        return converters;
    }

    public void addConverter(Converter converter, int priority) {
        Class<?> convertedType = resolveConvertedType(converter);
        addConverter(converter, priority, convertedType);
    }

    public void addConverter(Converter converter, int priority, Class<?> convertedType) {
        PriorityQueue priorityQueue = typedConverters.computeIfAbsent(convertedType, t -> new PriorityQueue<>());
        priorityQueue.offer(new PrioritizedConverter(converter, priority));
    }

    private Converter newInstance(Class<? extends Converter> convertClass) {
        Converter instance;
        try {
            instance = convertClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
        return instance;
    }

    public Class<?> resolveConvertedType(Converter<?> converter) {
        assertConverter(converter);
        Class<?> convertedType = null;
        Class<?> converterClass = converter.getClass();
        while (converterClass != null) {
            convertedType = resolveConvertedTypeByClass(converterClass);
            if (convertedType != null) {
                break;
            }

            Type superType = converterClass.getGenericSuperclass();
            if (superType instanceof ParameterizedType) {
                convertedType = resolveConvertedTypeByType(superType);
            }

            if (convertedType != null) {
                break;
            }
            // recursively
            converterClass = converterClass.getSuperclass();
        }
        return convertedType;
    }

    private Class<?> resolveConvertedTypeByClass(Class<?> converterClass) {
        Class<?> convertedType = null;
        for (Type superInterface : converterClass.getGenericInterfaces()) {
            convertedType = resolveConvertedTypeByType(superInterface);
            if (convertedType != null) {
                break;
            }
        }
        return convertedType;
    }

    private Class<?> resolveConvertedTypeByType(Type type) {
        Class<?> convertedType = null;
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            if (pType.getRawType() instanceof Class) {
                Class<?> rawType = (Class) pType.getRawType();
                if (Converter.class.isAssignableFrom(rawType)) {
                    Type[] arguments = pType.getActualTypeArguments();
                    if (arguments.length == 1 && arguments[0] instanceof Class) {
                        convertedType = (Class) arguments[0];
                    }
                }
            }
        }
        return convertedType;
    }

    private void assertConverter(Converter<?> converter) {
        Class<?> converterClass = converter.getClass();
        if (converterClass.isInterface()) {
            throw new IllegalArgumentException("The implementation class of Converter must not be an interface");
        }
        if (Modifier.isAbstract(converterClass.getModifiers())) {
            throw new IllegalArgumentException("The implementation class of Converter must not be abstract");
        }
    }

    @Override
    public Iterator<Converter> iterator() {
        List<Converter> allConverters = new LinkedList<>();
        for (PriorityQueue<PrioritizedConverter> converters : typedConverters.values()) {
            for (PrioritizedConverter converter : converters) {
                allConverters.add(converter.getConverter());
            }
        }
        return allConverters.iterator();
    }
}
