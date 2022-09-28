package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import org.objectweb.asm.MethodVisitor;
import static org.objectweb.asm.Opcodes.*;

import java.util.HashMap;

/**
 * Assignment, i.e. lvalue = expr.
 *
 * @author gl19
 * @date 01/01/2022
 */
public class Assign extends AbstractBinaryExpr {

    @Override
    public AbstractLValue getLeftOperand() {
        // The cast succeeds by construction, as the leftOperand has been set
        // as an AbstractLValue by the constructor.
        return (AbstractLValue)super.getLeftOperand();
    }

    public Assign(AbstractLValue leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        DAddr leftOpReg = (DAddr) getLeftOperand().codeGenReg(compiler);

        DVal right = getRightOperand().codeGenReg(compiler);
        GPRegister reg;
        if(!(right instanceof GPRegister)){
            reg = genRegister(compiler);
            compiler.addInstruction(new LOAD(right, reg));
        } else {
            reg = (GPRegister) right;
        }
        compiler.addInstruction(new STORE(reg, leftOpReg));
        if(Identifier.inMain){
            compiler.getRegManager().freeLastOccupiedReg();
        }
    }

    @Override
    protected DVal codeGenReg(DecacCompiler compiler) {
        codeGenInst(compiler);
        return getLeftOperand().codeGenReg(compiler);
    }

    @Override
    protected void codeGenLabelFalse(DecacCompiler compiler, Label next) {
        codeGenInst(compiler);
        getLeftOperand().codeGenLabelFalse(compiler, next);
    }

    @Override
    protected void codeGenLabelTrue(DecacCompiler compiler, Label next) {
        codeGenInst(compiler);
        getLeftOperand().codeGenLabelTrue(compiler, next);
    }

    @Override
    public void genByte(MethodVisitor method, HashMap<SymbolTable.Symbol, Integer> indexTable) {
        getRightOperand().genByte(method,indexTable);
        int index = indexTable.get(((AbstractIdentifier)getLeftOperand()).getName());
        Type t= ((AbstractIdentifier)getLeftOperand()).getDefinition().getType();
        if(t.isFloat())
            method.visitVarInsn(FSTORE,index);
        else if(t.isInt())
            method.visitVarInsn(ISTORE,index);
        else if(t.isBoolean()) {
            method.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
            method.visitVarInsn(ASTORE, index);
        }
        else
            method.visitVarInsn(ASTORE,index);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type toType = this.getLeftOperand().verifyLvalue(compiler, localEnv, currentClass);
        setRightOperand(this.getRightOperand().verifyRValue(compiler, localEnv, currentClass,toType));
        setType(toType);
        return toType;
    }

    @Override
    protected String getOperatorName() {
        return "=";
    }

}
