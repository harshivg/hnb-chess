// dto/BrainMoveDto.java
package com.example.hnb_chess.dto;

import lombok.Data;

@Data
public class BrainMoveDto {
    private String gameId;
    private String playerId;
    private String selectedPiece;
}
