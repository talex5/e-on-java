package org.erights.e.elib.serial;

/** An object that might be able to do some of its work at compile time. */
public interface CompiletimeCallable {
    /** Return the result of E.callAll(object, verb, args), if the result can be
     * evaluated once at compile-time.
     * Return null if not.
     */
    Object optCompileCall(String verb, Object[] args);
}
