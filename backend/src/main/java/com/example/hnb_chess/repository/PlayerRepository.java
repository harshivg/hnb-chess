// repository/PlayerRepository.java
package com.example.hnb_chess.repository;

import com.example.hnb_chess.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<Player, String> {
  boolean existsByUsername(String username);
}
