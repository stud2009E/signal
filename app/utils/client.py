import os
from tinkoff.invest import Client as TClient

tinkoff_token = os.environ["tinkoff_token"]


class Client(TClient):
    def __init__(self, **kwarg):
        super(Client, self).__init__(tinkoff_token, **kwarg)