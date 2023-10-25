class NfcUsagePort:

    def readNfc(self):
        raise NotImplementedError

    def sendNfc(self, text: str):
        raise NotImplementedError