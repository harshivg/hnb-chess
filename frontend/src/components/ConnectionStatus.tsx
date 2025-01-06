// src/components/ConnectionStatus.tsx
interface ConnectionStatusProps {
  isConnected: boolean;
  reconnect: () => void;
}

const ConnectionStatus = ({ isConnected, reconnect }: ConnectionStatusProps) => {
  return (
      <div className="fixed top-4 left-4 z-50 flex items-center space-x-2">
          <div className={`w-3 h-3 rounded-full ${isConnected ? 'bg-green-500' : 'bg-red-500'}`} />
          <span className="text-sm text-gray-300">
              {isConnected ? 'Connected' : 'Disconnected'}
          </span>
          {!isConnected && (
              <button 
                  onClick={reconnect}
                  className="px-2 py-1 text-sm bg-blue-600 text-white rounded hover:bg-blue-700"
              >
                  Reconnect
              </button>
          )}
      </div>
  );
};

export default ConnectionStatus;