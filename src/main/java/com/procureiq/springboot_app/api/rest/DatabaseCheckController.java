package com.procureiq.springboot_app.api.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
public class DatabaseCheckController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/db-check")
    public Map<String, Object> checkDb() {
        Map<String, Object> response = new HashMap<>();
        try {
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            if (Integer.valueOf(1).equals(result)) {
                response.put("status", "connected");
                response.put("message", "Successfully connected to the database");
            } else {
                response.put("status", "error");
                response.put("message", "Unexpected response from database");
            }
        } catch (Exception e) {
            response.put("status", "disconnected");
            response.put("error", e.getMessage());
        }
        return response;
    }
}
