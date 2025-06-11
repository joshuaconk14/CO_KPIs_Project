import React, { useEffect, useState } from 'react';
import { Client } from '@stomp/stompjs';
import { Button, TextField, Paper, Typography, Box, Grid } from '@mui/material';
import SockJS from 'sockjs-client';

// Define the interface for our test data
interface TestData {
    message: string;
    count: number;
    timestamp: string;
}

// Define the interface for our Instagram post data
interface InstagramPost {
    id: number;
    postId: string;
    caption: string;
    postedAt: string;
    likes: number;
    comments: number;
    shares: number;
    saves: number;
    reach: number;
    impressions: number;
    createdAt: string;
    updatedAt: string;
}

const TestComponent: React.FC = () => {
    const [posts, setPosts] = useState<InstagramPost[]>([]);
    const [selectedPost, setSelectedPost] = useState<InstagramPost | null>(null);
    const [stompClient, setStompClient] = useState<Client | null>(null);
    const [isConnected, setIsConnected] = useState<boolean>(false);

    useEffect(() => {
        const socket = new SockJS('http://localhost:8080/ws/kpi');
        const client = new Client({
            webSocketFactory: () => socket,
            onConnect: () => {
                console.log('Connected to WebSocket');
                setIsConnected(true);
                client.subscribe('/topic/kpi-updates', (message) => {
                    try {
                        const newData = JSON.parse(message.body) as InstagramPost;
                        setSelectedPost(newData);
                    } catch (error) {
                        console.error('Error parsing message:', error);
                    }
                });
            },
            onDisconnect: () => {
                console.log('Disconnected from WebSocket');
                setIsConnected(false);
            },
            onStompError: (frame) => {
                console.error('STOMP error:', frame);
            }
        });

        client.activate();
        setStompClient(client);

        return () => {
            if (client.connected) {
                client.deactivate();
            }
        };
    }, []);

    const fetchPosts = async (): Promise<void> => {
        try {
            const response = await fetch('http://localhost:8080/api/instagram/posts');
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const data = await response.json();
            setPosts(data);
            if (data.length > 0) {
                setSelectedPost(data[0]);
            }
        } catch (error) {
            console.error('Error fetching posts:', error);
        }
    };

    const refreshPosts = async (): Promise<void> => {
        try {
            await fetch('http://localhost:8080/api/instagram/refresh', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                }
            });
        } catch (error) {
            console.error('Error refreshing posts:', error);
        }
    };

    return (
        <Box sx={{ p: 3 }}>
            <Grid container spacing={3}>
                <Grid item xs={12} md={6}>
                    <Paper sx={{ p: 3 }}>
                        <Typography variant="h6" gutterBottom>
                            Instagram Posts
                        </Typography>
                        <Button 
                            variant="contained" 
                            onClick={fetchPosts} 
                            sx={{ mb: 2, mr: 2 }}
                        >
                            Fetch Posts
                        </Button>
                        <Button 
                            variant="contained" 
                            onClick={refreshPosts} 
                            sx={{ mb: 2 }}
                            disabled={!isConnected}
                        >
                            Refresh Posts
                        </Button>
                        {selectedPost && (
                            <Box sx={{ mt: 2 }}>
                                <Typography variant="subtitle1">Selected Post Details:</Typography>
                                <Typography>Caption: {selectedPost.caption}</Typography>
                                <Typography>Posted At: {new Date(selectedPost.postedAt).toLocaleString()}</Typography>
                                <Typography>Likes: {selectedPost.likes}</Typography>
                                <Typography>Comments: {selectedPost.comments}</Typography>
                                <Typography>Reach: {selectedPost.reach}</Typography>
                                <Typography>Impressions: {selectedPost.impressions}</Typography>
                            </Box>
                        )}
                    </Paper>
                </Grid>

                <Grid item xs={12} md={6}>
                    <Paper sx={{ p: 3 }}>
                        <Typography variant="h6" gutterBottom>
                            WebSocket Status
                        </Typography>
                        <Typography variant="body2" color="text.secondary" gutterBottom>
                            Connection Status: {isConnected ? 'Connected' : 'Disconnected'}
                        </Typography>
                        {posts.length > 0 && (
                            <Box sx={{ mt: 2 }}>
                                <Typography variant="subtitle1">Available Posts:</Typography>
                                {posts.map((post) => (
                                    <Button
                                        key={post.id}
                                        variant="text"
                                        onClick={() => setSelectedPost(post)}
                                        sx={{ display: 'block', textAlign: 'left', mb: 1 }}
                                    >
                                        {post.caption.substring(0, 50)}...
                                    </Button>
                                ))}
                            </Box>
                        )}
                    </Paper>
                </Grid>
            </Grid>
        </Box>
    );
};

export default TestComponent; 