# Гэрэгэ — Локал хөгжүүлэлтийн орчин

Энэ нь **Ory Hydra** (OIDC цөм) + Postgres + Redis-ийг локалд босгоно — яг TARA-Login
ажилладаг бүтэц.

## Шаардлага
- Docker + Docker Compose
- `curl`, `jq` (script-үүдэд)

## Хурдан эхлэх

```bash
cd deploy

# 1. Орчныг асаах
docker compose up -d            # эсвэл: make up

# 2. Hydra бэлэн болохыг хүлээх (~10-20 сек)
make ready                      # → "✓ Hydra бэлэн"

# 3. OIDC discovery баримтыг шалгах (систем амьд эсэх нотолгоо)
make discovery

# 4. Туршилтын үйлчилгээ (RP) бүртгэх
make client
```

## Портууд

| Порт | Үйлчилгээ | Зориулалт |
|------|-----------|-----------|
| 4444 | Hydra **public** | `/oauth2/auth`, `/oauth2/token`, `/.well-known/...` — иргэн/үйлчилгээ хандана |
| 4445 | Hydra **admin** | Клиент бүртгэх, login/consent зөвшөөрөх — **дотоод сүлжээнд л** |
| 6379 | Redis | Сесс кэш (ирээдүйд gerege-login ашиглана) |

## Архитектурын тэмдэглэл

Hydra зориудаар **нэвтрэлтийн UI-гүй**. `hydra.yml`-д тохируулсанаар тэр иргэнийг
`http://localhost:3000/auth/login` руу чиглүүлнэ — энэ нь **бидний `gerege-login`**
webapp болно (дараагийн үе шатанд бүтээнэ). Тиймээс одоогоор `make discovery`,
`make client` ажиллана; харин бүрэн нэвтрэлтийн урсгал нь `gerege-login` бэлэн болмогц
эхэлнэ.

```
Иргэн → Hydra(4444) /authorize → [login_challenge] → gerege-login(3000) → нэвтрэлт
      ← authorization code ←──────────────────────── [accept login] ←──┘
```

## Дараагийн алхам

`gerege-login` Spring Boot webapp-ыг `:3000`-д босгож, Hydra-гийн login/consent урсгалыг
хэрэгжүүлэх. Үүнийг `docs/03-ZAM-MOR.md`-ийн **Үе шат 1** гүйцэтгэнэ.
