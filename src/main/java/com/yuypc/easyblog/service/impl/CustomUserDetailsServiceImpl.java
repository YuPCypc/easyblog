package com.yuypc.easyblog.service.impl;

import com.yuypc.easyblog.dto.resp.UserRespDTO;
import com.yuypc.easyblog.service.UserService;
import com.yuypc.easyblog.utils.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CustomUserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserService userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserRespDTO user = userRepository.getUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new CustomUserDetails(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                new ArrayList<>()
        );
    }
}
