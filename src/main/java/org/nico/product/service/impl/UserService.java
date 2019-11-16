package org.nico.product.service.impl;

import org.nico.common.RedisRateLimit;
import org.nico.product.entity.Person;
import org.nico.product.mapper.UserMapper;
import org.nico.product.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    @RedisRateLimit(key ="UserService_getPerson:{0}",once = true,per = 1000)
    public Person getPerson(Integer id) {
        return userMapper.selectUser(id);
    }
}
