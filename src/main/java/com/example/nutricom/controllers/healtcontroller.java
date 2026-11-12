package com.example.nutricom.controllers;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;




    @RestController
    public class healtcontroller {

        private final JdbcTemplate jdbcTemplate;

        public healtcontroller(JdbcTemplate jdbcTemplate) {
            this.jdbcTemplate = jdbcTemplate;
        }

        @GetMapping("/api/health")
        public String health() {
            return "OK";
        }

        @GetMapping("/api/health/db")
        public String healthDb() {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return "Database OK";
        }
    }
