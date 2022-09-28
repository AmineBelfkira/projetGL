package fr.ensimag.deca.tree;

import com.sun.org.apache.bcel.internal.generic.ICONST;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.DVal;

import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import org.objectweb.asm.MethodVisitor;
import static org.objectweb.asm.Opcodes.*;
import java.io.PrintStream;
import java.util.HashMap;

/**
 *
 * @author gl19
 * @date 01/01/2022
 */
public class BooleanLiteral extends AbstractExpr {

    private boolean value;

    public BooleanLiteral(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    protected void codeGenLabelFalse(DecacCompiler compiler, Label next) {
        if(!value){
            compiler.addInstruction(new BRA(next));
        }
    }

    @Override
    protected void codeGenLabelTrue(DecacCompiler compiler, Label next) {
        if(value){
            compiler.addInstruction(new BRA(next));
        }
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        setType(compiler.getEnvironmentType().get(compiler.getSymbolTable().create("boolean")).getType());
        return  getType();
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        int isTrue = value? 1 : 0;
        compiler.addInstruction(new LOAD(isTrue, genRegister(compiler)));
    }
    @Override
    public void genByte(MethodVisitor method, HashMap<SymbolTable.Symbol, Integer> indexTable) {
        if(value) method.visitInsn(ICONST_1);
        else method.visitInsn(ICONST_0);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(Boolean.toString(value));
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }

    @Override
    String prettyPrintNode() {
        return "BooleanLiteral (" + value + ")";
    }

}
