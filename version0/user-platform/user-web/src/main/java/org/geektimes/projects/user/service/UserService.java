package org.geektimes.projects.user.service;

import org.geektimes.projects.user.domain.MessageResult;
import org.geektimes.projects.user.domain.User;

import java.util.List;
import java.util.Map;

/**
 * 用户服务
 */
public interface UserService {

    /**
     * 注册用户
     *
     * @param user
     * @return
     */
    MessageResult register(User user);

    /**
     * 保存用户
     *
     * @param user 用户对象
     * @return 成功返回<code>true</code>
     */
    boolean save(User user);

    /**
     * 注销用户
     *
     * @param user 用户对象
     * @return 成功返回<code>true</code>
     */
    boolean deregister(User user);

    /**
     * 更新用户信息
     *
     * @param user 用户对象
     * @return
     */
    boolean update(User user);

    User queryUserById(Long id);

    User queryUserByNameAndPassword(String name, String password);

    List<User> queryAllUsers();

    Map queryGiteeUserInfo(String code, String clientId, String clientSecre, String redirectUrl);

}
