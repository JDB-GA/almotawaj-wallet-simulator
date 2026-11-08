package com.almotawaj.wallet.support;

import com.almotawaj.wallet.account.model.AccountHolder;
import com.almotawaj.wallet.config.constants.SecurityConstants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

public final class TestTokens {
    private TestTokens() {
    }

    public static String forAccount(AccountHolder account, String jwtSecret) {
        Date now = new Date();
        return Jwts.builder()
                .subject(account.getEmailAddress())
                .claim(SecurityConstants.USER_ID_CLAIM, account.getId().toString())
                .claim(SecurityConstants.AUTHORITIES_CLAIM, List.of(
                        SecurityConstants.ROLE_PREFIX + account.getRole().name(), SecurityConstants.EMAIL_VERIFIED_AUTHORITY))
                .issuedAt(now)
                .expiration(new Date(now.getTime() + 600_000))
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }
}
