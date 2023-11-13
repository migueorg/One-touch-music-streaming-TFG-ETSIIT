import java.net.DatagramPacket;
import java.net.DatagramSocket;

public interface Ports {

    void iniciaServidorAudio(int puertoEscucha, int sampleRate) throws Exception;
    void reproduceAudio(byte audio[]);
    void recibeAudio(DatagramSocket serverSocket, DatagramPacket receivePacket) throws Exception;
}
