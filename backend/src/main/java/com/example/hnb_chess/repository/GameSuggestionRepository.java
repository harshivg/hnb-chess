
package com.example.hnb_chess.repository;

import com.example.hnb_chess.model.GameSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GameSuggestionRepository extends JpaRepository<GameSuggestion, String> {
    List<GameSuggestion> findByGameId(String gameId);
    List<GameSuggestion> findByGameIdOrderBySuggestionNumberAsc(String gameId);
}