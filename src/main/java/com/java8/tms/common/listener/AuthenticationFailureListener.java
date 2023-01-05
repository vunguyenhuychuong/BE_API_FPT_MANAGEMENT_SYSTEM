//package com.java8.tms.common.listener;
//
//
//import com.java8.tms.common.security.bruteForceProtection.DefaultBruteForceProtectionService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationListener;
//import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
//import org.springframework.stereotype.Component;
//
//import javax.servlet.http.HttpServletRequest;
//@Component
//public class AuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {
//
//    private static Logger LOG = LoggerFactory.getLogger(AuthenticationFailureListener.class);
//
//    @Autowired
//    private HttpServletRequest request;
//   @Autowired
//    private DefaultBruteForceProtectionService bruteForceProtectionService;
//
//    @Override
//    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
//        String email = event.getAuthentication().getPrincipal().toString();
//        LOG.info("********* login failed for user {} ", email);
//        bruteForceProtectionService.registerLoginFailure(request,email);
//    }
//
//}
