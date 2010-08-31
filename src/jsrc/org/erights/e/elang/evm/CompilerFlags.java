package org.erights.e.elang.evm;

/*
Copyright University of Southampton IT Innovation Centre, 2010,
under the terms of the MIT X license, available from
http://www.opensource.org/licenses/mit-license.html
*/

/**
 * Compiler options to be passed to EExpr.compile.
 */
public final class CompilerFlags {
    public CompilerFlags() {
    }

    /** Perform basic compile-time optimisations. */
    public boolean basicOptimisations = false;

    /** Warn about likely mistakes.
     * E provides well-defined behaviour for all code. For example,
     * <tt>Ref.problem("foo")</tt> throws NoSuchMethodException.
     * However, this is probably not what the author intended.
     * This flag causes some such patterns to be reported as
     * warnings at compile time.
     * <p>
     * Turning on more optimisation will allow more errors
     * to be detected.
     */
    public boolean warnings = false;
}
