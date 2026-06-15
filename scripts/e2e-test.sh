#!/usr/bin/env bash
# ДАН — нэг аргын end-to-end OIDC тест.  Хэрэглээ: dan-e2e.sh METHOD РД
set -uo pipefail
METHOD="${1:-MOBILE_ID}"
RD="${2:-УУ00010101}"

JAR="/tmp/dan-cookies-$METHOD.txt"; rm -f "$JAR"
PUB=http://localhost:4444; APP=http://localhost:3000
AUTH_URL="$PUB/oauth2/auth?client_id=dan-demo-rp&response_type=code&scope=openid%20profile&redirect_uri=http://localhost:8080/callback&state=teststate12345&nonce=noncevalue123"
hop() { curl -s -c "$JAR" -b "$JAR" -o /dev/null -w '%{redirect_url}' "$1"; }
urldecode() { python3 -c "import urllib.parse,sys;print(urllib.parse.unquote(sys.argv[1]))" "$1"; }

loc1=$(hop "$AUTH_URL")
challenge=$(echo "$loc1" | sed -n 's/.*login_challenge=\([^&]*\).*/\1/p')
challenge=$(urldecode "$challenge")        # ← URL-задлах (browser-ийг дуурайв)
[ -z "$challenge" ] && { echo "✗ [$METHOD] login_challenge алга"; exit 1; }

verify=$(curl -s -c "$JAR" -b "$JAR" \
  --data-urlencode "login_challenge=$challenge" \
  --data-urlencode "method=$METHOD" \
  --data-urlencode "personalCode=$RD" \
  --data-urlencode "phoneNumber=99112233" \
  "$APP/auth/login")
sid=$(echo "$verify" | grep -oE 'data-session-id="[^"]+"' | head -1 | sed 's/.*"\(.*\)"/\1/')
[ -z "$sid" ] && { echo "✗ [$METHOD] sessionId алга"; exit 1; }

redirectTo=""
for i in $(seq 1 12); do
  sleep 1
  pj=$(curl -s "$APP/auth/login/poll?sessionId=$sid")
  st=$(echo "$pj" | sed -n 's/.*"status":"\([^"]*\)".*/\1/p')
  if [ "$st" = "SUCCESS" ]; then redirectTo=$(echo "$pj" | sed -n 's/.*"redirectTo":"\([^"]*\)".*/\1/p'); break; fi
  [ "$st" = "FAILED" ] && { echo "✗ [$METHOD] FAILED: $pj"; exit 1; }
done
[ -z "$redirectTo" ] && { echo "✗ [$METHOD] SUCCESS ирсэнгүй — сүүлийн poll: $pj"; exit 1; }

loc2=$(hop "$redirectTo")            # → /auth/consent
loc3=$(hop "$loc2")                  # → consent_verifier
loc4=$(hop "$loc3")                  # → RP callback
code=$(echo "$loc4" | sed -n 's/.*[?&]code=\([^&]*\).*/\1/p')
[ -z "$code" ] && { echo "✗ [$METHOD] code ирсэнгүй: $loc4"; exit 1; }

tok=$(curl -s -u dan-demo-rp:demo-secret-change-me -d grant_type=authorization_code -d "code=$code" \
  --data-urlencode "redirect_uri=http://localhost:8080/callback" "$PUB/oauth2/token")
idt=$(echo "$tok" | sed -n 's/.*"id_token":"\([^"]*\)".*/\1/p')
payload=$(echo "$idt" | cut -d. -f2); pad=$(( ${#payload} % 4 )); [ $pad -ne 0 ] && payload="$payload$(printf '=%.0s' $(seq 1 $((4-pad))))"
claims=$(echo "$payload" | tr '_-' '/+' | base64 -d 2>/dev/null)
g() { echo "$claims" | python3 -c "import sys,json;print(json.load(sys.stdin).get('$1'))" 2>/dev/null; }
printf "✅ %-11s → sub=%s  name=%-20s loa=%-12s method=%s\n" "$METHOD" "$(g sub)" "$(g name)" "$(g loa)" "$(g auth_method)"
