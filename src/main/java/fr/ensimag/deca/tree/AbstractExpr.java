package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;

import java.io.PrintStream;
import java.util.HashMap;

import org.apache.commons.lang.Validate;
import org.objectweb.asm.MethodVisitor;

/**
 * Expression, i.e. anything that has a value.
 *
 * @author gl19
 * @date 01/01/2022
 */
public abstract class AbstractExpr extends AbstractInst {
    
    private static boolean loadThis = true;
    /**
     * @return true if the expression does not correspond to any concrete token
     * in the source code (and should be decompiled to the empty string).
     */
    public boolean getLoadThis() {
        return loadThis;
    }

    /**
     * setter for the field loadThis 
     * that if it is true the expression does not correspond to any concrete token
     * @param bool
     */
    public void SetLoadThis(boolean bool) {
        loadThis = bool;
    }

    /**
     * return false if the expression is implicitly envolved
     * @return
     */
    boolean isImplicit() {
        return false;
    }

    /**
     * Get the type decoration associated to this expression (i.e. the type computed by contextual verification).
     */
    public Type getType() {
        return type;
    }

    protected void setType(Type type) {
        Validate.notNull(type);
        this.type = type;
    }

    private Type type;

    @Override
    protected void checkDecoration() {
        if (getType() == null) {
            throw new DecacInternalError("Expression " + decompile() + " has no Type decoration");
        }
    }

    /**
     * Verify the expression for contextual error.
     * <p>
     * implements non-terminals "expr" and "lvalue"
     * of [SyntaxeContextuelle] in pass 3
     *
     * @param compiler     (contains the "env_types" attribute)
     * @param localEnv     Environment in which the expression should be checked
     *                     (corresponds to the "env_exp" attribute)
     * @param currentClass Definition of the class containing the expression
     *                     (corresponds to the "class" attribute)
     *                     is null in the main bloc.
     * @return the Type of the expression
     * (corresponds to the "type" attribute)
     */
    public abstract Type verifyExpr(DecacCompiler compiler,
                                    EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError;

    /**
     * Verify the expression in right hand-side of (implicit) assignments
     * <p>
     * implements non-terminal "rvalue" of [SyntaxeContextuelle] in pass 3
     *
     * @param compiler     contains the "env_types" attribute
     * @param localEnv     corresponds to the "env_exp" attribute
     * @param currentClass corresponds to the "class" attribute
     * @param expectedType corresponds to the "type1" attribute
     * @return this with an additional ConvFloat if needed...
     */
    public AbstractExpr verifyRValue(DecacCompiler compiler,
                                     EnvironmentExp localEnv, ClassDefinition currentClass,
                                     Type expectedType)
            throws ContextualError {
        Type fromType = this.verifyExpr(compiler, localEnv, currentClass);
        if (!compiler.getEnvironmentType().assign_compatible(expectedType, fromType)) {
            throw new ContextualError("Can not assign " + fromType.getName() + " to " + expectedType.getName(), getLocation());
        }

        if (expectedType.sameType(fromType)) {
            return this;
        } else if (fromType.isInt() && expectedType.isFloat()){
            ConvFloat rightOperand = new ConvFloat(this);
            rightOperand.setLocation(getLocation());
            return rightOperand;
        }
        return this;
    }


    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
                              ClassDefinition currentClass, Type returnType)
            throws ContextualError {
        verifyExpr(compiler, localEnv, currentClass);
    }

    /**
     * Verify the expression as a condition, i.e. check that the type is
     * boolean.
     *
     * @param localEnv     Environment in which the condition should be checked.
     * @param currentClass Definition of the class containing the expression, or null in
     *                     the main program.
     */
    void verifyCondition(DecacCompiler compiler, EnvironmentExp localEnv,
                         ClassDefinition currentClass) throws ContextualError {
        if (!(verifyExpr(compiler, localEnv, currentClass).isBoolean())) {
            throw new ContextualError("Condition type must be boolean", getLocation());
        }
    }

    /**
     * Generate code to print the expression
     *
     * @param compiler
     */
    protected void codeGenPrint(DecacCompiler compiler) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     * Generate code to print Hexadecimal 
     * 
     * @param compiler
     */
    protected void codeGenPrintHexa(DecacCompiler compiler) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    protected DVal codeGenReg(DecacCompiler compiler) {
        codeGenInst(compiler);
        return Register.getR(compiler.getRegManager().getIndexLastRegister());
    }
    
    protected GPRegister genRegister(DecacCompiler compiler) {
        int x = compiler.getRegManager().getIndexRegistre();
        if (x == -1) {
            compiler.addInstruction(new PUSH(Register.getR(compiler.getRegManager().getIndexLastRegister())));
            compiler.getRegManager().IncrementNeedPop();
        }
        return Register.getR(compiler.getRegManager().getIndexLastRegister());
    }

    protected void doLoadPop(DecacCompiler compiler) {
        compiler.addInstruction(new LOAD(Register.getR(compiler.getRegManager().getIndexLastRegister()), Register.R0));
        compiler.addInstruction(new POP(Register.getR(compiler.getRegManager().getIndexLastRegister())));
        compiler.getRegManager().decrementNeedPop();
    }


    @Override
    protected void decompileInst(IndentPrintStream s) {
        decompile(s);
        s.print(";");
    }

    @Override
    protected void prettyPrintType(PrintStream s, String prefix) {
        Type t = getType();
        if (t != null) {
            s.print(prefix);
            s.print("type: ");
            s.print(t);
            s.println();
        }
    }
    public abstract void genByte(MethodVisitor method, HashMap<SymbolTable.Symbol, Integer> indexTable);

}
