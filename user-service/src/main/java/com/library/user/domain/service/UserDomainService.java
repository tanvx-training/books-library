package com.library.user.domain.service;

import com.library.user.domain.exception.InvalidUserDataException;
import com.library.user.domain.exception.UserNotFoundException;
import com.library.user.domain.model.librarycard.CardNumber;
import com.library.user.domain.model.librarycard.LibraryCard;
import com.library.user.domain.model.user.*;
import com.library.user.domain.repository.LibraryCardRepository;
import com.library.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserDomainService {

    private final UserRepository userRepository;
    private final LibraryCardRepository libraryCardRepository;
    private final PasswordEncoder passwordEncoder;
    private static final int DEFAULT_CARD_VALIDITY_YEARS = 3;

    public User createUser(
            String username,
            String email,
            String password,
            String firstName,
            String lastName,
            String phone,
            Set<Role> roles) {

        Username usernameVO = Username.of(username);
        Email emailVO = Email.of(email);

        // Check if username already exists
        if (userRepository.existsByUsername(usernameVO)) {
            throw new InvalidUserDataException("username", "Username đã tồn tại: " + username);
        }

        // Check if email already exists
        if (userRepository.existsByEmail(emailVO)) {
            throw new InvalidUserDataException("email", "Email đã tồn tại: " + email);
        }

        // Hash password
        PasswordHash passwordHash = PasswordHash.of(passwordEncoder.encode(password));

        // Create value objects
        FirstName firstNameVO = firstName != null ? FirstName.of(firstName) : FirstName.of(null);
        LastName lastNameVO = lastName != null ? LastName.of(lastName) : LastName.of(null);
        Phone phoneVO = phone != null ? Phone.of(phone) : Phone.of(null);

        // Create user
        User user = User.create(
                usernameVO,
                emailVO,
                passwordHash,
                firstNameVO,
                lastNameVO,
                phoneVO,
                roles != null ? roles : new HashSet<>()
        );

        return userRepository.save(user);
    }

    public User updateUser(
            Long userId,
            String firstName,
            String lastName,
            String phone) {

        User user = userRepository.findById(UserId.of(userId))
                .orElseThrow(() -> new UserNotFoundException(userId));

        FirstName firstNameVO = firstName != null ? FirstName.of(firstName) : FirstName.of(null);
        LastName lastNameVO = lastName != null ? LastName.of(lastName) : LastName.of(null);
        Phone phoneVO = phone != null ? Phone.of(phone) : Phone.of(null);

        user.updateInformation(firstNameVO, lastNameVO, phoneVO);

        return userRepository.save(user);
    }

    public User updateEmail(Long userId, String email) {
        User user = userRepository.findById(UserId.of(userId))
                .orElseThrow(() -> new UserNotFoundException(userId));

        Email emailVO = Email.of(email);

        // Check if email already exists
        if (userRepository.existsByEmail(emailVO)) {
            throw new InvalidUserDataException("email", "Email đã tồn tại: " + email);
        }

        user.updateEmail(emailVO);

        return userRepository.save(user);
    }

    public User updatePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(UserId.of(userId))
                .orElseThrow(() -> new UserNotFoundException(userId));

        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword().getValue())) {
            throw new InvalidUserDataException("currentPassword", "Mật khẩu hiện tại không chính xác");
        }

        // Hash new password
        PasswordHash newPasswordHash = PasswordHash.of(passwordEncoder.encode(newPassword));

        user.updatePassword(newPasswordHash);

        return userRepository.save(user);
    }

    public User addRole(Long userId, String roleName) {
        User user = userRepository.findById(UserId.of(userId))
                .orElseThrow(() -> new UserNotFoundException(userId));

        Role role = Role.of(roleName);
        user.addRole(role);

        return userRepository.save(user);
    }

    public User removeRole(Long userId, String roleName) {
        User user = userRepository.findById(UserId.of(userId))
                .orElseThrow(() -> new UserNotFoundException(userId));

        Role role = Role.of(roleName);
        user.removeRole(role);

        return userRepository.save(user);
    }

    public LibraryCard createLibraryCardForUser(Long userId) {
        User user = userRepository.findById(UserId.of(userId))
                .orElseThrow(() -> new UserNotFoundException(userId));

        // Generate card number
        CardNumber cardNumber = CardNumber.generate();

        // Set issue date to today
        LocalDate issueDate = LocalDate.now();

        // Set expiry date to 3 years from now
        LocalDate expiryDate = issueDate.plusYears(DEFAULT_CARD_VALIDITY_YEARS);

        // Create library card
        LibraryCard libraryCard = LibraryCard.create(
                user.getId(),
                cardNumber,
                issueDate,
                expiryDate
        );

        return libraryCardRepository.save(libraryCard);
    }

    public LibraryCard renewLibraryCard(Long cardId) {
        LibraryCard libraryCard = libraryCardRepository.findById(com.library.user.domain.model.librarycard.LibraryCardId.of(cardId))
                .orElseThrow(() -> new InvalidUserDataException("cardId", "Library card not found with ID: " + cardId));

        // Set new expiry date to 3 years from now
        LocalDate newExpiryDate = LocalDate.now().plusYears(DEFAULT_CARD_VALIDITY_YEARS);

        libraryCard.renew(newExpiryDate);

        return libraryCardRepository.save(libraryCard);
    }

    public User getUserById(Long userId) {
        return userRepository.findById(UserId.of(userId))
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getAllUsers(int page, int size) {
        // This would typically be implemented in the repository
        // For now, we'll just return all and handle pagination in the application layer
        return userRepository.findAll();
    }
}