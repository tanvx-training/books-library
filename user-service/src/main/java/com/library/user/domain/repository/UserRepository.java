package com.library.user.domain.repository;

import com.library.user.domain.model.user.Email;
import com.library.user.domain.model.user.User;
import com.library.user.domain.model.user.UserId;
import com.library.user.domain.model.user.Username;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(UserId id);

    Optional<User> findByEmail(Email email);

    Optional<User> findByUsername(Username username);

    List<User> findAll();

    List<User> findAll(int page, int size);

    long count();

    void delete(User user);

    boolean existsByUsername(Username username);

    boolean existsByEmail(Email email);
}