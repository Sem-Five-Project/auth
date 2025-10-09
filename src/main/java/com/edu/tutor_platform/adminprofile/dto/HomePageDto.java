package com.edu.tutor_platform.adminprofile.dto;

import com.edu.tutor_platform.tutorprofile.dto.SubjectInfoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HomePageDto {
    private long totalSessions;
    private float sessionGainPercentage;
    private long totalProfit;
    private float sessionProfitGainPercentage;
    private long totalSubjects;
    private float subjectGainPercentage;
    private long totalUsers;
    private float userGainPercentage;
    //payment overview
    private List<paymentDto> monthlyPaymentsForLastYear;
    private List<paymentDto> yearlyPayments;
    //session overview
    private List<SessionOverviewDto> sessionsThisWeek;
    private List<SessionOverviewDto> sessionsLastWeek;
}
