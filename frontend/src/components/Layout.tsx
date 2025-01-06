// src/components/Layout.tsx
import { ReactNode } from 'react';

interface LayoutProps {
    children: ReactNode;
}

const Layout = ({ children }: LayoutProps) => {
    return (
        <div className="min-h-screen bg-gray-900 text-white">
            <header className="bg-gray-800 shadow-lg">
                <div className="container mx-auto px-4 py-6">
                    <h1 className="text-3xl font-bold">Hand and Brain Chess</h1>
                </div>
            </header>
            <main className="container mx-auto px-4 py-8">
                {children}
            </main>
        </div>
    );
};

export default Layout;