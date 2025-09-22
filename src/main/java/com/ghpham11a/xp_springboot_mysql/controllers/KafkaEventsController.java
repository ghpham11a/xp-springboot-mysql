package com.ghpham11a.xp_springboot_mysql.controllers;

import com.ghpham11a.xp_springboot_mysql.models.Account;
import com.ghpham11a.xp_springboot_mysql.models.KafkaEventMessage;
import com.ghpham11a.xp_springboot_mysql.services.KafkaEventService;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/kafka-events")
public class KafkaEventsController {

    private final KafkaEventService kafkaEventService;

    public KafkaEventsController(KafkaEventService kafkaEventService) {
        this.kafkaEventService = kafkaEventService;
    }

    // Kafka Listeners to consume messages from the topics
    @KafkaListener(topics = "account_created", groupId = "account-events-group")
    public void handleAccountCreated(@Payload Account message,
                                   @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        KafkaEventMessage event = new KafkaEventMessage("account_created", key, message);
        kafkaEventService.addEvent(event);
    }

    @KafkaListener(topics = "account_updated", groupId = "account-events-group")
    public void handleAccountUpdated(@Payload Account message,
                                   @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        KafkaEventMessage event = new KafkaEventMessage("account_updated", key, message);
        kafkaEventService.addEvent(event);
    }

    @KafkaListener(topics = "account_deleted", groupId = "account-events-group")
    public void handleAccountDeleted(@Payload String message,
                                   @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        KafkaEventMessage event = new KafkaEventMessage("account_deleted", key, message);
        kafkaEventService.addEvent(event);
    }

    // REST endpoints to check the events

    // GET all events
    @GetMapping
    public ResponseEntity<List<KafkaEventMessage>> getAllEvents() {
        return ResponseEntity.ok(kafkaEventService.getAllEvents());
    }

    // GET events by topic
    @GetMapping("/topic/{topicName}")
    public ResponseEntity<List<KafkaEventMessage>> getEventsByTopic(@PathVariable String topicName) {
        return ResponseEntity.ok(kafkaEventService.getEventsByTopic(topicName));
    }

    // GET event counts
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getEventStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalEvents", kafkaEventService.getEventCount());
        stats.put("accountCreatedCount", kafkaEventService.getEventCountByTopic("account_created"));
        stats.put("accountUpdatedCount", kafkaEventService.getEventCountByTopic("account_updated"));
        stats.put("accountDeletedCount", kafkaEventService.getEventCountByTopic("account_deleted"));
        return ResponseEntity.ok(stats);
    }

    // Clear all events
    @DeleteMapping
    public ResponseEntity<String> clearAllEvents() {
        kafkaEventService.clearEvents();
        return ResponseEntity.ok("All events cleared successfully!");
    }
}