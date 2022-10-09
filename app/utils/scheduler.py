from tinkoff.invest import Client
from datetime import datetime, timezone
from utils import tinkoff_token


class Schedule:
    def __init__(self, exchange:str):
        self.exchange = exchange
        self.start = datetime.now(tz=timezone.utc)
        self._schedule = self._get_today_schedule()


    def _with_client(self, *, from_, to):
        schedule = None
        with Client(tinkoff_token) as client:
            schedule = client.instruments.trading_schedules(exchange=self.exchange, from_ = from_, to = to)

        return schedule


    def get_data(self):
        now = datetime.now(tz=timezone.utc)

        today = datetime(year=now.year, month=now.month, day=now.day, tzinfo=timezone.utc)
        start = datetime(year=self.start.year, month=self.start.month, day=self.start.day, tzinfo=timezone.utc)

        if today > start:
            self.start = now
            self._schedule = self._get_today_schedule()
        
        return self._schedule


    def _get_today_schedule(self):
        day = None
        
        tradingSchedulesResponse = self._with_client(from_=self.start, to=self.start)
        tradingSchedule = tradingSchedulesResponse.exchanges[0]

        if not tradingSchedule is None: 
            day = tradingSchedule.days[0]
        
        return day