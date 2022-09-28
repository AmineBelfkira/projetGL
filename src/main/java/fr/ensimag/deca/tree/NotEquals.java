package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BNE;
import fr.ensimag.ima.pseudocode.instructions.SNE;
import org.objectweb.asm.MethodVisitor;

import java.util.HashMap;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.ICONST_1;

/**
 *
 * @author gl19
 * @date 01/01/2022
 */
public class NotEquals extends AbstractOpExactCmp {

    public NotEquals(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected void codeGenLabelTrue(DecacCompiler compiler , Label next)
    {
        codeGenCMP(compiler);
        compiler.addInstruction(new BNE(next));
        verifyPop(compiler);
    }

    @Override
    protected void codeGenLabelFalse(DecacCompiler compiler , Label next)
    {
        codeGenCMP(compiler);
        compiler.addInstruction(new BEQ(next));
        verifyPop(compiler);
    }

    @Override
    protected void mnemoOp(DecacCompiler compiler, GPRegister rightReg) {
        compiler.addInstruction(new SNE(rightReg));
    }   

    @Override
    protected String getOperatorName() {
        return "!=";
    }
    @Override
    public void genByte(MethodVisitor method, HashMap<SymbolTable.Symbol, Integer> indexTable) {
        getLeftOperand().genByte(method,indexTable);
        getRightOperand().genByte(method,indexTable);
        org.objectweb.asm.Label label = new org.objectweb.asm.Label();
        method.visitJumpInsn(IF_ICMPEQ, label);
        method.visitInsn(ICONST_1);
        org.objectweb.asm.Label end = new org.objectweb.asm.Label();
        method.visitJumpInsn(GOTO, end);
        method.visitLabel(label);
        method.visitInsn(ICONST_0);
        method.visitLabel(end);
    }
}
