package com.almotawaj.wallet.config.security;

import com.almotawaj.wallet.user.model.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilsTest {
    private static final String SECRET = "test-secret-that-is-at-least-32-bytes-long!!";

    private final JwtUtils jwtUtils = new JwtUtils(SECRET, 60_000);

    private MyUserDetails userWithEmail(String email) {
        User user = new User();
        user.setEmailAddress(email);
        return new MyUserDetails(user);
    }

    @Test
    void generatedToken_containsTheUsersEmail() {
        String token = jwtUtils.generateToken(userWithEmail("test@mail.com"));

        assertThat(jwtUtils.parseToken(token).map(TokenClaims::username)).contains("test@mail.com");
    }

    @Test
    void tokenSignedWithAnotherKey_isRejected() {
        JwtUtils otherServer = new JwtUtils("a-completely-different-secret-of-32-bytes!!", 60_000);
        String token = otherServer.generateToken(userWithEmail("test@mail.com"));

        assertThat(jwtUtils.parseToken(token)).isEmpty();
    }

    @Test
    void expiredToken_isRejected() {
        JwtUtils alreadyExpired = new JwtUtils(SECRET, -1_000);
        String token = alreadyExpired.generateToken(userWithEmail("test@mail.com"));

        assertThat(jwtUtils.parseToken(token)).isEmpty();
    }

    @Test
    void garbageToken_isRejected() {
        assertThat(jwtUtils.parseToken("not-a-jwt")).isEmpty();
    }
}
