package kr.sparta.backendbasic2.serivce;

import kr.sparta.backendbasic2.entity.MenuOption;
import kr.sparta.backendbasic2.repository.LunchRoundRepository;
import kr.sparta.backendbasic2.repository.MenuOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuOptionService {

    private final MenuOptionRepository menuOptionRepository;
    private final LunchRoundRepository lunchRoundRepository;

    @Transactional
    public MenuOption addMenuOption(Long roundId, MenuOption menuOption) {
        if (menuOptionRepository.existsByRoundIdAndMenu(roundId, menuOption.getMenu())) {
            throw new RuntimeException("Duplicate menu option for round: " + roundId + " and menu: " + menuOption.getMenu());
        }
        var round = lunchRoundRepository.findById(roundId).orElseThrow(() -> new RuntimeException("Round not found"));
        menuOption.setRound(round);
        return menuOptionRepository.save(menuOption);
    }

    public List<MenuOption> getMenuOptionsByRoundId(Long roundId) {
        return menuOptionRepository.findByRoundId(roundId);
    }

    public List<MenuOption> getAllMenuOptions() {
        return menuOptionRepository.findAll();
    }

    public MenuOption getMenuOptionById(Long id) {
        return menuOptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("MenuOption not found with id: " + id));
    }

    @Transactional
    public MenuOption updateMenuOption(Long id, MenuOption menuOption) {
        MenuOption existing = getMenuOptionById(id);
        existing.setMenu(menuOption.getMenu());
        existing.setType(menuOption.getType());
        existing.setPrice(menuOption.getPrice());
        if (menuOption.getRound() != null) {
            existing.setRound(menuOption.getRound());
        }
        return menuOptionRepository.save(existing);
    }

    @Transactional
    public void deleteMenuOption(Long id) {
        if (!menuOptionRepository.existsById(id)) {
            throw new RuntimeException("MenuOption not found with id: " + id);
        }
        menuOptionRepository.deleteById(id);
    }
}
