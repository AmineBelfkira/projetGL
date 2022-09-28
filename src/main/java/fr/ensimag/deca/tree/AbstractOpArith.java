package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.FLOAT;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.ima.pseudocode.instructions.WFLOATX;
import fr.ensimag.ima.pseudocode.instructions.WINT;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 * Arithmetic binary operations (+, -, /, ...)
 * 
 * @author gl19
 * @date 01/01/2022
 */
public abstract class AbstractOpArith extends AbstractBinaryExpr {
    
    public AbstractOpArith(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    /**
     * Mnémonique d’un opérateur binaire
     * @param compiler
     * @param leftOp Dval of the right Operand
     * @param rightOp GPRegister containing the left Operand
     */
    protected abstract void mnemoOp(DecacCompiler compiler, DVal rightOp, GPRegister leftOp);

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        codeGenInst(compiler);
        compiler.addInstruction(new LOAD(Register.getR(compiler.getRegManager().getIndexLastRegister()), Register.R1));
        compiler.getRegManager().freeLastOccupiedReg();
        if(this.getType().isInt()){
            compiler.addInstruction(new WINT());
        }else if (this.getType().isFloat()){
            compiler.addInstruction(new WFLOAT());
        }
    }

    @Override
    protected void codeGenPrintHexa(DecacCompiler compiler) {
        codeGenInst(compiler);
        compiler.addInstruction(new LOAD(Register.getR(compiler.getRegManager().getIndexLastRegister()), Register.R1));
        compiler.getRegManager().freeLastOccupiedReg();
        if(this.getType().isInt()){
            GPRegister reg = Register.getR(compiler.getRegManager().getIndexLastRegister());
            compiler.addInstruction(new FLOAT(reg, reg));
            compiler.addInstruction(new LOAD(Register.getR(compiler.getRegManager().getIndexLastRegister()), Register.R1));
        }
        compiler.addInstruction(new WFLOATX());
    }

    static boolean popDone = false; 
    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        int numPush = compiler.getRegManager().getNeedPop();
        popDone = false;
        getLeftOperand().codeGenInst(compiler);
        Identifier.isGPR = false;
        DVal rightOpReg = getRightOperand().codeGenReg(compiler);
        if (numPush < compiler.getRegManager().getNeedPop()) {
            if (rightOpReg instanceof GPRegister || Identifier.isGPR){
                compiler.getRegManager().freeLastOccupiedReg();
                Identifier.isGPR = false;
            }
            mnemoOp(compiler, rightOpReg, Register.getR(compiler.getRegManager().getIndexLastRegister()));
            doLoadPop(compiler);
            popDone = true;
        } else {
            if (popDone == true) {
                mnemoOp(compiler, Register.R0, Register.getR(compiler.getRegManager().getIndexLastRegister()));
                popDone = false;
            }
            else {
                if (rightOpReg instanceof GPRegister || Identifier.isGPR){
                    compiler.getRegManager().freeLastOccupiedReg();
                    Identifier.isGPR = false;
                }
                mnemoOp(compiler, rightOpReg, Register.getR(compiler.getRegManager().getIndexLastRegister()));
            }
        }
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type leftType = super.getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type rightType =  super.getRightOperand().verifyExpr(compiler, localEnv, currentClass);

        if ((!leftType.isFloat() && !leftType.isInt()) ||
            (!rightType.isFloat() && !rightType.isInt())){
                //modification message exemple avant can not operate boolean and boolean --pas clair 
            throw new ContextualError("Can not do operation " + leftType.getName() + getOperatorName() + rightType.getName() , getLocation());
        }

        if (leftType.sameType(rightType)){
            setType(leftType);
        } else if (leftType.isFloat() || rightType.isFloat()){
            if (!leftType.isFloat()){
                ConvFloat newLeft = new ConvFloat(super.getLeftOperand());
                newLeft.verifyExpr(compiler, localEnv, currentClass);
                setLeftOperand(newLeft);
            }
            if (!rightType.isFloat()){
                ConvFloat newRight = new ConvFloat(super.getRightOperand());
                newRight.verifyExpr(compiler, localEnv, currentClass);
                setRightOperand(newRight); 
            }
            setType(getRightOperand().getType());
        }
        return getType();
    }
}
