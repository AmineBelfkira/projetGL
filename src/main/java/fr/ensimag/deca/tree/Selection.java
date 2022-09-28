package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.LabelException;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.FLOAT;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.ima.pseudocode.instructions.WFLOATX;
import fr.ensimag.ima.pseudocode.instructions.WINT;
import org.apache.commons.lang.Validate;
import java.io.PrintStream;
import org.objectweb.asm.MethodVisitor;
import java.util.HashMap;

/**
 * selection d'un field 
 * @author gl19
 * @date 01/01/2022
 */
public class Selection extends AbstractLValue{
    private AbstractExpr obj ;
    private AbstractIdentifier field ;

    public Selection(AbstractExpr obj,AbstractIdentifier field) {
        Validate.notNull(obj);
        Validate.notNull(field);
        this.obj =obj;
        this.field = field;
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        DVal reg = codeGenReg(compiler);
        compiler.addInstruction(new LOAD(reg,Register.getR(compiler.getRegManager().getIndexLastRegister())));
    }

    @Override
    protected void codeGenLabelFalse(DecacCompiler compiler, Label next) {
        codeGenReg(compiler);
        field.codeGenLabelFalse(compiler, next);
    }

    @Override
    protected void codeGenLabelTrue(DecacCompiler compiler, Label next) {
        codeGenReg(compiler);
        field.codeGenLabelTrue(compiler, next);
    }


    @Override
    protected DVal codeGenReg(DecacCompiler compiler) {
        obj.codeGenInst(compiler);
        GPRegister reg = Register.getR(compiler.getRegManager().getIndexLastRegister());
        compiler.addInstruction(new CMP(new NullOperand(), reg));
        compiler.addInstruction(new BEQ(LabelException.dereferencement_null));
        if (obj instanceof This) {
            compiler.getRegManager().freeLastOccupiedReg();
            SetLoadThis(false);
        }
        DVal op = field.codeGenReg(compiler);
        return op;
    }


    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        DVal op = codeGenReg(compiler);
        compiler.addInstruction(new LOAD(op, Register.R1));
        if (this.getType().isInt()) {
            compiler.addInstruction(new WINT());
        }
        else {
            compiler.addInstruction(new WFLOAT());
        }
    }

    @Override
    protected void codeGenPrintHexa(DecacCompiler compiler) {
        DVal op = codeGenReg(compiler);
        compiler.addInstruction(new LOAD(op, Register.R1));
        if (this.getType().isInt()) {
            compiler.addInstruction(new FLOAT(Register.R1, Register.R1));
        }
        compiler.addInstruction(new WFLOATX());
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        Type t = obj.verifyExpr(compiler,localEnv,currentClass);
        EnvironmentExp env_exp2 = t.asClassType(t.getName().toString()+" must be a class",getLocation()).getDefinition().getMembers();
        Type fType = field.verifyExpr(compiler,env_exp2,t.asClassType("Error : must be a class",getLocation()).getDefinition());
        if(field.getDefinition().isField()){
            
        }
        if(field.getDefinition().asFieldDefinition("Error : Selection error", getLocation()).getVisibility()==Visibility.PUBLIC){
            setType(fType);   
            return getType();
        }

        if(currentClass == null){
            throw new ContextualError("Can't select the protected field "+ field.getName(), getLocation());
        }

        if(!(compiler.getEnvironmentType().subtype(t.asClassType("Error",getLocation()),currentClass.getType())
                &&(compiler.getEnvironmentType().subtype(currentClass.getType(), field.getFieldDefinition().getContainingClass().getType()))))
                throw new ContextualError("Error : subtype operation failed",getLocation());
        setType(fType);
        return getType();
    }
    @Override
    public Type verifyLvalue(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        return verifyExpr(compiler,localEnv,currentClass);
    }
    @Override
    public void decompile(IndentPrintStream s) {
        obj.decompile(s);
        s.print(".");
        field.decompile(s);
    }

    @Override
    protected void checkDecoration() {
        // nothing
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        obj.prettyPrint(s, prefix, false);
        field.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        obj.iter(f);
        field.iter(f);
    }
    @Override
    public void genByte(MethodVisitor method, HashMap<SymbolTable.Symbol, Integer> indexTable) {
        // nothing
    }
}
