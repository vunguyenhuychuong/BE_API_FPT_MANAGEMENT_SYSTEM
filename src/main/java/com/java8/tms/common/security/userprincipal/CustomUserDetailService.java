package com.java8.tms.common.security.userprincipal;

import com.java8.tms.common.entity.User;
import com.java8.tms.common.repository.UserRepository;
import com.java8.tms.user.custom_exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    @Cacheable(value = "userDetails")
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email ,"Not found!"));
        return UserPrinciple.build(user);
    }
}
