package com.ghpham11a.xp_springboot_mysql.services;

import com.ghpham11a.xp_springboot_mysql.models.KafkaEventMessage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

@Service
public class KafkaEventService {

    private final ConcurrentLinkedQueue<KafkaEventMessage> events = new ConcurrentLinkedQueue<>();
    private static final int MAX_EVENTS = 100;

    public void addEvent(KafkaEventMessage event) {
        events.offer(event);

        // Keep only the last MAX_EVENTS messages
        while (events.size() > MAX_EVENTS) {
            events.poll();
        }
    }

    public List<KafkaEventMessage> getAllEvents() {
        return new ArrayList<>(events);
    }

    public List<KafkaEventMessage> getEventsByTopic(String topic) {
        return events.stream()
                .filter(event -> topic.equals(event.getTopic()))
                .collect(Collectors.toList());
    }

    public void clearEvents() {
        events.clear();
    }

    public long getEventCount() {
        return events.size();
    }

    public long getEventCountByTopic(String topic) {
        return events.stream()
                .filter(event -> topic.equals(event.getTopic()))
                .count();
    }
}