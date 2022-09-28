package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.FLOAT;
import fr.ensimag.ima.pseudocode.instructions.OPP;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.ima.pseudocode.instructions.WFLOATX;
import fr.ensimag.ima.pseudocode.instructions.WINT;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import org.objectweb.asm.MethodVisitor;

import java.util.HashMap;

/**
 * @author gl19
 * @date 01/01/2022
 */
public class UnaryMinus extends AbstractUnaryExpr {

    public UnaryMinus(AbstractExpr operand) {
        super(operand);
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        DVal oprand = this.getOperand().codeGenReg(compiler);
        compiler.addInstruction(new OPP(oprand, Register.R1));
        if(this.getType().isFloat()){
            compiler.addInstruction(new WFLOAT());
        }else{
            compiler.addInstruction(new WINT());
        }
    }

    @Override
    protected void codeGenPrintHexa(DecacCompiler compiler) {
        DVal oprand = this.getOperand().codeGenReg(compiler);
        compiler.addInstruction(new OPP(oprand, Register.R1));
        if(this.getType().isInt()){
            compiler.addInstruction(new FLOAT(Register.R1, Register.R1));
        }
        compiler.addInstruction(new WFLOATX());
    }

    protected void codeGenInst(DecacCompiler compiler) {
        DVal oprand = this.getOperand().codeGenReg(compiler);
        GPRegister reg;
        if (oprand instanceof GPRegister){
            reg = Register.getR(compiler.getRegManager().getIndexLastRegister());
        }else{
            reg = genRegister(compiler);
        }
        compiler.addInstruction(new OPP(oprand,reg));
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type Optype=getOperand().verifyExpr(compiler, localEnv, currentClass);
        if(!Optype.isInt() && !Optype.isFloat()){
            throw new ContextualError("Error UnaryMinus: Must be an integer or Float", getLocation());
        }
        setType(Optype);
        return this.getType();
    }


    @Override
    protected String getOperatorName() {
        return "-";
    }
    @Override
    public void genByte(MethodVisitor method, HashMap<SymbolTable.Symbol, Integer> indexTable) {
        // nothing
    }
}
