from tinkoff.invest import Client
from datetime import datetime, timedelta
from utils import tinkoff_token

class Schedule:
    def __init__(self, exchange:str):
        self.exchange = exchange

    def _with_client(self, *, from_, to):
        schedule = None
        with Client(tinkoff_token) as client:
            schedule = client.instruments.trading_schedules(exchange=self.exchange, from_ = from_, to = to)

        return schedule


    def get_today_schedule(self):
        now = datetime.now()
        tradingSchedulesResponse = self._with_client(from_=now, to=now)
        return self._get_days(tradingSchedulesResponse)


    def get_week_schedule(self):
        now = datetime.now()
        start = now - timedelta(days=datetime.weekday())
        end = start + timedelta(days=6)

        tradingSchedulesResponse = self._with_client(from_=start, to=end)
        return self._get_days(tradingSchedulesResponse)


    def get_next_week_schedule(self):
        now = datetime.now() + timedelta(days=7)
        start = now - timedelta(days=datetime.weekday())
        end = start + timedelta(days=6)

        tradingSchedulesResponse = self._with_client(from_=start, to=end)
        return self._get_days(tradingSchedulesResponse)


    def _get_days(self, tradingSchedulesResponse):
        tradingSchedule = tradingSchedulesResponse.exchanges[0]
        days = []
        if tradingSchedule is not None: 
            days = tradingSchedule.days
        
        return days