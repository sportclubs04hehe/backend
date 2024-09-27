package fpt.edu.vn.backend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import fpt.edu.vn.backend.auth.AuthenticationRequest;
import fpt.edu.vn.backend.auth.AuthenticationResponse;
import fpt.edu.vn.backend.auth.RegisterRequest;
import fpt.edu.vn.backend.repository.PhotoRepository;
import fpt.edu.vn.backend.token.Token;
import fpt.edu.vn.backend.repository.TokenRepository;
import fpt.edu.vn.backend.token.TokenType;
import fpt.edu.vn.backend.entity.User;
import fpt.edu.vn.backend.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;

import static fpt.edu.vn.backend.entity.Role.USER;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PhotoRepository photoRepository;

    public AuthenticationResponse register(RegisterRequest request) {
        var role = request.getRole() != null ? request.getRole() : USER;

        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .pass(passwordEncoder.encode(request.getPass()))
                .dateOfBirth(request.getDateOfBirth())
                .knowAs(request.getKnowAs())
                .gender(request.getGender())
                .dateCreated(LocalDateTime.now())
                .lastActive(LocalDateTime.now())
                .role(role)
                .build();

        var savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(savedUser, jwtToken);

        return AuthenticationResponse.builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .gender(request.getGender())
                .photoUrl(getPhotoUrl(savedUser))
                .jwt(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder()
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .gender(user.getGender())
                .photoUrl(getPhotoUrl(user))
                .jwt(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponse refreshToken(
            HttpServletRequest request
    ) throws IOException {
        final String authHeader = request.getHeader(AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid authorization header");
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.userRepository.findByEmail(userEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                return AuthenticationResponse.builder()
                        .email(user.getEmail())
                        .firstName(user.getFirstName())
                        .gender(user.getGender())
                        .photoUrl(getPhotoUrl(user))
                        .jwt(accessToken)
                        .refreshToken(refreshToken)
                        .build();
            }
        }
        throw new RuntimeException("Invalid refresh token");
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }


    private String getPhotoUrl(User user) {
        var photo = photoRepository.findByUser(user);
        return photo != null ? photo.getUrl() : "";
    }
}
