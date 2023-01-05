package com.java8.tms.common.metamodel;

import com.java8.tms.common.entity.Role;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import java.util.UUID;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
public abstract class Role_ {
    public static volatile SingularAttribute<Role, UUID> id;
    public static volatile SingularAttribute<Role, String> name;
}
