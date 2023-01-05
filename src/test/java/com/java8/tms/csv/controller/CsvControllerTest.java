package com.java8.tms.csv.controller;

import org.apache.poi.sl.usermodel.ObjectMetaData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ObjectMetaData.Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WebAppConfiguration
public class CsvControllerTest {
    @LocalServerPort
    private int port;
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
}
