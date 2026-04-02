package com.team.docrate.domain.user.service;

import com.team.docrate.domain.user.dto.LoginRequestDto;
import com.team.docrate.domain.user.dto.LoginResponseDto;
import com.team.docrate.domain.user.dto.SignupRequestDto;
import com.team.docrate.domain.user.dto.SignupResponseDto;
import com.team.docrate.domain.user.dto.TokenReissueResponseDto;
import com.team.docrate.domain.user.entity.User;
import com.team.docrate.domain.user.repository.UserRepository;
import com.team.docrate.global.exception.DuplicateEmailException;
import com.team.docrate.global.exception.DuplicateNicknameException;
import com.team.docrate.global.exception.InvalidLoginException;
import com.team.docrate.global.exception.InvalidRefreshTokenException;
import com.team.docrate.global.exception.InvalidTokenException;
import com.team.docrate.global.exception.PasswordMismatchException;
import com.team.docrate.global.security.jwt.JwtTokenProvider;
import com.team.docrate.infra.redis.AccessTokenBlacklistService;
import com.team.docrate.infra.redis.RefreshTokenRedisService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRedisService refreshTokenRedisService;
    private final AccessTokenBlacklistService accessTokenBlacklistService;

    @Transactional
    public SignupResponseDto signup(SignupRequestDto requestDto) {
        validateSignupRequest(requestDto);

        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        User user = User.createUser(
                requestDto.getEmail(),
                encodedPassword,
                requestDto.getNickname()
        );

        User savedUser = userRepository.save(user);

        return SignupResponseDto.from(savedUser);
    }

    @Transactional
    public LoginResponseDto login(LoginRequestDto requestDto) {
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(InvalidLoginException::new);

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new InvalidLoginException();
        }

        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

        refreshTokenRedisService.saveRefreshToken(user.getEmail(), refreshToken);

        return LoginResponseDto.of(user, accessToken, refreshToken);
    }

    @Transactional
    public TokenReissueResponseDto reissueToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new InvalidRefreshTokenException("Refresh Token이 존재하지 않습니다.");
        }

        jwtTokenProvider.validateToken(refreshToken);

        String tokenType = jwtTokenProvider.getTokenType(refreshToken);
        if (!"refresh".equals(tokenType)) {
            throw new InvalidRefreshTokenException("Refresh Token이 아닙니다.");
        }

        String email = jwtTokenProvider.getEmail(refreshToken);

        String savedRefreshToken = refreshTokenRedisService.getRefreshToken(email);
        if (savedRefreshToken == null) {
            throw new InvalidRefreshTokenException("저장된 Refresh Token이 없습니다.");
        }

        if (!savedRefreshToken.equals(refreshToken)) {
            throw new InvalidRefreshTokenException("Refresh Token이 일치하지 않습니다.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidRefreshTokenException("사용자를 찾을 수 없습니다."));

        String newAccessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getRole());
        String newRefreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

        refreshTokenRedisService.saveRefreshToken(user.getEmail(), newRefreshToken);

        return TokenReissueResponseDto.of(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void logout(String accessToken, String refreshToken) {
        String email = null;

        // 1. Access Token이 정상적이면 블랙리스트 저장 + email 확보
        if (StringUtils.hasText(accessToken)) {
            try {
                jwtTokenProvider.validateToken(accessToken);

                String tokenType = jwtTokenProvider.getTokenType(accessToken);
                if ("access".equals(tokenType)) {
                    email = jwtTokenProvider.getEmail(accessToken);
                    long remainingTime = jwtTokenProvider.getRemainingTime(accessToken);

                    if (remainingTime > 0) {
                        accessTokenBlacklistService.blacklistAccessToken(accessToken, remainingTime);
                    }
                }
            } catch (Exception e) {
                System.out.println("로그아웃 중 Access Token 블랙리스트 처리 생략: " + e.getMessage());
            }
        }

        // 2. email이 없으면 Refresh Token 기준으로 email 확보
        if (!StringUtils.hasText(email) && StringUtils.hasText(refreshToken)) {
            try {
                jwtTokenProvider.validateToken(refreshToken);

                String tokenType = jwtTokenProvider.getTokenType(refreshToken);
                if ("refresh".equals(tokenType)) {
                    email = jwtTokenProvider.getEmail(refreshToken);
                }
            } catch (Exception e) {
                System.out.println("로그아웃 중 Refresh Token 이메일 추출 실패: " + e.getMessage());
            }
        }

        // 3. email을 찾았으면 Redis RT 삭제
        if (StringUtils.hasText(email)) {
            refreshTokenRedisService.deleteRefreshToken(email);
        }
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    private void validateSignupRequest(SignupRequestDto requestDto) {
        if (!requestDto.isPasswordMatched()) {
            throw new PasswordMismatchException();
        }

        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new DuplicateEmailException();
        }

        if (userRepository.existsByNickname(requestDto.getNickname())) {
            throw new DuplicateNicknameException();
        }
    }
}