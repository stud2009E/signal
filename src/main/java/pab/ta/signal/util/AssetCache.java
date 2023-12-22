package pab.ta.signal.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.tinkoff.piapi.contract.v1.Asset;
import ru.tinkoff.piapi.contract.v1.AssetFull;
import ru.tinkoff.piapi.core.InvestApi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

@Component
public class AssetCache {

    private final Map<String, String> instrumentAssetMap = new HashMap<>();
    private InvestApi investApi;
    @Autowired
    public void setInvestApi(InvestApi investApi) {
        this.investApi = investApi;
    }


     public synchronized String getAssetFullUid(String instrumentUid) throws ExecutionException, InterruptedException {

        if (instrumentAssetMap.isEmpty()){
            List<Asset> assets = investApi.getInstrumentsService().getAssets().get();

            assets.forEach(asset -> {
                asset.getInstrumentsList().forEach(instrument -> {
                    instrumentAssetMap.put(instrument.getUid(), asset.getUid());
                });
            });
        }

        return instrumentAssetMap.get(instrumentUid);
    }
}
