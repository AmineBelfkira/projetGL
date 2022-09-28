package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import org.apache.commons.lang.Validate;
import java.io.PrintStream;
import java.util.HashMap;

/**
 * Method Body
 * @author gl19
 * @date 17/01/2022
 */
public class MethodBody extends AbstractMethodBody{

    private ListDeclVar declVariables;
    private ListInst insts;
    public MethodBody(ListDeclVar declVariables,ListInst insts) {
        Validate.notNull(declVariables);
        Validate.notNull(insts);
        this.declVariables = declVariables;
        this.insts = insts;
    }

    public ListDeclVar getDeclVar(){
        return declVariables;
    }

    public ListInst getInst() {
        return insts;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.println("{");
        s.indent();
        declVariables.decompile(s);
        insts.decompile(s);
        s.unindent();
        s.println("}");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        declVariables.iter(f);
        insts.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        declVariables.prettyPrint(s, prefix, false);
        insts.prettyPrint(s, prefix, true);
    }

    @Override
    protected void verifyClassBody(DecacCompiler compiler, EnvironmentExp localEnv,EnvironmentExp envParams, ClassDefinition cls,Type ret)
            throws ContextualError {
        envParams.setParentEnvironment(localEnv);
        declVariables.verifyListDeclVariable(compiler,envParams,cls);
        insts.verifyListInst(compiler,envParams,cls,ret);
    }

    @Override
    protected void checkLocation() {
        // nothing
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        Identifier.inMethod = true;
        Identifier.locals = new HashMap<Symbol,Integer>();
        declVariables.codeGenDeclVar(compiler);
        insts.codeGenListInst(compiler);
        Identifier.inMethod = false;
    }

}
