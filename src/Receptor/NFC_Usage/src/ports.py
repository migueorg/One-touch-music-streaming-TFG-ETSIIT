class NfcUsagePort:

    def sendNfc(self, text: str):
        raise NotImplementedError
    
    def createNdefFile(self, ip: str):
        raise NotImplementedError