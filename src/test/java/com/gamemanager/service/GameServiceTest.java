package com.gamemanager.service;

import com.gamemanager.model.Game;
import com.gamemanager.repository.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private PublisherService publisherService;

    private GameService gameService;

    @BeforeEach
    public void setUp() {
        gameService = new GameService();
        gameService.gameRepository = gameRepository;
        gameService.publisherService = publisherService;
    }

    @Test
    public void testCreateGameWithValidPublisher() {
        // Arrange
        Game game = new Game();
        game.setPublisherId("nintendo");
        game.setName("Mario");
        
        Map<String, Integer> timePlayed = new HashMap<>();
        timePlayed.put("2024-01-20", 10);
        game.setTimePlayed(timePlayed);
        
        when(publisherService.validatePublisherId("nintendo")).thenReturn(true);
        when(gameRepository.save(game)).thenReturn(game);
        
        // Act
        Game createdGame = gameService.createGame(game);
        
        // Assert
        assertNotNull(createdGame);
        assertEquals("nintendo", createdGame.getPublisherId());
        assertEquals("Mario", createdGame.getName());
        verify(gameRepository, times(1)).save(game);
    }

    @Test
    public void testCreateGameWithInvalidPublisher() {
        // Arrange
        Game game = new Game();
        game.setPublisherId("invalid-publisher");
        game.setName("Mario");
        
        Map<String, Integer> timePlayed = new HashMap<>();
        timePlayed.put("2024-01-20", 10);
        game.setTimePlayed(timePlayed);
        
        when(publisherService.validatePublisherId("invalid-publisher")).thenReturn(false);
        
        // Act & Assert
        assertThrows(InvalidPublisherException.class, () -> gameService.createGame(game));
        verify(gameRepository, never()).save(game);
    }

    @Test
    public void testCreateGameInvalidPublisherErrorMessage() {
        // Arrange
        Game game = new Game();
        game.setPublisherId("unknown");
        game.setName("Game");
        
        Map<String, Integer> timePlayed = new HashMap<>();
        timePlayed.put("2024-01-20", 5);
        game.setTimePlayed(timePlayed);
        
        when(publisherService.validatePublisherId("unknown")).thenReturn(false);
        
        // Act & Assert
        InvalidPublisherException exception = assertThrows(InvalidPublisherException.class, 
            () -> gameService.createGame(game));
        
        assertTrue(exception.getMessage().contains("unknown"));
        assertTrue(exception.getMessage().contains("not registered"));
    }

    @Test
    public void testGetAllGames() {
        // Arrange
        List<Game> games = new ArrayList<>();
        Game game1 = new Game();
        game1.setPublisherId("nintendo");
        game1.setName("Mario");
        games.add(game1);
        
        when(gameRepository.findAll()).thenReturn(games);
        
        // Act
        List<Game> result = gameService.getAllGames();
        
        // Assert
        assertEquals(1, result.size());
        assertEquals("Mario", result.get(0).getName());
        verify(gameRepository, times(1)).findAll();
    }

    @Test
    public void testGetGamesByPublisherId() {
        // Arrange
        List<Game> games = new ArrayList<>();
        Game game1 = new Game();
        game1.setPublisherId("nintendo");
        game1.setName("Mario");
        games.add(game1);
        
        when(gameRepository.findByPublisherId("nintendo")).thenReturn(games);
        
        // Act
        List<Game> result = gameService.getGamesByPublisherId("nintendo");
        
        // Assert
        assertEquals(1, result.size());
        assertEquals("Mario", result.get(0).getName());
        verify(gameRepository, times(1)).findByPublisherId("nintendo");
    }

    @Test
    public void testCreateGameMultipleValidPublishers() {
        // Arrange
        Game game1 = new Game();
        game1.setPublisherId("nintendo");
        game1.setName("Mario");
        Map<String, Integer> timePlayed1 = new HashMap<>();
        timePlayed1.put("2024-01-20", 5);
        game1.setTimePlayed(timePlayed1);
        
        Game game2 = new Game();
        game2.setPublisherId("konami");
        game2.setName("Castlevania");
        Map<String, Integer> timePlayed2 = new HashMap<>();
        timePlayed2.put("2024-01-20", 8);
        game2.setTimePlayed(timePlayed2);
        
        when(publisherService.validatePublisherId("nintendo")).thenReturn(true);
        when(publisherService.validatePublisherId("konami")).thenReturn(true);
        when(gameRepository.save(game1)).thenReturn(game1);
        when(gameRepository.save(game2)).thenReturn(game2);
        
        // Act
        Game created1 = gameService.createGame(game1);
        Game created2 = gameService.createGame(game2);
        
        // Assert
        assertNotNull(created1);
        assertNotNull(created2);
        verify(gameRepository, times(2)).save(any(Game.class));
    }
}
