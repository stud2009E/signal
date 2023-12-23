package pab.ta.signal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import pab.ta.signal.util.AssetCache;
import ru.tinkoff.piapi.contract.v1.*;
import ru.tinkoff.piapi.core.InvestApi;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping(value = "/asset")
public class AssetController {
    private InvestApi investApi;

    private AssetCache assetCache;

    @Autowired
    public void setAssetCache(AssetCache assetCache) {
        this.assetCache = assetCache;
    }

    @Autowired
    public void setInvestApi(InvestApi investApi) {
        this.investApi = investApi;
    }

    @RequestMapping(value = "/share/{uid}")
    public String getShare(Model model, @PathVariable(value = "uid") String uid) throws ExecutionException, InterruptedException {

        CompletableFuture<Share> shareFuture = investApi.getInstrumentsService().getShareByUid(uid);
        Share share = shareFuture.get();

        model.addAttribute("share", share);
        addBrand(uid, model);


//        Instant to = LocalDateTime.now().toInstant(ZoneOffset.ofHours(0));
//        Instant from = LocalDateTime.now().minusMonths(6).toInstant(ZoneOffset.ofHours(0));
//
//        CompletableFuture<List<HistoricCandle>> candlesFeature = investApi.getMarketDataService()
//                .getCandles(uid, from, to, CandleInterval.CANDLE_INTERVAL_DAY);
//
//        List<HistoricCandle> candleList =  candlesFeature.get();
//
//        model.addAttribute("candles", candleList);

        return "share";
    }

    @RequestMapping(value = "/currency/{uid}")
    public String getCurrency(Model model, @PathVariable(value = "uid") String uid) throws ExecutionException, InterruptedException {
        CompletableFuture<Currency> currencyFuture = investApi.getInstrumentsService().getCurrencyByUid(uid);
        Currency currency = currencyFuture.get();

        model.addAttribute("currency", currency);

        return "currency";
    }

    @RequestMapping(value = "/future/{uid}")
    public String getFuture(Model model, @PathVariable(value = "uid") String uid) throws ExecutionException, InterruptedException {
        CompletableFuture<Future> futureFuture = investApi.getInstrumentsService().getFutureByUid(uid);
        Future future = futureFuture.get();

        model.addAttribute("future", future);

        return "future";
    }

    private void addBrand(String uid, Model model) throws ExecutionException, InterruptedException {
        String assetUid = assetCache.getAssetFullUid(uid);
        Brand brand = null;
        if (!Objects.isNull(assetUid)) {
            CompletableFuture<AssetFull> assetFuture = investApi.getInstrumentsService().getAssetBy(assetUid);
            AssetFull asset = assetFuture.get();
            brand = asset.getBrand();
        }

        model.addAttribute("brand", brand);
    }

}