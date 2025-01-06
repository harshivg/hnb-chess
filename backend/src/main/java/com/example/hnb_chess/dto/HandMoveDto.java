// dto/HandMoveDto.java
package com.example.hnb_chess.dto;

import lombok.Data;

@Data
public class HandMoveDto {
    private String gameId;
    private String playerId;
    private String move;
}