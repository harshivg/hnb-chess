// src/components/GameInfo.tsx
import { Game, TeamColor, PlayerRole } from '../types/game';

interface GameInfoProps {
    game: Game;
    currentPlayerId?: string | null;
}

const GameInfo = ({ game, currentPlayerId }: GameInfoProps) => {
    const getCurrentPlayer = () => {
        const team = game.currentTeam === TeamColor.WHITE ? 'White' : 'Black';
        const role = game.currentRole === PlayerRole.BRAIN ? 'Brain' : 'Hand';
        return `${team} ${role}'s turn`;
    };

    const getPlayerName = (playerId: string | undefined) => {
        if (!playerId) return 'Waiting...';
        const player = [game.whiteHand, game.whiteBrain, game.blackHand, game.blackBrain]
            .find(p => p?.id === playerId);
        return player?.username || 'Unknown';
    };

    const isCurrentPlayer = (playerId: string | undefined) => {
        return currentPlayerId === playerId;
    };

    return (
        <div className="p-6 bg-gray-800 rounded-lg shadow-lg">
            <h2 className="text-2xl font-bold text-white mb-4">
                Game Status: {game.status}
            </h2>
            <div className="text-lg text-green-400 mb-4">
                {getCurrentPlayer()}
            </div>
            {game.selectedPiece && (
                <div className="text-yellow-400 mb-4">
                    Selected Piece: {game.selectedPiece}
                </div>
            )}
            <div className="grid grid-cols-2 gap-8">
                <div className="bg-gray-700 p-4 rounded-lg">
                    <h3 className="text-xl font-semibold text-white mb-3">White Team</h3>
                    <div className={`mb-2 ${isCurrentPlayer(game.whiteBrain?.id) ? 'text-blue-400 font-bold' : 'text-gray-300'}`}>
                        Brain: {getPlayerName(game.whiteBrain?.id)}
                    </div>
                    <div className={`${isCurrentPlayer(game.whiteHand?.id) ? 'text-blue-400 font-bold' : 'text-gray-300'}`}>
                        Hand: {getPlayerName(game.whiteHand?.id)}
                    </div>
                </div>
                <div className="bg-gray-700 p-4 rounded-lg">
                    <h3 className="text-xl font-semibold text-white mb-3">Black Team</h3>
                    <div className={`mb-2 ${isCurrentPlayer(game.blackBrain?.id) ? 'text-blue-400 font-bold' : 'text-gray-300'}`}>
                        Brain: {getPlayerName(game.blackBrain?.id)}
                    </div>
                    <div className={`${isCurrentPlayer(game.blackHand?.id) ? 'text-blue-400 font-bold' : 'text-gray-300'}`}>
                        Hand: {getPlayerName(game.blackHand?.id)}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default GameInfo;