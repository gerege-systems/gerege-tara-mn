# gerege-admin

Гэрэгэ-гийн **үйлчилгээ (RP) бүртгэлийн портал** — үйлчилгээ эрхлэгчид
аппликейшнээ Гэрэгэ-нд бүртгэдэг UI (TARA-гийн "Liitumine" процессын дүйцэл).

Дотооддоо Hydra-гийн admin API (`/admin/clients`) дээр OAuth2 client үүсгэж,
жагсааж, устгана.

## Боломжууд

- Бүртгэлтэй үйлчилгээнүүдийн жагсаалт
- Шинэ үйлчилгээ бүртгэх (client_id, нэр, redirect URI, scope) — `client_secret`
  үүсэж, зөвхөн нэг удаа харагдана
- Үйлчилгээ устгах

## Ажиллуулах (порт 4000)

```bash
# Hydra ажиллаж байх ёстой (../deploy)
./mvnw spring-boot:run
# нээх: http://localhost:4000
```

Hydra admin хаягийг `GEREGE_HYDRA_ADMIN_URL` env-ээр тохируулна
(default `http://localhost:4445`; контейнерт `http://hydra:4445`).

## Аюулгүй байдал

Энэ портал Hydra-гийн **хүчтэй admin порт** руу ханддаг тул production-д заавал:
- Дотоод сүлжээнд тусгаарлах (интернетэд нээхгүй)
- Админ нэвтрэлтээр хамгаалах (одоогийн MVP-д нэвтрэлтгүй)

## Бүтэц

| Хэсэг | Файл |
|-------|------|
| Hydra client API | `HydraClientApi` |
| Форм → body хөрвүүлэлт | `ClientForms` (нэгж тесттэй) |
| Контроллер | `RpController` |
| UI (флат SVG icon, emoji-гүй) | `templates/*.html`, `static/admin.css` |
