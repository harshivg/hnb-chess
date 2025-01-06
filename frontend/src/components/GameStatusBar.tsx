// src/components/GameStatusBar.tsx
import { Game } from '../types/game';

interface PlayerRole {
    team: string;
    role: string;
}

interface GameStatusBarProps {
    game: Game;
    currentPlayerId: string | null;
    playerRole: PlayerRole | null;
}

const GameStatusBar = ({ game, currentPlayerId, playerRole }: GameStatusBarProps) => {
    if (!game || !currentPlayerId) return null;
    
    return (
        <div className="bg-gray-700 p-4 rounded-lg mb-4">
            <div className="flex justify-between items-center">
                <div>
                    <span className="text-gray-400">Your Role:</span>
                    <span className="ml-2 text-white font-bold">
                        {playerRole ? `${playerRole.team} ${playerRole.role}` : 'Spectator'}
                    </span>
                </div>
                <div>
                    <span className="text-gray-400">Current Turn:</span>
                    <span className="ml-2 text-white font-bold">
                        {`${game.currentTeam} ${game.currentRole}`}
                    </span>
                </div>
                {game.selectedPiece && (
                    <div>
                        <span className="text-gray-400">Selected Piece:</span>
                        <span className="ml-2 text-white font-bold">
                            {game.selectedPiece}
                        </span>
                    </div>
                )}
            </div>
        </div>
    );
};

export default GameStatusBar;