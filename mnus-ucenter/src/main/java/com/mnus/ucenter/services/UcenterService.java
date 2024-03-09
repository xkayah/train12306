package com.mnus.ucenter.services;

import com.mnus.common.exception.BizException;
import com.mnus.common.utils.IdGenUtil;
import com.mnus.ucenter.domain.User;
import com.mnus.ucenter.domain.UserExample;
import com.mnus.ucenter.mapper.UserMapper;
import com.mnus.ucenter.req.UserRegistryReq;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author: <a href="https://github.com/xkayah">xkayah</a>
 * @date: 2024/3/8 16:58:29
 */
@Service
public class UcenterService {

    @Resource
    private UserMapper userMapper;

    public Long count() {
        return userMapper.countByExample(null);
    }

    public Long registry(UserRegistryReq req) {
        String mobile = req.getMobile();

        UserExample userExample = new UserExample();
        userExample.createCriteria().andMobileEqualTo(mobile);
        List<User> list = userMapper.selectByExample(userExample);

        // 登录注册做成同一个接口时，可以将数据库实体类的id返回出去做校验
        if (!CollectionUtils.isEmpty(list)) {
            // return list.get(0).getId();
            throw new BizException("手机号已注册");
        }

        long id = IdGenUtil.nextId();
        User user = new User();
        user.setId(IdGenUtil.nextId());
        user.setMobile(mobile);
        userMapper.insert(user);

        return id;
    }
}
