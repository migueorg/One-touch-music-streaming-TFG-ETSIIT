from ports import NfcUsagePort
from binascii import unhexlify

class NfcUsageAdapter(NfcUsagePort):

    def sendNfc(self, text: str):
        pass

    def createNdefFile(self, ip):
        file = open("ip_actual.ndef","wb")

        if(len(ip)==11): #Cabecera para IP 192.168.1.x
            tam = '0E'
        elif(len(ip)==12): #Cabecera para IP 192.168.1.xx
            tam = '0F'
        else: #Cabecera para IP 192.168.1.xxx
            tam = '10'
        
        hex_1 = 'D101' + tam + '5402656E' #Cabecera
        hex_2 = ip.encode("utf-8").hex() #IP
        bin_s = unhexlify(hex_1+hex_2)

        file.write(bin_s)
        file.close()