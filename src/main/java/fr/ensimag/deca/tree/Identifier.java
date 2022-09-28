package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.TypeDefinition;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.EnvironmentType;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.ParamDefinition;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.VariableDefinition;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.FLOAT;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.ima.pseudocode.instructions.WFLOATX;
import fr.ensimag.ima.pseudocode.instructions.WINT;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

/**
 * Deca Identifier
 *
 * @author gl19
 * @date 01/01/2022
 */
public class Identifier extends AbstractIdentifier {

    public static boolean isGPR = false;
    public static boolean inMain = false;
    public static boolean inMethod = false;
    public static Map<Symbol, Integer> locals;

    @Override
    protected void checkLocation() {
        // nothing
    }

    @Override
    protected DVal codeGenReg(DecacCompiler compiler) {
        if (definition.isField()) {
            if(inMain){
                GPRegister reg = Register.getR(compiler.getRegManager().getIndexLastRegister());
                isGPR = true;
                return new RegisterOffset(((FieldDefinition)definition).getIndex(), reg);
            }
            GPRegister reg = genRegister(compiler);
            if (getLoadThis()) {
                compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), reg));
            }
            SetLoadThis(true);
            isGPR = true;
            return new RegisterOffset(((FieldDefinition)definition).getIndex(), reg);
        } 
        if (definition.isParam()) {
            return new RegisterOffset(-(2 + ((ParamDefinition)definition).getIndex()), Register.LB);
        }
        if(inMethod){
            return Register.getR(locals.get(name));
        }
        
        return compiler.getRegManager().getRegisterOffset(name);
    }
    
    @Override
    protected void codeGenLabelFalse(DecacCompiler compiler, Label next) {
        DVal ident = codeGenReg(compiler);
        int nbrPush = compiler.getRegManager().getNeedPop();
        GPRegister reg = genRegister(compiler);
        compiler.addInstruction(new LOAD(ident,reg));
        compiler.addInstruction(new CMP(0, reg));
        compiler.addInstruction(new BEQ(next));
        if (compiler.getRegManager().getNeedPop()>nbrPush){
            compiler.addInstruction(new POP(reg));
            compiler.getRegManager().decrementNeedPop();
        }else{
            compiler.getRegManager().freeLastOccupiedReg();
        }
    }

    @Override
    protected void codeGenLabelTrue(DecacCompiler compiler, Label next) {
        DVal ident = codeGenReg(compiler);
        int nbrPush = compiler.getRegManager().getNeedPop();
        GPRegister reg = genRegister(compiler);
        compiler.addInstruction(new LOAD(ident,reg));
        compiler.addInstruction(new CMP(1, reg));
        compiler.addInstruction(new BEQ(next));
        if (compiler.getRegManager().getNeedPop()>nbrPush){
            compiler.addInstruction(new POP(reg));
            compiler.getRegManager().decrementNeedPop();
        }else{
            compiler.getRegManager().freeLastOccupiedReg();
        }
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {   
        compiler.addInstruction(new LOAD(codeGenReg(compiler), Register.R1));
        if (getDefinition().getType().isInt()) {
           compiler.addInstruction(new WINT());
       } 
       else {
           compiler.addInstruction(new WFLOAT());
       }
    }

    @Override
    protected void codeGenPrintHexa(DecacCompiler compiler) {
        compiler.addInstruction(new LOAD(codeGenReg(compiler), Register.R1));
        if (getDefinition().getType().isInt()) {
            compiler.addInstruction(new FLOAT(Register.R1, Register.R1));
        }
        compiler.addInstruction(new WFLOATX());
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        isGPR = false;
        DVal offset = codeGenReg(compiler);
        if (isGPR){
            compiler.getRegManager().freeLastOccupiedReg();
            isGPR = false;
        }
        compiler.addInstruction(new LOAD(offset, genRegister(compiler)));
    }

    @Override
    protected void checkDecoration() {
        if (getDefinition() == null) {
            throw new DecacInternalError("Identifier " + this.getName() + " has no attached Definition");
        }
    }

    @Override
    public Definition getDefinition() {
        return definition;
    }

    @Override
    protected Type verifyLvalue(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        Type t = this.verifyExpr(compiler,localEnv,currentClass);
        Definition def = this.definition ;
        if(!(def.isField()|| def.isParam()|| def.isExpression()))
            throw new ContextualError("Incorrect L value",getLocation());
        setType(t);
        return t ;
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * ClassDefinition.
     * 
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     * 
     * @throws DecacInternalError
     *             if the definition is not a class definition.
     */
    @Override
    public ClassDefinition getClassDefinition() {
        try {
            return (ClassDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a class identifier, you can't call getClassDefinition on it");
        }
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * MethodDefinition.
     * 
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     * 
     * @throws DecacInternalError
     *             if the definition is not a method definition.
     */
    @Override
    public MethodDefinition getMethodDefinition() {
        try {
            return (MethodDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a method identifier, you can't call getMethodDefinition on it");
        }
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * FieldDefinition.
     * 
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     * 
     * @throws DecacInternalError
     *             if the definition is not a field definition.
     */
    @Override
    public FieldDefinition getFieldDefinition() {
        try {
            return (FieldDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a field identifier, you can't call getFieldDefinition on it");
        }
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * VariableDefinition.
     * 
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     * 
     * @throws DecacInternalError
     *             if the definition is not a field definition.
     */
    @Override
    public VariableDefinition getVariableDefinition() {
        try {
            return (VariableDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a variable identifier, you can't call getVariableDefinition on it");
        }
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a ExpDefinition.
     * 
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     * 
     * @throws DecacInternalError
     *             if the definition is not a field definition.
     */
    @Override
    public ExpDefinition getExpDefinition() {
        try {
            return (ExpDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a Exp identifier, you can't call getExpDefinition on it");
        }
    }

    @Override
    public void setDefinition(Definition definition) {
        this.definition = definition;
    }

    @Override
    public Symbol getName() {
        return name;
    }

    private Symbol name;

    public Identifier(Symbol name) {
        Validate.notNull(name);
        this.name = name;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {

        ExpDefinition def = localEnv.get(name);
        if (def == null){
            throw new ContextualError(name.toString() + " is not defined " , getLocation());
        }
        setDefinition(def);
        super.setType(def.getType());
        return super.getType();
    }

    /**
     * Implements non-terminal "type" of [SyntaxeContextuelle] in the 3 passes
     * @param compiler contains "env_types" attribute
     */
    @Override
    public Type verifyType(DecacCompiler compiler) throws ContextualError {
        EnvironmentType envTypes = compiler.getEnvironmentType();
        TypeDefinition def = envTypes.get(name);
        if (def == null){
            throw new ContextualError("type "+ name.toString() + " is not defined" , getLocation());
        }
        setDefinition(def);
        setType(def.getType());
        return getType();
    }

    private Definition definition;

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(name.toString());
    }

    @Override
    String prettyPrintNode() {
        return "Identifier (" + getName() + ")";
    }

    @Override
    protected void prettyPrintType(PrintStream s, String prefix) {
        Definition d = getDefinition();
        if (d != null) {
            s.print(prefix);
            s.print("definition: ");
            s.print(d);
            s.println();
        }
    }
    @Override
    public void genByte(MethodVisitor method, HashMap<Symbol, Integer> indexTable) {
        int index = indexTable.get(name);
        Type t = definition.getType();
        if(t.isFloat())
            method.visitVarInsn(FLOAD,index);
        else if(t.isInt())
            method.visitVarInsn(ILOAD,index);
        else if(t.isBoolean()) {
            method.visitVarInsn(ALOAD, index);
            method.visitMethodInsn(INVOKEVIRTUAL,"java/lang/Boolean","booleanValue","()Z",false);
        }
        else
            method.visitVarInsn(ALOAD,index);

    }
}
