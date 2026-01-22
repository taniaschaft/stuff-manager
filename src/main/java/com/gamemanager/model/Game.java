package com.gamemanager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import java.util.Map;
import java.util.Objects;

@Entity
@Table(name = "games")
@NoArgsConstructor
@AllArgsConstructor
public class Game {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "VARCHAR(36)")
    private String id;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "publisherId cannot be empty")
    private String publisherId;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "name cannot be empty")
    @Size(min = 3, max = 20, message = "name must contain from 3 up to 20 characters")
    private String name;

    @ElementCollection
    @CollectionTable(name = "game_time_played", joinColumns = @JoinColumn(name = "game_id"))
    @MapKeyColumn(name = "play_date")
    @Column(name = "hours_played")
    @NotEmpty(message = "timePlayed cannot be empty")
    @ValidGameHours
    private Map<String, Integer> timePlayed;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(String publisherId) {
        this.publisherId = publisherId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Integer> getTimePlayed() {
        return timePlayed;
    }

    public void setTimePlayed(Map<String, Integer> timePlayed) {
        this.timePlayed = timePlayed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return Objects.equals(id, game.id) &&
                Objects.equals(publisherId, game.publisherId) &&
                Objects.equals(name, game.name) &&
                Objects.equals(timePlayed, game.timePlayed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, publisherId, name, timePlayed);
    }

    @Override
    public String toString() {
        return "Game{" +
                "id='" + id + '\'' +
                ", publisherId='" + publisherId + '\'' +
                ", name='" + name + '\'' +
                ", timePlayed=" + timePlayed +
                '}';
    }
}
