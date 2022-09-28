package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import java.util.Iterator;

import org.apache.commons.lang.Validate;

/**
 * Print statement (print, println, ...).
 *
 * @author gl19
 * @date 01/01/2022
 */
public abstract class AbstractPrint extends AbstractInst {

    private boolean printHex;
    private ListExpr arguments = new ListExpr();
    
    /**
     * renvoie le suffix de print
     * @return ""/"ln"/"x"/"lnx"
     */
    abstract String getSuffix();

    public AbstractPrint(boolean printHex, ListExpr arguments) {
        Validate.notNull(arguments);
        this.arguments = arguments;
        this.printHex = printHex;
    }

    /**
     * renvoie les arguments de Print
     * @return une ListExpr
     */
    public ListExpr getArguments() {
        return arguments;
    }

    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
                for (AbstractExpr a : arguments.getList()){
                    Type aType = a.verifyExpr(compiler, localEnv, currentClass);
                    if (!(aType.isString() || aType.isFloat() || aType.isInt())){
                        throw new ContextualError("arguments must be String, int or float, got a " + aType.getName(), getLocation());
                    }
                }
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        for (AbstractExpr a : getArguments().getList()) {
            if (getPrintHex()) {
                a.codeGenPrintHexa(compiler);
            }
            else {
                a.codeGenPrint(compiler);
            }
            
        }
    }

    private boolean getPrintHex() { 
        return printHex;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("print");
        s.print(this.getSuffix());
        if (getPrintHex()){
            s.print("x");
        }
        s.print("(");
        if (!arguments.isEmpty()){
            Iterator<AbstractExpr> iter = arguments.iterator();
            AbstractExpr i = iter.next();
            i.decompile(s);
            while (iter.hasNext()) {
                s.print(" ,");
                i = iter.next();
                i.decompile(s);
            }
        }
        s.println(");");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        arguments.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        arguments.prettyPrint(s, prefix, true);
    }

}
