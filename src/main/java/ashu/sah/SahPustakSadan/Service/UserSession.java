package ashu.sah.SahPustakSadan.Service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public class UserSession {

    @Getter
    private String username;

    @Getter
    private String role;

    private LocalDateTime loginTime;

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------
    @Getter
    private boolean loggedIn = false;

    // -------------------------------------------------------------------------
    // Session lifecycle
    // -------------------------------------------------------------------------
    public void login(String username) {
        login(username, "USER"); // default role if not provided
    }

    public void login(String username, String role) {
        this.username = username;
        this.role = role;
        this.loginTime = LocalDateTime.now();
        this.loggedIn = true;

        log.info("User '{}' logged in with role '{}'", username, role);
    }

    public void logout() {
        if (loggedIn) {
            log.info("User '{}' logged out", username);
        }
        clearSession();
    }

    private void clearSession() {
        this.username = null;
        this.role = null;
        this.loginTime = null;
        this.loggedIn = false;
    }

    public Optional<String> getCurrentUsername() {
        return Optional.ofNullable(username);
    }

    public Optional<String> getCurrentRole() {
        return Optional.ofNullable(role);
    }

    public Optional<LocalDateTime> getLoginTime() {
        return Optional.ofNullable(loginTime);
    }

    @Override
    public String toString() {
        return loggedIn
                ? "UserSession{username='" + username + "', role='" + role + "', loginTime=" + loginTime + "}"
                : "UserSession{not logged in}";
    }
}
