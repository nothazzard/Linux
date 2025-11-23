package com.linux.cheat.auth;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class AuthService {
    private final String panelPassword;
    private final Set<String> staff;

    public AuthService(String panelPassword, Set<String> staff) {
        this.panelPassword = panelPassword == null ? "" : panelPassword;
        this.staff = new HashSet<>();
        if (staff != null) {
            for (String s : staff) {
                if (s != null) this.staff.add(s.toLowerCase());
            }
        }
    }

    public boolean isStaff(String nickname) {
        return nickname != null && staff.contains(nickname.toLowerCase());
    }

    public boolean verifyPassword(String password) {
        return panelPassword != null && panelPassword.equals(password);
    }

    public Set<String> getStaff() {
        return Collections.unmodifiableSet(staff);
    }
}
