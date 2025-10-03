package kr.sparta.backendbasic2.repository;

import kr.sparta.backendbasic2.entity.MenuOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuOptionRepository extends JpaRepository<MenuOption, Long> {
    boolean existsByRoundIdAndMenu(Long roundId, String menu);
    List<MenuOption> findByRoundId(Long roundId);
}