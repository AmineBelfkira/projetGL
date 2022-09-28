package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 *
 * @author gl19
 * @date 01/01/2022
 */
public abstract class AbstractOpCmp extends AbstractBinaryExpr {

    public AbstractOpCmp(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }
    private static GPRegister leftReg;
    private static GPRegister rightReg;
    private static int nbrPushLeft;
    private static int nbrPushRight;

  

    protected void codeGenCMP(DecacCompiler compiler) {
 
        nbrPushLeft = compiler.getRegManager().getNeedPop();
        DVal leftOpReg = getLeftOperand().codeGenReg(compiler);

        if(!(leftOpReg instanceof GPRegister)){
            leftReg = genRegister(compiler);
            compiler.addInstruction(new LOAD(leftOpReg,leftReg));
        }else 
        {
            leftReg = (GPRegister)leftOpReg;
        }
        nbrPushLeft = compiler.getRegManager().getNeedPop() - nbrPushLeft;

        nbrPushRight = compiler.getRegManager().getNeedPop();
        DVal rightOpReg = getRightOperand().codeGenReg(compiler);

        if(!(rightOpReg instanceof GPRegister)){
            rightReg = genRegister(compiler);
            compiler.addInstruction(new LOAD(rightOpReg,rightReg));
        }else 
        {
            rightReg = (GPRegister)rightOpReg;
        }
        nbrPushRight = compiler.getRegManager().getNeedPop() - nbrPushRight;
        
        compiler.addInstruction(new CMP(rightReg, leftReg));

    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        codeGenCMP(compiler);
        mnemoOp(compiler, rightReg);
        if (nbrPushLeft>0){
            compiler.addInstruction(new POP(leftReg));
            compiler.getRegManager().decrementNeedPop();
        }
    }

    protected void verifyPop(DecacCompiler compiler){
        if (nbrPushLeft>0){
            compiler.addInstruction(new POP(leftReg));
            compiler.getRegManager().decrementNeedPop();
        }else{
            compiler.getRegManager().freeLastOccupiedReg();
        }
        if (nbrPushRight>0){
            compiler.addInstruction(new POP(rightReg));
            compiler.getRegManager().decrementNeedPop();
        }else{
            compiler.getRegManager().freeLastOccupiedReg();
        }
    }

    protected abstract void mnemoOp(DecacCompiler compiler, GPRegister rightReg);

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type leftType = getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type rightType = getRightOperand().verifyExpr(compiler, localEnv, currentClass);
        if ((!leftType.isFloat() && !leftType.isInt()) ||
            (!rightType.isFloat() && !rightType.isInt())){
            throw new ContextualError(getOperatorName() + " can not be applied on "+ 
                                    leftType.getName() + " and " + rightType.getName(), getLocation());
        }

        if (leftType.isInt() && rightType.isFloat()){
            ConvFloat newLeft = new ConvFloat(super.getLeftOperand());
            setLeftOperand(newLeft);
        }else if(leftType.isFloat() && rightType.isInt()){
            ConvFloat newRight = new ConvFloat(super.getRightOperand());
            setLeftOperand(newRight);
        }

        setType(compiler.getEnvironmentType().get(compiler.getSymbolTable().create("boolean")).getType());
        return getType();
    }


}
