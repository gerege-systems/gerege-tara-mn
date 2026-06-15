# dan-login

ДАН-гийн нэвтрэлтийн webapp — **Ory Hydra-гийн login/consent provider**
(Эстонийн `TARA-Login`-ий дүйцэл).

## Урсгал

```
authorize(Hydra) → /auth/login (арга сонгох) → Mobile-ID (mock, poll)
   → accept login → /auth/consent (авто-зөвшөөрөл) → RP callback дээр code
```

## Ажиллуулах

Эхлээд Hydra орчныг асаа (`../deploy`):

```bash
cd ../deploy && docker compose up -d && make client
```

Дараа нь webapp-ыг асаа (порт **3000**):

```bash
./mvnw spring-boot:run
```

## End-to-end тест

`../deploy` орчин + энэ апп ажиллаж байх үед:

```bash
bash /tmp/dan-e2e.sh    # эсвэл docs дахь жишээ скриптийг ашиглана
```

Амжилттай бол `id_token`-д `sub` (РД), `name`, `loa: HIGH` claim-ууд харагдана.

## Бүтэц

| Хэсэг | Файл |
|-------|------|
| Нэвтрэлтийн арын гэрээ | `AuthenticationMethodHandler`, `AuthMethod`, `LevelOfAssurance` |
| Загварын төрлүүд | `AuthRequest`, `AuthSession`, `AuthResult` |
| Hydra admin клиент | `hydra/HydraAdminClient` |
| Mobile-ID (mock) | `auth/MockMobileIdHandler` |
| Контроллерууд | `web/LoginController`, `web/ConsentController`, `web/MiscController` |
| UI | `resources/templates/*.html`, `resources/static/dan.css` |

## Дараа нь

- `MockMobileIdHandler`-ийг бодит оператор SIM-PKI-аар солих (урсгал хэвээр)
- `auth-eid`, `auth-smartid`, `auth-biometric` адаптерууд нэмэх
- Сессийг Redis рүү шилжүүлэх (одоо in-memory)
