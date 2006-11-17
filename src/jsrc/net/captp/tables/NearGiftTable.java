package net.captp.tables;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elib.tables.FlexMap;
import org.erights.e.elib.vat.WeakPtr;

import java.math.BigInteger;

/**
 * Rendezvous point for 3-vat live Granovetter introductions of Resolved remote
 * references (FarRefs).
 * <p/>
 * Where we normally speak of Alice giving Bob a reference to Carol, we here
 * speak of the Donor (VatA) giving to the Recipient (VatB) a reference to the
 * Gift (Carol) residing on the Host (VatC). There is one NearGiftTable in the
 * Host per Donor. The Donor associates the gift with a Nonce, the Recipient's
 * ID, and the SwissHash of the gift. She then tells the Nonce and swissHash to
 * the Recipient. The Recipient asks the Host "What gift has Donor (identified
 * by DonorID) deposited for me at this Nonce and swissHash?".
 * <p/>
 * A Nonce is a use-once unique number. It only needs to be "impossible" to
 * collide under benevolent assumptions; it doesn't need to be unguessable, so
 * we a 64 bit number.
 * <p/>
 * When the donor registers the gift, the swissHash should be verified (by the
 * NearGiftTable's immediate client, the NonceLocator) to correspond to the
 * gift, which should be Near. The key under which the gift is stored is the
 * triple [recipient ID, nonce, swissHash]. The corresponding loopup must match
 * on all three, and must be satisfied immediately. To <a href=
 * "http://www.erights.org/elib/distrib/captp/WormholeOp.html">Allow services
 * with Near arguments</a> the recipient's Far reference must be made to
 * correspond to a local Near reference, or it must become a DisconnectedRef.
 * In order to guarantee that the loopup can be satisfied immediately, we must
 * ensure that the corresponding registration arrives first. For this, we
 * expect to use the unimplemented Wormhole technique, documented at the above
 * URL. Until then, the first case must be avoided. This avoidance results in
 * the <a href= "http://www.erights.org/elib/equality/same-ref.html#lost-resolution">Lost
 * Resolution</a> bug.
 * <p/>
 * An interesting complexity: A partition could prevent the operation from
 * completing, in which case the Recipient cannot be left hanging, and the
 * association must be cleaned up. <p>
 *
 * @author Mark S. Miller.
 */
public class NearGiftTable {

    /**
     * Maps from [recipID, Nonce, swissHash] to gift
     */
    private FlexMap myStuff;

    /**
     * @param nonceLocatorPromise A RemotePromise for the Donor's NonceLocator.
     *                            The ignore(vine) message is sent to it, so
     *                            that it can ignore it.
     */
    public NearGiftTable() {
        myStuff = FlexMap.fromTypes(Object[].class, Object.class);
    }

    /**
     * Disable this table.
     */
    public void smash(Throwable problem) {
        myStuff = null;
    }

    /**
     * Make the gift available to the recipient so long as the Vine is held
     * onto (isn't garbage).
     */
    public Vine provideFor(Object gift,
                           String recipID,
                           long nonce,
                           BigInteger swissHash) {
        Object[] keyTriple = {recipID, BigInteger.valueOf(nonce), swissHash};
        Vine result = new Vine(null);
        myStuff.put(keyTriple, gift);
        //XXX do we need to hold onto the weakPtr in order for it to be
        //finalized?  If so, then we must.
        WeakPtr weakPtr = new WeakPtr(result, this, "drop", keyTriple);
        return result;
    }

    /**
     * Lookup the gift.
     * <p/>
     * Note that this message is named "acceptFor" whereas the corresponding
     * NonceLocator message is named "acceptFrom". In acceptFor, the recipient
     * is explicit and the donor implicit. In acceptFrom, the donor is explicit
     * and the recipient implicit.
     *
     * @param recipID The vatID of the vat (Bob, the gift recipient) that the
     *                gift is being picked up for.
     * @param nonce   Identifies (together with the recipID) the gift.
     */
    public Object acceptFor(String recipID, long nonce, BigInteger swissHash) {
        Object[] keyTriple = {recipID, BigInteger.valueOf(nonce), swissHash};
        //if absent, get/1 will throw
        Object result = myStuff.get(keyTriple);
        myStuff.removeKey(keyTriple);
        return result;
    }

    /**
     * Automagically called when the vine is dropped. <p>
     * <p/>
     * The Donor is able to call this as well, which isn't appropriate, but
     * isn't dangerous, so what the hell.
     */
    public void drop(String recipID, long nonce, BigInteger swissHash) {
        Object[] keyTriple = {recipID, BigInteger.valueOf(nonce), swissHash};
        myStuff.removeKey(keyTriple);
    }
}
