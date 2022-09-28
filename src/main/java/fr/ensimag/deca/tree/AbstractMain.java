package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.SymbolTable;
import org.objectweb.asm.MethodVisitor;

import java.util.HashMap;

/**
 * Main block of a Deca program.
 *
 * @author gl19
 * @date 01/01/2022
 */
public abstract class AbstractMain extends Tree {

    public abstract int getNbSP();

    protected abstract void codeGenMain(DecacCompiler compiler);


    /**
     * Implements non-terminal "main" of [SyntaxeContextuelle] in pass 3 
     */
    protected abstract void verifyMain(DecacCompiler compiler) throws ContextualError;

    public abstract void genByte(MethodVisitor method,HashMap<SymbolTable.Symbol, Integer> indexTable);
}
