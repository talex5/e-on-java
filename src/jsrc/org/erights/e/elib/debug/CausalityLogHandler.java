package org.erights.e.elib.debug;

/*
Copyright University of Southampton IT Innovation Centre, 2010,
under the terms of the MIT X license, available from
http://www.opensource.org/licenses/mit-license.html
*/

import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;

import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.Handler;

public final class CausalityLogHandler extends Handler {
    private TextWriter myStream;

    public CausalityLogHandler(TextWriter stream) {
        myStream = stream;
        try {
            myStream.print("[\n");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void close() {
        try {
            myStream.close();
            myStream = null;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void flush() {
        try {
            myStream.flush();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void publish(LogRecord record) {
        if (record instanceof CausalityLogRecord) {
            synchronized (myStream) {
                try {
                    myStream.print(((CausalityLogRecord) record).asJSON());
                    myStream.println(",");
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }
}
