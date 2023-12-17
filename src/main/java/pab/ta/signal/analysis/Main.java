package pab.ta.signal.analysis;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandWidthIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ApplicationContext context = new AnnotationConfigApplicationContext("pab.ta.signal");

        AssetInfo info = context.getBean(AssetInfo.class);
        info.setFrom(LocalDateTime.now().minusMonths(12));

        BarSeries series = info.series();

        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);

        Indicator<Num> rsi = new RSIIndicator( closePrice,14);

        for (int i = series.getBeginIndex(); i <= series.getEndIndex(); i++) {
            System.out.println(series.getBar(i).getBeginTime());
            System.out.println("close=" + closePrice.getValue(i) + "; rsi=" + rsi.getValue(i));
        }
    }
}