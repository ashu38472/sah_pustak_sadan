package ashu.sah.SahPustakSadan.Controller;

import ashu.sah.SahPustakSadan.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class SignUpController {
    @Autowired
    private UserService userService;

    public boolean register(String name, String email, String password) {
        return userService.registerUser(name, email, password);
    }
}
