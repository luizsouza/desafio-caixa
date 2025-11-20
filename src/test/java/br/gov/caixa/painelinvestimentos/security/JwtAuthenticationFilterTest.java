package br.gov.caixa.painelinvestimentos.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.FilterChain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class JwtAuthenticationFilterTest {

    private final JwtService jwtService = mock(JwtService.class);
    private final UserDetailsService userDetailsService = mock(UserDetailsService.class);
    private final JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService, userDetailsService);

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Sem header Authorization deve pular autenticação")
    void shouldSkipWhenNoHeader() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/simulacoes");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsService);
    }

    @Test
    @DisplayName("Token inválido deve seguir sem autenticar")
    void shouldSkipWhenTokenInvalid() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/simulacoes");
        request.addHeader("Authorization", "Bearer invalido");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);
        when(jwtService.extractUsername("invalido")).thenThrow(new IllegalArgumentException("bad"));

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
    }

    @Test
    @DisplayName("Header sem prefixo Bearer deve ser ignorado")
    void shouldIgnoreHeaderWithoutBearer() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/simulacoes");
        request.addHeader("Authorization", "Token abc");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsService);
    }

    @Test
    @DisplayName("Username nulo não deve autenticar")
    void shouldNotAuthenticateWhenUsernameNull() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/simulacoes");
        request.addHeader("Authorization", "Bearer token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);
        when(jwtService.extractUsername("token")).thenReturn(null);

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        verifyNoInteractions(userDetailsService);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Não deve reautenticar se contexto já estiver preenchido")
    void shouldNotReauthenticateWhenContextAlreadySet() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/simulacoes");
        request.addHeader("Authorization", "Bearer token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        when(jwtService.extractUsername("token")).thenReturn("admin");
        UserDetails userDetails = User.withUsername("admin").password("x").roles("USER").build();
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);
        when(jwtService.isTokenValid("token", userDetails)).thenReturn(true);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        // userDetailsService não deve ser chamado novamente porque o contexto já estava autenticado
        verifyNoInteractions(userDetailsService);
    }

    @Test
    @DisplayName("Token válido deve popular o SecurityContext")
    void shouldAuthenticateWhenTokenValid() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/simulacoes");
        request.addHeader("Authorization", "Bearer token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        when(jwtService.extractUsername("token")).thenReturn("admin");
        UserDetails userDetails = User.withUsername("admin").password("x").roles("USER").build();
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);
        when(jwtService.isTokenValid("token", userDetails)).thenReturn(true);

        filter.doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isInstanceOf(UsernamePasswordAuthenticationToken.class);
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .isEqualTo(userDetails);
    }
}
