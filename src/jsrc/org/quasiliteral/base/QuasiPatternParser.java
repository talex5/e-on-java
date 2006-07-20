package org.quasiliteral.base;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import org.erights.e.elib.tables.Twine;

/**
 * Given a template string in the language this quasi parser understands,
 * parse it into a MatchMaker which will match objects of the form the
 * template describes. The template language is a value description language
 * augmented with $-holes and @-holes, representing values to be provided at
 * runtime to the MatchMaker, or values for it to extract from the specimen,
 * respectively.
 *
 * @author Mark S. Miller
 */
public interface QuasiPatternParser {

    /**
     * For the i'th $-hole, dlrHoles[i] is the position of that hole in
     * template, and the character at that position in template must be '$'.
     * <p/>
     * '$' characters that don't correspond to positions in dlrHoles are
     * treated as part of the parser's normal language rather than indicating
     * $-holes. Likewise for embedded '@' characters and positions in
     * atHoles.
     */
    MatchMaker matchMaker(Twine template, int[] dlrHoles, int[] atHoles);

    /**
     * In this old format, each $-hole is represented by a substring like
     * '${3}' for $-hole number 3 (the fourth hole).
     * <p/>
     * Likewise for embedded '@' characters and positions in atHoles. '$' and
     * '@' characters that are not holes must be doubled.
     *
     * @deprecated
     */
    MatchMaker matchMaker(Twine template);
}
