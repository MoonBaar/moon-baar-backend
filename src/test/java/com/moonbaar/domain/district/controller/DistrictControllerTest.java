package com.moonbaar.domain.district.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.moonbaar.common.config.SecurityTestConfig;
import com.moonbaar.common.exception.BusinessException;
import com.moonbaar.common.security.WithMockCustomUser;
import com.moonbaar.domain.district.dto.DistrictListResponse;
import com.moonbaar.domain.district.dto.DistrictResponse;
import com.moonbaar.domain.district.exception.DistrictErrorCode;
import com.moonbaar.domain.district.service.DistrictService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DistrictController.class)
@Import(SecurityTestConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockCustomUser
public class DistrictControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DistrictService districtService;

    @Test
    void getDistricts_shouldReturnDistrictList() throws Exception {
        // Given
        DistrictResponse district1 = new DistrictResponse(1L, "강남구");
        DistrictResponse district2 = new DistrictResponse(2L, "종로구");
        DistrictListResponse responseDTO = new DistrictListResponse(List.of(district1, district2));

        when(districtService.getAllDistricts()).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(get("/districts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getDistrictByExactName_shouldReturnSingleDistrict() throws Exception {
        // Given
        DistrictResponse district = new DistrictResponse(1L, "강남구");

        when(districtService.getDistrictByName("강남구")).thenReturn(district);

        // When & Then
        mockMvc.perform(get("/districts/by-name/강남구")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getDistrictByExactName_shouldReturn404WhenNotFound() throws Exception {
        // Given
        when(districtService.getDistrictByName(anyString()))
                .thenThrow(new BusinessException(DistrictErrorCode.DISTRICT_NOT_FOUND));

        // When & Then
        mockMvc.perform(get("/districts/by-name/존재하지않는행정구역")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
