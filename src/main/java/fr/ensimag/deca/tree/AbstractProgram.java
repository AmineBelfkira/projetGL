package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.SymbolTable;
import org.objectweb.asm.MethodVisitor;

import java.util.HashMap;

/**
 * Entry point for contextual verifications and code generation from outside the package.
 * 
 * @author gl19
 * @date 01/01/2022
 *
 */
public abstract class AbstractProgram extends Tree {
    public abstract void verifyProgram(DecacCompiler compiler) throws ContextualError;
    public abstract void codeGenProgram(DecacCompiler compiler) ;
    public abstract void genByte(MethodVisitor m, HashMap<SymbolTable.Symbol, Integer> indexTable);
    public abstract void genBytecode(String prefix);
}
