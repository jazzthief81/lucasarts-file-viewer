package lucasarts.format.gamefile;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Lars on 29-03-2017.
 */
public class EncryptingOutputStream extends OutputStream {

    private OutputStream delegate;
    private int encryptionKey = 0x69;
    private int position;

    public EncryptingOutputStream(OutputStream aDelegate) {
        delegate = aDelegate;
    }

    public EncryptingOutputStream(OutputStream aDelegate, int aEncryptionKey) {
        delegate = aDelegate;
        encryptionKey = aEncryptionKey;
    }

    public int getPosition() {
        return position;
    }

    @Override
    public void write(int b) throws IOException {
        position++;
        delegate.write(b ^ encryptionKey);
    }

    @Override
    public void write(byte[] b) throws IOException {
        super.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        super.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        delegate.flush();
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }

}
