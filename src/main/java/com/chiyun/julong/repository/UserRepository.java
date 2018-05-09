package com.chiyun.julong.repository;

import com.chiyun.julong.entity.UserEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface UserRepository extends CrudRepository<UserEntity, Long> {
    UserEntity findById(String id);

    UserEntity findByAccountAndPassword(String username, String password);

   @Query("select name,account,role,gender,jobtitle,idcard,birthdate,description from UserEntity")
    List<UserEntity> findAllUser();

   //使用原生sql通过id删除用户
    @Query(value = "delete from user where id=? ", nativeQuery = true)
    @Modifying
    @Transactional
    public int deleteOrderById(String id);
    //UserEntity delete(String id);
}
