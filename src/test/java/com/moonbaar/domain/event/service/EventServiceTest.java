package com.moonbaar.domain.event.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.moonbaar.common.exception.BusinessException;
import com.moonbaar.domain.category.entity.Category;
import com.moonbaar.domain.district.entity.District;
import com.moonbaar.domain.event.dto.EventDetailResponse;
import com.moonbaar.domain.event.dto.EventUserStatusResponse;
import com.moonbaar.domain.event.entity.CulturalEvent;
import com.moonbaar.domain.event.exeption.EventErrorCode;
import com.moonbaar.domain.event.exeption.EventNotFoundException;
import com.moonbaar.domain.event.repository.CulturalEventRepository;
import com.moonbaar.domain.like.entity.LikedEvent;
import com.moonbaar.domain.like.repository.LikedEventRepository;
import com.moonbaar.domain.user.entity.User;
import com.moonbaar.domain.visit.entity.Visit;
import com.moonbaar.domain.visit.repository.VisitRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private CulturalEventRepository eventRepository;

    @Mock
    private VisitRepository visitRepository;

    @Mock
    private LikedEventRepository likedEventRepository;

    @Mock
    private EventProvider eventProvider;

    @InjectMocks
    private EventService eventService;

    private User testUser;
    private CulturalEvent testEvent;
    private LikedEvent testLikedEvent;
    private Visit testVisit;

    private final Long EVENT_ID = 100L;
    private final LocalDateTime NOW = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        // 테스트 사용자 설정
        testUser = User.builder()
                .oauthId("test-user")
                .oauthProvider("google")
                .nickname("테스트유저")
                .profileImageUrl("http://example.com/profile.jpg")
                .build();
        ReflectionTestUtils.setField(testUser, "id", 1L);
        ReflectionTestUtils.setField(testUser, "createdAt", NOW.minusDays(30));
        ReflectionTestUtils.setField(testUser, "updatedAt", NOW.minusDays(30));

        // 테스트 이벤트 설정
        Category category = Category.builder()
                .name("연극")
                .build();
        ReflectionTestUtils.setField(category, "id", 1L);

        District district = District.builder()
                .name("종로구")
                .build();
        ReflectionTestUtils.setField(district, "id", 1L);

        testEvent = CulturalEvent.builder()
                .title("서울시극단 [코믹]")
                .place("세종M씨어터")
                .orgName("세종문화회관")
                .useTarget("전체 관람가")
                .useFee("30,000원 ~ 50,000원")
                .player("김문화, 이예술, 박공연")
                .program("유쾌한 코미디 연극입니다. 일상 속 소소한 웃음을 선사합니다.")
                .etcDesc("서울시극단이 선보이는 유쾌한 코미디 연극입니다.")
                .orgLink("https://www.sejongpac.or.kr")
                .mainImg("https://example.com/image1.jpg")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(3))
                .isFree("유료")
                .latitude(new BigDecimal("37.5725"))
                .longitude(new BigDecimal("126.9760"))
                .category(category)
                .district(district)
                .build();
        ReflectionTestUtils.setField(testEvent, "id", EVENT_ID);
    }

    @Test
    @DisplayName("행사 ID로 상세 정보를 조회할 수 있다")
    void getEventDetail_ShouldReturnEventDetail() {
        // given
        when(eventProvider.getEventById(EVENT_ID)).thenReturn(testEvent);

        // when
        EventDetailResponse response = eventService.getEventDetail( EVENT_ID);

        // then
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(EVENT_ID);
        assertThat(response.title()).isEqualTo("서울시극단 [코믹]");
        assertThat(response.category()).isEqualTo("연극");
        assertThat(response.district()).isEqualTo("종로구");
        assertThat(response.isFree()).isFalse();
        assertThat(response.useFee()).isEqualTo("30,000원 ~ 50,000원");
        assertThat(response.place()).isEqualTo("세종M씨어터");
    }

    @Test
    @DisplayName("존재하지 않는 행사 ID로 조회 시 예외가 발생한다")
    void getEventDetail_WithNonExistingId_ShouldThrowException() {
        // given
        Long nonExistingId = 999L;
        when(eventProvider.getEventById(nonExistingId)).thenThrow(new EventNotFoundException());

        // when & then
        assertThatThrownBy(() -> eventService.getEventDetail(nonExistingId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", EventErrorCode.EVENT_NOT_FOUND);
    }

    @Test
    @DisplayName("유저와 이벤트 아이디로 이벤트 상태 정보를 조회할 수 있다")
    void getUserEventStatus_ReturnsCorrectStatus() {
        // given
        when(eventProvider.getEventById(EVENT_ID)).thenReturn(testEvent);
        when(visitRepository.existsByUserAndEvent(testUser, testEvent)).thenReturn(true);
        when(likedEventRepository.existsByUserAndEvent(testUser, testEvent)).thenReturn(true);

        // when
        EventUserStatusResponse response = eventService.getUserEventStatus(testUser, EVENT_ID);

        // then
        assertThat(response).isNotNull();
        assertThat(response.eventId()).isEqualTo(EVENT_ID);
        assertThat(response.isVisited()).isTrue();
        assertThat(response.isLiked()).isTrue();
    }

    @Test
    @DisplayName("좋아요와 방문을 하지 않은 이벤트는 false로 표시된다")
    void getUserEventStatus_WithNoInteractions_ReturnsFalse() {
        // given
        when(eventProvider.getEventById(EVENT_ID)).thenReturn(testEvent);
        when(visitRepository.existsByUserAndEvent(testUser, testEvent)).thenReturn(false);
        when(likedEventRepository.existsByUserAndEvent(testUser, testEvent)).thenReturn(false);

        // when
        EventUserStatusResponse response = eventService.getUserEventStatus(testUser, EVENT_ID);

        // then
        assertThat(response).isNotNull();
        assertThat(response.eventId()).isEqualTo(EVENT_ID);
        assertThat(response.isVisited()).isFalse();
        assertThat(response.isLiked()).isFalse();
    }

    @Test
    @DisplayName("방문만 한 이벤트는 isVisited만 true로 표시된다")
    void getUserEventStatus_WithOnlyVisit_ReturnsCorrectStatus() {
        // given
        when(eventProvider.getEventById(EVENT_ID)).thenReturn(testEvent);
        when(visitRepository.existsByUserAndEvent(testUser, testEvent)).thenReturn(true);
        when(likedEventRepository.existsByUserAndEvent(testUser, testEvent)).thenReturn(false);

        // when
        EventUserStatusResponse response = eventService.getUserEventStatus(testUser, EVENT_ID);

        // then
        assertThat(response).isNotNull();
        assertThat(response.isVisited()).isTrue();
        assertThat(response.isLiked()).isFalse();
    }

    @Test
    @DisplayName("좋아요만 한 이벤트는 isLiked만 true로 표시된다")
    void getUserEventStatus_WithOnlyLike_ReturnsCorrectStatus() {
        // given
        when(eventProvider.getEventById(EVENT_ID)).thenReturn(testEvent);
        when(visitRepository.existsByUserAndEvent(testUser, testEvent)).thenReturn(false);
        when(likedEventRepository.existsByUserAndEvent(testUser, testEvent)).thenReturn(true);

        // when
        EventUserStatusResponse response = eventService.getUserEventStatus(testUser, EVENT_ID);

        // then
        assertThat(response).isNotNull();
        assertThat(response.isVisited()).isFalse();
        assertThat(response.isLiked()).isTrue();
    }
}
