package com.example.springsocial.controller;

import com.example.springsocial.model.Feedback;
import com.example.springsocial.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class EngagementController {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @PostMapping("/feedback")
    public Feedback submitFeedback(@RequestBody Feedback feedback) {
        feedback.setPointsAwarded(50);
        return feedbackRepository.save(feedback);
    }

    @GetMapping("/rewards/{email}")
    public Map<String, Object> getRewards(@PathVariable String email) {
        List<Feedback> history = feedbackRepository.findByCustomerEmail(email);
        int totalPoints = history.stream().mapToInt(Feedback::getPointsAwarded).sum();
        return Map.of("email", email, "totalPoints", totalPoints, "submissions", history);
    }
}