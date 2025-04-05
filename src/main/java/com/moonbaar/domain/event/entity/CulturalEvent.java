package com.moonbaar.domain.event.entity;

import com.moonbaar.domain.category.entity.Category;
import com.moonbaar.domain.district.entity.District;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cultural_events", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"title", "start_date"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CulturalEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String title;

    @Column
    private String place;

    @Column(name = "org_name")
    private String orgName;

    @Column(name = "use_trgt")
    private String useTarget;

    @Column(name = "use_fee")
    private String useFee;

    @Column(columnDefinition = "TEXT")
    private String player;

    @Column(columnDefinition = "TEXT")
    private String program;

    @Column(name = "etc_desc", columnDefinition = "TEXT")
    private String etcDesc;

    @Column(name = "org_link")
    private String orgLink;

    @Column(name = "main_img")
    private String mainImg;

    @Column
    private LocalDate rgstdate;

    @Column
    private String ticket;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column
    private String themecode;

    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(name = "is_free")
    private String isFree;

    @Column(name = "hmpg_addr")
    private String hmpgAddr;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id")
    private District district;

    @Column(name = "api_last_updated")
    private LocalDateTime apiLastUpdated;
}
