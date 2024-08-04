package account.model.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "security_event")
public class SecurityEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false)
    private String object;

    @Column(nullable = false)
    private String path;

    public SecurityEvent() {
    }

    public SecurityEvent(String action, String subject, String object, String path) {
        this.date = LocalDateTime.now();
        this.action = action;
        this.subject = subject;
        this.object = object;
        this.path = path;
    }

    public SecurityEvent(LocalDateTime date, String action, String subject, String object, String path) {
        this.date = date;
        this.action = action;
        this.subject = subject;
        this.object = object;
        this.path = path;
    }

    public SecurityEvent(String action, String object, String path) {
        this(action, "Anonymous", object, path);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SecurityEvent securityEvent = (SecurityEvent) o;
        return Objects.equals(date, securityEvent.date)
                && Objects.equals(action, securityEvent.action)
                && Objects.equals(subject, securityEvent.subject)
                && Objects.equals(object, securityEvent.object)
                && Objects.equals(path, securityEvent.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, action, subject, object, path);
    }
}
