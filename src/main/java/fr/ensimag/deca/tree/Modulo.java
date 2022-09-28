package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.REM;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.LabelException;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import org.objectweb.asm.MethodVisitor;

import java.util.HashMap;

import static org.objectweb.asm.Opcodes.*;

/**
 *
 * @author gl19
 * @date 01/01/2022
 */
public class Modulo extends AbstractOpArith {

    public Modulo(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected void mnemoOp(DecacCompiler compiler, DVal rightOp, GPRegister leftOp) {
        compiler.addInstruction(new REM(rightOp, leftOp));
        compiler.addInstruction(new BOV(LabelException.overflow_error, compiler));
        
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type rightType = getRightOperand().verifyExpr(compiler, localEnv, currentClass);
        Type leftType = getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        if (!rightType.isInt()||!leftType.isInt()){
            throw new ContextualError("\"Modulo\" can not be applied on a non int operand, got "+
                                        rightType.getName()+ " and "+leftType.getName(), getLocation());
        }
        super.setType(compiler.getEnvironmentType().get(compiler.getSymbolTable().create("int")).getType());
        return super.getType();
    }


    @Override
    protected String getOperatorName() {
        return "%";
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
