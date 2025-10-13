#!/bin/bash

# DigitalOcean Deployment Script for edu-auth
# Make this file executable: chmod +x deploy.sh

set -e

# Configuration - Update these variables
REGISTRY_NAME="edimy"  # Your actual registry name
IMAGE_NAME="edu-auth"
VERSION="latest"

echo "🚀 Starting deployment to DigitalOcean..."

# Step 1: Build the Docker image
echo "📦 Building Docker image..."
docker build -t $IMAGE_NAME:$VERSION .

# Step 2: Tag for DigitalOcean registry
echo "🏷️  Tagging image for DigitalOcean registry..."
docker tag $IMAGE_NAME:$VERSION registry.digitalocean.com/$REGISTRY_NAME/$IMAGE_NAME:$VERSION

# Step 3: Push to registry
echo "⬆️  Pushing image to DigitalOcean registry..."
docker push registry.digitalocean.com/$REGISTRY_NAME/$IMAGE_NAME:$VERSION

echo "✅ Deployment completed successfully!"
echo "Your image is now available at: registry.digitalocean.com/$REGISTRY_NAME/$IMAGE_NAME:$VERSION"

# Optional: Clean up local images to save space
read -p "Do you want to clean up local Docker images? (y/n): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]
then
    docker rmi $IMAGE_NAME:$VERSION
    docker rmi registry.digitalocean.com/$REGISTRY_NAME/$IMAGE_NAME:$VERSION
    echo "🧹 Local images cleaned up"
fi
