package com.java8.tms.common.metamodel;

import com.java8.tms.common.entity.FSU;
import com.java8.tms.common.entity.Role;
import com.java8.tms.common.entity.TrainingClass;
import com.java8.tms.common.entity.User;
import com.java8.tms.common.meta.Gender;
import com.java8.tms.common.meta.UserStatus;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.util.Date;
import java.util.UUID;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(User.class)
public abstract class User_ {
    public static volatile SingularAttribute<User, UUID> id;
    public static volatile SingularAttribute<User, String> fullname;
    public static volatile SingularAttribute<User, String> password;
    public static volatile SingularAttribute<User, String> email;
    public static volatile SingularAttribute<User, String> avatar;
    public static volatile SingularAttribute<User, Date> birthday;
    public static volatile SingularAttribute<User, Gender> gender;
    public static volatile SingularAttribute<User, String> level;
    public static volatile SingularAttribute<User, FSU> fsu;
    public static volatile SingularAttribute<User, UserStatus> status;
    public static volatile SingularAttribute<User, Role> role;
    public static volatile ListAttribute<User, TrainingClass> createdClasses;
    public static volatile ListAttribute<User, TrainingClass> updatedClasses;
    public static volatile ListAttribute<User, TrainingClass> reviewedClasses;
    public static volatile ListAttribute<User, TrainingClass> approvedClasses;
}
