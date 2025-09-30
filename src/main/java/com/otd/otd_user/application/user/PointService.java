package com.otd.otd_user.application.user;

import com.otd.otd_challenge.entity.ChallengePointHistory;
import com.otd.otd_user.application.user.model.PointHistoryDTO;
import com.otd.otd_user.application.user.model.PointHistoryResponseDTO;
import com.otd.otd_user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointService {

    private final UserChallengePointRepository userChallengePointRepository;
    private final UserRepository userRepository;

    public PointHistoryResponseDTO getPointHistory(Long userId) {
        log.info("===== 포인트 내역 조회 시작 =====");
        log.info("요청 userId: {}", userId);

        // 사용자 정보 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        log.info("사용자 조회 성공 - nickName: {}, point: {}", user.getNickName(), user.getPoint());

        // 포인트 내역 조회
        List<ChallengePointHistory> histories =
                userChallengePointRepository.findByUserUserIdOrderByCreatedAtDesc(userId);

        log.info("조회된 포인트 내역 개수: {}", histories.size());

        // 데이터가 있으면 첫 번째 항목 로그 출력
        if (!histories.isEmpty()) {
            ChallengePointHistory first = histories.get(0);
            log.info("첫 번째 내역 - chId: {}, reason: {}, point: {}, createdAt: {}",
                    first.getChId(), first.getReason(), first.getPoint(), first.getCreatedAt());
        } else {
            log.warn("포인트 내역이 비어있습니다!");
        }

        // DTO 변환
        List<PointHistoryDTO> historyDTOs = histories.stream()
                .map(history -> PointHistoryDTO.builder()
                        .chId(history.getChId())
                        .reason(history.getReason())
                        .point(history.getPoint())
                        .createdAt(history.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        log.info("DTO 변환 완료: {} 건", historyDTOs.size());
        log.info("===== 포인트 내역 조회 종료 =====");

        // 응답 생성
        return PointHistoryResponseDTO.builder()
                .user(PointHistoryResponseDTO.UserPointDTO.builder()
                        .nickName(user.getNickName())
                        .totalPoint(user.getPoint())
                        .build())
                .pointHistory(historyDTOs)
                .build();
    }
}