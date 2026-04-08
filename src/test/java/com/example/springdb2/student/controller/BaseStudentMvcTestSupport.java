package com.example.springdb2.student.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

abstract class BaseStudentMvcTestSupport {
    @Autowired
    protected MockMvc mockMvc;
}
