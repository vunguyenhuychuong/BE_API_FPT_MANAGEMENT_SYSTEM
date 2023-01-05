package com.java8.tms.training_class.dto;

import com.java8.tms.common.entity.User;
import lombok.Data;

import java.util.List;

@Data
public class ClassData {

    private List<String> location;

    private List<String> fsu;

    private List<Info> trainers;

}
