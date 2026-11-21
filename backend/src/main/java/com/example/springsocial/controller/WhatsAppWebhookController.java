package com.example.springsocial.controller;

import com.example.springsocial.model.Feedback;
import com.example.springsocial.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/webhook")
public class WhatsAppWebhookController {

    private static final String VERIFY_TOKEN = "omnichannel_secret_token";

    @Autowired
    private FeedbackRepository feedbackRepository;

    // 1. Meta uses this GET endpoint to verify your ngrok webhook URL
    @GetMapping
    public ResponseEntity<String> verifyWebhook(
            @RequestParam("hub.mode") String mode,
            @RequestParam("hub.verify_token") String token,
            @RequestParam("hub.challenge") String challenge) {

        if ("subscribe".equals(mode) && VERIFY_TOKEN.equals(token)) {
            return ResponseEntity.ok(challenge);
        }
        return ResponseEntity.status(403).body("Verification failed");
    }

    // 2. Meta sends incoming WhatsApp messages to this POST endpoint
    @PostMapping
    public ResponseEntity<String> receiveMessage(@RequestBody Map<String, Object> payload) {
        Feedback feedback = new Feedback();
        feedback.setCustomerEmail("whatsapp-user@channel.local");
        feedback.setChannel("WHATSAPP");
        feedback.setMessage(payload.toString());
        feedback.setPointsAwarded(50);

        feedbackRepository.save(feedback);
        return ResponseEntity.ok("EVENT_RECEIVED");
    }
}