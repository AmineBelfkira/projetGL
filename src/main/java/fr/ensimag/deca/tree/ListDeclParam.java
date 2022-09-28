package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.ParamDefinition;
import java.util.Iterator;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * List de declaration de parametres
 * @author gl19
 * @date 17/01/2022
 */
public class ListDeclParam extends TreeList<AbstractDeclParam>{
    @Override
    public void decompile(IndentPrintStream s) {
        if (getList().isEmpty()){
            return;
        }
        Iterator<AbstractDeclParam> iter = iterator();
        AbstractDeclParam p = iter.next();
        p.decompile(s);
        while(iter.hasNext()) {
            s.print(" ,");
            p = iter.next();
            p.decompile(s);
        }
    }
    public Signature verifyClassMembers(DecacCompiler compiler) throws ContextualError {
        Signature sig = new Signature();
        for (AbstractDeclParam p: getList()) {
            sig.add(p.verifyClassMembers(compiler));
        }
        return sig;
    }
    public EnvironmentExp verifyClassBody(DecacCompiler compiler) throws ContextualError {
        ParamDefinition.resetParamIndex();
        EnvironmentExp envExp = new EnvironmentExp(null);
        for (AbstractDeclParam p: getList()) {
            p.verifyClassBody(compiler,envExp);
        }
        return envExp;
    }
}
