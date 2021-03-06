package org.erights.e.elib.serial;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.slot.BaseAuditor;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.util.OneArgFunc;

import java.io.IOException;

/**
 * The guard and auditor known as "PassByCopy".
 *
 * @author Mark S. Miller
 */
public class PassByCopyAuditor extends BaseAuditor {

    static public final PassByCopyAuditor THE_ONE = new PassByCopyAuditor();

    /**
     *
     */
    private PassByCopyAuditor() {
    }

    /**
     * Coerces shortSpecimen to be PassByCopy, ie, both Selfless and
     * PassByConstruction.
     * <p/>
     * If the shortSpecimen can't be coerced, exit according to optEjector.
     */
    protected Object tryCoerceR(Object shortSpecimen, OneArgFunc optEjector) {
        if (Ref.isSelfless(shortSpecimen) && Ref.isPBC(shortSpecimen)) {
            if (shortSpecimen != null && shortSpecimen.getClass().isArray()) {
                //Because we try to pretend that arrays are PassByCopy lists,
                //we coerce it to a ConstList.
                return ConstList.fromArray(shortSpecimen);
            } else {
                return shortSpecimen;
            }
        } else {
            return super.tryCoerceR(shortSpecimen, optEjector);
        }
    }

    /**
     * @param out
     * @throws java.io.IOException
     */
    public void __printOn(TextWriter out) throws IOException {
        out.print("PassByCopy");
    }
}
