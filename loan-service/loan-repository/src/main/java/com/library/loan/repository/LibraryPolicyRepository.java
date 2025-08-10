package com.library.loan.repository;

import com.library.loan.repository.entity.LibraryPolicy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LibraryPolicyRepository extends JpaRepository<LibraryPolicy, Integer>, JpaSpecificationExecutor<LibraryPolicy> {

    Optional<LibraryPolicy> findByPolicyName(String policyName);

    boolean existsByPolicyName(String policyName);

    @Query("SELECT lp FROM LibraryPolicy lp WHERE " +
           "(:policyName IS NULL OR LOWER(lp.policyName) LIKE LOWER(CONCAT('%', :policyName, '%')))")
    Page<LibraryPolicy> findWithFilters(@Param("policyName") String policyName, Pageable pageable);

    void deleteByPolicyName(String policyName);
}