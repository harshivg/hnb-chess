// src/services/api.ts
import { Game, TeamColor, PlayerRole, Player } from '../types/game';

const API_BASE = 'http://localhost:8080/api';

export const api = {
    players: {
        getAll: async (): Promise<Player[]> => {
            const response = await fetch(`${API_BASE}/players`);
            if (!response.ok) throw new Error('Failed to fetch players');
            return response.json();
        },

        create: async (username: string): Promise<Player> => {
            const response = await fetch(`${API_BASE}/players?username=${encodeURIComponent(username)}`, {
                method: 'POST'
            });
            if (!response.ok) throw new Error('Failed to create player');
            return response.json();
        }
    },

    games: {
        getAll: async (): Promise<Game[]> => {
            const response = await fetch(`${API_BASE}/games`);
            if (!response.ok) throw new Error('Failed to fetch games');
            return response.json();
        },

        create: async (playerId: string, teamColor: TeamColor, role: PlayerRole): Promise<Game> => {
            const response = await fetch(
                `${API_BASE}/games?playerId=${playerId}&teamColor=${teamColor}&role=${role}`,
                { method: 'POST' }
            );
            if (!response.ok) throw new Error('Failed to create game');
            return response.json();
        },

        join: async (gameId: string, playerId: string, teamColor: TeamColor, role: PlayerRole): Promise<Game> => {
            const response = await fetch(
                `${API_BASE}/games/${gameId}/join?playerId=${playerId}&teamColor=${teamColor}&role=${role}`,
                { method: 'POST' }
            );
            if (!response.ok) throw new Error('Failed to join game');
            return response.json();
        }
    }
};