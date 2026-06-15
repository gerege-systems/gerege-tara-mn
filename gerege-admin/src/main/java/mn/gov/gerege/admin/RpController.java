package mn.gov.gerege.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;
import java.util.Map;

/**
 * Үйлчилгээ (RP) бүртгэлийн порталын контроллер.
 */
@Controller
public class RpController {

    private static final Logger log = LoggerFactory.getLogger(RpController.class);

    private final HydraClientApi hydra;

    public RpController(HydraClientApi hydra) {
        this.hydra = hydra;
    }

    /** Бүртгэгдсэн үйлчилгээнүүдийн жагсаалт. */
    @GetMapping("/")
    public String list(Model model) {
        List<Map<String, Object>> clients = hydra.listClients();
        model.addAttribute("clients", clients);
        model.addAttribute("count", clients.size());
        return "clients";
    }

    /** Шинэ үйлчилгээ бүртгэх форм. */
    @GetMapping("/clients/new")
    public String newForm() {
        return "client-new";
    }

    /** Шинэ үйлчилгээ үүсгэх. */
    @PostMapping("/clients")
    public String create(@RequestParam("clientId") String clientId,
                         @RequestParam("clientName") String clientName,
                         @RequestParam("redirectUris") String redirectUris,
                         @RequestParam(value = "profileScope", defaultValue = "false") boolean profileScope,
                         Model model) {
        Map<String, Object> body = ClientForms.toCreateBody(clientId, clientName, redirectUris, profileScope);
        try {
            Map<String, Object> created = hydra.createClient(body);
            log.info("Үйлчилгээ бүртгэгдлээ: {}", created.get("client_id"));
            model.addAttribute("client", created);
            return "client-created";
        } catch (RestClientResponseException e) {
            log.warn("Бүртгэл амжилтгүй: {}", e.getResponseBodyAsString());
            model.addAttribute("error", "Бүртгэл амжилтгүй: " + friendlyError(e));
            model.addAttribute("clientId", clientId);
            model.addAttribute("clientName", clientName);
            model.addAttribute("redirectUris", redirectUris);
            return "client-new";
        }
    }

    /** Үйлчилгээ устгах. */
    @PostMapping("/clients/{id}/delete")
    public String delete(@PathVariable("id") String clientId) {
        hydra.deleteClient(clientId);
        log.info("Үйлчилгээ устгагдлаа: {}", clientId);
        return "redirect:/";
    }

    private String friendlyError(RestClientResponseException e) {
        if (e.getStatusCode().value() == 409) {
            return "ийм client_id аль хэдийн бүртгэлтэй байна.";
        }
        return e.getStatusText();
    }
}
