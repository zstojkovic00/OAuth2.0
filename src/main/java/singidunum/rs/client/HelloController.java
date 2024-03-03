package singidunum.rs.client;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.UUID;


@Controller
public class HelloController {
    @Value("${client.id}")
    private String clientId;
    @Value("${client.secret.id}")
    private String clientSecret;

    @GetMapping("/")
    public String index(HttpServletRequest request, Model model) {
        var session = request.getSession();
        if (session != null && session.getAttribute("username") != null) {
            model.addAttribute("username", session.getAttribute("username"));
            model.addAttribute("attributes", session.getAttribute("attributes"));
            return "authenticated";
        } else {
            var loginUri = UriComponentsBuilder.fromHttpUrl("https://dev-80556277.okta.com/oauth2/default/v1/authorize")
                    .queryParam("redirect_uri", "http://localhost:8080/oauth2/callback")
                    .queryParam("response_type", "code")
                    .queryParam("state", UUID.randomUUID())
                    .queryParam("scope", "openid email profile")
                    .queryParam("client_id", clientId)
                    .build();
            model.addAttribute("loginUri", loginUri);

            return "anonymous";
        }
    }

    @PostMapping("/login")
    public String login(HttpServletRequest request) {
        var session = request.getSession(true);
        session.setAttribute("username", "Zeljko");
        request.getSession().setAttribute("attributes",
                Map.of(
                        "firstName", "Zeljko",
                        "lastName", "Stojkovic",
                        "company", "Yettel",
                        "userType", "hardcoded"
                )
        );
        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        var session = request.getSession();
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }
}
