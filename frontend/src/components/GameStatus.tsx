// src/components/GameStatus.tsx
import { Game, Player } from '../types/game';
import { getPlayerRole } from '../utils/gameUtils';

interface GameStatusProps {
    game: Game | null;
    currentPlayerId: string | null;
    players: Player[];
    isConnected: boolean;
}

const GameStatus = ({ game, currentPlayerId, players, isConnected }: GameStatusProps) => {
    return (
        <div className="p-4 bg-gray-800 rounded-lg">
            <h3 className="text-lg font-semibold mb-2 text-white">
                Status
            </h3>
            <div className="space-y-2">
                <div className="text-sm text-gray-400">
                    WebSocket: {isConnected ? 'Connected' : 'Disconnected'}
                </div>
                <div className="text-sm text-gray-400">
                    Current Player: {
                        players.find(p => p.id === currentPlayerId)?.username || 
                        'None selected'
                    }
                </div>
                {game && (
                    <div className="text-sm text-gray-400">
                        Your Role: {
                            getPlayerRole(game, currentPlayerId)
                                ? `${getPlayerRole(game, currentPlayerId)?.team} ${getPlayerRole(game, currentPlayerId)?.role}`
                                : 'Spectator'
                        }
                    </div>
                )}
                <div className="text-sm text-gray-400">
                    Game Status: {game?.status || 'No game selected'}
                </div>
                <div className="text-sm text-gray-400">
                    Players: {players.length}
                </div>
            </div>
        </div>
    );
};

export default GameStatus;