from tinkoff.invest import Client
from datetime import datetime, timedelta
from utils import tinkoff_token


class Schedule:
    def __init__(self, exchange:str):
        self.exchange = exchange
        self.start = datetime.now()
        self._schedule = self._get_today_schedule(self.start)


    def _with_client(self, *, from_, to):
        schedule = None
        with Client(tinkoff_token) as client:
            schedule = client.instruments.trading_schedules(exchange=self.exchange, from_ = from_, to = to)

        return schedule


    def get_today(self):
        now = datetime.now()

        if (now - self.start) > timedelta(days=1):
            self.start = now
            self._schedule = self._get_today_schedule(now)
        
        return self._schedule


    def _get_today_schedule(self, today):
        day = None
        
        tradingSchedulesResponse = self._with_client(from_=today, to=today)
        tradingSchedule = tradingSchedulesResponse.exchanges[0]

        if not tradingSchedule is None: 
            day = tradingSchedule.days[0]
        
        return day