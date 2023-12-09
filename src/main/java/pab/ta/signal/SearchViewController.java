package pab.ta.signal;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(consumes = MediaType.ALL_VALUE)
public class SearchViewController {
    @GetMapping(value = "/")
    public String index(@RequestParam(name = "search", required = false, defaultValue = "") String value, Model model){

        if (!value.isEmpty()){
            model.addAttribute("search", value);
            model.addAttribute("data", new String[]{value, "Apple", "TESLA", "LIVENT"});
        }

        return "search";
    }

    @PostMapping(value = "/search")
    public String search(@RequestParam(name = "value", required = false, defaultValue = "") String value){
        return "redirect:/?search=" + value;
    }

}
