package co.com.bancolombia.model.common.gateways;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AuthContextPortTest {
    @Test
    void hasRoleReturnsTrueWhenRolePresent() {
        var user = new AuthContextPort.AuthUser("sub", "a@b.com", List.of("ADMIN","USER"), List.of("read:orders"));
        assertTrue(user.hasRole("ADMIN"));
        assertFalse(user.hasRole("MANAGER"));
    }

    @Test
    void hasReturnsTrueIfRoleOrPermissionPresent() {
        var user = new AuthContextPort.AuthUser("sub", "a@b.com", List.of("USER"), List.of("write:reports"));
        assertTrue(user.has("USER"));
        assertTrue(user.has("write:reports"));
        assertFalse(user.has("delete:all"));
    }

    @Test
    void hasRoleAndHasAreSafeOnNullLists() {
        var user = new AuthContextPort.AuthUser("sub", "a@b.com", null, null);
        assertFalse(user.hasRole("ANY"));
        assertFalse(user.has("ANY"));
    }
}