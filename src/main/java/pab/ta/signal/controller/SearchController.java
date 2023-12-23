package pab.ta.signal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pab.ta.signal.util.AssetCache;
import ru.tinkoff.piapi.contract.v1.InstrumentShort;

import java.util.List;

@Controller
@RequestMapping(consumes = MediaType.ALL_VALUE)
public class SearchController {

    private AssetCache assetCache;

    @Autowired
    public void setAssetCache(AssetCache assetCache) {
        this.assetCache = assetCache;
    }

    @GetMapping(value = "/")
    public String index(@RequestParam(name = "search", required = false, defaultValue = "") String search,
                        @RequestParam(name = "type", required = false, defaultValue = "all") String type,
                        Model model) {

        if (search.length() >= 3) {
            model.addAttribute("search", search);

            List<InstrumentShort> instrumentShorts;
            switch (type) {
                case "futures", "currency", "share" -> {
                    model.addAttribute("type", type);
                    instrumentShorts = assetCache.findInstrument(search, type);
                }
                default -> {
                    model.addAttribute("type", "all");
                    instrumentShorts = assetCache.findInstrument(search);
                }
            };

            model.addAttribute("instruments", instrumentShorts);
        }

        return "search";
    }
}
