package kr.sparta.backendbasic2.serivce;

import kr.sparta.backendbasic2.entity.LunchRound;
import kr.sparta.backendbasic2.entity.User;
import kr.sparta.backendbasic2.dto.LunchRoundRequest;
import kr.sparta.backendbasic2.repository.LunchRoundRepository;
import kr.sparta.backendbasic2.repository.TeamRepository;
import kr.sparta.backendbasic2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LunchRoundService {

    private final LunchRoundRepository lunchRoundRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    @Transactional
    public LunchRound createLunchRound(LunchRound lunchRound, String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found with userId: " + userId));

        if (lunchRoundRepository.existsByCreatorAndDate(user, lunchRound.getDate())) {
            throw new RuntimeException("User can only create one lunch round per day");
        }

        lunchRound.setCreator(user);
        lunchRound.setTeam(user.getTeam());
        return lunchRoundRepository.save(lunchRound);
    }

    public List<LunchRound> getAllLunchRounds() {
        return lunchRoundRepository.findAll();
    }

    public LunchRound getLunchRoundById(Long id) {
        return lunchRoundRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("LunchRound not found with id: " + id));
    }

    @Transactional
    public LunchRound updateLunchRound(String userId, Long id, LunchRoundRequest lunchRound) {
        //이 사용자가 등록한 lunch_round

        LunchRound existing = getLunchRoundById(id);
        existing.setStatus(lunchRound.status());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDateTime = LocalDate.parse(lunchRound.date(), formatter);
        existing.setDate(localDateTime);

        var team = teamRepository.findById(lunchRound.teamId()).orElseThrow(() -> new RuntimeException("Team not found with id: " + lunchRound.teamId()));
        existing.setTeam(team);

        existing.setCreator(getUser(userId));
        return lunchRoundRepository.save(existing);
    }

    private User getUser(String userId) {
        return userRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public LunchRound updateLunchRoundStatus(Long id, String status) {
        LunchRound existing = getLunchRoundById(id);
        existing.setStatus(status);
        return lunchRoundRepository.save(existing);
    }

    @Transactional
    public void deleteLunchRound(Long id) {
        if (!lunchRoundRepository.existsById(id)) {
            throw new RuntimeException("LunchRound not found with id: " + id);
        }
        lunchRoundRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return lunchRoundRepository.existsById(id);
    }
}
