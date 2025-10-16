// java
package com.edu.tutor_platform.adminprofile.service;

import com.edu.tutor_platform.adminprofile.dto.HomePageDto;
import com.edu.tutor_platform.adminprofile.dto.paymentDto;
import com.edu.tutor_platform.adminprofile.dto.SessionOverviewDto;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class AdminService {

    public HomePageDto getHomePageData() {
        // numeric summary values
        long totalSessions = 24L;
        float sessionGainPercentage = 12.5f;
        long totalProfit = 820000L;
        float sessionProfitGainPercentage = 8.3f;
        long totalSubjects = 3L;
        float subjectGainPercentage = 4.7f;
        long totalUsers = 27L;
        float userGainPercentage = 3.2f;

        // monthly payments for last year (month number -> totalPayments)
        List<paymentDto> monthly = Arrays.asList(
                new paymentDto(1, 21000f),
                new paymentDto(2, 25000f),
                new paymentDto(3, 23000f),
                new paymentDto(4, 22000f),
                new paymentDto(5, 24000f),
                new paymentDto(6, 26000f),
                new paymentDto(7, 27000f),
                new paymentDto(8, 28000f),
                new paymentDto(9, 30000f),
                new paymentDto(10, 31000f),
                new paymentDto(11, 32000f),
                new paymentDto(12, 33000f)
        );

        // yearly payments (year -> totalPayments)
        List<paymentDto> yearly = Arrays.asList(
                new paymentDto(2022, 240000f),
                new paymentDto(2023, 280000f),
                new paymentDto(2024, 300000f)
        );

        // session overview - this week (day string, completed, cancelled)
        List<SessionOverviewDto> thisWeek = Arrays.asList(
                new SessionOverviewDto("Mon", 18L, 2L),
                new SessionOverviewDto("Tue", 16L, 2L),
                new SessionOverviewDto("Wed", 20L, 2L),
                new SessionOverviewDto("Thu", 23L, 2L),
                new SessionOverviewDto("Fri", 17L, 2L),
                new SessionOverviewDto("Sat", 20L, 1L),
                new SessionOverviewDto("Sun", 22L, 1L)
        );

        // session overview - last week
        List<SessionOverviewDto> lastWeek = Arrays.asList(
                new SessionOverviewDto("Mon", 15L, 2L),
                new SessionOverviewDto("Tue", 14L, 1L),
                new SessionOverviewDto("Wed", 18L, 1L),
                new SessionOverviewDto("Thu", 19L, 1L),
                new SessionOverviewDto("Fri", 16L, 1L),
                new SessionOverviewDto("Sat", 21L, 2L),
                new SessionOverviewDto("Sun", 20L, 1L)
        );

        // build and return DTO
        return HomePageDto.builder()
                .totalSessions(totalSessions)
                .sessionGainPercentage(sessionGainPercentage)
                .totalProfit(totalProfit)
                .sessionProfitGainPercentage(sessionProfitGainPercentage)
                .totalSubjects(totalSubjects)
                .subjectGainPercentage(subjectGainPercentage)
                .totalUsers(totalUsers)
                .userGainPercentage(userGainPercentage)
                .monthlyPaymentsForLastYear(monthly)
                .yearlyPayments(yearly)
                .sessionsThisWeek(thisWeek)
                .sessionsLastWeek(lastWeek)
                .build();
    }
}
