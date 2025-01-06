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
  createdAt: string;
  updatedAt: string;
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