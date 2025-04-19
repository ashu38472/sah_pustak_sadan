package ashu.sah.SahPustakSadan.Service;

import ashu.sah.SahPustakSadan.Model.User;
import ashu.sah.SahPustakSadan.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public boolean authenticate(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            System.out.println(user.getPassword());
            return passwordEncoder.matches(password, user.getPassword());
        }
        return false;
    }
}
