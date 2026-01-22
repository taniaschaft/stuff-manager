package com.gamemanager.service;

import com.gamemanager.model.Game;
import com.gamemanager.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    // Create
    public Game createGame(Game game) {
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
