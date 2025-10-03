package kr.sparta.backendbasic2.serivce;

import kr.sparta.backendbasic2.entity.LunchRound;
import kr.sparta.backendbasic2.entity.Team;
import kr.sparta.backendbasic2.entity.User;
import kr.sparta.backendbasic2.repository.LunchRoundRepository;
import kr.sparta.backendbasic2.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LunchRoundServiceTest {

    @Mock
    private LunchRoundRepository lunchRoundRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LunchRoundService lunchRoundService;

    private LunchRound testLunchRound;
    private User testUser;
    private Team testTeam;

    @BeforeEach
    void setUp() {
        testTeam = new Team();
        testTeam.setId(1L);
        testTeam.setName("Test Team");

        testUser = new User();
        testUser.setId(1L);
        testUser.setUserId("testuser");
        testUser.setName("Test User");
        testUser.setRole("USER");
        testUser.setTeam(testTeam);

        testLunchRound = new LunchRound();
        testLunchRound.setId(1L);
        testLunchRound.setDate(LocalDate.of(2025, 9, 20));
        testLunchRound.setStatus("PLANNING");
        testLunchRound.setCreator(testUser);
        testLunchRound.setTeam(testTeam);
    }

    @Test
    @DisplayName("점심 라운드 생성 실패 - 같은 날짜에 이미 생성된 라운드 존재")
    void createLunchRound_Failure_DuplicateRoundForSameDay() {
        // given
        String userId = "testuser";
        LocalDate sameDate = LocalDate.of(2025, 9, 20);

        LunchRound newLunchRound = new LunchRound();
        newLunchRound.setDate(sameDate);
        newLunchRound.setStatus("PLANNING");

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(testUser));
        when(lunchRoundRepository.existsByCreatorAndDate(testUser, sameDate)).thenReturn(true);

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            lunchRoundService.createLunchRound(newLunchRound, userId);
        });

        assertEquals("User can only create one lunch round per day", exception.getMessage());

        verify(userRepository).findByUserId(userId);
        verify(lunchRoundRepository).existsByCreatorAndDate(testUser, sameDate);
        verify(lunchRoundRepository, never()).save(any());
    }

    @Test
    @DisplayName("점심 라운드 생성 성공 - 다른 날짜에는 생성 가능")
    void createLunchRound_Success_DifferentDate() {
        // given
        String userId = "testuser";
        LocalDate newDate = LocalDate.of(2025, 9, 21);

        LunchRound newLunchRound = new LunchRound();
        newLunchRound.setDate(newDate);
        newLunchRound.setStatus("PLANNING");

        LunchRound savedLunchRound = new LunchRound();
        savedLunchRound.setId(2L);
        savedLunchRound.setDate(newDate);
        savedLunchRound.setStatus("PLANNING");
        savedLunchRound.setCreator(testUser);
        savedLunchRound.setTeam(testTeam);

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(testUser));
        when(lunchRoundRepository.existsByCreatorAndDate(testUser, newDate)).thenReturn(false);
        when(lunchRoundRepository.save(newLunchRound)).thenReturn(savedLunchRound);

        // when
        LunchRound result = lunchRoundService.createLunchRound(newLunchRound, userId);

        // then
        assertNotNull(result);
        assertEquals(testUser, result.getCreator());
        assertEquals(testTeam, result.getTeam());
        assertEquals(newDate, result.getDate());

        verify(userRepository).findByUserId(userId);
        verify(lunchRoundRepository).existsByCreatorAndDate(testUser, newDate);
        verify(lunchRoundRepository).save(newLunchRound);

        // Service가 creator와 team을 설정하는지 확인
        assertEquals(testUser, newLunchRound.getCreator());
        assertEquals(testTeam, newLunchRound.getTeam());
    }

    @Test
    @DisplayName("점심 라운드 생성 성공 - 다른 사용자는 같은 날짜에 생성 가능")
    void createLunchRound_Success_DifferentUser() {
        // given
        Team anotherTeam = new Team();
        anotherTeam.setId(2L);
        anotherTeam.setName("Another Team");

        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setUserId("anotheruser");
        anotherUser.setName("Another User");
        anotherUser.setRole("USER");
        anotherUser.setTeam(anotherTeam);

        String anotherUserId = "anotheruser";
        LocalDate sameDate = LocalDate.of(2025, 9, 20);

        LunchRound newLunchRound = new LunchRound();
        newLunchRound.setDate(sameDate);
        newLunchRound.setStatus("PLANNING");

        LunchRound savedLunchRound = new LunchRound();
        savedLunchRound.setId(2L);
        savedLunchRound.setDate(sameDate);
        savedLunchRound.setStatus("PLANNING");
        savedLunchRound.setCreator(anotherUser);
        savedLunchRound.setTeam(anotherTeam);

        when(userRepository.findByUserId(anotherUserId)).thenReturn(Optional.of(anotherUser));
        when(lunchRoundRepository.existsByCreatorAndDate(anotherUser, sameDate)).thenReturn(false);
        when(lunchRoundRepository.save(newLunchRound)).thenReturn(savedLunchRound);

        // when
        LunchRound result = lunchRoundService.createLunchRound(newLunchRound, anotherUserId);

        // then
        assertNotNull(result);
        assertEquals(anotherUser, result.getCreator());
        assertEquals(anotherTeam, result.getTeam());
        assertEquals(sameDate, result.getDate());

        verify(userRepository).findByUserId(anotherUserId);
        verify(lunchRoundRepository).existsByCreatorAndDate(anotherUser, sameDate);
        verify(lunchRoundRepository).save(newLunchRound);

        // Service가 creator와 team을 설정하는지 확인
        assertEquals(anotherUser, newLunchRound.getCreator());
        assertEquals(anotherTeam, newLunchRound.getTeam());
    }

}