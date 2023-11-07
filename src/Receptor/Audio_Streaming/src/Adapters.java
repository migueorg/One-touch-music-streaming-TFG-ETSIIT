import java.net.DatagramPacket;
import java.net.DatagramSocket;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class Adapters implements Ports {

    static AudioFormat formato;

    @Override
    public void iniciaServidorAudio(int puertoEscucha, int sampleRate)  throws Exception{
        
        DatagramSocket serverSocket = new DatagramSocket(puertoEscucha);
        byte[] receiveData = new byte[8192];
        formato = new AudioFormat(sampleRate, 16, 1, true, false);
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
    
    }
    
    @Override
    public void recibeAudio(DatagramSocket serverSocket, DatagramPacket receivePacket) throws Exception{
        new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void reproduceAudio(byte[] audio) {
        try {

            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, formato);
            SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);

            sourceDataLine.open(formato);
            sourceDataLine.start();

            sourceDataLine.write(audio, 0, audio.length);

            sourceDataLine.drain();
            sourceDataLine.close();

        } catch (Exception e) {
            System.out.println("Error al intentar reproducir");
            e.printStackTrace();
        }
    }
}