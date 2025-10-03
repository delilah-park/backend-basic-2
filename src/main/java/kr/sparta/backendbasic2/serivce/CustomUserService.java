package kr.sparta.backendbasic2.serivce;

import kr.sparta.backendbasic2.entity.User;
import kr.sparta.backendbasic2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserService implements UserDetailsService {

    private final UserRepository userRepository;

    //user_id
    @Override
    public User loadUserByUsername(String loginId) throws UsernameNotFoundException {
        return userRepository.findByUserId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자 없음"));
    }
}
