package org.geektimes.projects.user.orm.jpa;

import org.apache.derby.jdbc.EmbeddedDataSource;
import org.geektimes.projects.user.domain.User;

import javax.annotation.Resource;
import javax.persistence.*;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class JPADemo {
    @PersistenceContext(name = "emf")
    private EntityManager entityManager;

    @Resource(name = "primaryDataSource")
    private DataSource dataSource;

    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("emf", getProperties());
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        User user = new User();
        user.setName("gql");
        user.setPassword("000000");
        user.setEmail("aaaaaa@qq.com");
        user.setPhoneNumber("12344345454243");
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.persist(user);
        transaction.commit();
        System.out.println(entityManager.find(User.class, 20L));
    }

    private static Map getProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", "org.hibernate.dialect.DerbyDialect");
        properties.put("hibernate.id.new_generator_mappings", false);
        properties.put("hibernate.connection.datasource", getDataSource());
        return properties;
    }

    private static Object getDataSource() {
        EmbeddedDataSource dataSource = new EmbeddedDataSource();
        dataSource.setDatabaseName("/db/user-platform");
        dataSource.setCreateDatabase("create");
        return dataSource;
    }
}
