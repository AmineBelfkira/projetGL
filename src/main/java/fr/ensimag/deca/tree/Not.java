package fr.ensimag.deca.tree;

import com.sun.org.apache.bcel.internal.generic.ICONST;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.SEQ;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import org.objectweb.asm.MethodVisitor;

import java.util.HashMap;
import static org.objectweb.asm.Label.*;
import static org.objectweb.asm.Opcodes.*;

/**
 *
 * @author gl19
 * @date 01/01/2022
 */
public class Not extends AbstractUnaryExpr {

    public Not(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type opType = getOperand().verifyExpr(compiler, localEnv, currentClass);
        if (!opType.isBoolean()){
            throw new ContextualError("\"Not\" can not be applied on a not boolean type, applied on "+ opType.getName(), getLocation());
        }
        super.setType(opType);
        return super.getType();
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        DVal opReg = getOperand().codeGenReg(compiler);
        GPRegister reg;
        if (!(opReg instanceof GPRegister)){
            reg = genRegister(compiler);
            compiler.addInstruction(new LOAD(opReg, reg));
        }else{
            reg = (GPRegister) opReg;
        }
        compiler.addInstruction(new CMP(0, reg));
        compiler.addInstruction(new SEQ(reg));
    }

    @Override
    protected void codeGenLabelFalse(DecacCompiler compiler, Label next) {
        getOperand().codeGenLabelTrue(compiler, next);
    }

    @Override
    protected void codeGenLabelTrue(DecacCompiler compiler, Label next) {
        getOperand().codeGenLabelFalse(compiler, next);
    }

    @Override
    protected String getOperatorName() {
        return "!";
    }
    @Override
    public void genByte(MethodVisitor method, HashMap<SymbolTable.Symbol, Integer> indexTable) {
        getOperand().genByte(method,indexTable);
        org.objectweb.asm.Label label = new org.objectweb.asm.Label();
        method.visitJumpInsn(IFNE, label);
        method.visitLdcInsn(ICONST_1);
        org.objectweb.asm.Label end = new org.objectweb.asm.Label();
        method.visitJumpInsn(GOTO, end);
        method.visitLabel(label);
        method.visitInsn(ICONST_0);
        method.visitLabel(end);
    }
}
