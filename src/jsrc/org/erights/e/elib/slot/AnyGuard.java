package org.erights.e.elib.slot;

/*
The contents of this file are subject to the Electric Communities E Open
Source Code License Version 1.0 (the "License"); you may not use this file
except in compliance with the License. You may obtain a copy of the License
at http://www.communities.com/EL/.

Software distributed under the License is distributed on an "AS IS" basis,
WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
the specific language governing rights and limitations under the License.

The Original Code is the Distributed E Language Implementation, released
July 20, 1998.

The Initial Developer of the Original Code is Electric Communities.
Copyright (C) 1998 Electric Communities. All Rights Reserved.

Contributor(s): ______________________________________.
*/

import org.erights.e.elib.base.ClassDesc;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.prim.JMatcher;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.util.OneArgFunc;

import java.io.IOException;

/**
 * The "any" guard behavior -- don't worry, be happy.
 * <p/>
 * As a Guard, accepts everything without any coercion (the identity coercion).
 * The object named "any" is the one AnyGuard.
 *
 * @author Mark S. Miller
 */
public final class AnyGuard implements Guard, JMatcher {

    /**
     * The one instance
     */
    static public final AnyGuard THE_ONE = new AnyGuard();

    private AnyGuard() {
    }

    /**
     * Any specimen is acceptable as is, so return the specimen unchanged.
     */
    public Object coerce(Object specimen, OneArgFunc optEjector) {
        return specimen;
    }

    /**
     * @throws NoSuchMethodException if the verb isn't "get"
     */
    public Object match(String verb, ConstList args)
      throws NoSuchMethodException {

        if ("get".equals(verb)) {
            return new UnionGuard(args);
        }
        if ("__respondsTo".equals(verb) && 2 == args.size()) {
            //XXX should say yes if args[0] =~ `get`
            return Boolean.FALSE;
        }
        if ("__getAllegedType".equals(verb) && 0 == args.size()) {
            //XXX kludge
            return E.call(null, "__getAllegedType");
        }
        throw new NoSuchMethodException(verb + "/" + args.size());
    }

    /**
     * Exists for consistency with match__of_1.
     */
    public UnionGuard of(ConstList guards) {
        return new UnionGuard(guards);
    }

    /**
     * Matches a union (any[x, y, ...]) guard.
     * <p/>
     * The call-pattern must use the form any.of([x, y, ...]).
     */
    public Object match__of_1(Object specimen, OneArgFunc optEjector) {
        ClassDesc kind = ClassDesc.make(UnionGuard.class);
        UnionGuard ofKind = (UnionGuard)kind.coerce(specimen, optEjector);
        Object[] result = {ConstList.fromArray(ofKind.getChoices())};
        return ConstList.fromArray(result);
    }

    /**
     *
     */
    public void __printOn(TextWriter out) throws IOException {
        out.print("any");
    }
}
