package course.spring.homework1.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Document("users")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private String id;
    @NonNull
    @NotNull
    @Size(min= 2)
    private String firstName;
    private String finalName;
    @NonNull
    @Email
    private String email;
    @NonNull
    @Size(min= 4)
    private String password;
    private String role = "Blogger";
    private String profilePhoto;
}
