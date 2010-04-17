package org.erights.e.elang.evm;

/*
Copyright University of Southampton IT Innovation Centre, 2010,
under the terms of the MIT X license, available from
http://www.opensource.org/licenses/mit-license.html
*/

import org.erights.e.elang.scope.EvalContext;
import org.erights.e.elang.scope.ScopeLayout;
import org.erights.e.elang.syntax.ELexer;
import org.erights.e.elang.visitors.ETreeVisitor;
import org.erights.e.elib.base.Ejection;
import org.erights.e.elib.base.SourceSpan;
import org.erights.e.elib.debug.EStackItem;
import org.erights.e.elib.oldeio.TextWriter;
import org.erights.e.elib.prim.Selector;
import org.erights.e.elib.prim.StaticMaker;
import org.erights.e.elib.prim.Thrower;
import org.erights.e.elib.ref.Ref;
import org.erights.e.elib.tables.ConstList;
import org.erights.e.elib.tables.FlexList;
import org.erights.e.elib.util.OneArgFunc;
import org.erights.e.elib.vat.Runner;
import org.erights.e.elib.base.Script;

import java.io.IOException;

public class FastCallExpr extends CallExpr {
    private final Object myReceiver;
    private final Script myScript;

    public FastCallExpr(SourceSpan optSpan,
                        EExpr recipient,
                        String verb,
                        EExpr[] args,
                        Object receiver,
                        Script script,
                        ScopeLayout optScopeLayout) {
        super(optSpan, recipient, verb, args, optScopeLayout);
        myReceiver = receiver;
        myScript = script;
    }

    protected Object subEval(EvalContext ctx, boolean forValue) {
        Object[] argVals = new Object[myArgs.length];
        for (int i = 0, max = argVals.length; i < max; i++) {
            argVals[i] = myArgs[i].subEval(ctx, true);
        }

        //Runner.pushEStackItem(this);
        try {
            return myScript.execute(myReceiver, myVerb, argVals);
        } catch (Ejection ej) {
            throw ej;
        } catch (Throwable problem) {
            SourceSpan optSpan = getOptSpan();
            String msg = "@ " + myVerb + "/" + myArgs.length;
            if (null != optSpan) {
                msg += ": " + optSpan;
            }
            throw Ejection.backtrace(problem, msg);
        } finally {
            //Runner.popEStackItem();
        }
    }

    public void subPrintOn(TextWriter out, int priority) throws IOException {
        if (PR_CALL < priority) {
            out.print("(");
        }
        out.print("[" + myReceiver + "." + myScript);
        printListOn("](", myArgs, ", ", ")", out, PR_EEXPR);
        if (PR_CALL < priority) {
            out.print(")");
        }
    }
}
