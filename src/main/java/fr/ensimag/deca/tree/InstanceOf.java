package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BNE;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LEA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.SEQ;

import org.apache.commons.lang.Validate;
import org.objectweb.asm.MethodVisitor;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;

/**
 * instanceof
 *
 * @author gl19
 * @date 17/01/2022
 */
public class InstanceOf extends AbstractExpr{
    private AbstractExpr expr ;
    private AbstractIdentifier type ;
    private static int nbInstanceOf;

    public InstanceOf(AbstractExpr expr,AbstractIdentifier type) {
        Validate.notNull(expr);
        Validate.notNull(type);
        this.expr = expr;
        this.type = type ;
        nbInstanceOf = 0;
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        //super.codeGenInst(compiler);
        Label labelInstanceOf = new Label("EndInstanceOf" + nbInstanceOf++);
        expr.codeGenInst(compiler);
        //pour les fils et lui meme
        Iterator<Integer> tousFils = compiler.getRegManager().getClassFilsEtKey((type).getClassDefinition().getClassGBOffset());
        while(tousFils.hasNext()) {
            int typeRegOffset = tousFils.next();
            GPRegister tmp =GPRegister.getR(compiler.getRegManager().getIndexLastRegister());
            compiler.addInstruction(new LOAD(new RegisterOffset(0, tmp),Register.R0));
            compiler.addInstruction(new LEA(new RegisterOffset(typeRegOffset, Register.GB), Register.R1));
            compiler.addInstruction(new CMP(Register.R0,Register.R1));
            compiler.addInstruction(new BEQ(labelInstanceOf));
           
        }
        compiler.addLabel(labelInstanceOf);
        compiler.addInstruction(new CMP(Register.R0,Register.R1));
        compiler.addInstruction(new SEQ(GPRegister.getR(compiler.getRegManager().getIndexLastRegister())));

  
    }

    @Override
    protected void codeGenLabelFalse(DecacCompiler compiler, Label next) {
        Label labelInstanceOf = new Label("OrInstanceOf" + nbInstanceOf++);
        expr.codeGenInst(compiler);

        //pour les fils et lui meme
        Iterator<Integer> tousFils = compiler.getRegManager().getClassFilsEtKey((type).getClassDefinition().getClassGBOffset());
        while(tousFils.hasNext()) // 
        {
            int typeRegOffset = tousFils.next();
            GPRegister tmp =GPRegister.getR(compiler.getRegManager().getIndexLastRegister());
            compiler.addInstruction(new LOAD(new RegisterOffset(0, tmp),Register.R0));
            compiler.addInstruction(new LEA(new RegisterOffset(typeRegOffset, Register.GB), Register.R1));
            compiler.addInstruction(new CMP(Register.R0,Register.R1));
            if(!tousFils.hasNext()) // ou < 1
            {
                if(next != null)
                {
                    compiler.addInstruction(new BNE(next));
                    compiler.addLabel(labelInstanceOf);
                }
                    
            }else
            {
                compiler.addInstruction(new BEQ(labelInstanceOf));
            }
        }

    }

    @Override
    protected void codeGenLabelTrue(DecacCompiler compiler, Label next) {
        expr.codeGenInst(compiler);
        //pour les fils et lui meme
        Iterator<Integer> tousFils = compiler.getRegManager().getClassFilsEtKey((type).getClassDefinition().getClassGBOffset());
        while(tousFils.hasNext()) {
            int typeRegOffset = tousFils.next();
            GPRegister tmp =GPRegister.getR(compiler.getRegManager().getIndexLastRegister());
            compiler.addInstruction(new LOAD(new RegisterOffset(0, tmp),Register.R0));
            compiler.addInstruction(new LEA(new RegisterOffset(typeRegOffset, Register.GB), Register.R1));
            compiler.addInstruction(new CMP(Register.R0,Register.R1));
            compiler.addInstruction(new BEQ(next));
           
        }
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {
        Type fromType = expr.verifyExpr(compiler,localEnv,currentClass);
        Type toType = type.verifyType(compiler);
        if(!(fromType.isClassOrNull()&&toType.isClass()))
            throw new ContextualError("operation instance_of failed",getLocation());
        setType(compiler.getEnvironmentType().get(compiler.getSymbolTable().create("boolean")).getType());
        return getType();
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("(");
        expr.decompile(s);
        s.print(" instanceof ");
        type.decompile(s);
        s.print(")");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        expr.prettyPrint(s, prefix, false);
        type.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        expr.iter(f);
        type.iter(f);
    }
    @Override
    public void genByte(MethodVisitor method, HashMap<SymbolTable.Symbol, Integer> indexTable) {
        // nothing
    }
}
