package com.otd.otd_admin.application.admin.Repository;

import com.otd.otd_user.entity.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminInquiryRepository extends JpaRepository<Inquiry, Long> {
}
