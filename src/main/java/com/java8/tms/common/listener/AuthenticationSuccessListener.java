//package com.java8.tms.common.listener;
//
//import com.java8.tms.common.security.bruteForceProtection.DefaultBruteForceProtectionService;
//import com.java8.tms.common.security.userprincipal.UserPrinciple;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationListener;
//import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
//import org.springframework.stereotype.Component;
//
//@Component
//public class AuthenticationSuccessListener implements ApplicationListener < AuthenticationSuccessEvent > {
//
//    private static Logger LOG = LoggerFactory.getLogger(AuthenticationSuccessListener.class);
//
//    @Autowired
//    private DefaultBruteForceProtectionService bruteForceProtectionService;
//
//    @Override
//    public void onApplicationEvent(AuthenticationSuccessEvent event) {
//        UserPrinciple userPrinciple = (UserPrinciple) event.getAuthentication().getPrincipal();
//        String email = userPrinciple.getEmail();
//        LOG.info("********* Login successful for user {} ", email);
//        bruteForceProtectionService.resetBruteForceCounter(email);
//    }
//
//}