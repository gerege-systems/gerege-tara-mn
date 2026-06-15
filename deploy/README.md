# ДАН — Локал хөгжүүлэлтийн орчин

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
| 6379 | Redis | Сесс кэш (ирээдүйд dan-login ашиглана) |

## Архитектурын тэмдэглэл

Hydra зориудаар **нэвтрэлтийн UI-гүй**. `hydra.yml`-д тохируулсанаар тэр иргэнийг
`http://localhost:3000/auth/login` руу чиглүүлнэ — энэ нь **бидний `dan-login`**
webapp болно (дараагийн үе шатанд бүтээнэ). Тиймээс одоогоор `make discovery`,
`make client` ажиллана; харин бүрэн нэвтрэлтийн урсгал нь `dan-login` бэлэн болмогц
эхэлнэ.

```
Иргэн → Hydra(4444) /authorize → [login_challenge] → dan-login(3000) → нэвтрэлт
      ← authorization code ←──────────────────────── [accept login] ←──┘
```

## Бүх стекийг контейнерээр (dan-login орсон)

`dan-login`-ийг Docker image болгож, бүх стекийг нэг командаар асааж болно:

```bash
docker compose up -d --build           # hydra + postgres + redis + dan-login
make client                            # туршилтын RP бүртгэх
```

`dan-login` нь контейнерт `hydra:4445` (admin) болон `redis:6379`-д сүлжээгээр
холбогдоно (`DAN_HYDRA_ADMIN_URL`, `REDIS_HOST` env-ээр). Хост дээр `:3000`
нээгдэнэ.

> Хэрэв `:3000` өөр процессоор эзлэгдсэн бол энэ service асахгүй. Тэр тохиолдолд
> тухайн процессыг зогсоох эсвэл локал mvn горимыг (доор) ашиглана.

## Зөвхөн дэд бүтэц + локал mvn (хөгжүүлэлтэд)

Хурдан давталтад `dan-login`-ийг хостод mvn-ээр ажиллуулах нь тохиромжтой:

```bash
docker compose up -d hydra redis       # зөвхөн дэд бүтэц
cd ../dan-login && ./mvnw spring-boot:run
```
