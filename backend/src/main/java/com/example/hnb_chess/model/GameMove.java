// backend/model/GameMove.java
@Entity
@Table(name = "game_moves")
@Data
public class GameMove {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    private String move;  // e.g., "e2e4"
    private String piece; // For brain suggestions
    
    @Enumerated(EnumType.STRING)
    private PlayerRole playerRole;  // BRAIN or HAND

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}