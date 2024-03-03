package singidunum.rs.client;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;


@Controller
public class HelloController {

    @GetMapping("/")
    public String index(HttpServletRequest request, Model model){
        var session = request.getSession();
        if(session != null && session.getAttribute("username") != null){
            model.addAttribute("username", session.getAttribute("username"));
            model.addAttribute("attributes", session.getAttribute("attributes"));
            return "authenticated";
        } else {
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
