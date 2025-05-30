package lp.boble.aubos.security.apikey;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.exception.custom.apikey.CustomApiKeyGenerationException;
import lp.boble.aubos.exception.custom.apikey.CustomApiKeyValidationException;
import lp.boble.aubos.exception.custom.auth.CustomForbiddenActionException;
import lp.boble.aubos.exception.custom.global.CustomDeactivatedException;
import lp.boble.aubos.exception.custom.global.CustomNotFoundException;
import lp.boble.aubos.model.apikey.ApiKeyModel;
import lp.boble.aubos.model.user.UserModel;
import lp.boble.aubos.response.error.ErrorResponse;
import lp.boble.aubos.service.apikey.ApiKeyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SecurityApiKeyFilter extends OncePerRequestFilter {
    private final ApiKeyService apiKeyService;
    private final ObjectMapper objectMapper;


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try{
            String apiKey = recoverApiKey(request);

            if(apiKey == null){
                filterChain.doFilter(request, response);
                return;
            }

            ApiKeyModel key = apiKeyService.validateApiKey(apiKey);

            UserModel requester = key.getOwner();

            if(requester.getSoftDeleted()){
                throw CustomNotFoundException.key();
            }

            Authentication auth = new UsernamePasswordAuthenticationToken(
                    key.getOwner(),
                    null,
                    List.of(new SimpleGrantedAuthority("API_KEY_ACCESS"))
            );

            SecurityContextHolder.getContext().setAuthentication(auth);

            filterChain.doFilter(request, response);
        } catch (CustomNotFoundException e){ // Chave não encontrada ou não bateu os hash.
            handleException(response, HttpStatus.NOT_FOUND, e.getMessage(), request.getRequestURI());
        } catch (CustomApiKeyValidationException e){ // Limites de chave alcançados.
            handleException(response, HttpStatus.UNAUTHORIZED, e.getMessage(), request.getRequestURI());
        } catch (CustomApiKeyGenerationException e){ // Em caso de falha na geração do Hash de verificação.
            handleException(response, HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), request.getRequestURI());
        }


    }

    private String recoverApiKey(HttpServletRequest request) {
        String apikeyHeader = request.getHeader("X-API-Key");

        if(apikeyHeader == null || !apikeyHeader.startsWith("client_")){
            return null;
        }

        return apikeyHeader;
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
