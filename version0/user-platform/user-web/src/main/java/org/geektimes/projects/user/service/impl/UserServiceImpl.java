package org.geektimes.projects.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.geektimes.projects.user.domain.MessageResult;
import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.service.UserService;
import org.geektimes.projects.user.utils.UserHttpUtils;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.*;
import java.util.function.Consumer;

public class UserServiceImpl implements UserService {
    @Resource(name = "bean/EntityManager")
    private EntityManager entityManager;

    @Resource(name = "bean/Validator")
    private Validator validator;

    @Override
    public MessageResult register(User user) {
        MessageResult msgRes = new MessageResult();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (violations.size() == 0) {
            msgRes.setStatus(save(user));
        } else {
            msgRes.setStatus(false);
            msgRes.setMessage(extractErrorInfo(violations));
        }
        return msgRes;
    }

    private String extractErrorInfo(Set<ConstraintViolation<User>> violations) {
        StringBuilder sb = new StringBuilder();
        violations.forEach(new Consumer<ConstraintViolation<User>>() {
            @Override
            public void accept(ConstraintViolation<User> cv) {
                sb.append(cv.getPropertyPath() + "" + cv.getMessage() + ";");
            }
        });
        return sb.substring(0, sb.length() - 1);
    }

    //默认需要事务
    @Override
    public boolean save(User user) {
        // before process
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.persist(user);
        entityManager.flush();
        transaction.commit();
        // 调用其他方法方法
        //update(user); // 涉及事务
        // register 方法和 update 方法存在于同一线程
        // register 方法属于 Outer 事务（逻辑）
        // update 方法属于 Inner 事务（逻辑）
        // Case 1 : 两个方法均涉及事务（并且传播行为和隔离级别相同）
        // 两者共享一个物理事务，但存在两个逻辑事务
        // 利用 ThreadLocal 管理一个物理事务（Connection）

        // rollback 情况 1 : update 方法（Inner 事务），它无法主动去调用 rollback 方法
        // 设置 rollback only 状态，Inner TX(rollback only)，说明 update 方法可能存在执行异常或者触发了数据库约束
        // 当 Outer TX 接收到 Inner TX 状态，它来执行 rollback
        // A -> B -> C -> D -> E 方法调用链条
        // A (B,C,D,E) 内联这些方法，合成大方法
        // 关于物理事务是哪个方法创建
        // 其他调用链路事务传播行为是一致时，都是逻辑事务

        // Case 2: register 方法是 PROPAGATION_REQUIRED（事务创建者），update 方法 PROPAGATION_REQUIRES_NEW
        // 这种情况 update 方法也是事务创建者
        // update 方法 rollback-only 状态不会影响 Outer TX，Outer TX 和 Inner TX 是两个物理事务

        // Case 3: register 方法是 PROPAGATION_REQUIRED（事务创建者），update 方法 PROPAGATION_NESTED
        // 这种情况 update 方法同样共享了 register 方法物理事务，并且通过 Savepoint 来实现局部提交和回滚

        // after process
        // transaction.commit();
        return true;
    }

    @Override
    public boolean deregister(User user) {
        return false;
    }

    @Override
    public boolean update(User user) {
        return false;
    }

    @Override
    public User queryUserById(Long id) {
        return null;
    }

    @Override
    public User queryUserByNameAndPassword(String name, String password) {
        return null;
    }

    @Override
    public List<User> queryAllUsers() {
        List<User> userList;
        Query query = entityManager.createNativeQuery("select * from users");
        List list = query.getResultList();
        userList = new ArrayList<>(list.size());
        list.forEach(new Consumer() {
            @Override
            public void accept(Object obj) {
                Object[] objects = (Object[]) obj;
                User user = new User();
                user.setId(Long.valueOf((null != objects[0] ? objects[0].toString() : null)));
                user.setName((null != objects[1] ? objects[1].toString() : null));
                user.setPassword((null != objects[2] ? objects[2].toString() : null));
                user.setEmail((null != objects[3] ? objects[3].toString() : null));
                user.setPhoneNumber((null != objects[4] ? objects[4].toString() : null));
                userList.add(user);
            }
        });
        return userList;
    }

    @Override
    public Map queryGiteeUserInfo(String code, String clientId, String clientSecret, String redirectUrl) {
        String url = "https://gitee.com/oauth/token";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("grant_type", "authorization_code");
        paramMap.put("code", code);
        paramMap.put("client_id", clientId);
        paramMap.put("redirect_uri", redirectUrl);
        paramMap.put("client_secret", clientSecret);
        String result = UserHttpUtils.sendHttpPostRequest(url, paramMap);
        JSONObject jsonObject = JSON.parseObject(result);

        String accessToken = jsonObject.getString("access_token");
        url = "https://gitee.com/api/v5/user?access_token="+accessToken;
        result = UserHttpUtils.sendHttpGetRequest(url);
        jsonObject = JSON.parseObject(result);
        Map resMap = jsonObject.toJavaObject(Map.class);
        return resMap;
    }
}
