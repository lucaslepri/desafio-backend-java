package com.itau.desafio.vendas.infrastructure.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.itau.desafio.vendas.infrastructure.adapters.out.persistence.mongodb.MongoUserRepository;
import com.itau.desafio.vendas.infrastructure.adapters.out.persistence.mongodb.documents.UserDocument;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SecurityBeanConfig {

    private final MongoUserRepository userRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return u -> userRepository.findByUsername(u)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + u));
    }

    @Bean
    @SuppressWarnings("deprecation")
    public AuthenticationProvider authenticationProvider(
            PasswordEncoder p,
            UserDetailsService uds) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(uds);
        authProvider.setPasswordEncoder(p);
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration c) throws Exception {
        return c.getAuthenticationManager();
    }

    @Bean
    CommandLineRunner commandLineRunner(
            PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                userRepository.save(UserDocument.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin"))
                        .role(UserDocument.Role.ADMINISTRADOR)
                        .build());
                log.info("Usuário 'admin' criado com senha 'admin'.");
            }
            if (userRepository.findByUsername("analista").isEmpty()) {
                userRepository.save(UserDocument.builder()
                        .username("analista")
                        .password(passwordEncoder.encode("analista"))
                        .role(UserDocument.Role.ANALISTA_NEGOCIOS)
                        .build());
                log.info("Usuário 'analista' criado com senha 'analista'.");
            }
            if (userRepository.findByUsername("agente").isEmpty()) {
                userRepository.save(UserDocument.builder()
                        .username("agente")
                        .password(passwordEncoder.encode("agente"))
                        .role(UserDocument.Role.AGENTE_NEGOCIOS)
                        .build());
                log.info("Usuário 'agente' criado com senha 'agente'.");
            }
        };
    }
}
