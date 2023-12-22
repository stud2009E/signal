package pab.ta.signal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.tinkoff.piapi.contract.v1.InstrumentShort;
import ru.tinkoff.piapi.contract.v1.InstrumentType;
import ru.tinkoff.piapi.core.InvestApi;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping(consumes = MediaType.ALL_VALUE)
public class SearchController {

    private InvestApi investApi;

    @Autowired
    public void setInvestApi(InvestApi investApi) {
        this.investApi = investApi;
    }

    @GetMapping(value = "/")
    public String index(@RequestParam(name = "search", required = false, defaultValue = "") String value, Model model) throws ExecutionException, InterruptedException {

        if (!value.isEmpty()) {
            model.addAttribute("search", value);
            model.addAttribute("typeShare", InstrumentType.INSTRUMENT_TYPE_SHARE);
            model.addAttribute("typeCurrency", InstrumentType.INSTRUMENT_TYPE_CURRENCY);
            model.addAttribute("typeFuture", InstrumentType.INSTRUMENT_TYPE_FUTURES);

            CompletableFuture<List<InstrumentShort>> feature = investApi.getInstrumentsService().findInstrument(value);
            List<InstrumentShort> instruments = feature.get().stream()
                    .filter(instrumentShort ->
                            instrumentShort.getInstrumentKind() == InstrumentType.INSTRUMENT_TYPE_SHARE
                                    || instrumentShort.getInstrumentKind() == InstrumentType.INSTRUMENT_TYPE_CURRENCY
                                    || instrumentShort.getInstrumentKind() == InstrumentType.INSTRUMENT_TYPE_FUTURES)
                    .toList();

            model.addAttribute("data", instruments);
        }

        return "search";
    }

}
