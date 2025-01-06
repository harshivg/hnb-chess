// src/services/websocketService.ts
import { Client } from '@stomp/stompjs';
import { Game, BrainMoveDto, HandMoveDto } from '../types/game';

export class WebSocketService {
    private client: Client;
    private gameUpdateCallback: ((game: Game) => void) | null = null;
    private connectionStatusCallback: ((connected: boolean) => void) | null = null;

    constructor() {
        this.client = new Client({
            brokerURL: 'ws://localhost:8080/ws',
            onConnect: () => {
                console.log('Connected to WebSocket');
                this.connectionStatusCallback?.(true);
            },
            onDisconnect: () => {
                console.log('Disconnected from WebSocket');
                this.connectionStatusCallback?.(false);
            },
            onStompError: (frame) => {
                console.error('STOMP error:', frame);
                this.connectionStatusCallback?.(false);
            }
        });
    }

    public connect(): void {
        this.client.activate();
    }

    public disconnect(): void {
        this.client.deactivate();
    }

    public setConnectionStatusCallback(callback: (connected: boolean) => void): void {
        this.connectionStatusCallback = callback;
    }

    public subscribeToGame(gameId: string, callback: (game: Game) => void): void {
        if (this.client.connected) {
            this.gameUpdateCallback = callback;
            this.client.subscribe(`/topic/game/${gameId}`, (message) => {
                const game: Game = JSON.parse(message.body);
                this.gameUpdateCallback?.(game);
            });
        } else {
            console.error('WebSocket not connected');
        }
    }

    public sendBrainSelection(brainMove: BrainMoveDto): void {
        if (this.client.connected) {
            this.client.publish({
                destination: '/app/brain.select',
                body: JSON.stringify(brainMove)
            });
        } else {
            console.error('WebSocket not connected');
        }
    }

    public sendHandMove(handMove: HandMoveDto): void {
        if (this.client.connected) {
            this.client.publish({
                destination: '/app/hand.move',
                body: JSON.stringify(handMove)
            });
        } else {
            console.error('WebSocket not connected');
        }
    }
}

// Create and export singleton instance
export const webSocketService = new WebSocketService();