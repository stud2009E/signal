import os

tinkoff_token = os.environ["tinkoff_token"]
telegram_token = os.environ["telegram_token"]

from .schedule import Schedule

__all__ = (
    "tinkoff_token",
    "telegram_token",
    "Schedule"
)