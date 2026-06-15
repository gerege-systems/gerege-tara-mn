# 02 — Гэрэгэ системийн архитектур

> TARA-гийн "стандарт цөм + үндэсний адаптер" загварыг Монголд буулгасан нь.

---

## 2.1. Өндөр түвшний бүтэц

```
┌──────────────────────────────────────────────────────────────────┐
│                      ИРГЭН (browser / mobile)                      │
└───────────────────────────────┬──────────────────────────────────┘
                                 │ HTTPS
┌───────────────────────────────▼──────────────────────────────────┐
│  ҮЙЛЧИЛГЭЭ (Relying Party) — E-Mongolia, татвар, ЭМД, НД г.м.      │
│  Холболт: стандарт OIDC client (ямар ч хэл, ямар ч платформ)       │
└───────────────────────────────┬──────────────────────────────────┘
                                 │ OIDC: /authorize, /token, /userinfo
┌───────────────────────────────▼──────────────────────────────────┐
│  ГЭРЭГЭ ЦӨМ                                                      │
│  ┌────────────────────┐      ┌──────────────────────────────────┐ │
│  │  Ory Hydra         │◀────▶│  Гэрэгэ-Login (Spring Boot)      │ │
│  │  (OIDC/OAuth2)     │login/│  ── Method Router ──             │ │
│  │  токен, JWKS,      │consent  ├─ eID Handler                  │ │
│  │  client бүртгэл    │      │  ├─ MobileID Handler  ← MVP      │ │
│  └────────────────────┘      │  ├─ SmartID Handler              │ │
│                              │  └─ Biometric Handler            │ │
│                              └───────────────┬──────────────────┘ │
└──────────────────────────────────────────────┼───────────────────┘
                                                │ X-Road (ХУР)
┌───────────────────────────────────────────────▼──────────────────┐
│  ҮНДЭСНИЙ ДЭД БҮТЭЦ                                                 │
│  ХУР (дата солилцоо) · ДАН · УБЕГ регистр · Оператор PKI · CA/OCSP │
└───────────────────────────────────────────────────────────────────┘
```

---

## 2.2. Модулиуд (Maven multi-module)

```
gerege/
├── gerege-login/              ← гол webapp (Spring Boot)
│   ├── core/                  ← Hydra интеграц, login/consent урсгал
│   ├── auth-api/              ← AuthenticationMethodHandler интерфейс
│   ├── auth-mobileid/         ← Mobile-ID адаптер (MVP)
│   ├── auth-eid/              ← eID карт адаптер
│   ├── auth-smartid/          ← Smart-ID/App адаптер
│   ├── auth-biometric/        ← Биометр адаптер (Монгол нэмэлт)
│   ├── xroad-client/          ← ХУР (X-Road) клиент
│   └── ui/                    ← Thymeleaf + JS, MN/EN орчуулга
├── gerege-admin/              ← RP (үйлчилгээ) бүртгэлийн админ
├── gerege-mock/               ← хөгжүүлэлтийн mock (TARA-Mock шиг)
└── docs/
```

**Зарчим:** `auth-api` нь нэг л интерфейс тодорхойлно — `AuthenticationMethodHandler`. Адаптер бүр үүнийг хэрэгжүүлнэ. Шинэ арга нэмэх = шинэ модуль бичих, цөмд хүрэхгүй. *(TARA-гийн нээлттэй/өргөтгөх загвар.)*

> **Одоогийн MVP бодит бүтэц** (нэг Maven модуль, дараа нь дээрх олон модуль болгож
> хуваана): `mn.gov.gerege` — гол интерфейс, төрлүүд; `.auth` — handler-ууд + registry;
> `.registry` — ХУР клиент (`PersonRegistry`/`MockHurClient`); `.hydra` — `HydraAdminClient`;
> `.web` — контроллер, `SessionStore` (Redis); `.audit` — `AuditLogger`.

---

## 2.3. Гол интерфейс (бүх адаптерийн гэрээ)

```java
// gerege-login/auth-api
public interface AuthenticationMethodHandler {

    /** Энэ арга идэвхтэй эсэх, ямар LoA түвшин өгөх вэ */
    AuthMethod method();              // MOBILE_ID, EID, SMART_ID, BIOMETRIC
    LevelOfAssurance assuranceLevel();// LOW / SUBSTANTIAL / HIGH

    /** Нэвтрэлт эхлүүлэх (жнь иргэнд QR/код харуулах, оператор руу хүсэлт явуулах) */
    AuthSession initiate(AuthRequest request);

    /** Төлөв шалгах (polling эсвэл callback) */
    AuthResult poll(AuthSession session);
}
```

Үр дүн нь баталгаажсан **иргэний дугаар (РД), нэр, LoA** болж Hydra-д `accept login` болж буцна.

---

## 2.4. Технологийн сонголт (TARA-тай нийцүүлэв)

| Давхарга | Технологи | Шалтгаан |
|----------|-----------|----------|
| OIDC цөм | **Ory Hydra** | TARA-тай ижил, баталгаажсан, нээлттэй эх |
| Webapp | **Java 17 + Spring Boot** | TARA-гийн стекийг хадгалснаар код дахин ашиглах |
| Сесс | **Redis** (сонгосон) | TARA Ignite ашигладаг; бид Redis сонгов (TTL-тэй, түгээмэл) |
| UI | Thymeleaf + ванила JS | Хөнгөн, серверээс render, олон хэл |
| Дата солилцоо | **X-Road / ХУР** | Монголын одоо байгаа дэд бүтэц |
| Контейнер | Docker + Helm/K8s | Орчин үеийн deploy |

> **Шийдэгдсэн:** Сесс хадгалалтыг **Redis**-ээр хийхээр сонгов. `SessionStore` нь
> `StringRedisTemplate`-ээр JSON хадгалж, 5 минутын TTL-тэй (хаягдсан нэвтрэлт автоматаар
> устах). Бодит хэрэгжүүлэлт: `gerege-login/.../web/SessionStore.java`.

---

## 2.5. Аюулгүй байдлын суурь (TARA-гаас өвлөв)

- **LoA mapping** — арга бүрд итгэмжлэлийн түвшин
- **OCSP + circuit breaker** — гэрчилгээ шалгалт, fault tolerance
- **Аудит лог** — бүх нэвтрэлтийг бүртгэх (ХЗХ-ийн шаардлага)
- **Хувийн мэдээллийн хамгаалалт** — Монголын *Хүний хувийн мэдээлэл хамгаалах тухай хууль*-д нийцүүлэх
- **TLS, CSP, rate limiting**
