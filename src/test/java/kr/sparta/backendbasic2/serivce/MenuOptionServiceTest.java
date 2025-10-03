package kr.sparta.backendbasic2.serivce;

import kr.sparta.backendbasic2.entity.LunchRound;
import kr.sparta.backendbasic2.entity.MenuOption;
import kr.sparta.backendbasic2.repository.LunchRoundRepository;
import kr.sparta.backendbasic2.repository.MenuOptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuOptionServiceTest {

    @Mock
    private MenuOptionRepository menuOptionRepository;

    @Mock
    private LunchRoundRepository lunchRoundRepository;

    @InjectMocks
    private MenuOptionService menuOptionService;

    private MenuOption testMenuOption;
    private LunchRound testLunchRound;

    @BeforeEach
    void setUp() {
        testLunchRound = new LunchRound();
        testLunchRound.setId(1L);
        testLunchRound.setDate(converToLocalDate("2024-01-01"));
        testLunchRound.setStatus("ACTIVE");

        testMenuOption = new MenuOption();
        testMenuOption.setId(1L);
        testMenuOption.setMenu("비빔밥");
        testMenuOption.setType("KOREAN");
        testMenuOption.setPrice(8000);
        testMenuOption.setRound(testLunchRound);
    }

    private LocalDate converToLocalDate(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(dateStr, formatter);
    }

    @Test
    @DisplayName("메뉴 옵션 추가 성공")
    void addMenuOption_Success() {
        // given
        Long roundId = 1L;
        MenuOption newMenuOption = new MenuOption();
        newMenuOption.setMenu("비빔밥");
        newMenuOption.setType("KOREAN");
        newMenuOption.setPrice(8000);

        when(menuOptionRepository.existsByRoundIdAndMenu(roundId, newMenuOption.getMenu())).thenReturn(false);
        when(lunchRoundRepository.findById(roundId)).thenReturn(Optional.of(testLunchRound));
        when(menuOptionRepository.save(any(MenuOption.class))).thenReturn(newMenuOption);

        // when
        MenuOption result = menuOptionService.addMenuOption(roundId, newMenuOption);

        // then
        assertNotNull(result);
        assertEquals(newMenuOption.getMenu(), result.getMenu());
        assertEquals(newMenuOption.getType(), result.getType());
        assertEquals(newMenuOption.getPrice(), result.getPrice());

        verify(menuOptionRepository).existsByRoundIdAndMenu(roundId, newMenuOption.getMenu());
        verify(lunchRoundRepository).findById(roundId);
        verify(menuOptionRepository).save(any(MenuOption.class));
    }

    @Test
    @DisplayName("메뉴 옵션 추가 실패 - 중복 메뉴")
    void addMenuOption_Failure_DuplicateMenu() {
        // given
        Long roundId = 1L;
        MenuOption duplicateMenuOption = new MenuOption();
        duplicateMenuOption.setMenu("비빔밥");
        duplicateMenuOption.setType("KOREAN");
        duplicateMenuOption.setPrice(8000);

        when(menuOptionRepository.existsByRoundIdAndMenu(roundId, duplicateMenuOption.getMenu())).thenReturn(true);

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            menuOptionService.addMenuOption(roundId, duplicateMenuOption);
        });

        assertTrue(exception.getMessage().contains("Duplicate menu option"));
        assertTrue(exception.getMessage().contains("round: " + roundId));
        assertTrue(exception.getMessage().contains("menu: " + duplicateMenuOption.getMenu()));

        verify(menuOptionRepository).existsByRoundIdAndMenu(roundId, duplicateMenuOption.getMenu());
        verify(lunchRoundRepository, never()).findById(anyLong());
        verify(menuOptionRepository, never()).save(any());
    }

    @Test
    @DisplayName("메뉴 옵션 추가 실패 - 존재하지 않는 라운드")
    void addMenuOption_Failure_RoundNotFound() {
        // given
        Long nonExistentRoundId = 999L;
        MenuOption newMenuOption = new MenuOption();
        newMenuOption.setMenu("테스트 메뉴");
        newMenuOption.setType("KOREAN");
        newMenuOption.setPrice(8000);

        when(menuOptionRepository.existsByRoundIdAndMenu(nonExistentRoundId, newMenuOption.getMenu())).thenReturn(false);
        when(lunchRoundRepository.findById(nonExistentRoundId)).thenReturn(Optional.empty());

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            menuOptionService.addMenuOption(nonExistentRoundId, newMenuOption);
        });

        assertTrue(exception.getMessage().contains("Round not found"));

        verify(menuOptionRepository).existsByRoundIdAndMenu(nonExistentRoundId, newMenuOption.getMenu());
        verify(lunchRoundRepository).findById(nonExistentRoundId);
        verify(menuOptionRepository, never()).save(any());
    }

    @Test
    @DisplayName("roundId로 메뉴 옵션 목록 조회 성공")
    void getMenuOptionsByRoundId_Success() {
        // given
        Long roundId = 1L;
        MenuOption menuOption2 = new MenuOption();
        menuOption2.setId(2L);
        menuOption2.setMenu("김치찌개");
        menuOption2.setType("KOREAN");
        menuOption2.setPrice(7000);

        List<MenuOption> expectedList = Arrays.asList(testMenuOption, menuOption2);
        when(menuOptionRepository.findByRoundId(roundId)).thenReturn(expectedList);

        // when
        List<MenuOption> result = menuOptionService.getMenuOptionsByRoundId(roundId);

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedList, result);

        verify(menuOptionRepository).findByRoundId(roundId);
    }

    @Test
    @DisplayName("ID로 메뉴 옵션 조회 성공")
    void getMenuOptionById_Success() {
        // given
        Long menuOptionId = 1L;
        when(menuOptionRepository.findById(menuOptionId)).thenReturn(Optional.of(testMenuOption));

        // when
        MenuOption result = menuOptionService.getMenuOptionById(menuOptionId);

        // then
        assertNotNull(result);
        assertEquals(testMenuOption.getId(), result.getId());
        assertEquals(testMenuOption.getMenu(), result.getMenu());

        verify(menuOptionRepository).findById(menuOptionId);
    }

    @Test
    @DisplayName("ID로 메뉴 옵션 조회 실패 - 존재하지 않는 ID")
    void getMenuOptionById_Failure_NotFound() {
        // given
        Long nonExistentId = 999L;
        when(menuOptionRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            menuOptionService.getMenuOptionById(nonExistentId);
        });

        assertTrue(exception.getMessage().contains("MenuOption not found with id: " + nonExistentId));

        verify(menuOptionRepository).findById(nonExistentId);
    }

    @Test
    @DisplayName("메뉴 옵션 업데이트 성공")
    void updateMenuOption_Success() {
        // given
        Long menuOptionId = 1L;
        MenuOption updateData = new MenuOption();
        updateData.setMenu("수정된 비빔밥");
        updateData.setType("FUSION");
        updateData.setPrice(9000);

        when(menuOptionRepository.findById(menuOptionId)).thenReturn(Optional.of(testMenuOption));
        when(menuOptionRepository.save(testMenuOption)).thenReturn(testMenuOption);

        // when
        MenuOption result = menuOptionService.updateMenuOption(menuOptionId, updateData);

        // then
        assertNotNull(result);
        assertEquals("수정된 비빔밥", testMenuOption.getMenu());
        assertEquals("FUSION", testMenuOption.getType());
        assertEquals(9000, testMenuOption.getPrice());

        verify(menuOptionRepository).findById(menuOptionId);
        verify(menuOptionRepository).save(testMenuOption);
    }

    @Test
    @DisplayName("메뉴 옵션 업데이트 실패 - 존재하지 않는 ID")
    void updateMenuOption_Failure_NotFound() {
        // given
        Long nonExistentId = 999L;
        MenuOption updateData = new MenuOption();
        updateData.setMenu("새로운 메뉴");

        when(menuOptionRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            menuOptionService.updateMenuOption(nonExistentId, updateData);
        });

        assertTrue(exception.getMessage().contains("MenuOption not found with id: " + nonExistentId));

        verify(menuOptionRepository).findById(nonExistentId);
        verify(menuOptionRepository, never()).save(any());
    }

    @Test
    @DisplayName("메뉴 옵션 삭제 성공")
    void deleteMenuOption_Success() {
        // given
        Long menuOptionId = 1L;
        when(menuOptionRepository.existsById(menuOptionId)).thenReturn(true);

        // when
        menuOptionService.deleteMenuOption(menuOptionId);

        // then
        verify(menuOptionRepository).existsById(menuOptionId);
        verify(menuOptionRepository).deleteById(menuOptionId);
    }

    @Test
    @DisplayName("메뉴 옵션 삭제 실패 - 존재하지 않는 ID")
    void deleteMenuOption_Failure_NotFound() {
        // given
        Long nonExistentId = 999L;
        when(menuOptionRepository.existsById(nonExistentId)).thenReturn(false);

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            menuOptionService.deleteMenuOption(nonExistentId);
        });

        assertTrue(exception.getMessage().contains("MenuOption not found with id: " + nonExistentId));

        verify(menuOptionRepository).existsById(nonExistentId);
        verify(menuOptionRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("같은 라운드에 동일한 메뉴명으로 여러 번 추가 시도 - 모두 실패")
    void addMenuOption_MultipleDuplicateAttempts_AllFail() {
        // given
        Long roundId = 1L;
        String duplicateMenuName = "중복메뉴";

        MenuOption firstAttempt = new MenuOption();
        firstAttempt.setMenu(duplicateMenuName);
        firstAttempt.setType("KOREAN");
        firstAttempt.setPrice(8000);

        MenuOption secondAttempt = new MenuOption();
        secondAttempt.setMenu(duplicateMenuName);
        secondAttempt.setType("WESTERN");
        secondAttempt.setPrice(9000);

        MenuOption thirdAttempt = new MenuOption();
        thirdAttempt.setMenu(duplicateMenuName);
        thirdAttempt.setType("CHINESE");
        thirdAttempt.setPrice(7000);

        // 첫 번째 추가는 성공
        when(menuOptionRepository.existsByRoundIdAndMenu(roundId, duplicateMenuName))
                .thenReturn(false)
                .thenReturn(true)
                .thenReturn(true);
        when(lunchRoundRepository.findById(roundId)).thenReturn(Optional.of(testLunchRound));
        when(menuOptionRepository.save(any(MenuOption.class))).thenReturn(firstAttempt);

        // when & then
        // 첫 번째 추가는 성공
        MenuOption firstResult = menuOptionService.addMenuOption(roundId, firstAttempt);
        assertNotNull(firstResult);

        // 두 번째 추가는 실패
        RuntimeException secondException = assertThrows(RuntimeException.class, () -> {
            menuOptionService.addMenuOption(roundId, secondAttempt);
        });
        assertTrue(secondException.getMessage().contains("Duplicate menu option"));
        assertTrue(secondException.getMessage().contains(duplicateMenuName));

        // 세 번째 추가도 실패
        RuntimeException thirdException = assertThrows(RuntimeException.class, () -> {
            menuOptionService.addMenuOption(roundId, thirdAttempt);
        });
        assertTrue(thirdException.getMessage().contains("Duplicate menu option"));
        assertTrue(thirdException.getMessage().contains(duplicateMenuName));

        // 검증
        verify(menuOptionRepository, times(3)).existsByRoundIdAndMenu(roundId, duplicateMenuName);
        verify(menuOptionRepository, times(1)).save(any()); // 첫 번째만 저장됨
    }

    @Test
    @DisplayName("같은 메뉴명이지만 다른 라운드에는 추가 가능")
    void addMenuOption_SameMenuDifferentRound_Success() {
        // given
        Long firstRoundId = 1L;
        Long secondRoundId = 2L;
        String sameMenuName = "공통메뉴";

        MenuOption firstRoundMenu = new MenuOption();
        firstRoundMenu.setMenu(sameMenuName);
        firstRoundMenu.setType("KOREAN");
        firstRoundMenu.setPrice(8000);

        MenuOption secondRoundMenu = new MenuOption();
        secondRoundMenu.setMenu(sameMenuName);
        secondRoundMenu.setType("WESTERN");
        secondRoundMenu.setPrice(9000);

        when(menuOptionRepository.existsByRoundIdAndMenu(firstRoundId, sameMenuName)).thenReturn(false);
        when(menuOptionRepository.existsByRoundIdAndMenu(secondRoundId, sameMenuName)).thenReturn(false);
        when(lunchRoundRepository.findById(firstRoundId)).thenReturn(Optional.of(testLunchRound));
        when(lunchRoundRepository.findById(secondRoundId)).thenReturn(Optional.of(testLunchRound));
        when(menuOptionRepository.save(any(MenuOption.class))).thenReturn(firstRoundMenu).thenReturn(secondRoundMenu);

        // when
        MenuOption firstResult = menuOptionService.addMenuOption(firstRoundId, firstRoundMenu);
        MenuOption secondResult = menuOptionService.addMenuOption(secondRoundId, secondRoundMenu);

        // then
        assertNotNull(firstResult);
        assertNotNull(secondResult);
        assertEquals(sameMenuName, firstResult.getMenu());
        assertEquals(sameMenuName, secondResult.getMenu());

        verify(menuOptionRepository).existsByRoundIdAndMenu(firstRoundId, sameMenuName);
        verify(menuOptionRepository).existsByRoundIdAndMenu(secondRoundId, sameMenuName);
        verify(menuOptionRepository, times(2)).save(any());
    }

}