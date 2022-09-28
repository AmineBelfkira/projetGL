package fr.ensimag.deca.tree;

import java.util.Iterator;

import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * List of expressions (eg list of parameters).
 *
 * @author gl19
 * @date 01/01/2022
 */
public class ListExpr extends TreeList<AbstractExpr> {

    @Override
    public void decompile(IndentPrintStream s) {
        if (isEmpty()){
            return;
        }
        Iterator<AbstractExpr> iter = iterator();
        AbstractExpr i = iter.next();
        i.decompile(s);
        while (iter.hasNext()) {
            s.print(" ,");
            i = iter.next();
            i.decompile(s);
        }
    }
}
