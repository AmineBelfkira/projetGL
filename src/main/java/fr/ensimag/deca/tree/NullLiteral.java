package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import org.objectweb.asm.MethodVisitor;

import java.io.PrintStream;
import java.util.HashMap;

/**
 * null literal
 *
 * @author gl19
 * @date 10/01/2022
 */
public class NullLiteral extends AbstractExpr {

    public NullLiteral(){
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        setType(compiler.getEnvironmentType().get(compiler.getSymbolTable().create("Null")).getType());
        return  getType();
    }

    @Override
    public void genByte(MethodVisitor method, HashMap<SymbolTable.Symbol, Integer> indexTable) {
    // nothing
    }
    @Override
    public void decompile(IndentPrintStream s) {
        s.print("null");
    }

    @Override
    String prettyPrintNode() {
        return "Null";
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }

}
