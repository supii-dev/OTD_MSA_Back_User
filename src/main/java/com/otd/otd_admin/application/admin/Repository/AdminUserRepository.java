package com.otd.otd_admin.application.admin.Repository;

import com.otd.otd_admin.application.admin.model.GenderCountRes;
import com.otd.otd_user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AdminUserRepository extends JpaRepository<User, Integer> {
    @Query("SELECT new com.otd.otd_admin.application.admin.model.GenderCountRes(u.gender, COUNT(u)) " +
            "FROM User u GROUP BY u.gender")
    List<GenderCountRes> countUserByGender();
}
