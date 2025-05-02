package com.moonbaar.domain.visit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.moonbaar.domain.event.entity.CulturalEvent;
import com.moonbaar.domain.event.service.EventProvider;
import com.moonbaar.domain.user.entity.User;
import com.moonbaar.domain.visit.dto.VisitRequest;
import com.moonbaar.domain.visit.dto.VisitResponse;
import com.moonbaar.domain.visit.entity.Visit;
import com.moonbaar.domain.visit.exception.EventNotActiveException;
import com.moonbaar.domain.visit.exception.InvalidLocationException;
import com.moonbaar.domain.visit.repository.VisitRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class VisitServiceTest {

    @Mock
    private EventProvider eventProvider;

    @Mock
    private VisitRepository visitRepository;

    @InjectMocks
    private VisitService visitService;

    private User user;
    private CulturalEvent event;
    private VisitRequest validRequest;

    @BeforeEach
    void setUp() {
        // 테스트용 사용자 생성
        user = User.builder()
                .oauthId("test-id")
                .oauthProvider("test-provider")
                .nickname("테스트 유저")
                .build();
        ReflectionTestUtils.setField(user, "id", 1L);

        // 테스트용 행사 생성 (현재 진행 중)
        LocalDateTime now = LocalDateTime.now();
        event = CulturalEvent.builder()
                .title("테스트 행사")
                .startDate(now.minusDays(1))
                .endDate(now.plusDays(1))
                .latitude(new BigDecimal("37.5665"))
                .longitude(new BigDecimal("126.9780"))
                .build();
        ReflectionTestUtils.setField(event, "id", 1L);

        // 유효한 방문 요청 (행사장 위치와 가까움)
        validRequest = new VisitRequest(
                new BigDecimal("37.5665"),
                new BigDecimal("126.9780")
        );

        // 설정값 주입
        ReflectionTestUtils.setField(visitService, "MAX_DISTANCE_KM", 0.5);
        ReflectionTestUtils.setField(visitService, "REVISIT_HOURS_INTERVAL", 24);
    }

    @Test
    @DisplayName("행사 방문 인증 성공")
    void visitEvent_Success() {
        // given
        when(eventProvider.getEventById(1L)).thenReturn(event);
        when(visitRepository.findTopByUserAndEventOrderByVisitedAtDesc(user, event))
                .thenReturn(Optional.empty());
        when(visitRepository.save(any(Visit.class))).thenAnswer(invocation -> {
            Visit visit = invocation.getArgument(0);
            ReflectionTestUtils.setField(visit, "id", 1L);
            ReflectionTestUtils.setField(visit, "visitedAt", LocalDateTime.now());
            return visit;
        });

        // when
        VisitResponse response = visitService.visitEvent(user, 1L, validRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.eventId()).isEqualTo(1L);
        assertThat(response.eventTitle()).isEqualTo("테스트 행사");
    }

    @Test
    @DisplayName("아직 시작하지 않은 행사 방문 인증 실패")
    void visitEvent_NotStartedYet_Fails() {
        // given
        LocalDateTime now = LocalDateTime.now();
        CulturalEvent futureEvent = CulturalEvent.builder()
                .title("미래 행사")
                .startDate(now.plusDays(1))
                .endDate(now.plusDays(3))
                .latitude(new BigDecimal("37.5665"))
                .longitude(new BigDecimal("126.9780"))
                .build();
        ReflectionTestUtils.setField(futureEvent, "id", 2L);

        when(eventProvider.getEventById(2L)).thenReturn(futureEvent);

        // when & then
        assertThatThrownBy(() -> visitService.visitEvent(user, 2L, validRequest))
                .isInstanceOf(EventNotActiveException.class);
    }

    @Test
    @DisplayName("이미 종료된 행사 방문 인증 실패")
    void visitEvent_AlreadyEnded_Fails() {
        // given
        LocalDateTime now = LocalDateTime.now();
        CulturalEvent pastEvent = CulturalEvent.builder()
                .title("지난 행사")
                .startDate(now.minusDays(3))
                .endDate(now.minusDays(1))
                .latitude(new BigDecimal("37.5665"))
                .longitude(new BigDecimal("126.9780"))
                .build();
        ReflectionTestUtils.setField(pastEvent, "id", 3L);

        when(eventProvider.getEventById(3L)).thenReturn(pastEvent);

        // when & then
        assertThatThrownBy(() -> visitService.visitEvent(user, 3L, validRequest))
                .isInstanceOf(EventNotActiveException.class);
    }

    @Test
    @DisplayName("위치가 너무 먼 경우 방문 인증 실패")
    void visitEvent_LocationTooFar_Fails() {
        // given
        when(eventProvider.getEventById(1L)).thenReturn(event);
        when(visitRepository.findTopByUserAndEventOrderByVisitedAtDesc(user, event))
                .thenReturn(Optional.empty());

        // 멀리 떨어진 위치의 요청 (예: 강남)
        VisitRequest farRequest = new VisitRequest(
                new BigDecimal("37.4979"),
                new BigDecimal("127.0276")
        );

        // when & then
        assertThatThrownBy(() -> visitService.visitEvent(user, 1L, farRequest))
                .isInstanceOf(InvalidLocationException.class);
    }
}
