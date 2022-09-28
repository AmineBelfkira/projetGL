package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * List de declaration de method
 * @author gl19
 * @date 17/01/2022
 */
public class ListDeclMethod extends TreeList<AbstractDeclMethod>{

    public static int nbMaxPara = 0;
    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclMethod m : getList()) {
            m.decompile(s);
            s.println();
        }
    }
    public EnvironmentExp verifyClassMembers(DecacCompiler compiler, ClassDefinition superCls)
            throws ContextualError {
        EnvironmentExp envExp = new EnvironmentExp(null);
        DeclMethod.COUNT=0;
        for (AbstractDeclMethod m : getList()) {
            m.verifyClassMembers(compiler,superCls,envExp);
        }
        return envExp;
    }
    public void verifyClassBody(DecacCompiler compiler,EnvironmentExp localEnv ,ClassDefinition cls)
            throws ContextualError {
        for (AbstractDeclMethod m : getList()) {
            m.verifyClassBody(compiler,localEnv,cls);
            
            int tmp = ((DeclMethod)m).getNbParam();
            if( tmp > nbMaxPara )
                nbMaxPara = tmp;
        }
    }
}
