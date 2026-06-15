package mn.gov.dan.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.Locale;

/**
 * Олон хэлний дэмжлэг — Монгол (default) ба Англи.
 *
 * <p>Хэлийг {@code DAN_LANG} cookie-гээр тогтооно (frontend дээр сэлгэнэ), мөн
 * {@code ?lang=en} параметрээр ч өөрчилж болно. Текстүүд messages_mn / messages_en
 * .properties-д байна.</p>
 */
@Configuration
public class I18nConfig implements WebMvcConfigurer {

    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver resolver = new CookieLocaleResolver("DAN_LANG");
        resolver.setDefaultLocale(new Locale("mn"));
        resolver.setCookiePath("/");
        resolver.setCookieMaxAge(java.time.Duration.ofDays(365));
        return resolver;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        return interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
}
