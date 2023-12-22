package pab.ta.signal.analysis;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.Rule;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandWidthIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.rules.IsRisingRule;
import ru.tinkoff.piapi.contract.v1.Brand;
import ru.tinkoff.piapi.contract.v1.Share;
import ru.tinkoff.piapi.core.InvestApi;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ApplicationContext context = new AnnotationConfigApplicationContext("pab.ta.signal");
        InvestApi api = context.getBean(InvestApi.class);

        Map<String, List<Share>> result = api.getInstrumentsService().getAllShares().get().stream()
                .filter(share -> share.getCountryOfRisk().equals("RU"))
                .filter(share -> share.getExchange().equals("MOEX"))
                .collect(Collectors.groupingBy(Share::getSector));

        result.forEach((key, value) -> {
            System.out.println(key);
            System.out.println(value.stream().map(Share::getName).collect(Collectors.toList()));
            System.out.println(value.size());
        });

//        api.getInstrumentsService().getBrands().get().forEach(System.out::println);

//        AssetInfo info = context.getBean(AssetInfo.class);
//        info.setFrom(LocalDateTime.now().minusMonths(12));
//
//        BarSeries series = info.series();
//
//        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
//        Indicator<Num> rsi = new RSIIndicator( closePrice,14);
//
//        for (int i = series.getEndIndex() - 5; i <= series.getEndIndex(); i++) {
//            System.out.println(series.getBar(i).getBeginTime());
//            System.out.println("close=" + closePrice.getValue(i) + "; rsi=" + rsi.getValue(i));
//        }
//
//        Rule risingRule = new IsRisingRule(rsi, 2);
//        System.out.println(risingRule.isSatisfied(series.getEndIndex() - 3, null));



    }
}