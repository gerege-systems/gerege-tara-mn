# 01 — Эх сурвалжийн судалгаа: Эстонийн TARA

> *"Сайн архитектурыг ойлгохын тулд эхлээд түүнийг хүндэтгэ."*
> Энэ бүлэг нь Монголд хувилахаас өмнө TARA-гийн **бодит инженерчлэлийг** задлан шинжилнэ.

---

## 1.1. TARA гэж юу вэ?

**TARA** (эст. *Riigi Autentimisteenus* = "Төрийн нэвтрэлтийн үйлчилгээ") бол Эстони улсын **Төрийн мэдээллийн системийн газар (RIA)**-аас үзүүлдэг төвлөрсөн баталгаажуулалтын үйлчилгээ. Аливаа төрийн (болон зөвшөөрөгдсөн хувийн) цахим үйлчилгээ нь TARA-д холбогдсоноор иргэдээ **ID-карт, Mobile-ID, Smart-ID, эсвэл ЕХ-ны eIDAS** хэрэгслээр нэвтрүүлэх боломжтой болдог.

**Хэмжээ (магтууштай үр дүн):** 112 байгууллагын **582 мэдээллийн систем** TARA-д холбогдсон — татварын алба, тээврийн агентлаг, төрийн порталууд гэх мэт.

[RIA — Central authentication services](https://www.ria.ee/en/state-information-system/electronic-identity-eid-and-trust-services/central-authentication-services)

---

## 1.2. Бүрэлдэхүүн хэсгүүд (e-gov GitHub репозиториуд)

Эстони бүх зүйлээ нээлттэй тавьсан нь биднийг **дугуйг дахин зохиохоос** аварч байна:

| Репозитори | Хэл | Үүрэг |
|------------|-----|-------|
| [`TARA-Login`](https://github.com/e-gov/TARA-Login) | Java | Нэвтрэлтийн гол webapp (login/consent урсгал) |
| [`TARA-Doku`](https://github.com/e-gov/TARA-Doku) | CSS/MD | Техникийн ба бизнес баримт бичиг |
| [`TARA-Test`](https://github.com/e-gov/TARA-Test) | Groovy | Интеграцийн тестүүд |
| [`GOVSSO`](https://github.com/e-gov/GOVSSO) | — | GovSSO протоколын баримт |
| [`TARA-GovSSO-InProxy`](https://github.com/e-gov/TARA-GovSSO-InProxy) | Java | Орох прокси |
| [`GovSSO-Test`](https://github.com/e-gov/GovSSO-Test) | — | GovSSO интеграцийн тест |

> **TARA vs GovSSO:** TARA = **нэвтрэлт** (authentication, "чи хэн бэ"). GovSSO (2023.09.18-нд нэвтэрсэн) = **нэг удаагийн нэвтрэлт (SSO)** + сесс хуваалцах. Гэрэгэ эхлээд TARA хэсгийг хувилж, дараа нь GovSSO-г нэмж болно.

---

## 1.3. TARA-Login технологийн стек

```
┌─────────────────────────────────────────────┐
│  Java 17+ · Spring Boot · embedded Tomcat    │  ← гол runtime
│  Maven (build) · Docker (spring-boot:build-image)│
├─────────────────────────────────────────────┤
│  Apache Ignite   → тархсан сесс кэш (cluster) │
│  Web eID library → ID-карт нэвтрэлт           │
│  OCSP + circuit breaker → гэрчилгээ шалгалт   │
│  Spring Boot Actuator → мониторинг            │
└─────────────────────────────────────────────┘
Код: ~80.9% Java, ~12.5% JavaScript (frontend)
Лиценз: MIT
```

**Гол ажиглалт:** TARA-Login нь **OIDC серверийг өөрөө хэрэгжүүлдэггүй**. Тэр зөвхөн **Ory Hydra**-гийн *login & consent flow*-г хэрэгжүүлдэг webapp. Hydra нь OAuth2/OIDC токен, authorization endpoint, JWKS зэргийг бүрэн хариуцна. TARA-Login зөвхөн "хэрэглэгчийг хэрхэн таних вэ" гэдгийг л шийднэ.

---

## 1.4. Нэвтрэлтийн 5 арга (handler-ууд)

TARA-Login-д арга бүр өөрийн handler-тай, гэхдээ бүгд ижил Hydra урсгалд нийлдэг:

1. **Estonian ID-card** — Web eID (browser/native), OCSP-ээр гэрчилгээ шалгана
2. **Mobile-ID** — SK-гийн MID үйлчилгээтэй холбогдоно (SIM дээрх PKI)
3. **Smart-ID** — олон урсгал: notification, Web2App, QR код
4. **eIDAS** — ЕХ хооронд хил дамнасан нэвтрэлт (eIDAS-Node)
5. **Legal Person** — бизнес регистрийн нэвтрэлт (X-Road-аар)

Дэлгэрэнгүй: [TechnicalSpecification](https://e-gov.github.io/TARA-Doku/TechnicalSpecification)

---

## 1.5. Гол урсгал (OIDC login flow)

```
RP (үйлчилгээ)                Hydra              Гэрэгэ-Login           Гадны үйлчилгээ
   │  authorize request         │                     │                     │
   │ ─────────────────────────▶ │                     │                     │
   │                            │  login_challenge    │                     │
   │                            │ ──────────────────▶ │                     │
   │                            │                     │  арга сонгох UI     │
   │                            │                     │  (иргэнд харуулна)  │
   │                            │                     │  ── MID/eID/...  ──▶ │
   │                            │                     │ ◀── баталгаажлаа ── │
   │                            │  accept login       │                     │
   │                            │ ◀────────────────── │                     │
   │  ◀── authorization code ── │                     │                     │
   │  токен солилцоно (id_token, access_token)        │                     │
```

**Гэрэгэд хувилахдаа өөрчлөгдөх хэсэг:** зөвхөн *"Гадны үйлчилгээ"* багана (SK → Монголын оператор PKI / ХУР / ДАН). Бусад бүх урсгал **яг хэвээрээ үлдэнэ.**

---

## 1.6. Аюулгүй байдлын онцлогууд (хувилж авах ёстой)

- **OCSP** гэрчилгээний хүчинтэй эсэхийг шалгах + **circuit breaker** (гадны үйлчилгээ унавал систем бүхэлдээ унахгүй)
- **Level of Assurance (LoA)** — eIDAS-ийн `low/substantial/high` түвшин. Арга бүр өөр итгэмжлэлийн түвшинтэй (жнь Mobile-ID = high)
- **Сесс аюулгүй байдал** — Apache Ignite дээр түр зуурын, таймауттай (default 300s)
- **CSP толгойнууд**, TLS гэрчилгээний удирдлага
- **Бүтэцлэгдсэн лог** + аудит (хэн, хэзээ, ямар аргаар нэвтэрсэн)

---

## 1.7. Монголд хувилахад: тааруулалтын газрын зураг

| TARA хэсэг | Хадгалах уу? | Монгол дахь өөрчлөлт |
|------------|:---:|----------------------|
| Ory Hydra (OIDC цөм) | Яг хэвээр | Өөрчлөлтгүй |
| Login/consent урсгал | Яг хэвээр | Орчуулга (MN/EN), UI брэндинг |
| `AuthMethodHandler` интерфейс | Хэвээр | Шинэ адаптерууд залгана |
| ID-card handler | Адаптер | ҮДШ/чипт үнэмлэх + PKI |
| Mobile-ID handler | Адаптер | Оператор SIM-PKI (Mobicom г.м.) |
| Smart-ID handler | Адаптер | Банк/E-Mongolia апп |
| eIDAS handler | Хасах | Монголд хамаарахгүй (ЕХ бус) |
| X-Road холболт | Хэвээр | ХУР систем (мөн X-Road суурьтай!) |
| Биометр | Шинэ | TARA-д байхгүй — Монгол нэмэлт |

> **Азтай давхцал:** Монголын **ХУР** систем нь Эстонийн X-Road технологид суурилсан тул TARA-гийн өгөгдөл солилцооны загвар бараг шууд буудаг.
