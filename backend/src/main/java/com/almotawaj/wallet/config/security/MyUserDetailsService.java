package com.almotawaj.wallet.config.security;

import com.almotawaj.wallet.config.constants.ErrorMessages;
import com.almotawaj.wallet.user.model.User;
import com.almotawaj.wallet.user.repository.UserRepository;
import com.almotawaj.wallet.util.LoginIdentifier;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public MyUserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        return findByIdentifier(identifier)
                .map(MyUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException(ErrorMessages.USER_NOT_FOUND));
    }

    private Optional<User> findByIdentifier(String identifier) {
        return LoginIdentifier.isEmail(identifier)
                ? userRepository.findByEmailAddress(identifier)
                : userRepository.findByMobileNumber(identifier);
    }
}
