// service/GameService.java
package com.example.hnb_chess.service;

import com.example.hnb_chess.model.Game;
import com.example.hnb_chess.model.Player;
import com.example.hnb_chess.model.enums.GameStatus;
import com.example.hnb_chess.model.enums.PlayerRole;
import com.example.hnb_chess.model.enums.TeamColor;
import com.example.hnb_chess.exception.GameException;
import com.example.hnb_chess.repository.GameRepository;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.Side;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import com.github.bhlangonijr.chesslib.Piece;

@Service
public class GameService {
  private final GameRepository gameRepository;
  private final PlayerService playerService;

  @Autowired
  public GameService(GameRepository gameRepository, PlayerService playerService) {
    this.gameRepository = gameRepository;
    this.playerService = playerService;
  }

  public Game createGame(String playerId, TeamColor teamColor, PlayerRole role) {
    Player player = playerService.getPlayer(playerId);
    Game game = new Game();
      
    // Assign player to their chosen position
    assignPlayerToPosition(game, player, teamColor, role);
      
    return gameRepository.save(game);
  }

  public Game joinGame(String gameId, String playerId, TeamColor teamColor, PlayerRole role) {
    Game game = gameRepository.findById(gameId)
        .orElseThrow(() -> new RuntimeException("Game not found"));
          
    Player player = playerService.getPlayer(playerId);

    if (isPositionTaken(game, teamColor, role)){
      throw new RuntimeException("Position already taken");
    }

    assignPlayerToPosition(game, player, teamColor, role);
      
    if (areAllPlayersJoined(game)) {
        game.setStatus(GameStatus.IN_PROGRESS);
    }

    return gameRepository.save(game);
  }

  public List<Game> getAllGames() {
    return gameRepository.findAll();
  }

  public Game getGame(String gameId) {
    return gameRepository.findById(gameId)
        .orElseThrow(() -> new GameException("Game not found"));
  }

  public List<Game> getGamesByStatus(GameStatus status) {
    return gameRepository.findByStatus(status);
  }

  // public List<Game> getGamesByPlayer(String playerId) {
  //   return gameRepository.findByWhiteHandIdOrWhiteBrainIdOrBlackHandIdOrBlackBrainId(
  //       playerId, playerId, playerId, playerId);
  // }

  public void deleteGame(String gameId) {
    gameRepository.deleteById(gameId);
  }

  private void assignPlayerToPosition(Game game, Player player, TeamColor teamColor, PlayerRole role) {
    if (teamColor == TeamColor.WHITE) {
      if (role == PlayerRole.HAND) {
        game.setWhiteHand(player);
      } else {
        game.setWhiteBrain(player);
      }
    } else {
      if (role == PlayerRole.HAND) {
        game.setBlackHand(player);
        } else {
        game.setBlackBrain(player);
      }
    }
  }

  private boolean isPositionTaken(Game game, TeamColor teamColor, PlayerRole role) {
    if (teamColor == TeamColor.WHITE) {
      return (role == PlayerRole.HAND && game.getWhiteHand() != null) ||
      (role == PlayerRole.BRAIN && game.getWhiteBrain() != null);
    } else {
      return (role == PlayerRole.HAND && game.getBlackHand() != null) ||
      (role == PlayerRole.BRAIN && game.getBlackBrain() != null);
    }
  }

  private boolean areAllPlayersJoined(Game game) {
    return game.getWhiteHand() != null &&
      game.getWhiteBrain() != null &&
      game.getBlackHand() != null &&
      game.getBlackBrain() != null;
  }

  // Add these methods to GameService
  // Update handleBrainSelection to use this
  public Game handleBrainSelection(String gameId, String playerId, String selectedPiece) {
      Game game = gameRepository.findById(gameId)
          .orElseThrow(() -> new GameException("Game not found"));

      validateBrainTurn(game, playerId);

      // Check if selected piece has legal moves
      List<String> legalMoves = getLegalMovesForPiece(game.getFen(), selectedPiece);
      if (legalMoves.isEmpty()) {
          throw new GameException("No legal moves for selected piece");
      }

      game.setSelectedPiece(selectedPiece);
      game.setCurrentRole(PlayerRole.HAND);

      return gameRepository.save(game);
  }

  public Game handleHandMove(String gameId, String playerId, String move) {
      Game game = gameRepository.findById(gameId)
          .orElseThrow(() -> new GameException("Game not found"));

      // Validate it's hand's turn
      validateHandTurn(game, playerId);

      // Validate the move is for the selected piece
      validateMoveMatchesSelectedPiece(game, move);

      try {
          Board board = new Board();
          board.loadFromFen(game.getFen());
          
          Move chessMove = new Move(move, board.getSideToMove());
          
          // Validate move is legal
          if (!board.legalMoves().contains(chessMove)) {
              throw new GameException("Illegal move");
          }

          // Make the move
          board.doMove(chessMove);
          game.setFen(board.getFen());
          game.setSelectedPiece(null);
          game.setCurrentTeam(game.getCurrentTeam() == TeamColor.WHITE ? 
              TeamColor.BLACK : TeamColor.WHITE);
          game.setCurrentRole(PlayerRole.BRAIN);

          checkGameEnd(game);

          return gameRepository.save(game);
      } catch (Exception e) {
          throw new GameException("Invalid move format");
      }
  }

  private void validateMoveMatchesSelectedPiece(Game game, String move) {
      if (game.getSelectedPiece() == null) {
          throw new GameException("Brain hasn't selected a piece yet");
      }

      Board board = new Board();
      board.loadFromFen(game.getFen());
      
      // Get the piece at the 'from' square
      String fromSquare = move.substring(0, 2); // e.g., "e2" from "e2e4"
      Piece piece = board.getPiece(Square.valueOf(fromSquare.toUpperCase()));
      
      // Check if it's the type of piece that brain selected
      if (!piece.getPieceType().name().equalsIgnoreCase(game.getSelectedPiece())) {
          throw new GameException("Must move the piece type selected by brain: " + game.getSelectedPiece());
      }

      // Verify piece color matches current team
      boolean isWhitePiece = piece.toString().startsWith("W");  // White pieces start with 'W'
      if ((game.getCurrentTeam() == TeamColor.WHITE && !isWhitePiece) ||
          (game.getCurrentTeam() == TeamColor.BLACK && isWhitePiece)) {
          throw new GameException("Must move your own piece");
      }
  }

  private void validateBrainTurn(Game game, String playerId) {
    if (game.getCurrentRole() != PlayerRole.BRAIN) {
      throw new GameException("It's not brain's turn");
    }

    boolean isCorrectBrain = game.getCurrentTeam() == TeamColor.WHITE ?
      playerId.equals(game.getWhiteBrain().getId()) :
      playerId.equals(game.getBlackBrain().getId());

    if (!isCorrectBrain) {
      throw new GameException("Not your turn");
    }
  }

  private void validateHandTurn(Game game, String playerId) {
    if (game.getCurrentRole() != PlayerRole.HAND) {
      throw new GameException("It's not hand's turn");
    }

    boolean isCorrectHand = game.getCurrentTeam() == TeamColor.WHITE ?
      playerId.equals(game.getWhiteHand().getId()) :
      playerId.equals(game.getBlackHand().getId());

    if (!isCorrectHand) {
      throw new GameException("Not your turn");
    }
  }

  private void checkGameEnd(Game game) {
    Board board = new Board();
    board.loadFromFen(game.getFen());
    
    if (board.isMated()) {
        game.setStatus(GameStatus.FINISHED);
        // Determine winner based on current team
        TeamColor winner = game.getCurrentTeam() == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
        // Could add a winner field to Game class
    } else if (board.isDraw() || board.isStaleMate()) {
        game.setStatus(GameStatus.FINISHED);
    }
  }

  private String calculateNewFen(String currentFen, String move) {
    try {
        Board board = new Board();
        board.loadFromFen(currentFen);
        
        // Convert string move to Move object
        Move chessMove = new Move(move, board.getSideToMove());
        
        // Validate and make move
        if (board.legalMoves().contains(chessMove)) {
            board.doMove(chessMove);
            return board.getFen();
        } else {
            throw new GameException("Illegal move");
        }
    } catch (Exception e) {
        throw new GameException("Invalid move format");
    }
  }

  private List<String> getLegalMovesForPiece(String fen, String pieceType) {
    Board board = new Board();
    board.loadFromFen(fen);
      
    return board.legalMoves().stream()
        .filter(move -> {
            Piece piece = board.getPiece(move.getFrom());
            return piece.getPieceType().name().equalsIgnoreCase(pieceType);
        })
        .map(Move::toString)
        .collect(Collectors.toList());
  }

}