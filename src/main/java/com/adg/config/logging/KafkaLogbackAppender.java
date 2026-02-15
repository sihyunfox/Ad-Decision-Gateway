package com.adg.config.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Logback Appender 샘플: 로그 이벤트를 지정한 Kafka 토픽으로 전송.
 * <p>
 * 일반 로그는 DB에 저장하지 않고, 콘솔/파일 또는 이처럼 외부 스트림(Kafka)으로만 전송.
 * <p>
 * logback-spring.xml 예시:
 * <pre>
 * &lt;appender name="KAFKA" class="com.adg.config.logging.KafkaLogbackAppender"&gt;
 *   &lt;topic&gt;app-logs&lt;/topic&gt;
 *   &lt;bootstrapServers&gt;localhost:9092&lt;/bootstrapServers&gt;
 *   &lt;!-- optional --&gt;
 *   &lt;keySerializer&gt;org.apache.kafka.common.serialization.StringSerializer&lt;/keySerializer&gt;
 * &lt;/appender&gt;
 * &lt;appender name="KAFKA_ASYNC" class="ch.qos.logback.classic.AsyncAppender"&gt;
 *   &lt;appender-ref ref="KAFKA"/&gt;
 * &lt;/appender&gt;
 * </pre>
 * 사용 시 spring.profiles.active에 kafka 포함 또는 로거에서 KAFKA_ASYNC 참조.
 */
public class KafkaLogbackAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    private String topic;
    private String bootstrapServers = "localhost:9092";
    private KafkaProducer<String, String> producer;

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    @Override
    public void start() {
        if (topic == null || topic.isBlank()) {
            addError("KafkaLogbackAppender: topic is required");
            return;
        }
        try {
            Map<String, Object> props = new HashMap<>();
            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            props.put(ProducerConfig.ACKS_CONFIG, "0");
            props.put(ProducerConfig.RETRIES_CONFIG, 0);
            producer = new KafkaProducer<>(props);
            super.start();
        } catch (Exception e) {
            addError("KafkaLogbackAppender failed to start", e);
        }
    }

    @Override
    protected void append(ILoggingEvent event) {
        if (producer == null || !isStarted()) {
            return;
        }
        try {
            String payload = toJson(event);
            producer.send(new ProducerRecord<>(topic, null, payload), (metadata, ex) -> {
                if (ex != null) {
                    addError("Kafka send failed (fail-safe, log dropped): " + ex.getMessage());
                }
            });
        } catch (Exception e) {
            addError("KafkaLogbackAppender append failed (fail-safe)", e);
        }
    }

    @Override
    public void stop() {
        try {
            if (producer != null) {
                producer.flush();
                producer.close();
            }
        } catch (Exception e) {
            addError("KafkaLogbackAppender stop error", e);
        } finally {
            producer = null;
            super.stop();
        }
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private String toJson(ILoggingEvent event) {
        StringBuilder sb = new StringBuilder(256);
        sb.append("{\"@timestamp\":\"").append(Instant.ofEpochMilli(event.getTimeStamp()).toString()).append("\"");
        sb.append(",\"level\":\"").append(escapeJson(event.getLevel().toString())).append("\"");
        sb.append(",\"logger\":\"").append(escapeJson(event.getLoggerName())).append("\"");
        sb.append(",\"message\":\"").append(escapeJson(event.getFormattedMessage())).append("\"");
        if (event.getMDCPropertyMap() != null && !event.getMDCPropertyMap().isEmpty()) {
            String mdc = event.getMDCPropertyMap().entrySet().stream()
                    .map(e -> "\"" + escapeJson(e.getKey()) + "\":\"" + escapeJson(e.getValue()) + "\"")
                    .collect(Collectors.joining(","));
            sb.append(",\"mdc\":{").append(mdc).append("}");
        }
        if (event.getThrowableProxy() != null) {
            sb.append(",\"exception\":\"").append(escapeJson(event.getThrowableProxy().getClassName() + ": " + event.getThrowableProxy().getMessage())).append("\"");
        }
        sb.append("}");
        return sb.toString();
    }
}
