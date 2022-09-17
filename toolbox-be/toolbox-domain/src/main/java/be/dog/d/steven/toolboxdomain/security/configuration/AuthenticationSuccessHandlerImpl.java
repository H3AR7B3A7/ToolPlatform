package be.dog.d.steven.toolboxdomain.security.configuration;

import be.dog.d.steven.toolboxdomain.security.model.UserRegistrationRequest;
import be.dog.d.steven.toolboxdomain.security.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {

    private final UserService userRegistrationService;

    @Value("${spring.application.domain-address}")
    private String domainAddress;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        var oidcUser = (OidcUser) authentication.getPrincipal();

        var registrationRequest = UserRegistrationRequest.builder()
                .username(oidcUser.getSubject())
                .email(oidcUser.getEmail())
                .firstname(oidcUser.getGivenName())
                .lastname(oidcUser.getFamilyName())
                .build();

        this.userRegistrationService.registerNewUser(registrationRequest);
        log.info("User logged in: " + authentication.getName());
        response.sendRedirect(domainAddress);
    }
}