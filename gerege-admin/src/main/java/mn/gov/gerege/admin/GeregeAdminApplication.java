package mn.gov.gerege.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Гэрэгэ — үйлчилгээ (RP) бүртгэлийн портал.
 *
 * <p>Үйлчилгээ эрхлэгчид өөрсдийн аппликейшнээ Гэрэгэ-нд бүртгэдэг UI. Дотооддоо
 * Hydra-гийн admin API (/admin/clients) дээр OAuth2 client үүсгэж/устгаж/жагсаана.
 * TARA-гийн "Liitumine" (холболт) процессын дүйцэл.</p>
 *
 * <p>⚠️ Энэ портал Hydra-гийн хүчтэй admin порт руу ханддаг тул production-д заавал
 * дотоод сүлжээнд тусгаарлаж, нэвтрэлтээр хамгаалах ёстой.</p>
 */
@SpringBootApplication
public class GeregeAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(GeregeAdminApplication.class, args);
    }
}
