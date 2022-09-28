package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BRA;

import java.io.PrintStream;
import java.util.HashMap;

import org.apache.commons.lang.Validate;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.ICONST_0;

/**
 * Full if/else if/else statement.
 *
 * @author gl19
 * @date 01/01/2022
 */
public class IfThenElse extends AbstractInst {
    
    private final AbstractExpr condition; 
    private final ListInst thenBranch;
    private ListInst elseBranch;
    private IfThenElse ifThenElse;
    private static int ifThenElseID;

    public IfThenElse(AbstractExpr condition, ListInst thenBranch,IfThenElse ifThenElse, ListInst elseBranch) {
        Validate.notNull(condition);
        Validate.notNull(thenBranch);
        Validate.notNull(elseBranch);
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
        this.ifThenElse = ifThenElse;
    }

    public void setElseIf(IfThenElse ifThenElse){
        this.ifThenElse = ifThenElse;
    } 


    
    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
        condition.verifyCondition(compiler, localEnv, currentClass);
        thenBranch.verifyListInst(compiler, localEnv, currentClass, returnType);
        elseBranch.verifyListInst(compiler, localEnv, currentClass, returnType);
        if( ifThenElse != null){
            ifThenElse.verifyInst(compiler, localEnv, currentClass, returnType);
        }
    }


    /**
     * genere le code assembleur d'une if puis else,
     * une methode recursive pour generer tous les else if 
     * @param compiler
     * @param elseLabel 
     * @param endIfLabel
     */
    protected void codeGenInsideIf(DecacCompiler compiler, Label elseLabel, Label endIfLabel){
        Label next = endIfLabel;
        Label elseIfLabel = null;
        if (elseLabel != null){
            next = elseLabel;
        }
        if( ifThenElse != null){
            ifThenElseID++;
            elseIfLabel = new Label("ElseIf"+ifThenElseID);
            next = elseIfLabel;
        }

        condition.codeGenLabelFalse(compiler, next);
        compiler.getRegManager().ReinitRegOccup();
        
        thenBranch.codeGenListInst(compiler);
        compiler.addInstruction(new BRA(endIfLabel));
        if( ifThenElse != null){
            compiler.addLabel(elseIfLabel);
            ifThenElse.codeGenInsideIf(compiler, elseLabel, endIfLabel);
        }
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        ifThenElseID++;
        Label endIfLabel = new Label("EndIf"+ifThenElseID);
        Label elseLabel = null;
        if (!elseBranch.getList().isEmpty()){
            elseLabel = new Label("Else"+ifThenElseID);
        }
        codeGenInsideIf(compiler, elseLabel, endIfLabel);
        if(elseLabel!=null){
            compiler.addLabel(elseLabel);
            elseBranch.codeGenListInst(compiler);
        }
        compiler.addLabel(endIfLabel);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("if (");
        condition.decompile(s);
        s.println(") {");
        s.indent();
        thenBranch.decompile(s);
        s.unindent();
        s.print("}");
        if( ifThenElse!=null){
            s.print(" else ");
            ifThenElse.decompile(s);
        }
        if(!elseBranch.isEmpty()){
            s.println(" else {");
            s.indent();
            elseBranch.decompile(s);
            s.unindent();
            s.println("}");
        }
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        condition.iter(f);
        thenBranch.iter(f);
        if( ifThenElse!=null){
            ifThenElse.iterChildren(f);
        }
        elseBranch.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        condition.prettyPrint(s,prefix,false);
        thenBranch.prettyPrint(s, prefix, false); 
        if( ifThenElse!=null){
            ifThenElse.prettyPrint(s, prefix, false, true);
        }
        if (!elseBranch.getList().isEmpty()){
            elseBranch.prettyPrint(s, prefix, true);
        }
    }

    @Override
    public void genByte(MethodVisitor method, HashMap<SymbolTable.Symbol, Integer> indexTable) {
        condition.genByte(method,indexTable);
        org.objectweb.asm.Label label = new org.objectweb.asm.Label();
        method.visitJumpInsn(IFEQ, label);
        thenBranch.genByte(method,indexTable);
        org.objectweb.asm.Label end = new org.objectweb.asm.Label();
        method.visitJumpInsn(GOTO, end);
        method.visitLabel(label);
        elseBranch.genByte(method, indexTable);
        method.visitLabel(end);
        if( ifThenElse != null){
            ifThenElse.genByte(method,indexTable);
        }
    }
}