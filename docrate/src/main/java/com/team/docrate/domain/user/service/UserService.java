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
import com.team.docrate.global.exception.PasswordMismatchException;
import com.team.docrate.global.security.jwt.JwtTokenProvider;
import com.team.docrate.infra.redis.RefreshTokenRedisService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRedisService refreshTokenRedisService;

    @Transactional
    public SignupResponseDto signup(SignupRequestDto requestDto) {
    	// 1. 회원가입 요청값 검증
        validateSignupRequest(requestDto);

        // 2. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        // 3. 회원 엔티티 생성
        User user = User.createUser(
                requestDto.getEmail(),
                encodedPassword,
                requestDto.getNickname()
        );

        // 4. DB 저장
        User savedUser = userRepository.save(user);
        
        // 5. 응답 DTO 반환
        return SignupResponseDto.from(savedUser);
    }

    @Transactional
    public LoginResponseDto login(LoginRequestDto requestDto) {
    	// 1. 이메일로 사용자 조회
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(InvalidLoginException::new);
        
        // 2. 비밀번호 검증 (평문 vs 암호화된 비밀번호 비교)
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new InvalidLoginException();
        }

        // 3. 로그인 성공 시 JWT 발급
        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

        // 4. Redis에 Refresh Token 저장
        refreshTokenRedisService.saveRefreshToken(user.getEmail(), refreshToken);

        // 5. 사용자 정보 + 토큰을 DTO로 반환
        return LoginResponseDto.of(user, accessToken, refreshToken);
    }
    
    @Transactional
    public TokenReissueResponseDto reissueToken(String refreshToken) {
    	//확인용
//    	System.out.println("=== reissueToken 시작 ===");
//        System.out.println("입력 refreshToken = " + refreshToken);
    	
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new InvalidRefreshTokenException("Refresh Token이 존재하지 않습니다.");
        }

        // 1. refresh token 자체 유효성 검증
        jwtTokenProvider.validateToken(refreshToken);
        

        // 2. refresh token 타입 검증
        String tokenType = jwtTokenProvider.getTokenType(refreshToken);
     // 확인용 
//        System.out.println("tokenType = " + tokenType);
        if (!"refresh".equals(tokenType)) {
            throw new InvalidRefreshTokenException("Refresh Token이 아닙니다.");
        }

        // 3. 토큰에서 사용자 이메일 추출
        String email = jwtTokenProvider.getEmail(refreshToken);
        // 확인용 
//        System.out.println("email = " + email);
        
        // 4. Redis 저장된 refresh token 조회
        String savedRefreshToken = refreshTokenRedisService.getRefreshToken(email);
        // 확인용
//        System.out.println("Redis savedRefreshToken = " + savedRefreshToken);
        
        if (savedRefreshToken == null) {
            throw new InvalidRefreshTokenException("저장된 Refresh Token이 없습니다.");
        }

        // 5. Redis에 저장된 token과 요청 token이 일치하는지 검증
        if (!savedRefreshToken.equals(refreshToken)) {
            throw new InvalidRefreshTokenException("Refresh Token이 일치하지 않습니다.");
        }

        // 6. 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidRefreshTokenException("사용자를 찾을 수 없습니다."));

        // 7. 새 Access Token 발급
        String newAccessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getRole());

        // 8. RT Rotation: 새 Refresh Token 발급
        String newRefreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());
        
        // 확인용
//        System.out.println("newAccessToken = " + newAccessToken);
//        System.out.println("newRefreshToken = " + newRefreshToken);

        // 9. Redis refresh token 교체
        refreshTokenRedisService.saveRefreshToken(user.getEmail(), newRefreshToken);

        return TokenReissueResponseDto.of(newAccessToken, newRefreshToken);
    }
    


    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    private void validateSignupRequest(SignupRequestDto requestDto) {
    	// 비밀번호와 비밀번호 확인 불일치
        if (!requestDto.isPasswordMatched()) {
            throw new PasswordMismatchException();
        }

        // 이메일 중복 검사
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new DuplicateEmailException();
        }

        // 닉네임 중복 검사
        if (userRepository.existsByNickname(requestDto.getNickname())) {
            throw new DuplicateNicknameException();
        }
    }
}