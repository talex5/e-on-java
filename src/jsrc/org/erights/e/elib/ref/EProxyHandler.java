package org.erights.e.elib.ref;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elib.sealing.Brand;
import org.erights.e.elib.sealing.SealedBox;

/**
 * A deflected interface used to implement unprivileged comm systems.
 *
 * @author Mark S. Miller
 */
public interface EProxyHandler {

    /**
     * Normally just returns self.
     * <p/>
     * A handler wrapper generated by a {@link ReferenceMonitor} should
     * normally forward this to the wrapped handler (the underlying), so that
     * this message provides direct access to the underlying.
     * <p/>
     * Unlike the normal
     * <a href="http://www.erights.org/talks/asian03/">Caretaker</a> situation,
     * here it is safe for the underlying to provide such self revelation,
     * since the untrusted clients are not those holding the wrapper, but those
     * holding the {@link EProxy}. Starting from an EProxy, you can only obtain
     * access to a handler by
     * {@link #handleOptSealedDispatch(org.erights.e.elib.sealing.Brand)
     * rights amplification}, which you can only do if you have the needed
     * {@link org.erights.e.elib.sealing.Unsealer Unsealer}. (And rights
     * amplification normally gives direct access to the underlying anyway).
     */
    EProxyHandler unwrap();

    /**
     * How should my ref respond to an __optSealedDispatch request?
     */
    SealedBox handleOptSealedDispatch(Brand brand);

    /**
     * My Ref is asking me to deliver the message to the other side and
     * ignore the result.
     * <p/>
     * If the underlying transport mechanism is shut down or otherwise not
     * accepting messages, then it should throw a problem explaining why this
     * message wasn't queued.
     */
    void handleSendAllOnly(String verb, Object[] args);

    /**
     * My Ref is asking me to deliver the message to the other side, and
     * return a promise for the result.
     * <p/>
     * If the underlying transport mechanism is shut down or otherwise not
     * accepting messages, then it should either throw a problem explaining
     * why this message wasn't queued, or return a reference that's
     * <i>immediately</i> broken by this problem.
     */
    Ref handleSendAll(String verb, Object[] args);

    /**
     * My Ref no longer needs me, as it's become equivalent to newTarget.
     */
    void handleResolution(Object newTarget);

    /**
     * One of my Refs was GCed, and I have no current Ref.
     */
    void reactToGC();

    /**
     * A reference is fresh if it's redirection doesn't need to be delayed.
     * <p/>
     * A resolved reference doesn't get redirected (except to be smashed), and
     * so is always fresh. A remote promise is fresh if it hasn't been used.
     * A remote promise over which messages have already been sent is not
     * fresh. (Hypothetically, if we know that none of these remain in the air
     * -- that all of them have arrived -- then we could consider the remote
     * promise to be fresh again. This optimization is not currently
     * implemented.)
     */
    boolean isFresh();

    /**
     * Are 'other' and the remote reference handled by 'this' part of the
     * same comm system, and do they both connect to the same remote vat?
     */
    boolean sameConnection(Object other);

    /**
     * Complain if not disposable
     */
    void mustBeDisposable();
}
