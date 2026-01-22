package com.gamemanager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Entity
@Table(name = "games")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Game {

    @Id
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
    private Map<String, Integer> timePlayed;
}
