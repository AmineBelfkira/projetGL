package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.LabelException;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.BSR;
import fr.ensimag.ima.pseudocode.instructions.LEA;
import fr.ensimag.ima.pseudocode.instructions.NEW;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import org.apache.commons.lang.Validate;
import org.objectweb.asm.MethodVisitor;

import java.io.PrintStream;
import java.util.HashMap;

/**
 *   new
 *
 * @author gl19
 * @date 17/01/2022
 */
public class New extends AbstractExpr{
    private AbstractIdentifier classe ;

    public New(AbstractIdentifier classe) {
        Validate.notNull(classe);
        this.classe = classe;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        Type t =classe.verifyType(compiler);
        if(!t.isClass())
            throw new ContextualError(t.getName().toString()+" is not a class ",getLocation());
        return t;
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        int numPop = compiler.getRegManager().getNeedPop();
        GPRegister reg = genRegister(compiler);
        compiler.addInstruction(new NEW(classe.getClassDefinition().getNumberOfFields() + 1, reg));
        compiler.addInstruction(new BOV(LabelException.heapOverflow));
        compiler.addInstruction(new LEA(new RegisterOffset(classe.getClassDefinition().getClassGBOffset(), Register.GB), Register.R0));
        compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(0, reg)));
        compiler.addInstruction(new PUSH(reg));
        compiler.addInstruction(new BSR(new Label("init." + classe.getName().getName())));
        compiler.addInstruction(new POP(reg));
        if (compiler.getRegManager().getNeedPop() > numPop) {
            compiler.addInstruction(new POP(reg));
        }  
    }

    @Override
    protected void checkDecoration() {
       // nothing 
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("new ");
        classe.decompile(s);
        s.print("()");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        classe.prettyPrint(s,prefix,true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        classe.iter(f);
    }
    @Override
    public void genByte(MethodVisitor method, HashMap<SymbolTable.Symbol, Integer> indexTable) {
        // nothing
    }
}
