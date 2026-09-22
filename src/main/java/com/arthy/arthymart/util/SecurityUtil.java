package com.arthy.arthymart.util;

import com.arthy.arthymart.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public final class SecurityUtil {

    private SecurityUtil() {}

    public static User currentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return null;
        }
        Object obj = session.getAttribute("user");
        return (obj instanceof User) ? (User) obj : null;
    }

    public static boolean isAuthenticated(HttpServletRequest req) {
        return currentUser(req) != null;
    }

    public static boolean hasRole(HttpServletRequest req, User.Role role) {
        User user = currentUser(req);
        return user != null && user.getRole() == role;
    }

    public static boolean hasAnyRole(HttpServletRequest req, User.Role... roles) {
        User user = currentUser(req);
        if (user == null) {
            return false;
        }
        for (User.Role r : roles) {
            if (user.getRole() == r) {
                return true;
            }
        }
        return false;
    }

    /** Basic XSS-safe escaping for use when JSTL c:out is not available (e.g. JSON error messages). */
    public static String escapeHtml(String s) {
        if (s == null) {
            return null;
        }
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
}
