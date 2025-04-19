package ashu.sah.SahPustakSadan.Controller;

import ashu.sah.SahPustakSadan.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    public boolean authenticate(String email, String password) {
        System.out.println(email);
        return userService.authenticate(email, password);
    }
}
