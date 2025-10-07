package com.otd.otd_user.application.term;

import com.otd.otd_user.entity.Terms;
import com.otd.otd_user.entity.TermsType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TermsRepository extends JpaRepository<Terms, Long> {

    List<Terms> findByIsActiveTrueOrderByTypeAsc();

    Optional<Terms> findByTypeAndIsActiveTrue(TermsType type);

    List<Terms> findByTypeOrderByVersionDesc(TermsType type);

    List<Terms> findByIsActiveTrueAndIsRequiredTrue();
}