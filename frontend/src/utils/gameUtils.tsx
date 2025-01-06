// src/utils/gameUtils.ts
import { Game } from '../types/game';

export interface PlayerRole {
    team: string;
    role: string;
}

export const getPlayerRole = (game: Game | null, playerId: string | null): PlayerRole | null => {
    if (!game || !playerId) return null;
    
    if (game.whiteHand?.id === playerId) return { team: 'White', role: 'Hand' };
    if (game.whiteBrain?.id === playerId) return { team: 'White', role: 'Brain' };
    if (game.blackHand?.id === playerId) return { team: 'Black', role: 'Hand' };
    if (game.blackBrain?.id === playerId) return { team: 'Black', role: 'Brain' };
    
    return null;
};