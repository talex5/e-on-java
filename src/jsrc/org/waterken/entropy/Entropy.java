// Copyright 2004-2005 Waterken Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html
package org.waterken.entropy;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.UndeclaredThrowableException;
import java.security.SecureRandom;

/**
 * A random number device.
 *
 * @author Tyler
 */
public final class Entropy extends SecureRandom {

    static private final long serialVersionUID = 919938173770371790L;

    /**
     * The random number stream.
     */
    private final InputStream in;

    private Entropy(final InputStream in) {
        this.in = in;
    }

    /**
     * Constructs the best available random number generator.
     */
    static public SecureRandom make() {
        SecureRandom r;
        try {
            r = new Entropy(new FileInputStream("/dev/random"));
        } catch (final FileNotFoundException _) {
            r = new SecureRandom();
        }
        return r;
    }

    // java.security.SecureRandom interface.

    public byte[] generateSeed(final int numBytes) {
        final byte[] r = new byte[numBytes];
        nextBytes(r);
        return r;
    }

    public void nextBytes(final byte[] bytes) {
        try {
            for (int i = 0; i != bytes.length;) {
                final int n = in.read(bytes, i, bytes.length - i);
                if (-1 == n) {
                    throw new EOFException("Not enough entropy!");
                }
                i += n;
            }
        } catch (final IOException e) {
            throw new UndeclaredThrowableException(e);
        }
    }
}
