package com.ghpham11a.xp_springboot_mysql.models;

import java.io.Serializable;
import java.time.LocalDateTime;

public class KafkaEventMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private String topic;
    private String key;
    private Object value;
    private LocalDateTime timestamp;

    public KafkaEventMessage() {
    }

    public KafkaEventMessage(String topic, String key, Object value) {
        this.topic = topic;
        this.key = key;
        this.value = value;
        this.timestamp = LocalDateTime.now();
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}