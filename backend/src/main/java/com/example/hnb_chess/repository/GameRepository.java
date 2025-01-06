// repository/GameRepository.java
package com.example.hnb_chess.repository;

import com.example.hnb_chess.model.Game;
import com.example.hnb_chess.model.enums.GameStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, String> {
  List<Game> findByStatus(GameStatus status);
}