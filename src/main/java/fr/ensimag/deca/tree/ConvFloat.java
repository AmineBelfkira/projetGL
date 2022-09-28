package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.FLOAT;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.EnvironmentExp;

import fr.ensimag.deca.context.FloatType;
import fr.ensimag.deca.context.IntType;
import org.objectweb.asm.MethodVisitor;
import java.util.HashMap;

/**
 * Conversion of an int into a float. Used for implicit conversions.
 * 
 * @author gl19
 * @date 01/01/2022
 */
public class ConvFloat extends AbstractUnaryExpr {
    public ConvFloat(AbstractExpr operand) {
        super(operand);
    }

    @Override
    protected void checkDecoration() {
        // nothing
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        DVal reg = getOperand().codeGenReg(compiler);
        GPRegister temp;
        if (!(reg instanceof GPRegister)){
            temp = genRegister(compiler);
            compiler.addInstruction(new LOAD(reg, temp));
        }else{
            temp = (GPRegister) reg;
        }
        compiler.addInstruction(new FLOAT(reg, temp));
    }


    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) {
        setType(compiler.getEnvironmentType().get(compiler.getSymbolTable().create("float")).getType());
        return this.getType();
    }


    @Override
    protected String getOperatorName() {
        return "/* conv float */";
    }
    @Override
    public void genByte(MethodVisitor method, HashMap<SymbolTable.Symbol, Integer> indexTable) {
        // nothing
    }
}
