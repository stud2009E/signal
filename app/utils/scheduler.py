from .client import Client
from datetime import datetime, timezone


class Schedule:
    def __init__(self, exchange:str):
        self._exchange = exchange
        self._start = datetime.now(tz=timezone.utc)
        self._schedule = None


    def day_info(self):
        if self._schedule is None:
            self._schedule = self._get_today_schedule()
        
        return self._schedule


    def _get_today_schedule(self):
        with Client() as client:
            tradingSchedulesResponse = client.instruments.trading_schedules(
                exchange=self._exchange,
                from_=self._start,
                to=self._start
            )

        tradingSchedule = tradingSchedulesResponse.exchanges[0]

        if not tradingSchedule is None: 
            day = tradingSchedule.days[0]
        
        return day