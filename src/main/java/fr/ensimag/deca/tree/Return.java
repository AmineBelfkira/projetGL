package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import org.apache.commons.lang.Validate;
import org.objectweb.asm.MethodVisitor;
import java.io.PrintStream;
import java.util.HashMap;

/**
 * return
 * @author gl19
 * @date 17/01/2022
 */
public class Return extends AbstractInst{
    private AbstractExpr expr ;

    public Return(AbstractExpr expr) {
        Validate.notNull(expr);
        this.expr = expr;
    }

    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass, Type returnType) throws ContextualError {
        if(returnType.isVoid())
            throw new ContextualError("return type can't be void ",getLocation());
        try {
            expr.verifyRValue(compiler,localEnv,currentClass, returnType);
        } catch (ContextualError e) {
            throw new ContextualError("Return type is "+expr.getType()+", expected "+returnType, getLocation());
        }
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        DVal reg = expr.codeGenReg(compiler);
        compiler.addInstruction(new LOAD(reg, Register.R0));
        compiler.addInstruction(new BRA(DeclClass.getmethodFin()));
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("return ");
        expr.decompile(s);
        s.print(";");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        expr.prettyPrint(s,prefix,true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        expr.iter(f);
    }
    @Override
    public void genByte(MethodVisitor method, HashMap<SymbolTable.Symbol, Integer> indexTable) {
        // nothing
    }
}
