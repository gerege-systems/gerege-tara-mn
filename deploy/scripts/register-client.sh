#!/usr/bin/env bash
# ДАНд туршилтын үйлчилгээ (Relying Party / OAuth2 client) бүртгэх.
#
# Хэрэглээ:  ./register-client.sh
# Шаардлага: Hydra ажиллаж байх (docker compose up -d), curl, jq
#
# Энэ нь Hydra-гийн ADMIN API (4445) руу хандана. Production-д энэ портыг ХЭЗЭЭ Ч
# интернетэд нээж болохгүй — зөвхөн дотоод сүлжээнд.

set -euo pipefail

HYDRA_ADMIN="${HYDRA_ADMIN:-http://localhost:4445}"

echo "→ Hydra admin: $HYDRA_ADMIN"
echo "→ Туршилтын үйлчилгээ бүртгэж байна..."

curl -fsS -X POST "$HYDRA_ADMIN/admin/clients" \
  -H "Content-Type: application/json" \
  -d '{
    "client_id": "dan-demo-rp",
    "client_name": "ДАН жишиг үйлчилгээ",
    "client_secret": "demo-secret-change-me",
    "grant_types": ["authorization_code", "refresh_token"],
    "response_types": ["code"],
    "scope": "openid profile",
    "redirect_uris": ["http://localhost:8080/callback", "http://localhost:3000/callback", "http://localhost:3000/demo/callback"],
    "post_logout_redirect_uris": ["http://localhost:8080/"],
    "token_endpoint_auth_method": "client_secret_basic"
  }' | jq '{client_id, client_name, scope, redirect_uris}'

echo ""
echo "✓ Бүртгэгдлээ. Authorization URL жишээ:"
echo ""
echo "  http://localhost:4444/oauth2/auth?client_id=dan-demo-rp&response_type=code&scope=openid%20profile&redirect_uri=http://localhost:8080/callback&state=demo123"
echo ""
echo "  (login/consent provider буюу dan-login :3000 ажиллаж эхэлмэгц энэ урсгал бүрэн болно.)"
