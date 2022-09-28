package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.codegen.LabelException;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.MUL;
import org.objectweb.asm.MethodVisitor;

import java.util.HashMap;

import static org.objectweb.asm.Opcodes.*;
/**
 * @author gl19
 * @date 01/01/2022
 */
public class Multiply extends AbstractOpArith {
    public Multiply(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }
    
    @Override
    protected void mnemoOp(DecacCompiler compiler, DVal leftOp, GPRegister rightOp) {
        compiler.addInstruction(new MUL(leftOp,rightOp));
        compiler.addInstruction(new BOV(LabelException.overflow_error, compiler));
    }

    @Override
    protected String getOperatorName() {
        return "*";
    }
    @Override
    public void genByte(MethodVisitor method, HashMap<SymbolTable.Symbol, Integer> indexTable) {
        getLeftOperand().genByte(method,indexTable);
        getRightOperand().genByte(method,indexTable);
        if(getRightOperand().getType().isFloat())
            method.visitInsn(FREM);
        else
            method.visitInsn(IREM);
    }
}
