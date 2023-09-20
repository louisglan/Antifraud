package hyperskill.antifraud.config;

import hyperskill.antifraud.model.database.UserEntity;
import hyperskill.antifraud.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository repository;

    public UserDetailsServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = repository
                .findUserEntityByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Not found"));
        return new UserEntityAdapter(userEntity);
    }
}
