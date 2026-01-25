package com.gamemanager.service;

import com.gamemanager.model.Game;
import com.gamemanager.repository.GameRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {
    private static final Logger logger = LoggerFactory.getLogger(GameService.class);

    @Autowired
    private GameRepository gameRepository;

    // Create game without publisher validation
    public Game createGame(Game game) {
        logger.info("Creating game with publisherId: {}", game.getPublisherId());
        return gameRepository.save(game);
    }

    // Read All
    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    // Read by PublisherId 
    // I assume publisherId is going to be self explanatory
    public List<Game> getGamesByPublisherId(String publisherId) {
        return gameRepository.findByPublisherId(publisherId);
    }
}
