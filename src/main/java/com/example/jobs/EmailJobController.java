package com.example.jobs;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailJobController {

    private final EmailJobService service;

    public EmailJobController(EmailJobService service) {
        this.service = service;
    }

    @GetMapping("/job-test")
    public String test(@RequestParam String email) {
        service.sendWelcomeEmail(email);
        return "job queued";
    }
}
