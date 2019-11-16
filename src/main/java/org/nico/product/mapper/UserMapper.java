package org.nico.product.mapper;

import org.apache.ibatis.annotations.Select;
import org.nico.product.entity.Person;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper {
    @Select("SELECT * FROM person WHERE id = #{id}")
    Person selectUser(int id);
}
