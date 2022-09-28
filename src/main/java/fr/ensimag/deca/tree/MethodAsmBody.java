package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.InlinePortion;
import org.apache.commons.lang.Validate;
import java.io.PrintStream;

/**
 * ASM Method body
 * @author gl19
 * @date 17/01/2022
 */
public class MethodAsmBody extends AbstractMethodBody{

    private AbstractStringLiteral code ;

    public MethodAsmBody(AbstractStringLiteral code) {
        Validate.notNull(code);
        this.code = code ;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(" ");
        s.print("asm(");
        code.decompile(s);
        s.println(");");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        code.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        code.prettyPrint(s, prefix, true);
    }

    @Override
    protected void verifyClassBody(DecacCompiler compiler, EnvironmentExp envExp, EnvironmentExp params, ClassDefinition currentClass, Type ret) throws ContextualError {
        if (!ret.isVoid()){
            throw new ContextualError("asm Method type should be void", getLocation());
        }
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        compiler.add(new InlinePortion(code.getValue().substring(1, code.getValue().length()-1).replace("\\\"", "\"").replace("\\\\", "\\")));
    }

    @Override
    protected void checkLocation() {
        // nothing
    }
}
