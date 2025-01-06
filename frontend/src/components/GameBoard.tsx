// src/components/GameBoard.tsx
import { Game } from '../types/game';
import GameStatusBar from './GameStatusBar';
import ChessBoard from './ChessBoard';
import { getPlayerRole } from '../utils/gameUtils';

interface GameBoardProps {
    game: Game | null;
    currentPlayerId: string | null;
    onBrainSelect: (piece: string) => void;
    onHandMove: (move: { from: string; to: string }) => void;
}

const GameBoard = ({ game, currentPlayerId, onBrainSelect, onHandMove }: GameBoardProps) => {
    if (!currentPlayerId) {
        return (
            <div className="flex items-center justify-center h-[560px] bg-gray-800 rounded-lg">
                <p className="text-gray-400">Select a player to start</p>
            </div>
        );
    }

    if (!game) {
        return (
            <div className="flex items-center justify-center h-[560px] bg-gray-800 rounded-lg">
                <p className="text-gray-400">Join or create a game to start playing</p>
            </div>
        );
    }

    return (
        <>
            <GameStatusBar 
                game={game}
                currentPlayerId={currentPlayerId}
                playerRole={getPlayerRole(game, currentPlayerId)}
            />
            <ChessBoard
                game={game}
                currentPlayerId={currentPlayerId}
                onBrainSelect={onBrainSelect}
                onHandMove={onHandMove}
            />
        </>
    );
};

export default GameBoard;