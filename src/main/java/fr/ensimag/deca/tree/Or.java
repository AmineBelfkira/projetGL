package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.ADD;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.SGT;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.HashMap;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.ICONST_0;

/**
 *
 * @author gl19
 * @date 01/01/2022
 */
public class Or extends AbstractOpBool {

    public Or(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        int nbrPush = compiler.getRegManager().getNeedPop();
        DVal leftOpReg = getLeftOperand().codeGenReg(compiler);
        nbrPush = compiler.getRegManager().getNeedPop() - nbrPush;

        DVal rightOpReg = getRightOperand().codeGenReg(compiler);
        GPRegister rightReg;
        if (!(rightOpReg instanceof GPRegister)){
            rightReg = genRegister(compiler);
            compiler.addInstruction(new LOAD(rightOpReg, rightReg));
        }else{
            rightReg = (GPRegister) rightOpReg;
        }
        compiler.addInstruction(new ADD(leftOpReg, rightReg));
        compiler.addInstruction(new CMP(0, rightReg));
        compiler.addInstruction(new SGT(rightReg));

        if(nbrPush>0){
            // Le cast est verifié puisqu'on fait le pop que 
            //   si le nbre de push a changer lors du codeGenReg4
            //   et donc leftOpReg est un GPRegistre
            compiler.addInstruction(new POP((GPRegister) leftOpReg));
            compiler.getRegManager().decrementNeedPop();
        }
    }

    @Override
    protected void codeGenLabelTrue(DecacCompiler compiler, Label next) {   

        getLeftOperand().codeGenLabelTrue(compiler, next);
        getRightOperand().codeGenLabelTrue(compiler, next);

    }

    @Override
    protected void codeGenLabelFalse(DecacCompiler compiler, Label next) {
        
        Label label = new Label("OrTrue" + nbLabel++);
        getLeftOperand().codeGenLabelTrue(compiler, label);
         
        getRightOperand().codeGenLabelFalse(compiler, next);
        compiler.addLabel(label);
        
    }


    @Override
    protected String getOperatorName() {
        return "||";
    }
    @Override
    public void genByte(MethodVisitor method, HashMap<SymbolTable.Symbol, Integer> indexTable) {
        getLeftOperand().genByte(method,indexTable);
        org.objectweb.asm.Label label = new org.objectweb.asm.Label();
        method.visitJumpInsn(IFNE, label);
        getRightOperand().genByte(method,indexTable);
        method.visitJumpInsn(IFNE, label);
        method.visitInsn(ICONST_0);
        org.objectweb.asm.Label end = new org.objectweb.asm.Label();
        method.visitJumpInsn(GOTO, end);
        method.visitLabel(label);
        method.visitInsn(ICONST_1);
        method.visitLabel(end);
    }

}
