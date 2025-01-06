// model/Player.java
package com.example.hnb_chess.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "players")
public class Player {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;
  
  @Column(unique = true)
  private String username;
  
  private Integer rating = 1200;
}