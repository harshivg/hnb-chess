// service/PlayerService.java
package com.example.hnb_chess.service;

import com.example.hnb_chess.model.Player;
import com.example.hnb_chess.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PlayerService {
  private final PlayerRepository playerRepository;

  @Autowired
  public PlayerService(PlayerRepository playerRepository) {
    this.playerRepository = playerRepository;
  }

  public Player createPlayer(String username) {
    if (playerRepository.existsByUsername(username)) {
      throw new RuntimeException("Username already exists");
    }
    
    Player player = new Player();
    player.setUsername(username);
    player.setRating(1200); // Default rating
    return playerRepository.save(player);
  }

  public Player getPlayer(String id) {
    return playerRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("Player not found"));
  }

  public List<Player> getAllPlayers() {
    return playerRepository.findAll();
  }

  public void deletePlayer(String id) {
    playerRepository.deleteById(id);
  }

  public List<Player> getAvailablePlayers() {
    return playerRepository.findAll();
  }
}