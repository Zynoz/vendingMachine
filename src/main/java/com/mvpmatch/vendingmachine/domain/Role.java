package com.mvpmatch.vendingmachine.domain;

import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
public class Role implements GrantedAuthority {
    private static final long serialVersionUID = 1L;

    public static final String ADMIN = "ADMIN";
    public static final String SELLER = "SELLER";
    public static final String BUYER = "BUYER";

    @Id
    @Column(length = 50)
    @NotNull
    private String authority;

    public Role() {
    }

    public Role(RoleType authority) {
        this.authority = authority.toString();
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return authority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Role)) {
            return false;
        }
        return Objects.equals(authority, ((Role) o).authority);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(authority);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Role{" +
                "name='" + authority + '\'' +
                "}";
    }

    public enum RoleType{
        ADMIN,
        SELLER,
        BUYER
    }
}