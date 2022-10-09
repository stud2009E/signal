import os
from tinkoff.invest import Client as TClient, InstrumentIdType

tinkoff_token = os.environ["tinkoff_token"]


class Client(TClient):
    def __init__(self, **kwarg):
        super(Client, self).__init__(tinkoff_token, **kwarg)


class Instrument:
    def shares(self):
        with Client() as client:
            shares = client.instruments.shares()
        
        return shares.instruments


    def share_by(self, figi):
        with Client() as client:
            share = client.instruments.share_by(
                id=figi,
                id_type=InstrumentIdType.INSTRUMENT_ID_TYPE_FIGI
            )
        
        return share.instrument
    

    def bonds(self):
        with Client() as client:
            bonds = client.instruments.bonds()
        
        return bonds.instruments


    def bond_by(self, figi):
        with Client() as client:
            bond = client.instruments.bond_by(
                id=figi,
                id_type=InstrumentIdType.INSTRUMENT_ID_TYPE_FIGI)
        
        return bond.instrument
    

    def currencies(self):
        with Client() as client:
            currencies = client.instruments.currencies()
        
        return currencies.instruments


    def currency_by(self, figi):
        with Client() as client:
            currency = client.instruments.currency_by(
                id=figi,
                id_type=InstrumentIdType.INSTRUMENT_ID_TYPE_FIGI)
        
        return currency.instrument