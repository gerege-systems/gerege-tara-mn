package mn.gov.gerege.web;

import mn.gov.gerege.hydra.HydraAdminClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Logout болон error урсгал (hydra.yml-д заасан endpoint-ууд).
 */
@Controller
@RequestMapping("/auth")
public class MiscController {

    private final HydraAdminClient hydra;

    public MiscController(HydraAdminClient hydra) {
        this.hydra = hydra;
    }

    /** Hydra-гийн logout flow — зөвшөөрч буцаах. */
    @GetMapping("/logout")
    public String logout(@RequestParam("logout_challenge") String logoutChallenge) {
        String redirectTo = hydra.acceptLogout(logoutChallenge);
        return "redirect:" + redirectTo;
    }

    /** Hydra нэвтрэлтийн алдаа гарвал энд чиглүүлнэ. */
    @GetMapping("/error")
    public String error(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "error_description", required = false) String description,
                        Model model) {
        model.addAttribute("error", error != null ? error : "unknown_error");
        model.addAttribute("description", description != null ? description : "Тодорхойгүй алдаа гарлаа.");
        return "error";
    }
}
