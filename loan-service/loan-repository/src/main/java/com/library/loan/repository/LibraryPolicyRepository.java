package com.library.loan.repository;

import com.library.loan.repository.entity.LibraryPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LibraryPolicyRepository extends JpaRepository<LibraryPolicy, Integer> {

    Optional<LibraryPolicy> findByPolicyName(String policyName);

    boolean existsByPolicyName(String policyName);
}