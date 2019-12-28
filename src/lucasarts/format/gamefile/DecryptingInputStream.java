package lucasarts.format.gamefile;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Lars on 29-03-2017.
 */
public class DecryptingInputStream extends InputStream {

    private InputStream delegate;
    private int decryptionKey = 0x69;
    private int position;
    private int markedPosition;

    public DecryptingInputStream(InputStream aDelegate) {
        delegate = aDelegate;
    }

    public DecryptingInputStream(InputStream aDelegate, int aDecryptionKey) {
        delegate = aDelegate;
        decryptionKey = aDecryptionKey;
    }

    public int getPosition() {
        return position;
    }

    @Override
    public int read() throws IOException {
        int byteRead = delegate.read();
        position++;
        if (byteRead == -1) {
            return -1;
        }
        else {
            return byteRead ^ decryptionKey;
        }
    }

    @Override
    public int read(byte[] b) throws IOException {
        return super.read(b);
    }

    @Override
    public int available() throws IOException {
        return delegate.available();
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }

    @Override
    public synchronized void mark(int readlimit) {
        delegate.mark(readlimit);
        markedPosition = position;
    }

    @Override
    public synchronized void reset() throws IOException {
        delegate.reset();
        position = markedPosition;
    }

    @Override
    public boolean markSupported() {
        return delegate.markSupported();
    }

}
