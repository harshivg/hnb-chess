// src/types/game.ts
export interface Player {
  id: string;
  username: string;
  rating: number;
}

export interface Game {
  id: string;
  whiteHand: Player | null;
  whiteBrain: Player | null;
  blackHand: Player | null;
  blackBrain: Player | null;
  fen: string;
  status: GameStatus;
  currentTeam: TeamColor;
  currentRole: PlayerRole;
  selectedPiece: string | null;
}

export interface GameSuggestion {
  id: string;
  gameId: string;
  playerId: string;
  pieceType: PieceType;
  suggestionNumber: number;
}

export interface GameMove {
  id: string;
  gameId: string;
  playerId: string;
  move: string;
  moveNumber: number;
  fen: string;
}

export enum GameStatus {
  WAITING_FOR_PLAYERS = 'WAITING_FOR_PLAYERS',
  IN_PROGRESS = 'IN_PROGRESS',
  FINISHED = 'FINISHED'
}

export enum TeamColor {
  WHITE = 'WHITE',
  BLACK = 'BLACK'
}

export enum PlayerRole {
  HAND = 'HAND',
  BRAIN = 'BRAIN'
}

export enum PieceType {
  PAWN = 'PAWN',
  KNIGHT = 'KNIGHT',
  BISHOP = 'BISHOP',
  ROOK = 'ROOK',
  QUEEN = 'QUEEN',
  KING = 'KING'
}

export interface BrainMoveDto {
  gameId: string;
  playerId: string;
  selectedPiece: string;
}

export interface HandMoveDto {
  gameId: string;
  playerId: string;
  move: string;
}