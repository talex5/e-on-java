package org.erights.e.elang.scope;

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

import org.erights.e.develop.assertion.T;
import org.erights.e.develop.exception.ExceptionMgr;
import org.erights.e.develop.format.StringHelper;
import org.erights.e.elang.evm.FinalPattern;
import org.erights.e.elang.evm.LiteralSlotNounExpr;
import org.erights.e.elang.evm.NounExpr;
import org.erights.e.elang.evm.NounPattern;
import org.erights.e.elang.evm.OuterNounExpr;
import org.erights.e.elang.evm.SlotPattern;
import org.erights.e.elang.interp.LazyEvalSlot;
import org.erights.e.elang.interp.ScopeSetup;
import org.erights.e.elib.base.ClassDesc;
import org.erights.e.elib.base.Thunk;
import org.erights.e.elib.base.ValueThunk;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.oldeio.UnQuote;
import org.erights.e.elib.prim.E;
import org.erights.e.elib.slot.FinalSlot;
import org.erights.e.elib.slot.Guard;
import org.erights.e.elib.slot.MapGuard;
import org.erights.e.elib.slot.Slot;
import org.erights.e.elib.tables.AssocFunc;
import org.erights.e.elib.tables.ConstMap;
import org.erights.e.elib.tables.EIteratable;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.tables.FlexMap;

import java.util.HashSet;
import java.io.IOException;
import java.io.StringWriter;

/**
 * A ConstMap (sort of) from names (strings) to {@link Slot}s.
 * <p/>
 * Scopes inherit from each other in a tree, so they can be used to model
 * nesting lexical environments. The associations in the most leafward part of
 * an EEnv are called "locals". <p>
 *
 * The safeScope object given to emakers is an EEnv. The actual Scope object
 * is now internal to the E interpreter and created from the EEnv.
 *
 * @author E. Dean Tribble
 * @author Mark S. Miller
 */
public class EEnv implements EIteratable {

    static private final Guard SlotGuard = ClassDesc.make(Slot.class);
    static private final Guard StringGuard = ClassDesc.make(String.class);

    private final ConstMap myOuters;
    private final String myFqnPrefix;

    /** Constructor.
      * @param outers a map from names (without a leading &amp;) to Slots
      */
    public EEnv(ConstMap outers, String fqnPrefix) {
        MapGuard outersGuard = MapGuard.THE_BASE.get(StringGuard, SlotGuard);

        this.myOuters = (ConstMap) outersGuard.coerce(outers, null);
        this.myFqnPrefix = fqnPrefix;
    }

    /**
     * In slotted form, each association is
     * <pre>    "&amp;"varName =&gt; Slot</pre>
     * For those associations in state of the form
     * <pre>       varName =&gt; value</pre>
     * asSlottedState(state) will convert them by wrapping the value in a
     * FinalSlot.
     */
    static public ConstMap asSlottedState(ConstMap state) {
        final FlexMap slottedMap =
          FlexMap.fromTypes(String.class, Slot.class, state.size());
        state.iterate(new AssocFunc() {
            public void run(Object key, Object value) {
                String k = (String)key;
                if (k.startsWith("&")) {
                    Slot slot = (Slot)E.as(value, Slot.class);
                    slottedMap.put(k, slot, true);
                } else {
                    slottedMap.put("&" + k, new FinalSlot(value));
                }
            }
        });
        return slottedMap.snapshot();
    }

    /**
     * In mixed form, each association is either
     * <pre>    "&amp;"varName =&gt; Slot</pre> or
     * <pre>       varName =&gt; value</pre>
     * asMixedState(state) always prefers the second form when the slot {@link
     * Slot#isFinal isFinal}.
     */
    static public ConstMap asMixedState(ConstMap state) {
        final FlexMap mixedMap =
          FlexMap.fromTypes(String.class, Object.class, state.size());
        state.iterate(new AssocFunc() {
            public void run(Object key, Object value) {
                String k = (String)key;
                if (k.startsWith("&")) {
                    Slot slot = (Slot)E.as(value, Slot.class);
                    if (slot.isFinal()) {
                        mixedMap.put(k.substring(1), slot.get(), true);
                    } else {
                        mixedMap.put(k, slot, true);
                    }
                } else {
                    mixedMap.put(k, value, true);
                }
            }
        });
        return mixedMap.snapshot();
    }

    /**
     * Like the constructor, except that only names starting with &amp; are slots.
     * The rest get wrapped in a FinalSlot.
     */
    static public EEnv fromState(ConstMap state, String fqnPrefix) {
        //implementation should be a specialized form of ScopeMaker
        int len = state.size();

        final FlexMap outers = FlexMap.fromTypes(String.class, Slot.class);
        state.iterate(new AssocFunc() {
            public void run(Object key, Object value) {
                String name = (String)key;
                String varName;
                Slot slot;
                if (name.startsWith("&")) {
                    outers.put(name.substring(1), value);
                } else {
                    outers.put(name, new FinalSlot(value));
                }
            }
        });
        return new EEnv(outers.snapshot(), fqnPrefix);
    }

    public Scope asScope() {
        return new Scope(new ScopeLayoutEnv(this), EvalContext.make(0, new Slot[] {}));
    }

    /**
     *
     */
    public String getFQNPrefix() {
        return myFqnPrefix;
    }

    /**
     *
     */
    public boolean maps(String varName) {
        return myOuters.maps(varName);
    }

    /**
     *
     */
    public Slot getSlot(String varName) {
        return (Slot) myOuters.get(varName);
    }

    /**
     * Gets the value of the slot associated with varName.
     * <p/>
     * Just a convenience implemented out of getSlot/1
     */
    public Object get(String varName) {
        return getSlot(varName).get();
    }

    /**
     * Like {@link #get(String) get/1}, except that if varName isn't found,
     * return insteadThunk() instead.
     */
    public Object fetch(String varName, Thunk insteadThunk) {
        Slot slot = (Slot) myOuters.fetch(varName, ValueThunk.NULL_THUNK);
        if (slot == null) {
            return insteadThunk.run();
        } else {
            return slot.get();
        }
    }

    /**
     * The default put/2 is defined in the obvious fashion in terms of
     * getSlot(varName).put(newValue). <p>
     */
    public void put(String varName, Object newValue) {
        getSlot(varName).put(newValue);
    }

    /**
     * A new EEnv object just like this one, but with the given prefix.
     */
    public EEnv withPrefix(String fqnPrefix) {
        return new EEnv(myOuters, fqnPrefix);
    }

    /**
     * Enumerates <tt>slotName =&gt; Slot</tt> associations, where a
     * <tt>slotName</tt> is <tt>"&amp;"varName</tt>
     * XXX: with the &amp; bits?
     */
    public void iterate(final AssocFunc func) {
        //noinspection ParameterNameDiffersFromOverriddenParameter
        myOuters.iterate(new AssocFunc() {
            public void run(Object name, Object value) {
                String varName = (String)name;
                Slot slot = (Slot) value;
                func.run("&" + varName, slot);
            }
        });
    }

    /**
     * XXX: with the &amp; bits?
     */
    public ConstMap getState() {
        return ConstMap.fromIteratable(this, true);
    }

    /**
     * universalFlag defaults to true
     */
    public UnQuote bindings() throws IOException {
        return bindings(true);
    }

    /**
     * Returns a string showing the bindings in this scope in a pleasant, human
     * readable format.
     *
     * @param showSafeFlag If set, then unshadowable names from the safe scope
     *                     will be shown as well.
     */
    public UnQuote bindings(boolean showSafeFlag) throws IOException {
        StringWriter buf = new StringWriter();
        TextWriter out = new TextWriter(buf);
        printBindingsOn(showSafeFlag, out);
        return new UnQuote(StringHelper.canonical(buf.toString()));
    }

    /**
     *
     */
    private void printBindingsOn(final boolean showSafeFlag,
                                 final TextWriter out) {
        iterate(new AssocFunc() {
            public void run(Object key, Object value) {
                try {
                    String slotName = (String)key;
                    T.test(slotName.startsWith("&"));
                    String varName = slotName.substring(1);
                    if (!showSafeFlag &&
                      ScopeSetup.NonShadowable.contains(varName)) {

                        return; // really means break()
                    }
                    Slot slot = (Slot)value;
                    // we avoid another indented stream only by assuming only
                    // one line follows
                    out.print(varName, "\n    ");
                    try {
                        if (slot instanceof LazyEvalSlot) {
                            // kludgy special case to avoid forcing the
                            // LazyEvalSlot to evaluate.
                            out.print("...");
                        } else {
                            Object optValue = get(varName);
                            if (null == optValue) {
                                out.print("null");
                            } else {
                                String sig =
                                  ClassDesc.simpleSig(optValue.getClass());
                                out.print(StringHelper.aan(sig));
                            }
                        }
                    } catch (Exception ex) {
                        out.print("*** ", ex);
                    }
                    out.println();
                } catch (IOException e) {
                    throw ExceptionMgr.asSafe(e);
                }
            }
        });
    }

    public EEnv with(String varName, Object value) {
        return withSlot("&" + varName, new FinalSlot(value));
    }

    public EEnv withSlot(String slotName, Slot slot) {
        // XXX Horribly inefficient
        T.require(slotName.startsWith("&"), "slotName must start with &");
        return new EEnv(myOuters.with(slotName.substring(1), slot), myFqnPrefix);
    }

    /** Create a new scope based on this one and extended with the given mappings.
      * For keys starting with '&amp;', the value is used directly as
      * the slot. For other keys, the value is wrapped in a FinalSlot.
      * Any existing slots with these names are replaced.
      * @throws IllegalArgumentException if a key is given twice (with and without &amp;)
      */
    public EEnv with(ConstMap envExtra) {
        final HashSet seen = new HashSet();

        final FlexMap state = myOuters.diverge();
        envExtra.iterate(new AssocFunc() {
            public void run(Object key, Object value) {
                String name = (String)key;
                String slotName;
                Slot slot;
                if (name.startsWith("&")) {
                    slotName = name.substring(1);
                    slot = (Slot)SlotGuard.coerce(value, null);
                } else {
                    slotName = name;
                    slot = new FinalSlot(value);
                }
                if (seen.contains(slotName)) {
                    throw new IllegalArgumentException("two values given for slot '" + slotName + "'");
                }
                seen.add(slotName);
                state.put(slotName, slot);
            }
        });
        return new EEnv(state.snapshot(), myFqnPrefix);
    }

    /** A map from names to Slots. */
    public ConstMap getSlots() {
        return myOuters;
    }
}