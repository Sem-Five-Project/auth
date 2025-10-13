# 🚀 DigitalOcean Deployment Guide for edu-auth (HTTPS Enabled)

## ✅ Completed Steps
- ✅ Docker image built successfully
- ✅ DigitalOcean CLI (doctl) installed and authenticated
- ✅ Container registry 'edimy' created in blr1 region
- ✅ Docker image pushed to registry: `registry.digitalocean.com/edimy/edu-auth:latest`
- ✅ Image size: 220.87 MB (compressed)
- ✅ HTTPS configuration added

## 🎯 Deployment Options

### Option 1: Deploy to DigitalOcean App Platform (Recommended) - HTTPS Enabled

1. **Go to DigitalOcean Control Panel**
   - Navigate to Apps → Create App
   - Choose "Docker Hub or Container Registry"
   - Select your registry: `registry.digitalocean.com/edimy/edu-auth:latest`

2. **Configure App Settings**
   - App Name: `edu-auth-app`
   - Region: `Bangalore (blr1)` (same as your registry)
   - Plan: Basic ($5/month) or Pro ($12/month)

3. **Set Environment Variables** (HTTPS URLs)
   ```
   SPRING_PROFILES_ACTIVE=production
   SPRING_DATASOURCE_URL=jdbc:postgresql://db.shownuxwvuiooypptqtf.supabase.co:5432/postgres?sslmode=require
   SPRING_DATASOURCE_USERNAME=postgres
   SPRING_DATASOURCE_PASSWORD=Academicsem5@15
   CORS_ALLOWED_ORIGINS=https://your-frontend-domain.com
   PAYHERE_NOTIFY_URL=https://your-app-name.ondigitalocean.app/api/payments/callback
   PAYHERE_RETURN_URL=https://your-frontend-domain.com/success
   PAYHERE_CANCEL_URL=https://your-frontend-domain.com/cancel
   JWT_SECRET=your-secure-jwt-secret-key-here
   ```

4. **Configure HTTP Settings**
   - HTTP Port: `8080` (matches Spring Boot configuration)
   - Health Check Path: `/api/actuator/health`
   - **HTTPS**: Automatically enabled by DigitalOcean App Platform
   - **SSL Certificate**: Automatically managed by DigitalOcean

5. **Your App Will Be Available At:**
   - **HTTPS URL**: `https://your-app-name.ondigitalocean.app`
   - **API Base**: `https://your-app-name.ondigitalocean.app/api`
   - **Health Check**: `https://your-app-name.ondigitalocean.app/api/actuator/health`

### Option 2: Deploy to DigitalOcean Droplet with HTTPS

1. **Create a Droplet**
   ```bash
   doctl compute droplet create edu-auth-server \
     --image docker-20-04 \
     --size s-1vcpu-1gb \
     --region blr1 \
     --ssh-keys YOUR_SSH_KEY_ID
   ```

2. **SSH into the Droplet and Deploy**
   ```bash
   # Login to registry
   doctl registry login
   
   # Create environment file with HTTPS URLs
   nano .env
   # Add your environment variables (see example below)
   
   # Run the container
   docker run -d \
     --name edu-auth \
     --env-file .env \
     -p 8080:8080 \
     --restart unless-stopped \
     registry.digitalocean.com/edimy/edu-auth:latest
   ```

3. **Environment File for Droplet (.env)**
   ```bash
   SPRING_PROFILES_ACTIVE=production
   SPRING_DATASOURCE_URL=jdbc:postgresql://db.shownuxwvuiooypptqtf.supabase.co:5432/postgres?sslmode=require
   SPRING_DATASOURCE_USERNAME=postgres
   SPRING_DATASOURCE_PASSWORD=Academicsem5@15
   CORS_ALLOWED_ORIGINS=https://your-frontend-domain.com
   PAYHERE_NOTIFY_URL=https://your-droplet-domain.com/api/payments/callback
   PAYHERE_RETURN_URL=https://your-frontend-domain.com/success
   PAYHERE_CANCEL_URL=https://your-frontend-domain.com/cancel
   JWT_SECRET=your-secure-jwt-secret-key-here
   ```

4. **Set up HTTPS on Droplet (Using Nginx + Let's Encrypt)**
   ```bash
   # Install Nginx
   sudo apt update
   sudo apt install nginx certbot python3-certbot-nginx
   
   # Configure Nginx (create /etc/nginx/sites-available/edu-auth)
   sudo nano /etc/nginx/sites-available/edu-auth
   ```
   
   **Nginx Configuration:**
   ```nginx
   server {
       listen 80;
       server_name your-domain.com;
       
       location / {
           proxy_pass http://localhost:8080;
           proxy_set_header Host $host;
           proxy_set_header X-Real-IP $remote_addr;
           proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
           proxy_set_header X-Forwarded-Proto $scheme;
       }
   }
   ```
   
   ```bash
   # Enable site and get SSL certificate
   sudo ln -s /etc/nginx/sites-available/edu-auth /etc/nginx/sites-enabled/
   sudo nginx -t
   sudo systemctl reload nginx
   sudo certbot --nginx -d your-domain.com
   ```

## 🔧 Management Commands

### Update Deployment (Rebuild with HTTPS)
```bash
# Rebuild and push new version
./deploy.sh

# On App Platform: App will auto-deploy new versions
# On Droplet: Pull and restart
docker pull registry.digitalocean.com/edimy/edu-auth:latest
docker restart edu-auth
```

### Monitor Application (HTTPS URLs)
```bash
# Check logs
docker logs -f edu-auth

# Check health (HTTPS)
curl https://your-app-url.ondigitalocean.app/api/actuator/health
```

### Registry Management
```bash
# List repositories
doctl registry repository list edimy

# List tags
doctl registry repository list-tags edu-auth

# Delete old tags (optional)
doctl registry repository delete-tag edu-auth old-tag-name
```

## 🌐 Domain Configuration (HTTPS)

1. **App Platform URLs:**
   - Your app will get: `https://your-app-name.ondigitalocean.app`
   - API endpoints: `https://your-app-name.ondigitalocean.app/api/*`
   - Health check: `https://your-app-name.ondigitalocean.app/api/actuator/health`

2. **Update PayHere URLs with your actual domain:**
   ```
   PAYHERE_NOTIFY_URL=https://your-app-name.ondigitalocean.app/api/payments/callback
   PAYHERE_RETURN_URL=https://your-frontend-domain.com/success
   PAYHERE_CANCEL_URL=https://your-frontend-domain.com/cancel
   ```

3. **CORS Configuration:**
   ```
   CORS_ALLOWED_ORIGINS=https://your-frontend-domain.com,https://localhost:3000
   ```

## 🔒 Security Features Enabled

- ✅ **HTTPS Encryption**: All traffic encrypted with SSL/TLS
- ✅ **Automatic SSL Certificates**: Managed by DigitalOcean
- ✅ **Secure Headers**: Spring Security with proxy headers
- ✅ **JWT Security**: Configurable secret key
- ✅ **Database SSL**: Connection to Supabase with SSL
- ✅ **CORS Protection**: Configured allowed origins

## 📊 Cost Estimation (HTTPS Included)

- **App Platform Basic**: $5/month (HTTPS included)
- **Container Registry**: $5/month (500MB free, then $0.02/GB)
- **SSL Certificate**: FREE (auto-managed by DigitalOcean)
- **Database**: Current Supabase (external)
- **Total**: ~$10/month

## 🚀 Quick Start (HTTPS Ready)

Your Docker image is already configured for HTTPS! Just:

1. **Deploy to App Platform** (Easiest - HTTPS automatic)
2. **Your app will be available at**: `https://your-app-name.ondigitalocean.app`
3. **Update your frontend** to use the HTTPS API endpoints
4. **Update PayHere configuration** with your HTTPS URLs

Your application is now production-ready with full HTTPS support! 🎉🔒
