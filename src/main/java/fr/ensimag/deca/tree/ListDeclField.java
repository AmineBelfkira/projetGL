package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * List de declaration de field
 * @author gl19
 * @date 17/01/2022
 */
public class ListDeclField extends TreeList<AbstractDeclField>{
    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclField f : getList()) {
            f.decompile(s);
            s.println();
        }
    }
    public EnvironmentExp verifyClassMembers(DecacCompiler compiler, ClassDefinition superCls, ClassDefinition name) throws ContextualError {
        EnvironmentExp envExp = new EnvironmentExp(null);
        for (AbstractDeclField f : getList()) {
            f.verifyClassMembers(compiler,superCls,name,envExp);
        }
        return envExp;
    }
    public void verifyClassBody(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        for (AbstractDeclField f : getList()) {
            f.verifyClassBody(compiler,localEnv,currentClass);
        }
    }

    public void codeGenListField(DecacCompiler compiler) {
        for (AbstractDeclField field : getList()) {
            field.codeGenField(compiler);
        }
    }

}
