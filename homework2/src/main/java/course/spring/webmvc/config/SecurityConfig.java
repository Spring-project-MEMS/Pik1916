package course.spring.webmvc.config;

import course.spring.webmvc.domain.UsersService;
import course.spring.webmvc.exception.NonexistingEntityException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .exceptionHandling()
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/","/css/**", "/img/**", "/js/**", "/font/**", "/webjars/**").permitAll()
                .antMatchers(HttpMethod.GET, "/users/registration").permitAll()
                .antMatchers(HttpMethod.GET, "/**").authenticated()
                .antMatchers(HttpMethod.POST, "/users/registration").permitAll()
                .antMatchers(HttpMethod.POST, "/**").hasAnyRole("BLOGGER", "ADMIN")
                .antMatchers(HttpMethod.PUT).hasAnyRole("BLOGGER", "ADMIN")
                .antMatchers(HttpMethod.DELETE).hasAnyRole("BLOGGER", "ADMIN")
                .and()
                .formLogin()
                .permitAll()
                .failureHandler(new SimpleUrlAuthenticationFailureHandler())
                .and()
                .logout()
                .deleteCookies("JSESSIONID")
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .logoutUrl("/logout");
//                .and()
//                    .rememberMe();
    }

    @Bean
    public UserDetailsService userDetailsService(UsersService usersService) {

        return username -> {
            try {
                return usersService.findByUsername(username);
            } catch (NonexistingEntityException ex) {
                throw new UsernameNotFoundException(ex.getMessage(), ex);
            }
        };

    }
}
