package com.team.docrate.global.config;

import com.team.docrate.domain.request.hospitalrequest.entity.HospitalRequest;
import com.team.docrate.domain.request.hospitalrequest.enumtype.HospitalRequestStatus;
import com.team.docrate.domain.request.hospitalrequest.repository.HospitalRequestRepository;
import com.team.docrate.domain.user.entity.User;
import com.team.docrate.domain.user.enumtype.UserRole;
import com.team.docrate.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@RequiredArgsConstructor
@Profile("dev") 
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final HospitalRequestRepository requestRepository;

    @Override
    public void run(String... args) throws Exception {
    	String adminEmail = "admin@docrate.com";
        User admin = userRepository.findByEmail(adminEmail)
                .orElseGet(() -> userRepository.save(User.builder()
                        .email(adminEmail)
                        .password("1234")
                        .nickname("관리자")
                        .role(UserRole.ADMIN)
                        .build()));

        // 2. 병원 등록 요청 데이터 생성 (중복 방지를 위해 count 체크)
        if (requestRepository.count() == 0) {
            // [케이스 1] 대기 중인 요청 (가장 먼저 처리해야 할 데이터)
            requestRepository.save(HospitalRequest.builder()
                    .requester(admin)
                    .name("서울중앙내과")
                    .address("서울시 강남구 테헤란로 101")
                    .phone("02-123-4567")
                    .category("내과")
                    .status(HospitalRequestStatus.PENDING)
                    .build());

            // [케이스 2] 대기 중인 요청 (다른 카테고리)
            requestRepository.save(HospitalRequest.builder()
                    .requester(admin)
                    .name("튼튼소아청소년과")
                    .address("경기도 성남시 분당구 판교로 202")
                    .phone("031-987-6543")
                    .category("소아과")
                    .status(HospitalRequestStatus.PENDING)
                    .build());

            // [케이스 3] 이미 승인된 요청 (목록에서 승인 버튼이 안 보여야 함)
            requestRepository.save(HospitalRequest.builder()
                    .requester(admin)
                    .name("바른정형외과")
                    .address("부산광역시 해운대구 우동 303")
                    .phone("051-111-2222")
                    .category("정형외과")
                    .status(HospitalRequestStatus.APPROVED)
                    .build());

            // [케이스 4] 거절된 요청 (거절 사유 확인용)
            requestRepository.save(HospitalRequest.builder()
                    .requester(admin)
                    .name("가짜병원")
                    .address("주소 불분명")
                    .phone("010-0000-0000")
                    .category("기타")
                    .status(HospitalRequestStatus.REJECTED)
                    .build());

            System.out.println(">>> [Success] 4개의 병원 등록 요청 테스트 데이터가 생성되었습니다.");
//        // 1. 테스트용 유저(신청자) 생성 - 엔티티 구조에 맞게 수정
//        User tester = User.builder()
//                .email("tester@docrate.com")
//                .password("1234") // 실제 로그인 기능 구현 시에는 BCryptPasswordEncoder 등으로 암호화 필요
//                .nickname("병원지기")
//                .role(UserRole.USER) // 혹은 필요에 따라 ADMIN
//                .build();
//        
//        userRepository.save(tester);
//
//        // 2. 병원 등록 요청 데이터 생성 (PENDING 상태)
//        HospitalRequest req1 = HospitalRequest.builder()
//                .requester(tester)
//                .name("서울중앙병원")
//                .address("서울시 강남구 테헤란로 123")
//                .phone("02-123-4567")
//                .category("내과")
//                .status(HospitalRequestStatus.PENDING)
//                .build();
//
//        HospitalRequest req2 = HospitalRequest.builder()
//                .requester(tester)
//                .name("튼튼치과의원")
//                .address("경기도 성남시 분당구 456")
//                .phone("031-987-6543")
//                .category("치과")
//                .status(HospitalRequestStatus.PENDING)
//                .build();
//
//        requestRepository.save(req1);
//        requestRepository.save(req2);
//
//        System.out.println(">>> [docrate] 테스트 데이터 삽입 완료: 유저 1명, 병원 요청 2건");
        }
    }
}