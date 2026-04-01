package com.team.docrate.domain.user.service;


import com.team.docrate.domain.user.dto.SignupRequestDto;
import com.team.docrate.domain.user.dto.SignupResponseDto;
import com.team.docrate.domain.user.entity.User;
import com.team.docrate.global.exception.DuplicateEmailException;
import com.team.docrate.global.exception.DuplicateNicknameException;
import com.team.docrate.global.exception.PasswordMismatchException;
import com.team.docrate.domain.user.repository.UserRepository;

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
