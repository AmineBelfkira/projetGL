package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import org.objectweb.asm.MethodVisitor;

import java.io.PrintStream;
import java.util.HashMap;

public class This extends AbstractExpr{
    boolean impl ;


    public This(boolean impl) {
        this.impl =impl;
    }

    @Override
    boolean isImplicit() {
        return impl;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        if(currentClass==null)
            throw new ContextualError("This must be called inside a method",getLocation());
        return currentClass.getType();
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        GPRegister reg = genRegister(compiler);
        compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), reg));
    }

    protected DVal codeGenReg(DecacCompiler compiler) {
        codeGenInst(compiler);
        return Register.getR(compiler.getRegManager().getIndexLastRegister());
    }

    @Override
    public void decompile(IndentPrintStream s) {
        if(impl==false)
            s.print("this");
    }

    @Override
    String prettyPrintNode() {
        return "This : "+ (impl? "true":"false");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        //nothing
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        //nothing
    }

    @Override
    protected void checkDecoration()
    {
        //nothing
    }
    @Override
    public void genByte(MethodVisitor method, HashMap<SymbolTable.Symbol, Integer> indexTable) {
        //nothing
    }
    @Override
    protected void checkLocation() {
        // nothing
    }
}
