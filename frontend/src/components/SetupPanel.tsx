// src/components/SetupPanel.tsx
import { useState } from 'react';
import { TeamColor, PlayerRole, Game, Player } from '../types/game';

interface SetupPanelProps {
    onCreatePlayer: (username: string) => void;
    onCreateGame: (teamColor: TeamColor, role: PlayerRole) => void;
    onJoinGame: (gameId: string, teamColor: TeamColor, role: PlayerRole) => void;
    isLoading?: boolean;
    players: Player[];
    availableGames: Game[];
    currentPlayerId?: string | null;
    currentGame?: Game | null;
}

const SetupPanel = ({ 
    onCreatePlayer, 
    onCreateGame, 
    onJoinGame, 
    isLoading = false,
    players,
    availableGames,
    currentPlayerId, 
    currentGame
}: SetupPanelProps) => {
    const [username, setUsername] = useState('');
    const [gameId, setGameId] = useState('');
    const [selectedTeam, setSelectedTeam] = useState<TeamColor>(TeamColor.WHITE);
    const [selectedRole, setSelectedRole] = useState<PlayerRole>(PlayerRole.BRAIN);

    return (
        <div className="p-4 bg-gray-800 rounded-lg space-y-4">
            {/* Create Player Section */}
            <div className="space-y-2">
                <h3 className="text-lg font-semibold text-white">Create Player</h3>
                <div className="flex space-x-2">
                    <input
                        type="text"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        placeholder="Username"
                        className="px-3 py-2 bg-gray-700 rounded text-white flex-1"
                        disabled={isLoading}
                    />
                    <button
                        onClick={() => {
                            if (username) {
                                onCreatePlayer(username);
                                setUsername('');
                            }
                        }}
                        disabled={isLoading || !username}
                        className="px-4 py-2 bg-blue-600 rounded hover:bg-blue-700 text-white disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                        Create Player
                    </button>
                </div>
                
                {/* Players List */}
                <div className="mt-2">
                    <h4 className="text-sm font-medium text-gray-400">Current Players:</h4>
                    <div className="mt-1 space-y-1">
                        {players.map(player => (
                            <div 
                                key={player.id} 
                                className={`text-sm ${player.id === currentPlayerId ? 'text-green-400' : 'text-gray-300'}`}
                            >
                                {player.username} (Rating: {player.rating})
                            </div>
                        ))}
                    </div>
                </div>
            </div>

            {/* Game Setup Section */}
            <div className="space-y-2">
                <h3 className="text-lg font-semibold text-white">Game Setup</h3>
                <div className="space-y-2">
                    <select
                        value={selectedTeam}
                        onChange={(e) => setSelectedTeam(e.target.value as TeamColor)}
                        className="w-full px-3 py-2 bg-gray-700 rounded text-white"
                        disabled={isLoading}
                    >
                        <option value={TeamColor.WHITE}>White</option>
                        <option value={TeamColor.BLACK}>Black</option>
                    </select>
                    <select
                        value={selectedRole}
                        onChange={(e) => setSelectedRole(e.target.value as PlayerRole)}
                        className="w-full px-3 py-2 bg-gray-700 rounded text-white"
                        disabled={isLoading}
                    >
                        <option value={PlayerRole.BRAIN}>Brain</option>
                        <option value={PlayerRole.HAND}>Hand</option>
                    </select>
                </div>
                <div className="space-x-2">
                    <button
                        onClick={() => onCreateGame(selectedTeam, selectedRole)}
                        disabled={isLoading || !currentPlayerId}
                        className="px-4 py-2 bg-green-600 rounded hover:bg-green-700 text-white disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                        Create Game
                    </button>
                </div>
            </div>

            {/* Join Game Section */}
            <div className="space-y-2">
                <h3 className="text-lg font-semibold text-white">Join Game</h3>
                {availableGames.length > 0 ? (
                    <div className="space-y-2">
                        <select
                            value={gameId}
                            onChange={(e) => setGameId(e.target.value)}
                            className="w-full px-3 py-2 bg-gray-700 rounded text-white"
                            disabled={isLoading}
                        >
                            <option value="">Select a game</option>
                            {availableGames.map(game => (
                                <option key={game.id} value={game.id}>
                                    Game #{game.id.slice(0, 8)} - 
                                    {game.whiteHand ? 'WH ' : '__ '}
                                    {game.whiteBrain ? 'WB ' : '__ '}
                                    {game.blackHand ? 'BH ' : '__ '}
                                    {game.blackBrain ? 'BB' : '__'}
                                </option>
                            ))}
                        </select>
                        <button
                            onClick={() => {
                                if (gameId) {
                                    onJoinGame(gameId, selectedTeam, selectedRole);
                                    setGameId('');
                                }
                            }}
                            disabled={isLoading || !gameId || !currentPlayerId}
                            className="w-full px-4 py-2 bg-yellow-600 rounded hover:bg-yellow-700 text-white disabled:opacity-50 disabled:cursor-not-allowed"
                        >
                            Join Selected Game
                        </button>
                    </div>
                ) : (
                    <div className="text-gray-400 text-sm">
                        No games available to join
                    </div>
                )}
            </div>
        </div>
    );
};

export default SetupPanel;