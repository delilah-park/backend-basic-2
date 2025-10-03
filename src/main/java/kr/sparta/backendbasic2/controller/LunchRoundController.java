package kr.sparta.backendbasic2.controller;

import kr.sparta.backendbasic2.dto.LunchRoundRequest;
import kr.sparta.backendbasic2.entity.LunchRound;
import kr.sparta.backendbasic2.serivce.LunchRoundService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lunch-rounds")
@RequiredArgsConstructor
public class LunchRoundController {

    private final LunchRoundService lunchRoundService;

    @PostMapping
    public ResponseEntity<LunchRound> create(@RequestBody LunchRound round, Authentication authentication) {
        String userId = authentication.getName();
        var lunchRound =  lunchRoundService.createLunchRound(round, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(lunchRound);
    }

    @GetMapping
    public List<LunchRound> list() {
        return lunchRoundService.getAllLunchRounds();
    }

    @GetMapping("/{id}")
    public LunchRound getById(@PathVariable Long id) {
        return lunchRoundService.getLunchRoundById(id);
    }

    @PutMapping("/{id}")
    public LunchRound update(@PathVariable Long id, @RequestBody LunchRoundRequest round, Authentication authentication) {
        String userId = authentication.getName();
        return lunchRoundService.updateLunchRound(userId, id, round);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        lunchRoundService.deleteLunchRound(id);
    }
}