// src/App.tsx
import { useState, useEffect } from 'react';
import Layout from './components/Layout';
import GameBoard from './components/GameBoard';
import GameInfo from './components/GameInfo';
import PlayerSelector from './components/PlayerSelector';
import ConnectionStatus from './components/ConnectionStatus';
import SetupPanel from './components/SetupPanel';
import GameStatus from './components/GameStatus';
import { Game, TeamColor, PlayerRole, Player } from './types/game';
import { WebSocketService } from './services/webSocketService';
import { api } from './services/api';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import {GamePage} from './pages/GamePage';

// Initialize WebSocket service
const webSocketService = new WebSocketService();

// Error message timeout duration
const ERROR_TIMEOUT = 5000;

function App() {
    // Core game state
    const [game, setGame] = useState<Game | null>(null);
    const [currentPlayerId, setCurrentPlayerId] = useState<string | null>(null);
    
    // Connection and loading states
    const [isConnected, setIsConnected] = useState<boolean>(false);
    const [isLoading, setIsLoading] = useState<boolean>(false);
    
    // Data states
    const [players, setPlayers] = useState<Player[]>([]);
    const [availableGames, setAvailableGames] = useState<Game[]>([]);
    const [error, setError] = useState<string | null>(null);

    // Initialize data and cleanup on mount
    useEffect(() => {
        fetchInitialData();
        return () => {
            webSocketService.disconnect();
        };
    }, []);

    // Handle WebSocket connection when game changes
    useEffect(() => {
        if (game) {
            webSocketService.setConnectionStatusCallback(setIsConnected);
            connectToGame();
        }
    }, [game?.id]);

    // Clear error message after timeout
    useEffect(() => {
        if (error) {
            const timer = setTimeout(() => {
                setError(null);
            }, ERROR_TIMEOUT);
            return () => clearTimeout(timer);
        }
    }, [error]);

    // Fetch initial data from API
    const fetchInitialData = async () => {
        try {
            setIsLoading(true);
            const [fetchedPlayers, fetchedGames] = await Promise.all([
                api.players.getAll(),
                api.games.getAll()
            ]);
            setPlayers(fetchedPlayers);
            setAvailableGames(fetchedGames);
        } catch (err) {
            setError('Failed to fetch initial data');
            console.error('Initial data fetch error:', err);
        } finally {
            setIsLoading(false);
        }
    };

    // WebSocket connection handler
    const connectToGame = () => {
        if (!game) return;
        
        webSocketService.connect();
        webSocketService.subscribeToGame(game.id, (updatedGame: Game) => {
            setGame(updatedGame);
            setAvailableGames(prev => 
                prev.map(g => g.id === updatedGame.id ? updatedGame : g)
            );
        });
    };

    // Player management handlers
    const handleCreatePlayer = async (username: string) => {
        if (!username.trim()) return;
        
        setIsLoading(true);
        setError(null);
        try {
            const newPlayer = await api.players.create(username);
            setPlayers(prev => [...prev, newPlayer]);
            setCurrentPlayerId(newPlayer.id);
        } catch (err) {
            setError('Failed to create player');
            console.error('Player creation error:', err);
        } finally {
            setIsLoading(false);
        }
    };

    const handlePlayerSelect = (playerId: string) => {
        setCurrentPlayerId(playerId);
        setGame(null); // Reset game when switching players
    };

    // Game management handlers
    const handleCreateGame = async (teamColor: TeamColor, role: PlayerRole) => {
        if (!currentPlayerId) {
            setError('Please select a player first');
            return;
        }

        setIsLoading(true);
        setError(null);
        try {
            const newGame = await api.games.create(currentPlayerId, teamColor, role);
            setGame(newGame);
            setAvailableGames(prev => [...prev, newGame]);
        } catch (err) {
            setError('Failed to create game');
            console.error('Game creation error:', err);
        } finally {
            setIsLoading(false);
        }
    };

    const handleJoinGame = async (gameId: string, teamColor: TeamColor, role: PlayerRole) => {
        if (!currentPlayerId) {
            setError('Please select a player first');
            return;
        }

        setIsLoading(true);
        setError(null);
        try {
            const updatedGame = await api.games.join(gameId, currentPlayerId, teamColor, role);
            setGame(updatedGame);
            setAvailableGames(prev => 
                prev.map(g => g.id === gameId ? updatedGame : g)
            );
        } catch (err) {
            setError('Failed to join game');
            console.error('Game join error:', err);
        } finally {
            setIsLoading(false);
        }
    };

    // Game play handlers
    const handleBrainSelect = (piece: string) => {
        if (!game || !currentPlayerId) return;

        webSocketService.sendBrainSelection({
            gameId: game.id,
            playerId: currentPlayerId,
            selectedPiece: piece
        });
    };

    const handleHandMove = (move: { from: string; to: string }) => {
        if (!game || !currentPlayerId) return;

        webSocketService.sendHandMove({
            gameId: game.id,
            playerId: currentPlayerId,
            move: `${move.from}${move.to}`
        });
    };

    return (
        <BrowserRouter>
        <Layout>
            {/* Error Display */}
            {error && (
                <div className="fixed top-4 left-1/2 transform -translate-x-1/2 bg-red-600 text-white px-4 py-2 rounded z-50">
                    {error}
                </div>
            )}
            
            {/* Header Controls */}
            <div className="fixed top-4 right-4 z-40 flex space-x-4">
                <PlayerSelector 
                    currentPlayerId={currentPlayerId}
                    onPlayerSelect={handlePlayerSelect}
                    players={players}
                />
            </div>
            
            {/* Connection Status */}
            <ConnectionStatus 
                isConnected={isConnected}
                reconnect={connectToGame}
            />

            <Routes>
                <Route 
                    path="/"
                    element={
                    <SetupPanel 
                        onCreatePlayer={handleCreatePlayer}
                        onCreateGame={handleCreateGame}
                        onJoinGame={handleJoinGame}
                        isLoading={isLoading}
                        players={players}
                        availableGames={availableGames}
                        currentPlayerId={currentPlayerId}
                        currentGame={game}
                    />
                    }
                />
                <Route path = "/game/:gameId"
                    element={
                        <GamePage
                                game={game}
                                currentPlayerId={currentPlayerId}
                                players={players}
                                isConnected={isConnected}
                                onBrainSelect={handleBrainSelect}
                                onHandMove={handleHandMove}
                                setGame={setGame}
                                setError={setError}
                            />
                    }
                />
            </Routes>

            {/* Main Content */}
            
        </Layout>
        </BrowserRouter>
    );
}

export default App;