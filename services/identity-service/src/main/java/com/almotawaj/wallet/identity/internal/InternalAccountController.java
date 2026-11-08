package com.almotawaj.wallet.identity.internal;

import com.almotawaj.wallet.identity.user.repository.UserRepository;
import com.almotawaj.wallet.identity.user.service.WalletHolderNames;
import com.almotawaj.wallet.identity.util.LoginIdentifier;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Hidden
@RestController
@RequestMapping(InternalConstants.ACCOUNTS_PATH)
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InternalAccountController {
    private final UserRepository userRepository;
    private final WalletHolderNames names;

    @GetMapping("/{id}")
    public ResponseEntity<AccountSnapshot> byId(@PathVariable UUID id) {
        return ResponseEntity.of(userRepository.findById(id).map(user -> AccountSnapshot.from(user, names.fullName(user))));
    }

    @GetMapping
    public ResponseEntity<AccountSnapshot> lookup(@RequestParam String identifier) {
        String normalized = LoginIdentifier.normalize(identifier);
        var user = LoginIdentifier.isEmail(normalized)
                ? userRepository.findByEmailAddress(normalized)
                : userRepository.findByMobileNumber(normalized);
        return ResponseEntity.of(user.map(found -> AccountSnapshot.from(found, names.fullName(found))));
    }
}
