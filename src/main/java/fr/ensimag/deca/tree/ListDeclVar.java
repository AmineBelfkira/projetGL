package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import org.objectweb.asm.MethodVisitor;

import java.util.HashMap;

/**
 * 
 * 
 * @author gl19
 * @date 01/01/2022
 */
public class ListDeclVar extends TreeList<AbstractDeclVar> {

    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclVar c : getList()) {
            c.decompile(s);
            s.println();
        }
    }

    public void codeGenDeclVar(DecacCompiler compiler) {
        for (AbstractDeclVar i : getList()) {
            i.codeGenDeclVar(compiler);
        }
    }



    void verifyListDeclVariable(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        for (AbstractDeclVar decl: getList()){
            decl.verifyDeclVar(compiler, localEnv, currentClass);
        }
    }
    public void genByte(MethodVisitor method, HashMap<SymbolTable.Symbol, Integer> indexTable) {
        int index = 0;
        for (AbstractDeclVar decl: this.getList())
            decl.genByte(method,++index,indexTable);
    }
}
