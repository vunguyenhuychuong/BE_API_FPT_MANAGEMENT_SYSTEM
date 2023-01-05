//package com.java8.tms.common.security.bruteForceProtection;
//
//import com.java8.tms.common.entity.User;
//import com.java8.tms.common.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.servlet.ServletRequest;
//
//
//@Service
//public class DefaultBruteForceProtectionService implements IBruteForceProtectionService {
//
//    @Value("${security.failedlogin.count}")
//    private int maxFailedLogins;
//
//    @Autowired
//    UserRepository userRepository;
//
////    @Value("${jdj.brute.force.cache.max}")
////    private int cacheMaxLimit;
////
////    private final ConcurrentHashMap < String, FailedLogin > cache;
////
////    public DefaultBruteForceProtectionService() {
////        this.cache = new ConcurrentHashMap < > (cacheMaxLimit); //setting max limit for cache
////    }
//
//    @Override
//    @Transactional
//    public void registerLoginFailure(ServletRequest request, String email) {
//        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
//        if (user != null) {
//            int failedCounter = user.getFailedLoginAttempts();
//            if (maxFailedLogins < failedCounter + 1) {
////                user.setLoginDisabled(true); //disabling the account
//            } else {
//                //let's update the counter
//                user.setFailedLoginAttempts(failedCounter + 1);
//            }
//            userRepository.save(user);
//        }
//    }
//
//    @Override
//    @Transactional
//    public void resetBruteForceCounter(String email) {
//        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
//        if (user != null) {
//            user.setFailedLoginAttempts(0);
//            userRepository.save(user);
//        }
//    }
//
////    @Override
////    public boolean isBruteForceAttack(String username) {
////        UserEntity user = getUser(username);
////        if (user != null) {
////            return user.getFailedLoginAttempts() >= maxFailedLogins ? true : false;
////        }
////        return false;
////    }
////
////    protected FailedLogin getFailedLogin(final String username) {
////        FailedLogin failedLogin = cache.get(username.toLowerCase());
////
////        if (failedLogin == null) {
////            //setup the initial data
////            failedLogin = new FailedLogin(0, LocalDateTime.now());
////            cache.put(username.toLowerCase(), failedLogin);
////            if (cache.size() > cacheMaxLimit) {
////
////                // add the logic to remve the key based by timestamp
////            }
////        }
////        return failedLogin;
////    }
////
////    private User getUser(final String username) {
////        return userRepository.findByEmail(username);
////    }
////
////    public int getMaxFailedLogins() {
////        return maxFailedLogins;
////    }
////
////    public void setMaxFailedLogins(int maxFailedLogins) {
////        this.maxFailedLogins = maxFailedLogins;
////    }
////
////    public class FailedLogin {
////
////        private int count;
////        private LocalDateTime date;
////
////        public FailedLogin() {
////            this.count = 0;
////            this.date = LocalDateTime.now();
////        }
////
////        public FailedLogin(int count, LocalDateTime date) {
////            this.count = count;
////            this.date = date;
////        }
////
////        public int getCount() {
////            return count;
////        }
////
////        public void setCount(int count) {
////            this.count = count;
////        }
////
////        public LocalDateTime getDate() {
////            return date;
////        }
////
////        public void setDate(LocalDateTime date) {
////            this.date = date;
////        }
////    }
//}