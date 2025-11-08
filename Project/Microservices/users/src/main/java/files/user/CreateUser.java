package files.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateUser {

    @NotNull(message="cannot be null")
    @Size(min=2, message="must be more than 2 characters")
    private String firstName;

    @NotNull(message="cannot be null")
    @Size(min=2, message="must be more than 2 characters")
    private String lastName;

    @NotNull(message="cannot be null")
    @Size(min=8, max=16, message="between 8-16 characters")
    private String password;

    @NotNull(message="cannot be null")
    @Email
    private String email;
    
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    
}