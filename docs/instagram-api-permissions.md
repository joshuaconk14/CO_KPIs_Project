# Instagram API Permissions Guide

## Current Status Summary

### ✅ Working (with current permissions):
- **Latest Posts** - Successfully fetching 25 posts with insights
- **Account KPIs** - Successfully fetching 30-day reach data and follower counts
- **Latest Story** - Successfully fetching story data

### ❌ Still needs work:
- **Pinned Reel** - API call structure needs adjustment

## Required Permissions for Full Instagram Data Access

### Current Permissions (✅ You have these):
- `pages_show_list`
- `business_management` 
- `instagram_basic`
- `instagram_manage_insights`

### Additional Permissions Needed (❌ You need to add these):

#### For Enhanced Post Data:
- `instagram_manage_comments` (❌ Need to add) - for comment details
- `instagram_content_publish` (❌ Need to add) - for more post insights

## How to Add Permissions:

1. Go to [Meta for Developers](https://developers.facebook.com/)
2. Select your "ConklinOfficial App"
3. Go to "App Review" → "Permissions and Features"
4. Request these permissions:
   - `instagram_content_publish`
   - `instagram_manage_comments`

## API Endpoints for Each Data Type:

### Latest Posts (✅ Working):
```
GET /{instagram-business-account-id}/media?fields=id,caption,media_type,timestamp,permalink,like_count,comments_count,insights.metric(reach,impressions,saved,comments,shares,likes)
```

### Pinned Reel (🔄 Fixed):
```
GET /{instagram-business-account-id}/media?fields=id,caption,media_type,timestamp,permalink,like_count,comments_count,insights.metric(comments,shares,likes,saves,avg_watch_time)&limit=50
```
*Note: Filter for media_type=REELS in response processing*

### Latest Story (✅ Working):
```
GET /{instagram-business-account-id}/media?fields=id,media_type,timestamp,insights.metric(replies,shares,impressions,profile_visits)&media_type=STORY&limit=1
```

## Implementation Status:

1. ✅ **Environment variables** - Loading from .env file
2. ✅ **Latest Posts** - Fetching successfully
3. ✅ **Account KPIs** - Fetching successfully  
4. ✅ **Latest Story** - Fetching successfully
5. 🔄 **Pinned Reel** - API call fixed, needs testing
6. ❌ **Enhanced permissions** - Still need to request additional permissions

## Next Steps:

1. **Test the pinned reel fix** - Restart your application
2. **Request additional permissions** for enhanced data
3. **Update access token** once permissions are approved
4. **Implement data persistence** for pinned reel and story models 