package fr.ensimag.deca.tree;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;

import fr.ensimag.deca.tools.SymbolTable;
import org.apache.commons.lang.Validate;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.ERROR;
import fr.ensimag.ima.pseudocode.instructions.FLOAT;
import fr.ensimag.ima.pseudocode.instructions.INT;
import fr.ensimag.ima.pseudocode.instructions.LEA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.ima.pseudocode.instructions.WFLOATX;
import fr.ensimag.ima.pseudocode.instructions.WINT;
import fr.ensimag.ima.pseudocode.instructions.WNL;
import fr.ensimag.ima.pseudocode.instructions.WSTR;
import org.objectweb.asm.MethodVisitor;


/**
 *   Cast   (type) (expr)
 * @author gl19
 * @date 18/01/2022
 */
public class Cast extends AbstractExpr{

    private AbstractIdentifier type;
    private AbstractExpr expr;
    private static int Index = 0;

    public Cast (AbstractIdentifier type, AbstractExpr expr) {
        Validate.notNull(type);
        Validate.notNull(expr);
        this.type = type;
        this.expr = expr;
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {

        // same type rien a faire
        if(type.getType().sameType(expr.getType())){
            expr.codeGenInst(compiler);

        // int to float    
        } else if(type.getType().isFloat()){
            expr.codeGenInst(compiler);
            GPRegister tmp =GPRegister.getR(compiler.getRegManager().getIndexLastRegister());
            compiler.addInstruction(new FLOAT(tmp, tmp));

        // float to int
        } else if(type.getType().isInt()){
            expr.codeGenInst(compiler);
            GPRegister tmp =GPRegister.getR(compiler.getRegManager().getIndexLastRegister());
            compiler.addInstruction(new INT(tmp, tmp));

        // class to another
        } else {
            Label endCast = new Label("end.cast."+Index++);
    
            expr.codeGenInst(compiler);
    
            // si null
            compiler.addInstruction(new CMP(new NullOperand(), GPRegister.getR(compiler.getRegManager().getIndexLastRegister())));
            compiler.addInstruction(new BEQ(endCast));
    
            // si instanceof
            Iterator<Integer> tousFils = compiler.getRegManager().getClassFilsEtKey((type).getClassDefinition().getClassGBOffset());
            while(tousFils.hasNext()) {
                int typeRegOffset = tousFils.next();
                GPRegister tmp =GPRegister.getR(compiler.getRegManager().getIndexLastRegister());
                compiler.addInstruction(new LOAD(new RegisterOffset(0, tmp),Register.R0));
                compiler.addInstruction(new LEA(new RegisterOffset(typeRegOffset, Register.GB), Register.R1));
                compiler.addInstruction(new CMP(Register.R0,Register.R1));
                compiler.addInstruction(new BEQ(endCast));
            }
    
            // sinon
            compiler.addInstruction(new WSTR(getLocation()+": Erreur : Cast can't be done"));
            compiler.addInstruction(new WNL());
            compiler.addInstruction(new ERROR());
    
            compiler.addLabel(endCast);
        }
    }

    @Override
    protected void codeGenLabelFalse(DecacCompiler compiler, Label next) {
        expr.codeGenLabelFalse(compiler, next);
    }

    @Override
    protected void codeGenLabelTrue(DecacCompiler compiler, Label next) {
        expr.codeGenLabelTrue(compiler, next);
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        if(type.getType().sameType(expr.getType())){
            expr.codeGenPrint(compiler);

        // int to float    
        } else if(type.getType().isFloat()){
            expr.codeGenInst(compiler);
            GPRegister tmp =GPRegister.getR(compiler.getRegManager().getIndexLastRegister());
            compiler.addInstruction(new FLOAT(tmp, tmp));
            compiler.addInstruction(new LOAD(tmp, Register.R1));
            compiler.addInstruction(new WFLOAT());

        // float to int
        } else if(type.getType().isInt()){
            expr.codeGenInst(compiler);
            GPRegister tmp =GPRegister.getR(compiler.getRegManager().getIndexLastRegister());
            compiler.addInstruction(new INT(tmp, tmp));
            compiler.addInstruction(new LOAD(tmp, Register.R1));
            compiler.addInstruction(new WINT());

        }
    }

    @Override
    protected void codeGenPrintHexa(DecacCompiler compiler) {
        if(type.getType().sameType(expr.getType())){
            expr.codeGenPrint(compiler);

        // int to float    
        } else if(type.getType().isFloat()){
            expr.codeGenInst(compiler);
            GPRegister tmp =GPRegister.getR(compiler.getRegManager().getIndexLastRegister());
            compiler.addInstruction(new FLOAT(tmp, tmp));
            compiler.addInstruction(new LOAD(tmp, Register.R1));
            compiler.addInstruction(new WFLOATX());

        // float to int
        } else if(type.getType().isInt()){
            expr.codeGenInst(compiler);
            GPRegister tmp =GPRegister.getR(compiler.getRegManager().getIndexLastRegister());
            compiler.addInstruction(new INT(tmp, tmp));
            compiler.addInstruction(new FLOAT(tmp, tmp));
            compiler.addInstruction(new LOAD(tmp, Register.R1));
            compiler.addInstruction(new WFLOATX());

        }
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass)
        throws ContextualError {
        Type toType = type.verifyType(compiler);
        Type fromType = expr.verifyExpr(compiler, localEnv, currentClass);
        if(toType.sameType(fromType)){
            setType(toType);
            return getType();
        }

        if(toType.isFloat() && fromType.isInt()){
            setType(toType);
            return getType();
        }

        if(fromType.isInt() && toType.isFloat()){
            setType(toType);
            return getType();
        }

        if(!compiler.getEnvironmentType().cast_compatible(toType, fromType)){
            throw new ContextualError("Can not convert from "+fromType.getName()+" to "+toType.getName(),getLocation());
        }
        setType(toType);
        return getType();
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("(");
        type.decompile(s);
        s.print(") (");
        expr.decompile(s);
        s.print(")");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        expr.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        type.iter(f);
        expr.iter(f);
    }
    @Override
    public void genByte(MethodVisitor method, HashMap<SymbolTable.Symbol, Integer> indexTable) {
        // nothing
    }
}
