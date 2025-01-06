// controller/GameController.java
package com.example.hnb_chess.controller;

import com.example.hnb_chess.model.Game;
import com.example.hnb_chess.model.enums.GameStatus;
import com.example.hnb_chess.model.enums.PlayerRole;
import com.example.hnb_chess.model.enums.TeamColor;
import com.example.hnb_chess.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
@CrossOrigin(origins = "http://localhost:5173") // For Vite's default port
public class GameController {
    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping
    public ResponseEntity<Game> createGame(
            @RequestParam String playerId,
            @RequestParam TeamColor teamColor,
            @RequestParam PlayerRole role) {
        return ResponseEntity.ok(gameService.createGame(playerId, teamColor, role));
    }

    @PostMapping("/{gameId}/join")
    public ResponseEntity<Game> joinGame(
            @PathVariable String gameId,
            @RequestParam String playerId,
            @RequestParam TeamColor teamColor,
            @RequestParam PlayerRole role) {
        return ResponseEntity.ok(gameService.joinGame(gameId, playerId, teamColor, role));
    }

    @GetMapping
    public ResponseEntity<List<Game>> getAllGames() {
        return ResponseEntity.ok(gameService.getAllGames());
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<Game> getGame(@PathVariable String gameId) {
        return ResponseEntity.ok(gameService.getGame(gameId));
    }

    @GetMapping("/available")
    public ResponseEntity<List<Game>> getAvailableGames() {
        return ResponseEntity.ok(gameService.getGamesByStatus(GameStatus.WAITING_FOR_PLAYERS));
    }

    // @GetMapping("/player/{playerId}")
    // public ResponseEntity<List<Game>> getPlayerGames(@PathVariable String playerId) {
    //     return ResponseEntity.ok(gameService.getGamesByPlayer(playerId));
    // }

    @DeleteMapping("/{gameId}")
    public ResponseEntity<Void> deleteGame(@PathVariable String gameId) {
        gameService.deleteGame(gameId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{gameId}/status")
    public ResponseEntity<GameStatus> getGameStatus(@PathVariable String gameId) {
        return ResponseEntity.ok(gameService.getGame(gameId).getStatus());
    }
}