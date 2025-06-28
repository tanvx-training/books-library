package com.library.book.utils.audit;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        // Trả về "SYSTEM" làm giá trị mặc định
        return Optional.of("SYSTEM");
    }
}