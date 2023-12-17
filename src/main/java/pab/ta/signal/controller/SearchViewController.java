package pab.ta.signal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.tinkoff.piapi.contract.v1.InstrumentShort;
import ru.tinkoff.piapi.core.InvestApi;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping(consumes = MediaType.ALL_VALUE)
public class SearchViewController {

    private InvestApi investApi;

    @Autowired
    public void setInvestApi( InvestApi investApi) {
        this.investApi = investApi;
    }

    @GetMapping(value = "/")
    public String index(@RequestParam(name = "search", required = false, defaultValue = "") String value, Model model) throws ExecutionException, InterruptedException {

        if (!value.isEmpty()){
            model.addAttribute("search", value);

            CompletableFuture<List<InstrumentShort>> feature = investApi.getInstrumentsService().findInstrument(value);
            List<InstrumentShort> instruments = feature.get();

            model.addAttribute("data", instruments);
        }

        return "search";
    }

}
