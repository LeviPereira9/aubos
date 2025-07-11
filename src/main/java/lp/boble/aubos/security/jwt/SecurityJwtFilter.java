package lp.boble.aubos.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.exception.custom.auth.CustomForbiddenActionException;
import lp.boble.aubos.exception.custom.global.CustomFieldNotProvided;
import lp.boble.aubos.exception.custom.global.CustomNotFoundException;
import lp.boble.aubos.model.user.UserModel;
import lp.boble.aubos.response.error.ErrorResponse;
import lp.boble.aubos.service.jwt.AuthorizationService;
import lp.boble.aubos.service.jwt.TokenPayload;
import lp.boble.aubos.service.jwt.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SecurityJwtFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final AuthorizationService authorizationService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            // Caso já tenha autenticado pela api key
            if(SecurityContextHolder.getContext().getAuthentication() != null) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = this.recoverToken(request);

            if(token == null) {
                filterChain.doFilter(request, response);
                return;
            }

            TokenPayload tokenPayload = tokenService.validateToken(token);

            UserModel userDetails = authorizationService.loadUserByUsername(tokenPayload.getSubject());

            UUID payloadToken;
            try { // Ver se chegou o ID
                payloadToken = UUID.fromString(tokenPayload.getToken());
            } catch (IllegalArgumentException e) {
                filterChain.doFilter(request, response);
                return;
            }

            if(!userDetails.getTokenId().equals(payloadToken)) {
                filterChain.doFilter(request, response);
                return;
            }


            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (CustomNotFoundException e) { // Não achou o usuário pelo token
            handleException(response, HttpStatus.FORBIDDEN, e.getMessage(), request.getRequestURI());
        } catch (CustomFieldNotProvided e){ // Não forneceu login - Impossível aqui, mas vai que né.
            handleException(response, HttpStatus.BAD_REQUEST, e.getMessage(), request.getRequestURI());
        } catch (CustomForbiddenActionException e){ // Não forneceu token
            handleException(response, HttpStatus.UNAUTHORIZED, e.getMessage(), request.getRequestURI());
        }


    }

    private String recoverToken(HttpServletRequest request){
        String auth = request.getHeader("Authorization");

        if (auth == null  || !auth.startsWith("Bearer ")) {
            return null;
        }

        return auth.replace("Bearer ", "");
    }

    private void handleException(
            HttpServletResponse response,
            HttpStatus status,
            String message,
            String path) throws IOException {
            ErrorResponse error = new ErrorResponse(message, status.value(), path);

            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(status.value());
            response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}
