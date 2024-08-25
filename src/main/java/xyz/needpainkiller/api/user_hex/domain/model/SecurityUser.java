package xyz.needpainkiller.api.user_hex.domain.model;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Getter
public class SecurityUser implements UserDetails {

    @Serial
    private static final long serialVersionUID = -1232550005299865528L;

    private final User user;
    private final List<Role> roleList;

    public SecurityUser(User user, List<Role> roleList) {
        this.user = user;
        this.roleList = new ArrayList<>(roleList);
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roleList;
    }

    @Override
    public String getUsername() {
        return user.getUserId();
    }

    @Override
    public String getPassword() {
        return user.pwd();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.isLoginEnabled();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SecurityUser that = (SecurityUser) o;
        return Objects.equals(user, that.user) && Objects.equals(roleList, that.roleList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, roleList);
    }

    @Override
    public String
    toString() {
        return "SecurityUser{" +
                "user=" + user.getUserId() +
                ", roleList=" + roleList +
                '}';
    }
}
