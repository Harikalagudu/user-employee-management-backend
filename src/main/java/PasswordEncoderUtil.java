import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderUtil {
    public static void main(String[] args) {
        // Change "new_password" to the plain-text password you want to use
        String plainTextPassword = "password";

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(plainTextPassword);

        System.out.println("Encoded password: " + encodedPassword);
    }
}