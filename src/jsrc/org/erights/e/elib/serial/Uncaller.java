// Copyright 2003 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

package org.erights.e.elib.serial;

/**
 * If <tt>obj</tt> is neither a bare string nor a scalar, and is transparent to
 * <i>this Uncaller</i>, then <tt>uncaller.optUncall(obj)</tt> returns a triple
 * corresponding to the three arguments of E.call/3:<ul> <li>The receiver
 * <li>The "verb", ie, the message name to call with <li>The list of arguments
 * </ul> Alternatively, uncall may return null to indicate that obj was not
 * uncallable by this uncall function.
 *
 * @author Mark S. Miller
 */
public interface Uncaller {

    /**
     * Returns a description of the call that would reconstruct the argument
     * object.
     * <p/>
     * The reconstructing call would proceed not with the elements of this
     * description, but with reconstructions of the elements of this
     * description. Return null to indicate that this Uncaller has no
     * description to offer, in which case another one may be tried.
     * <p/>
     * If a description triple is returned, the elements of this triple must
     * not provide any further authority beyond that held by a client who
     * already has both this <tt>Uncaller</tt> and <tt>obj</tt>. In particular,
     * if this Uncaller is an authority-diminishing facet on a more powerful
     * Uncaller, then this facet may chose to implements its optUncall/1 in
     * terms of the underlying's optUncall/1. It so, it had better be very
     * careful to re-diminish the result before returning it. See {@link
     * BaseLoader#getOptWrappingUncall} for a convenience to help write such
     * wrappers.
     *
     * @return :nullOk[Tuple[any, String, List]]; either null or a 3-element
     *         list of<ul> <li>a receiver object whose reconstruction would
     *         receive the message <li>a String, which is the name of the
     *         message to call <li>a list of arguments, whose reconstruction
     *         are the actual arguments to the call. </ul>
     */
    Object[] optUncall(Object obj);
}
