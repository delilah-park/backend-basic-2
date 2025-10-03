package kr.sparta.backendbasic2.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record LunchRoundRequest (

        @NotNull(message = "날짜는 필수입니다")
        @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "날짜 형식은 yyyy-MM-dd 이어야 합니다")
        String date,

        String status,
        Long teamId

){}