package br.gov.caixa.painelinvestimentos.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private static final String SECRET = "Ynl0ZWNvbmZpZ3VyYWNhbmFsbGRvYWphdmE==";

    @Test
    @DisplayName("Deve gerar token válido e extrair o usuário corretamente")
    void shouldGenerateValidToken() {
        JwtService service = new JwtService(SECRET, 60);
        UserDetails user = User.withUsername("admin").password("x").roles("USER").build();

        String token = service.generateToken(user.getUsername());

        assertEquals("admin", service.extractUsername(token));
        assertTrue(service.isTokenValid(token, user));
    }

    @Test
    @DisplayName("Tokens expirados devem ser considerados inválidos")
    void shouldDetectExpiredToken() throws InterruptedException {
        JwtService service = new JwtService(SECRET, 0);
        UserDetails user = User.withUsername("admin").password("x").roles("USER").build();

        String token = service.generateToken(user.getUsername());
        Thread.sleep(5); // garante que a data atual será posterior à expiração

        assertThrows(io.jsonwebtoken.ExpiredJwtException.class,
                () -> service.isTokenValid(token, user));
    }

    @Test
    @DisplayName("Token com usuário diferente deve ser considerado inválido")
    void shouldBeInvalidWhenUsernameDoesNotMatch() {
        JwtService service = new JwtService(SECRET, 60);
        UserDetails tokenOwner = User.withUsername("admin").password("x").roles("USER").build();
        UserDetails anotherUser = User.withUsername("outra").password("x").roles("USER").build();

        String token = service.generateToken(tokenOwner.getUsername());

        assertFalse(service.isTokenValid(token, anotherUser));
    }
}
