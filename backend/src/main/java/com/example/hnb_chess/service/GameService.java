// service/GameService.java
package com.example.hnb_chess.service;

import com.example.hnb_chess.model.Game;
import com.example.hnb_chess.model.Player;
import com.example.hnb_chess.model.enums.GameStatus;
import com.example.hnb_chess.model.enums.PlayerRole;
import com.example.hnb_chess.model.enums.TeamColor;
import com.example.hnb_chess.model.GameSuggestion;
import com.example.hnb_chess.model.GameMove;
import com.example.hnb_chess.model.enums.PieceSuggestions;
import com.example.hnb_chess.exception.GameException;
import com.example.hnb_chess.exception.PlayerException;
import com.example.hnb_chess.repository.GameRepository;
import com.example.hnb_chess.repository.GameSuggestionRepository;
import com.example.hnb_chess.repository.GameMoveRepository;

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
  private final GameSuggestionRepository gameSuggestionRepository;
  private final GameMoveRepository gameMoveRepository;

  @Autowired
  public GameService(
    GameRepository gameRepository, 
    PlayerService playerService, 
    GameSuggestionRepository gameSuggestionRepository,
    GameMoveRepository gameMoveRepository
    ) {
    this.gameRepository = gameRepository;
    this.playerService = playerService;
    this.gameSuggestionRepository = gameSuggestionRepository;
    this.gameMoveRepository = gameMoveRepository;
  }

  public Game createGame(
    String playerId, 
    TeamColor teamColor, 
    PlayerRole role
  ) {
    Player player = playerService.getPlayer(playerId);

    Game game = new Game();
      
    // Assign player to their chosen position
    assignPlayerToPosition(game, player, teamColor, role);
      
    return gameRepository.save(game);
  }

  public Game joinGame(
    String gameId, 
    String playerId, 
    TeamColor teamColor, 
    PlayerRole role
  ) {
    Game game = gameRepository.findById(gameId).orElseThrow(() -> new GameException("Game not found"));
          
    Player player = playerService.getPlayer(playerId);

    // if(game.getWhiteHand() == playerId) {
    //   return game
    // }

    if (isPositionTaken(game, teamColor, role)){
      throw new RuntimeException("Position already taken");
    }

    assignPlayerToPosition(game, player, teamColor, role);
      
    if (areAllPlayersJoined(game)) {
      game.setStatus(GameStatus.IN_PROGRESS);
      // can also abstract this to a method start game.
    }

    return gameRepository.save(game);
  }

  public List<Game> getAllGames() {
    return gameRepository.findAll();
  }

  public Game getGame(String gameId) {
    return gameRepository.findById(gameId).orElseThrow(() -> new GameException("Game not found"));
  }

  public List<Game> getGamesByStatus(GameStatus status) {
    return gameRepository.findByStatus(status);
  }

  public void deleteGame(String gameId) {
    gameRepository.deleteById(gameId);
  }

  private void assignPlayerToPosition(
    Game game, 
    Player player, 
    TeamColor teamColor, 
    PlayerRole role
  ) {
    if (teamColor == TeamColor.WHITE) {
      if(role == PlayerRole.HAND){
        game.setWhiteHand(player);
      }
      else{
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

  private boolean isPositionTaken(
    Game game, 
    TeamColor teamColor, 
    PlayerRole role
  ) {
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

  public Game handleBrainSelection(
    String gameId, 
    String playerId, 
    String selectedPiece
    ) {
    Game game = gameRepository.findById(gameId).orElseThrow(() -> new GameException("Game not found"));

    validateBrainTurn(game, playerId);

    
    // Check if selected piece has legal moves
    List<String> legalMoves = getLegalMovesForPiece(game.getFen(), selectedPiece);
    if (legalMoves.isEmpty()) {
      throw new GameException("No legal moves for selected piece");
    }

    addGameSuggestion(game, playerId, selectedPiece);
    
    game.setSelectedPiece(selectedPiece);
    game.setCurrentRole(PlayerRole.HAND);

    return gameRepository.save(game);
  }

  public GameSuggestion addGameSuggestion(
    Game game, 
    String playerId, 
    String pieceType
  ) {
    Player player = playerService.getPlayer(playerId);

    GameSuggestion gameSuggestion = new GameSuggestion();
    gameSuggestion.setGame(game);
    gameSuggestion.setPlayer(player);
    gameSuggestion.setPieceType(PieceSuggestions.valueOf(pieceType.toUpperCase()));
    gameSuggestion.setSuggestionNumber(gameSuggestion.getSuggestionNumber() + 1);

    return gameSuggestionRepository.save(gameSuggestion);
  }

  public Game handleHandMove(String gameId, String playerId, String move) {
    Game game = gameRepository.findById(gameId).orElseThrow(() -> new GameException("Game not found"));

    validateHandTurn(game, playerId);

    validateMoveMatchesSelectedPiece(game, move);

    try {
      Board board = new Board();
      board.loadFromFen(game.getFen());
        
      Move chessMove = new Move(move, board.getSideToMove());
        
      if (!board.legalMoves().contains(chessMove)) {
        throw new GameException("Illegal move");
      }

      addGameMove(game, playerId, move);

      board.doMove(chessMove);
      game.setFen(board.getFen());
      game.setSelectedPiece(null);
      game.setCurrentTeam(game.getCurrentTeam() == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE);
      game.setCurrentRole(PlayerRole.BRAIN);

      checkGameEnd(game);

      return gameRepository.save(game);
    } catch (Exception e) {
      throw new GameException("Invalid move format");
    }
  }

  public GameMove addGameMove(
    Game game, 
    String playerId, 
    String move
  ) {
    Player player = playerService.getPlayer(playerId);

    GameMove gameMove = new GameMove();
    gameMove.setGame(game);
    gameMove.setPlayer(player);
    gameMove.setMoveNumber(gameMove.getMoveNumber() + 1);
    gameMove.setMove(move);
    gameMove.setFen(calculateNewFen(game.getFen(), move));

    return gameMoveRepository.save(gameMove);
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
    if ((game.getCurrentTeam() == TeamColor.WHITE && !isWhitePiece) ||(game.getCurrentTeam() == TeamColor.BLACK && isWhitePiece)) 
    {
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

      TeamColor winner = game.getCurrentTeam() == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
      // Could add a winner field to Game class
    } else if (board.isDraw() || board.isStaleMate()) { 
      // add more draw conditions + abandoned
      game.setStatus(GameStatus.FINISHED);
    }
  }

  private String calculateNewFen(String currentFen, String move) {
    try {
      Board board = new Board();
      board.loadFromFen(currentFen);
      
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
      
    return board.legalMoves().stream().filter(
      move -> 
        {
          Piece piece = board.getPiece(move.getFrom());
          return piece.getPieceType().name().equalsIgnoreCase(pieceType);
        }
      )
      .map(Move::toString)
      .collect(Collectors.toList());
  }

  public List<GameSuggestion> getGameSuggestions(String gameId) {
    // Game game = GameSuggestionRepository.findById(gameId).orElseThrow(() -> new GameException("Game not found")); fix

    return gameSuggestionRepository.findByGameIdOrderBySuggestionNumberAsc(gameId);
  }

  public List<GameMove> getGameMoves(String gameId) {
    // Game game = gameRepository.findById(gameId).orElseThrow(() -> new GameException("Game not found")); fix

    return gameMoveRepository.findByGameIdOrderByMoveNumberAsc(gameId);
  }
}