import os
from adapters import NfcUsageAdapter

def obtiene_ip():
    return os.popen('ip addr show eth0').read().split("inet ")[1].split("/")[0]


def main():

    print("Obteniendo IP...")
    ip = obtiene_ip()
    print("La IP es:" + ip)

    print("Emulando tarjeta NFC...")
    adaptador = NfcUsageAdapter()
    adaptador.sendNfc(ip)


if __name__ == "__main__": main()