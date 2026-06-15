# ДАН — Production deploy (dan.dgov.mn)

Caddy (автомат HTTPS) + Ory Hydra + dan-login + dan-admin + Postgres + Redis.

## Урьдчилсан нөхцөл
- Сервер дээр Docker + compose plugin
- `dan.dgov.mn` нь серверийн IP рүү зааж байх (DNS A record)
- 80/443 порт чөлөөтэй

## Алхам

```bash
git clone https://github.com/gerege-systems/gerege-tara-mn.git
cd gerege-tara-mn/deploy/prod

# 1. Нууцуудыг үүсгэх
cp .env.example .env
# .env доторх утгуудыг хүчтэй санамсаргүй утгаар солих (openssl rand -hex 32)

# 2. Стекийг асаах (Caddy TLS-ийг автоматаар авна)
docker compose -f docker-compose.prod.yml up -d --build

# 3. Демо RP бүртгэх
bash register-demo.sh

# 4. Шалгах
curl -s https://dan.dgov.mn/.well-known/openid-configuration | jq .issuer
# → "https://dan.dgov.mn"
```

## Архитектур / routing (Caddy)

| Зам | Зорилт |
|-----|--------|
| `/oauth2/*`, `/.well-known/*`, `/userinfo` | Hydra public (OIDC цөм) |
| бусад бүх зам (`/`, `/auth/*`, `/demo/*`) | dan-login |
| `dan-admin` (RP бүртгэл) | **public-д ил гарахгүй** — SSH tunnel-ээр |

Админд хандах (SSH tunnel):
```bash
ssh -L 4000:dan-admin:4000 root@<server>   # дараа нь http://localhost:4000
# (эсвэл сервер дээр: docker compose exec ... / порт нийтлэх)
```

## ⚠️ Хатууруулах (жинхэнэ production)
- Hydra-г `--dev`-гүй, TLS termination зөвшөөрлөөр ажиллуулах
- Демо хэсгийг (`/demo/*`, dan-demo-rp) хасах
- dan-admin-д нэвтрэлт нэмэх
- Нууцуудыг secrets manager-т хадгалах
