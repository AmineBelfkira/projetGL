package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.ImmediateString;
import fr.ensimag.ima.pseudocode.instructions.WSTR;
import java.io.PrintStream;
import java.util.HashMap;

import org.apache.commons.lang.Validate;
import org.objectweb.asm.MethodVisitor;

/**
 * String literal
 *
 * @author gl19
 * @date 01/01/2022
 */
public class StringLiteral extends AbstractStringLiteral {

    @Override
    public String getValue() {
        return value;
    }

    private String value;

    private String subValue;

    public StringLiteral(String value) {
        Validate.notNull(value);
        subValue = value.substring(1,value.length()-1);
        this.value = value;
    }

    @Override
    public void genByte(MethodVisitor method, HashMap<Symbol, Integer> indexTable) {
        method.visitLdcInsn(subValue);
    }
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        super.setType(compiler.getEnvironmentType().get(compiler.getSymbolTable().create("String")).getType());
        return super.getType();
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        compiler.addInstruction(new WSTR(new ImmediateString(subValue.replace("\\\"", "\"").replace("\\\\", "\\"))));
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(value);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }
    
    @Override
    String prettyPrintNode() {
        return "StringLiteral (" + value + ")";
    }

    @Override
    protected void checkLocation() {
        // nothing
    }

    @Override
    protected void checkDecoration() {
        // nothing
    }
}
