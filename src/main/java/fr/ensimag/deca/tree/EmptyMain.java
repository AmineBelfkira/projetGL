package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import org.objectweb.asm.MethodVisitor;

import java.io.PrintStream;
import java.util.HashMap;

/**
 * Empty main Deca program
 *
 * @author gl19
 * @date 01/01/2022
 */
public class EmptyMain extends AbstractMain {

    @Override
    protected void verifyMain(DecacCompiler compiler) throws ContextualError {
        // nothing
    }

    @Override
    public void genByte(MethodVisitor method, HashMap<SymbolTable.Symbol, Integer> indexTable) {
        //nothing
    }

    @Override
    protected void codeGenMain(DecacCompiler compiler) {
        //nothing
    }

    /**
     * Contains no real information => nothing to check.
     */
    @Override
    protected void checkLocation() {
        // nothing
    }
    
    @Override
    public void decompile(IndentPrintStream s) {
        // no main program => nothing
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
    public int getNbSP() {
        return 0;
    }
}
