package com.uvg.digital.security;

import com.uvg.digital.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private static final long serialVersionUID = 1L;
	private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Asumiendo que el campo "role" en User representa el rol del usuario
        return Collections.singletonList(new SimpleGrantedAuthority(user.getRole()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // o personalizar según tu lógica de negocio
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // o personalizar según tu lógica de negocio
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // o personalizar según tu lógica de negocio
    }

    @Override
    public boolean isEnabled() {
        return user.getVerified(); // Asumimos que un usuario verificado está habilitado
    }

    // Añadir un getter para acceder al User original si es necesario
    public User getUser() {
        return user;
    }
}
