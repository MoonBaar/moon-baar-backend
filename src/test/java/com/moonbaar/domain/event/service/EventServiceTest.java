package com.moonbaar.domain.event.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.moonbaar.common.exception.BusinessException;
import com.moonbaar.domain.category.entity.Category;
import com.moonbaar.domain.district.entity.District;
import com.moonbaar.domain.event.dto.EventDetailResponse;
import com.moonbaar.domain.event.entity.CulturalEvent;
import com.moonbaar.domain.event.exeption.EventErrorCode;
import com.moonbaar.domain.event.repository.EventRepository;
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
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    private CulturalEvent sampleEvent;

    @BeforeEach
    void setUp() {
        Category category = Category.builder()
                .name("연극")
                .build();
        ReflectionTestUtils.setField(category, "id", 1L);

        District district = District.builder()
                .name("종로구")
                .build();
        ReflectionTestUtils.setField(district, "id", 1L);

        sampleEvent = CulturalEvent.builder()
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
        ReflectionTestUtils.setField(sampleEvent, "id", 1L);
    }

    @Test
    @DisplayName("행사 ID로 상세 정보를 조회할 수 있다")
    void getEventDetail_ShouldReturnEventDetail() {
        // given
        when(eventRepository.findById(1L)).thenReturn(Optional.of(sampleEvent));

        // when
        EventDetailResponse response = eventService.getEventDetail(1L);

        // then
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
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
        when(eventRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> eventService.getEventDetail(nonExistingId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", EventErrorCode.EVENT_NOT_FOUND);
    }
}
