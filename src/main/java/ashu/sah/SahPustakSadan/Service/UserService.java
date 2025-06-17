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

    public boolean registerUser(String name, String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            return false; // User already exists
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(User.Role.valueOf("admin")); // default role

        userRepository.save(user);
        return true;
    }

}
