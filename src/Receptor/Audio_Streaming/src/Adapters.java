import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class Adapters implements Ports {

    static AudioFormat formato;

    @Override
    public byte[] iniciaServidorAudio() {
        new UnsupportedOperationException("Not implemented yet");
        return new byte[0];
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