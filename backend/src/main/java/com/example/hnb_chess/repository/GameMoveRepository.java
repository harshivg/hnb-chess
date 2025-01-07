package com.example.hnb_chess.repository;

import com.example.hnb_chess.model.GameMove;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GameMoveRepository extends JpaRepository<GameMove, String> {
    List<GameMove> findByGameId(String gameId);
    List<GameMove> findByGameIdOrderByMoveNumberAsc(String gameId);
}