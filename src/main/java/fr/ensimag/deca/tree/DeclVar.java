package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.VariableDefinition;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;

import java.io.PrintStream;
import java.util.HashMap;

import org.apache.commons.lang.Validate;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

/**
 * @author gl19
 * @date 01/01/2022
 */
public class DeclVar extends AbstractDeclVar {

    
    final private AbstractIdentifier type;
    final private AbstractIdentifier varName;
    final private AbstractInitialization initialization;

    public DeclVar(AbstractIdentifier type, AbstractIdentifier varName, AbstractInitialization initialization) {
        Validate.notNull(type);
        Validate.notNull(varName);
        Validate.notNull(initialization);
        this.type = type;
        this.varName = varName;
        this.initialization = initialization;
    }

    @Override
    protected void codeGenDeclVar(DecacCompiler compiler) {
        if(initialization instanceof Initialization){
            if(Identifier.inMethod){
                Identifier.locals.put(varName.getName(), compiler.getRegManager().getIndexRegistre());
                GPRegister op2 = Register.getR(Identifier.locals.get(varName.getName()));
                ((Initialization)initialization).getExpression().codeGenInst(compiler);
                Register op1 = Register.getR(compiler.getRegManager().getIndexLastRegister());
                compiler.addInstruction(new LOAD(op1, op2));
            }else{
                ((Initialization)initialization).getExpression().codeGenInst(compiler);
                Register op1 = Register.getR(compiler.getRegManager().getIndexLastRegister());
                DAddr op2 = compiler.getRegManager().getRegisterOffset(varName.getName());
                compiler.addInstruction(new STORE(op1, op2));
            }
            compiler.getRegManager().freeLastOccupiedReg();
        } 
        else {
            if(Identifier.inMethod){
                Identifier.locals.put(varName.getName(), compiler.getRegManager().getIndexRegistre());
            }else{
                compiler.getRegManager().setGBOffset(varName.getName());
            }
        }
    }

    @Override
    protected void verifyDeclVar(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {
        Type exactType =  type.verifyType(compiler);
        if(exactType.isVoid())
        {
            throw new ContextualError("variable "+varName.getName().toString() +" can't be void",getLocation());
        }
        if(exactType.isString())
        {
            throw new ContextualError("Can't declar "+varName.getName().toString() +" with type String",getLocation()); 
        }
        VariableDefinition def = new VariableDefinition(exactType, getLocation());
        try {
            localEnv.declare(varName.getName(),def);
            varName.setDefinition(def);
            varName.setType(exactType);
        } catch (EnvironmentExp.DoubleDefException e) {
            throw new ContextualError("Double definition of " + varName.getName(), getLocation());
        }
        initialization.verifyInitialization(compiler, exactType, localEnv, currentClass);
    }

    
    @Override
    public void decompile(IndentPrintStream s) {
        type.decompile(s);
        s.print(" ");
        varName.decompile(s);
        initialization.decompile(s);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        type.iter(f);
        varName.iter(f);
        initialization.iter(f);
    }
    
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        varName.prettyPrint(s, prefix, false);
        initialization.prettyPrint(s, prefix, true);
    }
    @Override
    public void genByte(MethodVisitor method, int index, HashMap<SymbolTable.Symbol, Integer> indexTable) {
        indexTable.put(varName.getName(),index);
        initialization.genByte(method,indexTable,type.getDefinition().getType());
        if (type.getDefinition().getType().isInt()) {
            method.visitVarInsn(ISTORE, index);
            return;
        }
        if (type.getDefinition().getType().isFloat()) {
            method.visitVarInsn(FSTORE, index);
            return;
        }
        if (type.getDefinition().getType().isBoolean()) {
            method.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
        }
        method.visitVarInsn(ASTORE,index);
    }
}
