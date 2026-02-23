package com.example;

import com.example.user.Role;
import com.example.user.User;
import com.example.user.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User userA;
    @BeforeEach
    void setup(){
        userA = userRepository.save(
                new User("a@test.com", passwordEncoder.encode("password"), Role.USER)
        );


    }
    private String loginAndGetToken(String email, String password) throws Exception {
        String response = mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content("""
                        {
                            "email": "%s",
                            "password": "%s"
                        }
                    """.formatted(email, password)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return response.trim().replace("\"", ""); // response is a plain JWT string
    }

    @Test
    void logoutInvalidatesToken() throws Exception {
        String token = loginAndGetToken("a@test.com", "password");

        // logout
        mockMvc.perform(post("/auth/logout")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        // same token should now be rejected
        mockMvc.perform(get("/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void duplicateRegistrationNotAllowed() throws Exception{
        mockMvc.perform(post("/auth/register")
                .contentType("application/json")
                .content("""
                        { "email": "a@test.com",
                          "password": "password" }
                    """))
                .andExpect(status().isConflict());
    }

    @Test
    void loginWithInvalidCredentialsNotAllowed() throws Exception{
        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content("""
                        { "email": "a@test.com",
                          "password": "wrongOne" }
                    """))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content("""
                        { "email": "wrongUser@test.com",
                          "password": "password" }
                    """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void passwordIsHashed() throws Exception {

        String rawPassword = "securePassword";

        mockMvc.perform(post("/auth/register")
                        .contentType("application/json")
                        .content("""
                {
                    "email": "newuser@test.com",
                    "password": "%s"
                }
            """.formatted(rawPassword)))
                .andExpect(status().isCreated());

        User savedUser = userRepository.findByEmail("newuser@test.com")
                .orElseThrow();


        assertThat(savedUser.getPassword()).isNotEqualTo(rawPassword);
        assertThat(passwordEncoder.matches(rawPassword, savedUser.getPassword())).isTrue();
    }

    @Test
    void validRegistrationSucceeds() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType("application/json")
                        .content("""
                        { "email": "new@test.com",
                          "password": "password" }
                    """))
                .andExpect(status().isCreated());
    }

    @Test
    void validLoginReturnsToken() throws Exception {
        String token = loginAndGetToken("a@test.com", "password");
        assertThat(token).isNotBlank();
    }

    @Test
    void loginValidationWorks() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content("""
                        { "email": "a@test.com",
                          "password": "short" }
                    """))
                .andExpect(status().isBadRequest());
    }

}
