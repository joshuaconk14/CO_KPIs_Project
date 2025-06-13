import React, { useEffect, useState } from 'react';
import { Box, Grid, Paper, Typography, CircularProgress, Divider, Button } from '@mui/material';
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, CartesianGrid, Legend, LineChart, Line } from 'recharts';
import Select from '@mui/material/Select';
import MenuItem from '@mui/material/MenuItem';
import './dashboard.css';

interface AccountKpi {
  id: number;
  date: string;
  followers: number;
  newFollowers: number;
  profileViews: number;
  reach: number;
  pinnedReelComments: number;
  pinnedReelShares: number;
  pinnedReelLikes: number;
  pinnedReelSaves: number;
  pinnedReelWatchTime: number;
}

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

const kpiOptions = [
  { value: 'likes', label: 'Likes' },
  { value: 'comments', label: 'Comments' },
  { value: 'shares', label: 'Shares' },
  { value: 'saves', label: 'Saves' },
  { value: 'reach', label: 'Reach' },
  { value: 'impressions', label: 'Impressions' },
];

const timeRanges = [
  { value: 'week', label: 'Past Week', days: 7 },
  { value: 'month', label: 'Past Month', days: 30 },
  { value: 'three_months', label: 'Past 3 Months', days: 90 },
  { value: 'six_months', label: 'Past 6 Months', days: 182 },
  { value: 'year', label: 'Past Year', days: 365 },
  { value: 'two_years', label: 'Past 2 Years', days: 730 },
];

const Dashboard: React.FC = () => {
  const [accountKpis, setAccountKpis] = useState<AccountKpi[]>([]);
  const [posts, setPosts] = useState<InstagramPost[]>([]);
  const [selectedKPI, setSelectedKPI] = useState<string>('likes');
  const [selectedTimeRange, setSelectedTimeRange] = useState<string>('month');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      const [kpiRes, postsRes] = await Promise.all([
        fetch('http://localhost:8080/api/instagram/account-kpis'),
        fetch('http://localhost:8080/api/instagram/posts'),
      ]);
      const kpiData = await kpiRes.json();
      const postsData = await postsRes.json();
      setAccountKpis(kpiData);
      setPosts(postsData);
      setLoading(false);
    };
    fetchData();
  }, []);

  // Get latest KPI for summary cards
  const latestKpi = accountKpis.length > 0 ? accountKpis[accountKpis.length - 1] : null;
  // Get previous KPI for delta calculations
  const prevKpi = accountKpis.length > 1 ? accountKpis[accountKpis.length - 2] : null;

  const now = new Date();
  const selectedRange = timeRanges.find(r => r.value === selectedTimeRange);
  const filteredPosts = posts.filter(post => {
    const postDate = new Date(post.postedAt);
    if (!selectedRange) return true;
    const diffTime = now.getTime() - postDate.getTime();
    const diffDays = diffTime / (1000 * 60 * 60 * 24);
    return diffDays <= selectedRange.days;
  });

  return (
    <Box sx={{ p: 3, bgcolor: '#181c2f', minHeight: '100vh', color: 'white' }}>
      <Typography variant="h3" fontWeight={700} align="center" sx={{ mb: 4, letterSpacing: 1, color: '#bfc9e0' }}>
        ConklinOfficial KPI Dashboard
      </Typography>
      <Grid container spacing={3}>
        {/* Followers, New Followers, Profile Views */}
        <Grid item xs={12} md={3}>
          <Paper sx={{ p: 3, bgcolor: '#23284a', color: 'white', borderRadius: 3 }}>
            <Typography variant="h4" fontWeight={700}>
              {latestKpi ? `${latestKpi.followers.toLocaleString()}K` : <CircularProgress color="inherit" size={24} />}
            </Typography>
            <Typography variant="subtitle1">Followers</Typography>
            <Box sx={{ mt: 1, display: 'flex', alignItems: 'center' }}>
              <Box sx={{ width: '70%', height: 8, bgcolor: '#2e365a', borderRadius: 2, mr: 1 }}>
                <Box sx={{ width: '70%', height: 8, bgcolor: '#3b82f6', borderRadius: 2 }} />
              </Box>
              <Typography variant="body2">70%</Typography>
            </Box>
          </Paper>
        </Grid>
        <Grid item xs={12} md={3}>
          <Paper sx={{ p: 3, bgcolor: '#23284a', color: 'white', borderRadius: 3 }}>
            <Typography variant="h5" fontWeight={700}>
              {latestKpi ? latestKpi.newFollowers : <CircularProgress color="inherit" size={24} />}
            </Typography>
            <Typography variant="subtitle1">New followers</Typography>
            <Typography variant="body2" color={latestKpi && prevKpi && latestKpi.newFollowers > prevKpi.newFollowers ? 'success.main' : 'error.main'}>
              {latestKpi && prevKpi ? `${latestKpi.newFollowers - prevKpi.newFollowers > 0 ? '+' : ''}${latestKpi.newFollowers - prevKpi.newFollowers} vs last` : ''}
            </Typography>
          </Paper>
        </Grid>
        <Grid item xs={12} md={3}>
          <Paper sx={{ p: 3, bgcolor: '#23284a', color: 'white', borderRadius: 3 }}>
            <Typography variant="h5" fontWeight={700}>
              {latestKpi ? latestKpi.profileViews.toLocaleString() : <CircularProgress color="inherit" size={24} />}
            </Typography>
            <Typography variant="subtitle1">Profile views</Typography>
            <Typography variant="body2" color={latestKpi && prevKpi && latestKpi.profileViews > prevKpi.profileViews ? 'success.main' : 'error.main'}>
              {latestKpi && prevKpi ? `${latestKpi.profileViews - prevKpi.profileViews > 0 ? '+' : ''}${latestKpi.profileViews - prevKpi.profileViews} vs last` : ''}
            </Typography>
          </Paper>
        </Grid>
        <Grid item xs={12} md={3}>
          <Paper sx={{ p: 3, bgcolor: '#23284a', color: 'white', borderRadius: 3 }}>
            <Typography variant="h5" fontWeight={700}>
              {latestKpi ? latestKpi.reach.toLocaleString() : <CircularProgress color="inherit" size={24} />}
            </Typography>
            <Typography variant="subtitle1">Reach (last 30 days)</Typography>
          </Paper>
        </Grid>

        {/* KPI Bar Chart (now the larger, longer graph on the left) */}
        <Grid item xs={12} md={8}>
          <Paper sx={{ p: 3, bgcolor: '#23284a', color: 'white', borderRadius: 3, height: 320 }}>
            <Typography variant="subtitle1" gutterBottom>
              KPI Bar Chart
            </Typography>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
              <Typography sx={{ mr: 2 }}>Select KPI:</Typography>
              <Select
                value={selectedKPI}
                onChange={(e) => setSelectedKPI(e.target.value as string)}
                size="small"
                sx={{ color: 'white', '.MuiOutlinedInput-notchedOutline': { borderColor: '#3b82f6' } }}
              >
                {kpiOptions.map((option) => (
                  <MenuItem key={option.value} value={option.value}>{option.label}</MenuItem>
                ))}
              </Select>
              <Typography sx={{ ml: 4, mr: 2 }}>Time Range:</Typography>
              <Select
                value={selectedTimeRange}
                onChange={(e) => setSelectedTimeRange(e.target.value as string)}
                size="small"
                sx={{ color: 'white', '.MuiOutlinedInput-notchedOutline': { borderColor: '#3b82f6' } }}
              >
                {timeRanges.map((option) => (
                  <MenuItem key={option.value} value={option.value}>{option.label}</MenuItem>
                ))}
              </Select>
            </Box>
            <ResponsiveContainer width="100%" height={200}>
              <BarChart data={filteredPosts} margin={{ top: 10, right: 10, left: 0, bottom: 30 }}>
                <CartesianGrid strokeDasharray="3 3" stroke="#2e365a" />
                <XAxis dataKey="postedAt" tickFormatter={(date) => new Date(date).toLocaleDateString()} angle={-30} textAnchor="end" height={60} stroke="#bfc9e0" />
                <YAxis stroke="#bfc9e0" />
                <Tooltip contentStyle={{ background: '#23284a', border: 'none', color: 'white' }} />
                <Legend />
                <Bar dataKey={selectedKPI} fill="#3b82f6" name={kpiOptions.find(opt => opt.value === selectedKPI)?.label} />
              </BarChart>
            </ResponsiveContainer>
          </Paper>
        </Grid>
        {/* Reach Line Chart (now the smaller graph on the right) */}
        <Grid item xs={12} md={4}>
          <Paper sx={{ p: 3, bgcolor: '#23284a', color: 'white', borderRadius: 3, height: 320 }}>
            <Typography variant="subtitle1" gutterBottom>
              Reach, past 30 days
            </Typography>
            <ResponsiveContainer width="100%" height={200}>
              <LineChart data={accountKpis} margin={{ top: 20, right: 30, left: 0, bottom: 0 }}>
                <CartesianGrid strokeDasharray="3 3" stroke="#2e365a" />
                <XAxis dataKey="date" tickFormatter={(date) => date && new Date(date).toLocaleDateString()} stroke="#bfc9e0" />
                <YAxis stroke="#bfc9e0" />
                <Tooltip contentStyle={{ background: '#23284a', border: 'none', color: 'white' }} />
                <Line type="monotone" dataKey="reach" stroke="#3b82f6" strokeWidth={2} dot={false} />
              </LineChart>
            </ResponsiveContainer>
          </Paper>
        </Grid>

        {/* Pinned Reel, Latest Post, Latest Story (summary cards) */}
        <Grid item xs={12} md={4}>
          <Paper sx={{ p: 3, bgcolor: '#23284a', color: 'white', borderRadius: 3 }}>
            <Typography variant="subtitle1">Pinned reel</Typography>
            <Divider sx={{ bgcolor: '#2e365a', my: 1 }} />
            <Typography variant="h5">{latestKpi ? latestKpi.pinnedReelComments : '-'}</Typography>
            <Typography variant="body2">Comments</Typography>
            <Typography variant="h5">{latestKpi ? latestKpi.pinnedReelShares : '-'}</Typography>
            <Typography variant="body2">Shares</Typography>
            <Typography variant="h5">{latestKpi ? latestKpi.pinnedReelLikes : '-'}</Typography>
            <Typography variant="body2">Likes</Typography>
            <Typography variant="h5">{latestKpi ? latestKpi.pinnedReelSaves : '-'}</Typography>
            <Typography variant="body2">Saves</Typography>
            <Typography variant="h5">{latestKpi ? latestKpi.pinnedReelWatchTime : '-'}s</Typography>
            <Typography variant="body2">Avg. watch time</Typography>
          </Paper>
        </Grid>
        <Grid item xs={12} md={4}>
          <Paper sx={{ p: 3, bgcolor: '#23284a', color: 'white', borderRadius: 3 }}>
            <Typography variant="subtitle1">Latest post</Typography>
            <Divider sx={{ bgcolor: '#2e365a', my: 1 }} />
            {/* You can add more details here if you want */}
            <Typography variant="h5">-</Typography>
            <Typography variant="body2">Comments</Typography>
            <Typography variant="h5">-</Typography>
            <Typography variant="body2">Shares</Typography>
            <Typography variant="h5">-</Typography>
            <Typography variant="body2">Likes</Typography>
            <Typography variant="h5">-</Typography>
            <Typography variant="body2">Saves</Typography>
          </Paper>
        </Grid>
        <Grid item xs={12} md={4}>
          <Paper sx={{ p: 3, bgcolor: '#23284a', color: 'white', borderRadius: 3 }}>
            <Typography variant="subtitle1">Latest story</Typography>
            <Divider sx={{ bgcolor: '#2e365a', my: 1 }} />
            <Typography variant="h5">11</Typography>
            <Typography variant="body2">Replies</Typography>
            <Typography variant="h5">183</Typography>
            <Typography variant="body2">Shares</Typography>
            <Typography variant="h5">3,492</Typography>
            <Typography variant="body2">Impressions</Typography>
            <Typography variant="h5">85</Typography>
            <Typography variant="body2">Profile visits</Typography>
          </Paper>
        </Grid>
      </Grid>
    </Box>
  );
};

export default Dashboard; 