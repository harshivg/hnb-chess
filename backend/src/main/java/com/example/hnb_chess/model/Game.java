// model/Game.java
package com.example.hnb_chess.model;

import com.example.hnb_chess.model.enums.GameStatus;
import com.example.hnb_chess.model.enums.PlayerRole;
import com.example.hnb_chess.model.enums.TeamColor;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "games")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "white_hand_id")
    private Player whiteHand;

    @ManyToOne
    @JoinColumn(name = "white_brain_id")
    private Player whiteBrain;

    @ManyToOne
    @JoinColumn(name = "black_hand_id")
    private Player blackHand;

    @ManyToOne
    @JoinColumn(name = "black_brain_id")
    private Player blackBrain;

    private String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    @Enumerated(EnumType.STRING)
    @Column(name = "game_status")  // Changed from status
    private GameStatus status = GameStatus.WAITING_FOR_PLAYERS;

    @Enumerated(EnumType.STRING)
    @Column(name = "active_team")  // Changed from current_team
    private TeamColor currentTeam = TeamColor.WHITE;

    @Enumerated(EnumType.STRING)
    @Column(name = "active_role")  // Changed from current_role
    private PlayerRole currentRole = PlayerRole.BRAIN;

    @Column(name = "piece_selected")  // Changed from selected_piece
    private String selectedPiece;

    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}