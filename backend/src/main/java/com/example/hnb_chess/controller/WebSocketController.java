package com.example.hnb_chess.controller;

import com.example.hnb_chess.dto.BrainMoveDto;
import com.example.hnb_chess.dto.HandMoveDto;
import com.example.hnb_chess.service.GameService;
import com.example.hnb_chess.model.Game;
import com.example.hnb_chess.exception.GameException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
    private final GameService gameService;

    @Autowired
    public WebSocketController(GameService gameService) {
        this.gameService = gameService;
    }

    @MessageMapping("/brain.select")
    @SendTo("/topic/game")  // Removed {gameId}
    public Game handleBrainSelection(BrainMoveDto brainMove) {
        try {
            return gameService.handleBrainSelection(
                brainMove.getGameId(), 
                brainMove.getPlayerId(), 
                brainMove.getSelectedPiece()
            );
        } catch (GameException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @MessageMapping("/hand.move")
    @SendTo("/topic/game")  // Removed {gameId}
    public Game handleHandMove(HandMoveDto handMove) {
        try {
            return gameService.handleHandMove(
                handMove.getGameId(), 
                handMove.getPlayerId(), 
                handMove.getMove()
            );
        } catch (GameException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public String handleException(Throwable exception) {
        return exception.getMessage();
    }
}