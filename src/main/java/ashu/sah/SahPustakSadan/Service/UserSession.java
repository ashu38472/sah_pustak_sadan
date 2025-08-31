package ashu.sah.SahPustakSadan.Service;

import ashu.sah.SahPustakSadan.enums.UserRole;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Getter
@Setter
@Service
public class UserSession {
    private static UserSession instance;
    private String currentUserId;
    private String currentUserEmail;
    private String currentUserName;
    private UserRole currentUserRole;
    private boolean isLoggedIn = false;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void login(String userId, String email, String name, UserRole role) {
        this.currentUserId = userId;
        this.currentUserEmail = email;
        this.currentUserName = name;
        this.currentUserRole = role;
        this.isLoggedIn = true;
    }

    public void logout() {
        this.currentUserId = null;
        this.currentUserEmail = null;
        this.currentUserName = null;
        this.currentUserRole = null;
        this.isLoggedIn = false;
    }

    public boolean hasRole(UserRole role) {
        return isLoggedIn && currentUserRole == role;
    }

    public boolean hasAnyRole(UserRole... roles) {
        if (!isLoggedIn || currentUserRole == null) return false;
        for (UserRole role : roles) {
            if (currentUserRole == role) return true;
        }
        return false;
    }

    public boolean canAccessInvoice() {
        return hasAnyRole(UserRole.ADMIN, UserRole.MANAGER, UserRole.STAFF);
    }

    public boolean canAccessProductStock() {
        return hasAnyRole(UserRole.ADMIN, UserRole.MANAGER);
    }

    public boolean canAccessPriceCalculator() {
        return hasAnyRole(UserRole.ADMIN, UserRole.MANAGER, UserRole.STAFF);
    }

    public boolean canAccessProfile() {
        return isLoggedIn;
    }

    public boolean canReportBugs() {
        return isLoggedIn;
    }
}
