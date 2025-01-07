// backend/model/GameSuggestion.java
package com.example.hnb_chess.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import com.example.hnb_chess.model.enums.PieceSuggestions;

@Entity
@Table(name = "game_suggestions")
@Data
public class GameSuggestion {
  @Id 
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id; 

  @ManyToOne
  @JoinColumn(name = "game_id")
  private Game game;

  @ManyToOne
  @JoinColumn(name = "player_id")
  private Player player;

  private Integer suggestionNumber = 0;

  @Enumerated(EnumType.STRING)
  @Column(name = "piece_suggestion")
  private PieceSuggestions pieceType;

  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
  }
}
