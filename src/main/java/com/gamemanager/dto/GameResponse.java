package com.gamemanager.dto;

import com.gamemanager.model.Game;
import java.util.List;

public class GameResponse {
    private int count;
    private List<Game> games;
    private String message;

    public GameResponse(List<Game> games, String message){
        this.count = games.size();
        this.games = games;
        this.message = message;
    }

    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
    public List<Game> getGames() { return games; }
    public void setGames(List<Game> games) { this.games = games; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
