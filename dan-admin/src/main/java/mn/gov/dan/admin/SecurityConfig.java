package mn.gov.dan.admin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Админ порталын нэвтрэлт.
 *
 * <p>Бүх хуудас нэвтрэлт шаардана (form login). Хэрэглэгчийн нэр/нууц үгийг
 * {@code DAN_ADMIN_USER} / {@code DAN_ADMIN_PASSWORD} env-ээр өгнө. Энэ портал
 * Hydra-гийн admin API руу ханддаг тул нэвтрэлт зайлшгүй.</p>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health").permitAll()
                        .anyRequest().authenticated())
                .formLogin(Customizer.withDefaults())
                .logout(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    UserDetailsService userDetailsService(
            @Value("${dan.admin.user}") String user,
            @Value("${dan.admin.password}") String password,
            PasswordEncoder encoder) {
        return new InMemoryUserDetailsManager(
                User.withUsername(user)
                        .password(encoder.encode(password))
                        .roles("ADMIN")
                        .build());
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
