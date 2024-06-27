package xyz.needpainkiller.service;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import xyz.needpainkiller.api.user_hex.domain.model.Role;
import xyz.needpainkiller.api.user_hex.domain.model.User;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Getter
public class Passport implements Serializable {

    @Serial
    private static final long serialVersionUID = 108926648857066641L;

    private final User user;
    private final List<Role> roleList;

    public Passport(User user, List<Role> roleList) {
        this.user = user;
        this.roleList = new ArrayList<>(roleList);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Passport that = (Passport) o;
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
