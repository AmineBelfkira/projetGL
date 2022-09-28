package fr.ensimag.deca.tree;

import com.sun.org.apache.bcel.internal.generic.IDIV;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.codegen.LabelException;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.DIV;
import fr.ensimag.ima.pseudocode.instructions.QUO;
import org.objectweb.asm.MethodVisitor;
import static org.objectweb.asm.Opcodes.*;

import java.util.HashMap;

/**
 *
 * @author gl19
 * @date 01/01/2022
 */
public class Divide extends AbstractOpArith {
    public Divide(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected void mnemoOp(DecacCompiler compiler, DVal rightOp, GPRegister leftOp) {

        if (getRightOperand().getType().isInt()) {
            compiler.addInstruction(new QUO(rightOp, leftOp));
        }else{
            compiler.addInstruction(new DIV(rightOp, leftOp));
        }
        compiler.addInstruction(new BOV(LabelException.overflow_error, compiler));
        
    }

    @Override
    protected String getOperatorName() {
        return "/";
    }

    @Override
    public void genByte(MethodVisitor method, HashMap<SymbolTable.Symbol, Integer> indexTable) {
        getLeftOperand().genByte(method,indexTable);
        getRightOperand().genByte(method,indexTable);
        if(getRightOperand().getType().isFloat())
            method.visitInsn(FDIV);
        else
            method.visitInsn(IDIV);

    }
}
