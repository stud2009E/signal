package pab.ta.signal.analysis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseBarSeriesBuilder;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;
import ru.tinkoff.piapi.contract.v1.CandleInterval;
import ru.tinkoff.piapi.contract.v1.HistoricCandle;
import ru.tinkoff.piapi.contract.v1.Quotation;
import ru.tinkoff.piapi.core.InvestApi;

import java.math.BigDecimal;
import java.time.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
public class AssetInfo {
    private InvestApi investApi;
    private final String figi;
    private LocalDateTime from = LocalDateTime.now().minusMonths(6);
    private LocalDateTime to = LocalDateTime.now();
    private CandleInterval candleInterval = CandleInterval.CANDLE_INTERVAL_DAY;
    private final BarSeries series;

    public AssetInfo(@Value("BBG004730N88") String figi) {
        this.figi = figi;
        series = new BaseBarSeriesBuilder().withNumTypeOf(DecimalNum.class).withName(figi).build();
    }

    @Autowired
    public void setInvestApi(InvestApi investApi) {
        this.investApi = investApi;
    }

    public BarSeries series() throws ExecutionException, InterruptedException {
        Instant instantFrom = from.atZone(ZoneId.systemDefault()).toInstant();
        Instant instantTo = to.atZone(ZoneId.systemDefault()).toInstant();

        CompletableFuture<List<HistoricCandle>> candles = investApi.getMarketDataService()
                .getCandles(figi, instantFrom, instantTo, candleInterval);

        candles.get().stream().map(candle -> {
            ZonedDateTime zdt = Instant.ofEpochSecond(candle.getTime().getSeconds(), candle.getTime()
                    .getNanos())
                    .atZone(ZoneId.systemDefault());

            return BaseBar.builder(DecimalNum::valueOf, Number.class)
                    .timePeriod(duration())
                    .endTime(zdt.plusDays(1))
                    .openPrice(this.getNum(candle.getOpen()))
                    .closePrice(this.getNum(candle.getClose()))
                    .lowPrice(this.getNum(candle.getLow()))
                    .highPrice(this.getNum(candle.getHigh()))
                    .volume(DecimalNum.valueOf(candle.getVolume()))
                    .build();
        }).forEach(series::addBar);

        return series;
    }

    private Num getNum(Quotation quotation) {
        BigDecimal bigDecimal = quotation.getUnits() == 0 && quotation.getNano() == 0
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(quotation.getUnits()).add(BigDecimal.valueOf(quotation.getNano(), 9));

        return DecimalNum.valueOf(bigDecimal.doubleValue());
    }

    private Duration duration() {
        return switch (candleInterval) {
            case CANDLE_INTERVAL_WEEK -> Duration.ofDays(7);
            case CANDLE_INTERVAL_4_HOUR -> Duration.ofHours(4);
            default -> Duration.ofDays(1);
        };
    }

    public void setFrom(LocalDateTime from) {
        this.from = from;
    }

    public void setTo(LocalDateTime to) {
        this.to = to;
    }

    public void setCandleInterval(CandleInterval candleInterval) {
        this.candleInterval = candleInterval;
    }

    public LocalDateTime from() {
        return from;
    }

    public LocalDateTime to() {
        return to;
    }

    public CandleInterval candleInterval() {
        return candleInterval;
    }
}
