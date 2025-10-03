package kr.sparta.backendbasic2.repository;

import kr.sparta.backendbasic2.entity.LunchRound;
import kr.sparta.backendbasic2.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface LunchRoundRepository extends JpaRepository<LunchRound, Long> {
    List<LunchRound> findByTeamId(Long teamId);

    boolean existsByCreatorAndDate(User creator, LocalDate date);
}
