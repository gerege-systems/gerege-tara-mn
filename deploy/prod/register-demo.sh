#!/usr/bin/env bash
# Production демо RP-г Hydra-д бүртгэх (dan.dgov.mn/demo/callback).
# Hydra admin порт ил гарахгүй тул сүлжээн доторх curl контейнерээр хандана.
set -euo pipefail
cd "$(dirname "$0")"
source .env

docker run --rm --network dan-prod_default curlimages/curl:8.10.1 -fsS \
  -X POST http://hydra:4445/admin/clients \
  -H 'Content-Type: application/json' \
  -d "{
    \"client_id\": \"dan-demo-rp\",
    \"client_name\": \"ДАН жишиг үйлчилгээ\",
    \"client_secret\": \"${DEMO_CLIENT_SECRET}\",
    \"grant_types\": [\"authorization_code\", \"refresh_token\"],
    \"response_types\": [\"code\"],
    \"scope\": \"openid profile\",
    \"redirect_uris\": [\"https://dan.dgov.mn/demo/callback\"],
    \"token_endpoint_auth_method\": \"client_secret_basic\"
  }" && echo "" && echo "✓ dan-demo-rp бүртгэгдлээ"
