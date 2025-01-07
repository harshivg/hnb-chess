// src/pages/GamePage.tsx
import { useEffect } from 'react';
import { useParams } from 'react-router-dom';
import GameBoard from '../components/GameBoard';
import GameInfo from '../components/GameInfo';
import GameStatus from '../components/GameStatus';
import { api } from '../services/api';  // Make sure to import api
import { Game, Player } from '../types/game';

interface GamePageProps {
    game: Game | null;
    currentPlayerId: string | null;
    players: Player[];
    isConnected: boolean;
    onBrainSelect: (piece: string) => void;
    onHandMove: (move: { from: string; to: string }) => void;
    setGame: (game: Game) => void;
    setError: (error: string) => void;
}

export const GamePage = ({
    game,
    currentPlayerId,
    players,
    isConnected,
    onBrainSelect,
    onHandMove,
    setGame,
    setError
}: GamePageProps) => {
    const { gameId } = useParams();
    
    useEffect(() => {
        console.log('GamePage mounted, gameId:', gameId);
        
        const fetchGame = async () => {
            if (!gameId) return;
            
            console.log('Fetching game data for:', gameId);
            try {
                const fetchedGame = await api.games.getGame(gameId);
                console.log('Fetched game:', fetchedGame);
                setGame(fetchedGame);
            } catch (err) {
                console.error('Error fetching game:', err);
                setError('Failed to fetch game');
            }
        };

        fetchGame();
    }, [gameId, setGame, setError, currentPlayerId]);

    console.log('Current game state:', game);

    if (!game) {
        return <div>Loading game...</div>;
    }

    return (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8 mt-16">
            <div className="lg:col-span-2">
                <GameBoard
                    game={game}
                    currentPlayerId={currentPlayerId}
                    onBrainSelect={onBrainSelect}
                    onHandMove={onHandMove}
                />
            </div>
            <div className="space-y-4">
                <GameInfo 
                    game={game}
                    currentPlayerId={currentPlayerId}
                />
                <GameStatus 
                    game={game}
                    currentPlayerId={currentPlayerId}
                    players={players}
                    isConnected={isConnected}
                />
            </div>
        </div>
    );
};