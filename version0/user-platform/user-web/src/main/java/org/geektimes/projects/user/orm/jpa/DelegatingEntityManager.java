package org.geektimes.projects.user.orm.jpa;

import org.apache.commons.lang.StringUtils;
import org.geektimes.projects.context.ClassicComponentContext;

import javax.annotation.PostConstruct;
import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.metamodel.Metamodel;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;

/**
 * 委派实现（静态AOP实现）
 * 保持容器内单例
 * 目前实现 DelegatingEntityManager : EntityManager = 1:1
 * DelegatingEntityManager : EntityManager = 1:N
 */
public class DelegatingEntityManager implements EntityManager {

    private String persistenceUnitName;
    private String propertiesLocation;
//    private EntityManager entityManager; //方法委派 entityManager 实现
    private EntityManagerFactory entityManagerFactory;

    @PostConstruct
    public void init() {
        this.entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName, loadProperties(propertiesLocation));
    }

    protected EntityManager getEntityManager(){
        return entityManagerFactory.createEntityManager();
    }

    // 假设子类
//    protected protected EntityManager getEntityManager(){
//        return this.entityManager; // wrap 一个同步实现
//    }

    private Map loadProperties(String propertiesLocation) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL url = classLoader.getResource(propertiesLocation);
        Properties properties = new Properties();
        try {
            properties.load(url.openStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //增加 JNDI 引用处理   @xxx 属性值
        ClassicComponentContext classicComponentContext = ClassicComponentContext.getInstance();
        properties.stringPropertyNames().forEach(new Consumer<String>() {
            @Override
            public void accept(String propName) {
                String propValue = properties.getProperty(propName);
                if (StringUtils.isNotEmpty(propValue) && propValue.startsWith("@")) {
                    String componentName = propValue.substring(1);
                    Object component = classicComponentContext.getComponent(componentName);
                    properties.put(propName, component);
                }
            }
        });
        return properties;
    }

    // Setter 方法会被 Tomcat JNDI 实现调用

    /**
     * @param persistenceUnitName
     */
    public void setPersistenceUnitName(String persistenceUnitName) {
        this.persistenceUnitName = persistenceUnitName;
    }

    public void setPropertiesLocation(String propertiesLocation) {
        this.propertiesLocation = propertiesLocation;
    }

    @Override
    public void persist(Object o) {
        getEntityManager().persist(o);
    }

    @Override
    public <T> T merge(T t) {
        return getEntityManager().merge(t);
    }

    @Override
    public void remove(Object o) {
        getEntityManager().remove(o);
    }

    @Override
    public <T> T find(Class<T> aClass, Object o) {
        return getEntityManager().find(aClass, o);
    }

    @Override
    public <T> T find(Class<T> aClass, Object o, Map<String, Object> map) {
        return getEntityManager().find(aClass, o, map);
    }

    @Override
    public <T> T find(Class<T> aClass, Object o, LockModeType lockModeType) {
        return getEntityManager().find(aClass, o, lockModeType);
    }

    @Override
    public <T> T find(Class<T> aClass, Object o, LockModeType lockModeType, Map<String, Object> map) {
        return getEntityManager().find(aClass, o, lockModeType, map);
    }

    @Override
    public <T> T getReference(Class<T> aClass, Object o) {
        return getEntityManager().getReference(aClass, o);
    }

    @Override
    public void flush() {
        getEntityManager().flush();
    }

    @Override
    public void setFlushMode(FlushModeType flushModeType) {
        getEntityManager().setFlushMode(flushModeType);
    }

    @Override
    public FlushModeType getFlushMode() {
        return getEntityManager().getFlushMode();
    }

    @Override
    public void lock(Object o, LockModeType lockModeType) {
        getEntityManager().lock(o, lockModeType);
    }

    @Override
    public void lock(Object o, LockModeType lockModeType, Map<String, Object> map) {
        getEntityManager().lock(o, lockModeType, map);
    }

    @Override
    public void refresh(Object o) {
        getEntityManager().refresh(o);
    }

    @Override
    public void refresh(Object o, Map<String, Object> map) {
        getEntityManager().refresh(o, map);
    }

    @Override
    public void refresh(Object o, LockModeType lockModeType) {
        getEntityManager().refresh(o, lockModeType);
    }

    @Override
    public void refresh(Object o, LockModeType lockModeType, Map<String, Object> map) {
        getEntityManager().refresh(o, lockModeType, map);
    }

    @Override
    public void clear() {
        getEntityManager().clear();
    }

    @Override
    public void detach(Object o) {
        getEntityManager().detach(o);
    }

    @Override
    public boolean contains(Object o) {
        return getEntityManager().contains(o);
    }

    @Override
    public LockModeType getLockMode(Object o) {
        return getEntityManager().getLockMode(o);
    }

    @Override
    public void setProperty(String s, Object o) {
        getEntityManager().setProperty(s, o);
    }

    @Override
    public Map<String, Object> getProperties() {
        return getEntityManager().getProperties();
    }

    @Override
    public Query createQuery(String s) {
        return getEntityManager().createQuery(s);
    }

    @Override
    public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery) {
        return getEntityManager().createQuery(criteriaQuery);
    }

    @Override
    public Query createQuery(CriteriaUpdate criteriaUpdate) {
        return getEntityManager().createQuery(criteriaUpdate);
    }

    @Override
    public Query createQuery(CriteriaDelete criteriaDelete) {
        return getEntityManager().createQuery(criteriaDelete);
    }

    @Override
    public <T> TypedQuery<T> createQuery(String s, Class<T> aClass) {
        return getEntityManager().createQuery(s, aClass);
    }

    @Override
    public Query createNamedQuery(String s) {
        return getEntityManager().createNamedQuery(s);
    }

    @Override
    public <T> TypedQuery<T> createNamedQuery(String s, Class<T> aClass) {
        return getEntityManager().createNamedQuery(s, aClass);
    }

    @Override
    public Query createNativeQuery(String s) {
        return getEntityManager().createNativeQuery(s);
    }

    @Override
    public Query createNativeQuery(String s, Class aClass) {
        return getEntityManager().createNativeQuery(s, aClass);
    }

    @Override
    public Query createNativeQuery(String s, String s1) {
        return getEntityManager().createNativeQuery(s, s1);
    }

    @Override
    public StoredProcedureQuery createNamedStoredProcedureQuery(String s) {
        return getEntityManager().createNamedStoredProcedureQuery(s);
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(String s) {
        return getEntityManager().createStoredProcedureQuery(s);
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(String s, Class... classes) {
        return getEntityManager().createStoredProcedureQuery(s, classes);
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(String s, String... strings) {
        return getEntityManager().createStoredProcedureQuery(s, strings);
    }

    @Override
    public void joinTransaction() {
        getEntityManager().joinTransaction();
    }

    @Override
    public boolean isJoinedToTransaction() {
        return getEntityManager().isJoinedToTransaction();
    }

    @Override
    public <T> T unwrap(Class<T> aClass) {
        return getEntityManager().unwrap(aClass);
    }

    @Override
    public Object getDelegate() {
        return getEntityManager().getDelegate();
    }

    @Override
    public void close() {
        getEntityManager().close();
    }

    @Override
    public boolean isOpen() {
        return getEntityManager().isOpen();
    }

    @Override
    public EntityTransaction getTransaction() {
        return getEntityManager().getTransaction();
    }

    @Override
    public EntityManagerFactory getEntityManagerFactory() {
        return getEntityManager().getEntityManagerFactory();
    }

    @Override
    public CriteriaBuilder getCriteriaBuilder() {
        return getEntityManager().getCriteriaBuilder();
    }

    @Override
    public Metamodel getMetamodel() {
        return getEntityManager().getMetamodel();
    }

    @Override
    public <T> EntityGraph<T> createEntityGraph(Class<T> aClass) {
        return getEntityManager().createEntityGraph(aClass);
    }

    @Override
    public EntityGraph<?> createEntityGraph(String s) {
        return getEntityManager().createEntityGraph(s);
    }

    @Override
    public EntityGraph<?> getEntityGraph(String s) {
        return getEntityManager().getEntityGraph(s);
    }

    @Override
    public <T> List<EntityGraph<? super T>> getEntityGraphs(Class<T> aClass) {
        return getEntityManager().getEntityGraphs(aClass);
    }

}
