package kr.sparta.backendbasic2.controller;

import kr.sparta.backendbasic2.entity.MenuOption;
import kr.sparta.backendbasic2.serivce.MenuOptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menu-options")
@RequiredArgsConstructor
public class MenuOptionController {

    private final MenuOptionService menuOptionService;

    @PostMapping("/{roundId}")
    public ResponseEntity<MenuOption> add(@PathVariable Long roundId, @RequestBody MenuOption option) {
        var menuOption = menuOptionService.addMenuOption(roundId, option);
        return ResponseEntity.status(HttpStatus.CREATED).body(menuOption);
    }

    @GetMapping
    public List<MenuOption> listAll() {
        return menuOptionService.getAllMenuOptions();
    }

    @GetMapping("/{roundId}")
    public List<MenuOption> list(@PathVariable Long roundId) {
        return menuOptionService.getMenuOptionsByRoundId(roundId);
    }

    @GetMapping("/option/{id}")
    public MenuOption getById(@PathVariable Long id) {
        return menuOptionService.getMenuOptionById(id);
    }

    @PutMapping("/{id}")
    public MenuOption update(@PathVariable Long id, @RequestBody MenuOption option) {
        return menuOptionService.updateMenuOption(id, option);
    }

    @PreAuthorize("hasRole('admin')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        menuOptionService.deleteMenuOption(id);
    }
}