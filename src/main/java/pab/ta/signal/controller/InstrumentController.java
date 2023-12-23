package pab.ta.signal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.tinkoff.piapi.contract.v1.*;
import ru.tinkoff.piapi.core.InvestApi;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping(value = "/instrument")
public class InstrumentController {
    private InvestApi investApi;

    @Autowired
    public void setInvestApi(InvestApi investApi) {
        this.investApi = investApi;
    }

    @RequestMapping(value = "/share/{uid}")
    public String getShare(Model model, @PathVariable(value = "uid") String uid) throws ExecutionException, InterruptedException {

        CompletableFuture<Share> shareFuture = investApi.getInstrumentsService().getShareByUid(uid);
        Share share = shareFuture.get();

        model.addAttribute("share", share);

        return "share";
    }

    @RequestMapping(value = "/currency/{uid}")
    public String getCurrency(Model model, @PathVariable(value = "uid") String uid) throws ExecutionException, InterruptedException {
        CompletableFuture<Currency> currencyFuture = investApi.getInstrumentsService().getCurrencyByUid(uid);
        Currency currency = currencyFuture.get();

        model.addAttribute("currency", currency);

        return "currency";
    }

    @RequestMapping(value = "/futures/{uid}")
    public String getFuture(Model model, @PathVariable(value = "uid") String uid) throws ExecutionException, InterruptedException {
        CompletableFuture<Future> futureFuture = investApi.getInstrumentsService().getFutureByUid(uid);
        Future future = futureFuture.get();

        model.addAttribute("future", future);

        return "future";
    }

}