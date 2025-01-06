// src/components/ChessBoard.tsx
import { useState } from 'react';
import { Chessboard } from 'react-chessboard';
import { Chess } from 'chess.js';
import { Game, TeamColor, PlayerRole } from '../types/game';

interface ChessBoardProps {
    game: Game;
    currentPlayerId?: string | null;
    onBrainSelect?: (piece: string) => void;
    onHandMove?: (move: { from: string; to: string }) => void;
}

const ChessBoard = ({ game, currentPlayerId, onBrainSelect, onHandMove }: ChessBoardProps) => {
    const [chessInstance] = useState(new Chess(game.fen));

    const canMove = (): boolean => {
        if (!currentPlayerId) return false;
        
        const isHand = game.currentRole === PlayerRole.HAND;
        const isWhiteTurn = game.currentTeam === TeamColor.WHITE;
        
        return isHand && (
            (isWhiteTurn && currentPlayerId === game.whiteHand?.id) ||
            (!isWhiteTurn && currentPlayerId === game.blackHand?.id)
        );
    };

    const makeMove = (sourceSquare: string, targetSquare: string) => {
        if (!canMove()) return false;

        try {
            const move = chessInstance.move({
                from: sourceSquare,
                to: targetSquare,
                promotion: 'q'
            });

            if (move) {
                onHandMove?.({ from: sourceSquare, to: targetSquare });
                return true;
            }
        } catch (error) {
            console.error('Invalid move:', error);
        }
        return false;
    };

    const pieces = ['PAWN', 'KNIGHT', 'BISHOP', 'ROOK', 'QUEEN', 'KING'];

    return (
        <div className="flex flex-col items-center space-y-4">
            <div className="w-[560px] aspect-square">
                <Chessboard 
                    position={game.fen}
                    onPieceDrop={makeMove}
                    boardWidth={560}
                    customBoardStyle={{
                        borderRadius: '4px',
                        boxShadow: '0 4px 6px -1px rgb(0 0 0 / 0.1)'
                    }}
                />
            </div>
            {game.currentRole === PlayerRole.BRAIN && (
                <div className="flex space-x-2">
                    {pieces.map(piece => (
                        <button
                            key={piece}
                            onClick={() => onBrainSelect?.(piece)}
                            disabled={!currentPlayerId || game.selectedPiece === piece}
                            className={`
                                px-4 py-2 rounded-md font-medium
                                ${!currentPlayerId || game.selectedPiece === piece
                                    ? 'bg-gray-600 text-gray-400 cursor-not-allowed'
                                    : 'bg-blue-600 text-white hover:bg-blue-700 transition-colors'}
                            `}
                        >
                            {piece}
                        </button>
                    ))}
                </div>
            )}
        </div>
    );
};

export default ChessBoard;