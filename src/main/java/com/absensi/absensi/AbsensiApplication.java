package com.absensi.absensi;

import com.absensi.absensi.model.*;
import com.absensi.absensi.model.enums.Role;
import com.absensi.absensi.repository.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
@Slf4j
public class AbsensiApplication {
    public static void main(String[] args) {
        SpringApplication.run(AbsensiApplication.class, args);
    }

    @Autowired
    DosenRepository dosenRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AbsenRepository absenRepository;

    @Autowired
    private MatakuliahRepository matakuliahRepository;


    @Bean
    UserDetailsService userDetailsServiceDosen() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                Optional<Dosen> optionalDosen = dosenRepository.findByUsername(username);
                if (optionalDosen.isPresent()) {
                    return optionalDosen.get();
                } else {
                    Optional<Student> studentOptional = studentRepository.findByNim(username);
                    if (studentOptional.isPresent()) {
                        return studentOptional.get();
                    } else {
                        throw new UsernameNotFoundException("user not found");
                    }
                }
            }

            ;
        };
    }

    @Bean
    AuthenticationManager authenticationManagerDosen(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsServiceDosen()).passwordEncoder(bCryptPasswordEncoder());
        return authenticationManagerBuilder.build();
    }


    // student


    @Bean
    public AuthenticationManager customAuthenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService()).passwordEncoder(bCryptPasswordEncoder());
        return authenticationManagerBuilder.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsServiceDosen());
        daoAuthenticationProvider.setPasswordEncoder(bCryptPasswordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    public PasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public class CustomSuccessHandler implements AuthenticationSuccessHandler {


        private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
            AuthenticationSuccessHandler.super.onAuthenticationSuccess(request, response, chain, authentication);

        }

        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
            handle(request, response, authentication);
            clearAuthenticationAttributes(request);
        }

        public void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

            String targetUrl = determineTargetUrl(authentication);

            if (response.isCommitted()) {
                return;
            }
            redirectStrategy.sendRedirect(request, response, targetUrl);
        }

        protected String determineTargetUrl(final Authentication authentication) {
            final Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            for (final GrantedAuthority grantedAuthority : authorities) {
                String authorityName = grantedAuthority.getAuthority();
                if (authorityName.equalsIgnoreCase("dosen")) {
                    return "dosen/dashboard";
                } else {
                    return "student/dashboard";
                }
            }
            throw new IllegalStateException();
        }

        protected void clearAuthenticationAttributes(HttpServletRequest request) {
            HttpSession session = request.getSession(false);
            if (session == null) {
                return;
            }
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
    }


    @Bean
    @Order(1)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests()
                .requestMatchers("/student")
                .authenticated()
                .requestMatchers("/resources/**", "/dosen/register", "/student/register", "/student/login").anonymous()
                .requestMatchers("/dosen/**").hasAnyAuthority("DOSEN")
                .requestMatchers("/student/**").hasAuthority("STUDENT")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .permitAll()
                .successHandler(new CustomSuccessHandler())
                .and()
                .logout()
                .deleteCookies("JSESSIONID")
                .and()
                .exceptionHandling()
                .accessDeniedPage("/403")
                .and()
                .csrf().disable()
                .cors().disable();
        return http.build();
    }

    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                return studentRepository.findByNim(username).orElseThrow(() -> new UsernameNotFoundException("user not found"));
            }

            ;
        };
    }

    @Bean
    CommandLineRunner commandLineRunner(StudentRepository studentRepository, JurusanRepository jurusanRepository) {
        return args -> {
            var informatika = Jurusan.builder()
                    .jurusan("informatika")
                    .build();
            var teknik_komputer = Jurusan.builder()
                    .jurusan("teknik komputer")
                    .build();
            Optional<Jurusan> informatika_optional = jurusanRepository.findByJurusan(informatika.getJurusan());
            Optional<Jurusan> teknikKomputer_optional = jurusanRepository.findByJurusan(informatika.getJurusan());
            if (informatika_optional.isPresent() && teknikKomputer_optional.isPresent()) {

            } else {
                jurusanRepository.insert(List.of(informatika, teknik_komputer));
            }
            Student student = Student.builder()
                    .nama("rahasia")
                    .nim("student")
                    .password(passwordEncoder.encode("rahasia"))
                    .jurusan(teknik_komputer)
                    .email("mohammadtajutzamzami07@gmail.com")
                    .role(Role.STUDENT)
                    .kelas("Smt 2 GOL e")
                    .build();
            var strukturData = Matakuliah.builder()
                    .name("struktur data")
                    .build();
            var algoritma = Matakuliah.builder()
                    .name("Algoritma")
                    .build();
            if (matakuliahRepository.findByName(strukturData.getName()).isPresent() && matakuliahRepository.findByName(algoritma.getName()).isPresent()) {

            } else {
                matakuliahRepository.insert(List.of(strukturData, algoritma));
            }

            var dosen = Dosen.builder()
                    .namaDosen("budi")
                    .username("dosen")
                    .role(Role.DOSEN)
                    .password(passwordEncoder.encode("rahasia")).
                    matakuliah(strukturData).
                    build();
            var jamal = Dosen.builder()
                    .namaDosen("jamal")
                    .username("jamal")
                    .role(Role.DOSEN)
                    .password(passwordEncoder.encode("rahasia")).
                    matakuliah(algoritma).
                    build();
            Optional<Dosen> byUsername = dosenRepository.findByUsername(dosen.getUsername());
            Optional<Dosen> byUsername1 = dosenRepository.findByUsername(jamal.getUsername());
            if (byUsername.isPresent() && byUsername1.isPresent()) {

            } else {
                dosenRepository.insert(List.of(dosen, jamal));
            }
            if (studentRepository.findByNim(student.getNim()).isPresent()) {

            } else {
                studentRepository.insert(student);
            }
        };
    }
}
