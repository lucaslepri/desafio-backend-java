package com.itau.desafio.vendas.infrastructure.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

/**
 * Implementação da interface AuditorAware do Spring Data para fornecer o
 * auditor
 * atual para fins de auditoria de entidades.
 * 
 * <p>
 * Esta classe recupera o usuário autenticado atual do contexto do Spring
 * Security
 * e o retorna como o auditor para a funcionalidade de trilha de auditoria. Ela
 * lida com
 * diferentes cenários de autenticação, incluindo usuários anônimos e vários
 * tipos
 * de principal.
 * </p>
 * 
 * <p>
 * A resolução do auditor segue esta prioridade:
 * </p>
 * <ul>
 * <li>Se não houver autenticação, não estiver autenticado ou for um usuário
 * anônimo:
 * retorna "system"</li>
 * <li>Se o principal for uma instância de UserDetails: retorna o nome de
 * usuário</li>
 * <li>Caso contrário: retorna a representação em string do principal</li>
 * </ul>
 * 
 * @see org.springframework.data.domain.AuditorAware
 * @see org.springframework.security.core.context.SecurityContextHolder
 */
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    @NonNull
    public Optional<String> getCurrentAuditor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()
                || auth.getPrincipal().equals("anonymousUser")) {
            return Optional.of("system");
        }

        Object p = auth.getPrincipal();

        if (p instanceof UserDetails) {
            return Optional.of(((UserDetails) p).getUsername());
        }

        return Optional.of(p.toString());
    }
}