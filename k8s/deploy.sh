#!/bin/bash
set -e

SECRET_NAME="app-secrets"

echo "🔄 Preparing Kubernetes secret: $SECRET_NAME"

# If running locally and .env exists, load it
if [ -f .env ]; then
  echo "📦 Loading variables from .env"
  export $(grep -v '^#' .env | xargs)
  VARS=$(grep -v '^#' .env | cut -d= -f1)
else
  echo "💡 No .env file found — assuming CI/CD environment"
  VARS="JWT_ACCESS_SECRET JWT_REFRESH_SECRET JWT_ISSUER \
  JWT_AUDIENCE JWT_ACCESS_EXPIRATION JWT_REFRESH_EXPIRATION \
  JWT_REALM DB_URL DB_USERNAME DB_PASSWORD DB_DRIVER"
fi

# Build --from-literal args dynamically
LITERALS=""
for VAR in $VARS; do
  VALUE="${!VAR}"
  if [ -z "$VALUE" ]; then
    echo "⚠️ Warning: $VAR is not set, skipping"
    continue
  fi
  LITERALS+=" --from-literal=$VAR=$VALUE"
done

# Create or update the secret in Kubernetes
kubectl create secret generic $SECRET_NAME \
  $LITERALS \
  -n monitoring-system-ktor-server \
  --dry-run=client -o yaml | kubectl apply -f -

echo "✅ Kubernetes secret '$SECRET_NAME' created/updated successfully"