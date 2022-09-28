package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.WNL;
import org.objectweb.asm.MethodVisitor;

import java.util.HashMap;

import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

/**
 * @author gl19
 * @date 01/01/2022
 */
public class Println extends AbstractPrint {
    /**
     * @param arguments arguments passed to the print(...) statement.
     * @param printHex if true, then float should be displayed as hexadecimal (printlnx)
     */
    public Println(boolean printHex, ListExpr arguments) {
        super(printHex, arguments);
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        super.codeGenInst(compiler);
        compiler.addInstruction(new WNL());
    }

    @Override
    String getSuffix() {
        return "ln";
    }

    @Override
    public void genByte(MethodVisitor method, HashMap<SymbolTable.Symbol, Integer> indexTable) {
        for(AbstractExpr exp : this.getArguments().getList()){
            method.visitFieldInsn(GETSTATIC, "java/lang/System", "out",
                    "Ljava/io/PrintStream;");
            exp.genByte(method,indexTable);
            Type t = exp.getType();
            if(t.isInt())
                method.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "print",
                        "(I)V",false);
            else if (t.isFloat())
                method.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "print",
                        "(F)V",false);
            else if (t.isString())
                method.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "print",
                        "(Ljava/lang/String;)V",false);
            else
                method.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "print",
                        "(Ljava/lang/Object;)V",false);
        }
        method.visitFieldInsn(GETSTATIC, "java/lang/System", "out",
                "Ljava/io/PrintStream;");
        method.visitLdcInsn("");
        method.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println",
                "(Ljava/lang/String;)V",false);
    }
}
