package kr.sparta.backendbasic2.repository;

import kr.sparta.backendbasic2.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {}