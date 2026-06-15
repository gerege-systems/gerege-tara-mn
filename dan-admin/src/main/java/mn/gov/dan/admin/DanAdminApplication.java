package mn.gov.dan.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ДАН — үйлчилгээ (RP) бүртгэлийн портал.
 *
 * <p>Үйлчилгээ эрхлэгчид өөрсдийн аппликейшнээ ДАН-нд бүртгэдэг UI. Дотооддоо
 * Hydra-гийн admin API (/admin/clients) дээр OAuth2 client үүсгэж/устгаж/жагсаана.
 * TARA-гийн "Liitumine" (холболт) процессын дүйцэл.</p>
 *
 * <p>⚠️ Энэ портал Hydra-гийн хүчтэй admin порт руу ханддаг тул production-д заавал
 * дотоод сүлжээнд тусгаарлаж, нэвтрэлтээр хамгаалах ёстой.</p>
 */
@SpringBootApplication
public class DanAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(DanAdminApplication.class, args);
    }
}
