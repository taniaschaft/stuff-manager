package com.gamemanager.controller;

import com.gamemanager.model.Game;
import com.gamemanager.service.GameService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.gamemanager.dto.GameResponse;

@RestController
@RequestMapping("/game")
@CrossOrigin(origins = "*", maxAge = 3600)
public class GameController {

    @Autowired
    private GameService gameService;

    /**
     * Create a new game
     * POST http://<game-manager-host>:<game-manager-port>/game
     */
    @PostMapping
    public ResponseEntity<Game> createGame(@Valid @RequestBody Game game) {
        Game createdGame = gameService.createGame(game);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdGame);
    }

    /**
     * Read Games by publisherId or all games
     * GET http://<game-manager-host>:<game-manager-port>/game?publisherId=<publisherId>
     * GET http://<game-manager-host>:<game-manager-port>/game
     */
    @GetMapping
    //public ResponseEntity<List<Game>> getGames(@RequestParam(required = false) String publisherId) {
    public ResponseEntity<GameResponse> getGames(@RequestParam(required = false) String publisherId) {
    List<Game> games;
        String message;

        if (publisherId != null && !publisherId.isEmpty()) {
            //List<Game> games = gameService.getGamesByPublisherId(publisherId);
            //return ResponseEntity.ok(games);
            games = gameService.getGamesByPublisherId(publisherId);
            message = "Games retrieved for publisher: "+ publisherId;
        } else {
            games = gameService.getAllGames();
            message = "All games retrieved successfully";
        }

        GameResponse response = new GameResponse(games, message);
        return ResponseEntity.ok(response);

        //List<Game> games = gameService.getAllGames();
        //return ResponseEntity.ok(games);
    }
}
