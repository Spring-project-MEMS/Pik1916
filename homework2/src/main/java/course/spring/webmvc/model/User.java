package course.spring.webmvc.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;

@Document("users")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
    @Id
    private String id;
    @NonNull
    @NotNull
    @Size(min= 2, max = 20)
    private String username;
    @Size(min= 2, max = 30)
    private String firstName;
    @Size(min= 2, max = 30)
    private String lastName;
    @NonNull
    @Email
    private String email;
    @NonNull
    @NotNull
    @Size(min= 4)
    private String password;
    @NonNull
    private String role;
    private String pictureUrl;
    @NonNull
    private String status = "active";

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        LinkedList<GrantedAuthority> authorities = new LinkedList<>();
        authorities.push(new SimpleGrantedAuthority(role));
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return status.equals("active");
    }

    @Override
    public boolean isAccountNonLocked() {
        return status.equals("active");
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return status.equals("active");
    }

    @Override
    public boolean isEnabled() {
        return status.equals("active");
    }
}
