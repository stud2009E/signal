package pab.ta.signal.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.tinkoff.piapi.contract.v1.Currency;
import ru.tinkoff.piapi.contract.v1.*;
import ru.tinkoff.piapi.core.InvestApi;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Component
public class AssetCache {
    private InvestApi investApi;
    private final List<Asset> assets = new ArrayList<>();

    private final Map<String, Share> shareMap = new HashMap<>();

    private final Map<String, Currency> currencyMap = new HashMap<>();

    @Autowired
    public void setInvestApi(InvestApi investApi) {
        this.investApi = investApi;
    }

    public Map<String, Share> getShareMap() {
        synchronized (shareMap) {
            if (shareMap.isEmpty()) {
                List<Share> shares = investApi.getInstrumentsService().getSharesSync(InstrumentStatus.INSTRUMENT_STATUS_BASE);
                shares.forEach(item -> shareMap.put(item.getUid(), item));
            }
        }

        return shareMap;
    }

    public Map<String, Currency> getCurrencyMap() {
        synchronized (currencyMap) {
            if (currencyMap.isEmpty()) {
                List<Currency> currencies = investApi.getInstrumentsService().getCurrenciesSync(InstrumentStatus.INSTRUMENT_STATUS_BASE);
                currencies.forEach(item -> currencyMap.put(item.getUid(), item));
            }
        }

        return currencyMap;
    }


    public List<AssetFull> getAssetFullForInstrument(List<String> instrumentIds) throws ExecutionException, InterruptedException {
        synchronized (assets) {
            if (assets.isEmpty()) {
                assets.addAll(investApi.getInstrumentsService().getAssets()
                        .get()
                        .stream()
                        .filter(item -> item.getType() != AssetType.ASSET_TYPE_UNSPECIFIED)
                        .toList());
            }
        }

        Map<String, String> mapId = new HashMap<>();
        instrumentIds.forEach(id -> mapId.put(id, id));

        List<String> assetId = assets.parallelStream()
                .filter(asset -> asset.getInstrumentsList()
                        .stream().anyMatch(instrument -> mapId.containsKey(instrument.getUid())))
                .map(Asset::getUid)
                .toList();

        return assetId.parallelStream().map(id -> investApi.getInstrumentsService().getAssetBy(id)).map(future -> {
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                return null;
            }
        }).filter(Objects::nonNull).toList();
    }

    public List<InstrumentShort> findInstrument(String search){
        return investApi.getInstrumentsService().findInstrumentSync(search);
    }

    public List<InstrumentShort> findInstrument(String search, String instrumentType) {
        return findInstrument(search).stream()
                .filter(item -> item.getInstrumentType().equals(instrumentType))
                .toList();
    }
}
