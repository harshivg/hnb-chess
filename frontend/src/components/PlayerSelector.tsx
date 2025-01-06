// src/components/PlayerSelector.tsx
import { Player } from '../types/game';

interface PlayerSelectorProps {
    currentPlayerId: string | null;
    onPlayerSelect: (playerId: string) => void;
    players: Player[];  // Changed to Player array
}

const PlayerSelector = ({ currentPlayerId, onPlayerSelect, players }: PlayerSelectorProps) => {
    return (
        <select
            value={currentPlayerId ?? ''}
            onChange={(e) => onPlayerSelect(e.target.value)}
            className="bg-gray-700 text-white px-4 py-2 rounded-md border border-gray-600"
        >
            <option value="">Select Player</option>
            {players.map(player => (
                <option key={player.id} value={player.id}>
                    {player.username} (Rating: {player.rating})
                </option>
            ))}
        </select>
    );
};

export default PlayerSelector;