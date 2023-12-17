package pab.ta.signal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.tinkoff.piapi.contract.v1.CandleInterval;
import ru.tinkoff.piapi.contract.v1.HistoricCandle;
import ru.tinkoff.piapi.contract.v1.Share;
import ru.tinkoff.piapi.core.InvestApi;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping(value = "/asset")
public class AssetController {
    private InvestApi investApi;

    @Autowired
    public void setInvestApi(InvestApi investApi) {
        this.investApi = investApi;
    }

    @RequestMapping(value = "/share/{figi}")
    public String getShare(Model model, @PathVariable(value = "figi") String figi) throws ExecutionException, InterruptedException {

        CompletableFuture<Share> future = investApi.getInstrumentsService().getShareByFigi(figi);
        Share share = future.get();

        model.addAttribute("share", share);

        Instant to = LocalDateTime.now().toInstant(ZoneOffset.ofHours(0));
        Instant from = LocalDateTime.now().minusMonths(6).toInstant(ZoneOffset.ofHours(0));

        CompletableFuture<List<HistoricCandle>> candlesFeature = investApi.getMarketDataService()
                .getCandles(figi, from, to, CandleInterval.CANDLE_INTERVAL_DAY);

        List<HistoricCandle> candleList =  candlesFeature.get();

        model.addAttribute("candles", candleList);

        return "share";
    }

}