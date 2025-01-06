// controller/PlayerController.java
package com.example.hnb_chess.controller;

import com.example.hnb_chess.model.Player;
import com.example.hnb_chess.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
@CrossOrigin(origins = "http://localhost:5173") // For Vite's default port
public class PlayerController {
    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping
    public ResponseEntity<Player> createPlayer(@RequestParam String username) {
        return ResponseEntity.ok(playerService.createPlayer(username));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Player> getPlayer(@PathVariable String id) {
        return ResponseEntity.ok(playerService.getPlayer(id));
    }

    @GetMapping
    public ResponseEntity<List<Player>> getAllPlayers() {
        return ResponseEntity.ok(playerService.getAllPlayers());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable String id) {
        playerService.deletePlayer(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/available")
    public ResponseEntity<List<Player>> getAvailablePlayers() {
        return ResponseEntity.ok(playerService.getAvailablePlayers());
    }
}