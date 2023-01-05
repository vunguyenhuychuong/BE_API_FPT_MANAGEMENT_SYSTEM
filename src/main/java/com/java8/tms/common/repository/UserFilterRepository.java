package com.java8.tms.common.repository;

import com.java8.tms.common.dto.UserDTO;
import com.java8.tms.common.dto.UserPage;
import com.java8.tms.common.entity.Role;
import com.java8.tms.common.entity.User;
import com.java8.tms.common.meta.Gender;
import com.java8.tms.common.meta.UserStatus;
import com.java8.tms.common.payload.request.UserFilterForm;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;

@Repository
public class UserFilterRepository {
    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;

    public UserFilterRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    public Page<UserDTO> UserFilterWithPaging(UserFilterForm userFilterForm, UserPage userPage) {
        //created query
        CriteriaQuery<User> userCriteriaQuery = criteriaBuilder.createQuery(User.class);
        Root<User> userRoot = userCriteriaQuery.from(User.class);
        Join<User, Role> roleJoin = userRoot.join("role");

        //set predicate for query
        Predicate predicate = getPredicate(userFilterForm, userRoot, roleJoin);
        userCriteriaQuery.where(predicate);

        //set sorting
        if (userPage.getSortDirection() != null && userPage.getSortBy() != null) {
            setOrder(userPage, userCriteriaQuery, userRoot);
        } else if (userFilterForm.getSearchValue() != null && userPage.getSortDirection() == null && userPage.getSortBy() == null) {
            setOrderLikeMost(userCriteriaQuery, userRoot, userFilterForm);
        }

        //get paging for page
        TypedQuery<User> typedQuery = entityManager.createQuery(userCriteriaQuery);
        typedQuery.setFirstResult(userPage.getPageNumber() * userPage.getPageSize());
        typedQuery.setMaxResults(userPage.getPageSize());

        Pageable pageable = getPageable(userPage);
        Long userCount = getUserCount(predicate);

        ModelMapper modelMapper = new ModelMapper();
        List<UserDTO> listUser = modelMapper.map(typedQuery.getResultList(), new TypeToken<List<UserDTO>>() {
        }.getType());

        return new PageImpl<>(listUser, pageable, userCount);
    }

    private Long getUserCount(Predicate predicate) {
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<User> userRoot = countQuery.from(User.class);
        userRoot.join("role");
        countQuery.select(criteriaBuilder.count(userRoot)).where(predicate);
        return entityManager.createQuery(countQuery).getSingleResult();
    }

    private Pageable getPageable(UserPage userPage) {
        return PageRequest.of(userPage.getPageNumber(), userPage.getPageSize());
    }

    //sort
    private void setOrder(UserPage userPage, CriteriaQuery<User> criteriaQuery, Root<User> userRoot) {
        if (userPage.getSortDirection().equals("ASC")) {
            criteriaQuery.orderBy(criteriaBuilder.asc(userRoot.get(userPage.getSortBy())));
        } else {
            criteriaQuery.orderBy(criteriaBuilder.desc(userRoot.get(userPage.getSortBy())));
        }
    }

    private void setOrderLikeMost(CriteriaQuery<User> criteriaQuery, Root<User> userRoot, UserFilterForm user) {

        List<Order> temp = new ArrayList<>();
        List<Order> temp1 = new ArrayList<>();
        List<Order> temp2 = new ArrayList<>();
        List<Order> temp3 = new ArrayList<>();
        List<Order> temp4 = new ArrayList<>();
        List<Order> temp5 = new ArrayList<>();

        user.getSearchValue().forEach(s -> {

            List<String> s1 = Arrays.asList(s.split(" "));
            String fistName = s1.get(s1.size()-1);
            String lastName = s1.get(0);

            Expression<Object> caseExpression = criteriaBuilder.selectCase()
                    .when(criteriaBuilder.like(userRoot.get("fullname"), criteriaBuilder.literal(s)), 1)
                    .when(criteriaBuilder.like(userRoot.get("email"), criteriaBuilder.literal(s)), 1)
                    .otherwise(6);
            temp1.add(criteriaBuilder.asc(caseExpression));

            Expression<Object> caseExpression2 = criteriaBuilder.selectCase()
                    .when(criteriaBuilder.like(userRoot.get("fullname"), criteriaBuilder.literal("%" + s)), 2)
                    .when(criteriaBuilder.like(userRoot.get("email"), criteriaBuilder.literal(s + "%")), 2)
                    .otherwise(6);
            temp2.add(criteriaBuilder.asc(caseExpression2));

            Expression<Object> caseExpression3 = criteriaBuilder.selectCase()
                    .when(criteriaBuilder.like(userRoot.get("fullname"), criteriaBuilder.literal(s + "%")), 3)
                    .otherwise(6);
            temp3.add(criteriaBuilder.asc(caseExpression3));

            Expression<Object> caseExpression4 = criteriaBuilder.selectCase()
                    .when(criteriaBuilder.like(userRoot.get("fullname"), criteriaBuilder.literal(lastName + "%" + fistName)), 2)
                    .otherwise(6);
            temp4.add(criteriaBuilder.asc(caseExpression4));

            Expression<Object> caseExpression5 = criteriaBuilder.selectCase()
                    .when(criteriaBuilder.like(userRoot.get("fullname"), criteriaBuilder.literal("%" + fistName)), 4)
                    .otherwise(6);
            temp5.add(criteriaBuilder.asc(caseExpression5));
        });

        temp.addAll(temp1);
        temp.addAll(temp2);
        temp.addAll(temp3);
        temp.addAll(temp4);
        temp.addAll(temp5);

        criteriaQuery.orderBy(temp);
    }

    private Predicate getPredicate(UserFilterForm userFilterForm, Root<User> userRoot, Join<User, Role> roleJoin) {

        //filter by search value
        List<Predicate> totalPredicate = new ArrayList<>();
        if (userFilterForm.getSearchValue() != null) {
            List<Predicate> predicates = new ArrayList<>();
            Set<String> listSearch = userFilterForm.getSearchValue();
            listSearch.forEach((searchValue) -> {
                //split keyword from search value
                String[] listKeyword = searchValue.trim().split(" ");
                for (String keyWord : listKeyword) {
                    predicates.add(criteriaBuilder.like(userRoot.get("fullname"), "%" + keyWord.trim() + "%"));
                }
                predicates.add(criteriaBuilder.like(userRoot.get("email"), "%" + searchValue.trim() + "%"));
            });

            totalPredicate.add(criteriaBuilder.or(predicates.toArray(new Predicate[0])));
        }

        //filter by gender
        if (userFilterForm.getGender() != null) {
            totalPredicate.add(criteriaBuilder.equal(userRoot.get("gender"), Gender.valueOf(userFilterForm.getGender().trim().toUpperCase())));
        }

        //filter by role
        if (userFilterForm.getTypeRole() != null) {
            List<Predicate> predicates = new ArrayList<>();
            for (String role : userFilterForm.getTypeRole()) {
                predicates.add(criteriaBuilder.equal(roleJoin.get("name"), role.trim().toUpperCase().replace(" ", "_")));
            }
            totalPredicate.add(criteriaBuilder.or(predicates.toArray(new Predicate[0])));
        }

        //filter by user status
        if (userFilterForm.getStatus() != null) {
            List<Predicate> predicates = new ArrayList<>();
            for (String status : userFilterForm.getStatus()) {
                predicates.add(criteriaBuilder.equal(userRoot.get("status"), UserStatus.valueOf(status.trim().toUpperCase())));
            }
            totalPredicate.add((criteriaBuilder.or(predicates.toArray(new Predicate[0]))));
        }

        //filter by day of birth
        if (userFilterForm.getFromBirthday() != null && userFilterForm.getToBirthday() != null) {
            totalPredicate.add(criteriaBuilder.between(userRoot.get("birthday"), userFilterForm.getFromBirthday(), userFilterForm.getToBirthday()));
        } else if (userFilterForm.getFromBirthday() != null) {
            totalPredicate.add(criteriaBuilder.greaterThanOrEqualTo(userRoot.get("birthday"), userFilterForm.getFromBirthday()));
        } else if (userFilterForm.getToBirthday() != null) {
            totalPredicate.add(criteriaBuilder.lessThanOrEqualTo(userRoot.get("birthday"), userFilterForm.getToBirthday()));
        }

        //filter user delete status
        totalPredicate.add(criteriaBuilder.notEqual(userRoot.get("status"), UserStatus.DELETE));

        //return predicate
        return criteriaBuilder.and(totalPredicate.toArray(new Predicate[0]));
    }

    public List<String> getAllValueUserStatus() {
        UserStatus[] statuses = UserStatus.class.getEnumConstants();
        List<String> listStatus = new ArrayList<>();
        for (UserStatus status : statuses) {
            listStatus.add(status.toString());
        }
        return listStatus;
    }

    public List<String> getAllValueGenderEnum() {
        Gender[] genders = Gender.class.getEnumConstants();
        List<String> listGender = new ArrayList<>();
        for (Gender gender : genders) {
            listGender.add(gender.toString());
        }
        return listGender;
    }

    public Set<String> getListKeyword(String keyword) {
        List<String> kws = List.of(keyword.split("\\s"));

        // create query
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        Root<User> root = query.from(User.class);

        // add predicate for search fullName
        List<Predicate> predicates = new ArrayList<>();
        kws.forEach(kw -> {
            Predicate predicate = builder.like(root.get("fullname"), "%" + kw + "%");
            predicates.add(predicate);
        });

        // add predicate for search email
        Predicate predicate = builder.like(root.get("email"), "%" + keyword + "%");
        predicates.add(predicate);

        // total predicate
        List<Predicate> totalPredicates = new ArrayList<>();
        totalPredicates.add(builder.or(predicates.toArray(new Predicate[0])));

        totalPredicates.add(builder.and(criteriaBuilder.notEqual(root.get("status"), UserStatus.DELETE)));

        // order
        Expression<Object> caseExpression = criteriaBuilder.selectCase()
                .when(criteriaBuilder.like(root.get("fullname"), criteriaBuilder.literal(keyword.trim())), 1)
                .when(criteriaBuilder.like(root.get("email"), criteriaBuilder.literal(keyword.trim())), 1)
                .when(criteriaBuilder.like(root.get("fullname"), criteriaBuilder.literal(keyword.trim() + "%")), 3)
                .when(criteriaBuilder.like(root.get("email"), criteriaBuilder.literal(keyword.trim() + "%")), 2)
                .when(criteriaBuilder.like(root.get("fullname"), criteriaBuilder.literal("%" + keyword.trim())), 2)
                .otherwise(4);


        // set total predicate
        Order temp = criteriaBuilder.asc(caseExpression);
        query.orderBy(temp);

        // execute query
        query.select(root).where(totalPredicates.toArray(new Predicate[0]));
        List<User> list = entityManager.createQuery(query).setMaxResults(20).getResultList();

        Set<String> listKeyword = new LinkedHashSet<>();
        listKeyword.add(keyword);
        list.forEach(user -> {
            if (user.getEmail().equalsIgnoreCase(keyword.trim()) || user.getEmail().toUpperCase().contains(keyword.trim().toUpperCase())){
                listKeyword.add(user.getEmail());
            }
            for (String kw : kws) {
                if (removeAccent(user.getFullname().toLowerCase()).contains(removeAccent(kw).toLowerCase())) {
                    listKeyword.add(user.getFullname());
                    break;
                }
            }
        });
        return listKeyword;
    }

    // replace sign vietnamese
    public static String removeAccent(String s) {
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("");
    }
}
