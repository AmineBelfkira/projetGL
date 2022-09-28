package fr.ensimag.deca.codegen;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.ERROR;
import fr.ensimag.ima.pseudocode.instructions.WNL;
import fr.ensimag.ima.pseudocode.instructions.WSTR;

/**
 * class permettant de gnerer le code des exceptions et gerer leurs labels
 * 
 * @author gl19
 * @date 17/01/2022
 */
public class LabelException {

    public static Label overflow_error = new Label("arithmeticOverflow");

    /**
     * Genere le code assembleur pour l'erreur d'arrithmetic Overflow
     * @param compiler
     */
    public static void OverflowError(DecacCompiler compiler) {
        compiler.addLabel(overflow_error);
        compiler.addInstruction(new WSTR("Error: Overflow during arithmetic operation"));
        compiler.addInstruction(new WNL());
        compiler.addInstruction(new ERROR());        
    }

    public static Label stack_overflow = new Label("stack_overflow_error");

    /**
     * Genere le code assembleur pour l'erreur de Stack Overflow
     * @param compiler
     */
    public static void StackOverflow(DecacCompiler compiler) {
        compiler.addLabel(stack_overflow);
        compiler.addInstruction(new WSTR("Error: Stack Overflow"));
        compiler.addInstruction(new WNL());
        compiler.addInstruction(new ERROR());        
    }

    public static Label io_error = new Label("io_error");

    /**
     * Genere le code assembleur pour l'erreur d'Input et Output
     * @param compiler
     */
    public static void IoError(DecacCompiler compiler) {
        compiler.addLabel(io_error);
        compiler.addInstruction(new WSTR("Error: Input/Output error"));
        compiler.addInstruction(new WNL());
        compiler.addInstruction(new ERROR());        
    }

    public static Label dereferencement_null = new Label("dereferencement_null");

    /**
     * Genere le code assembleur pour l'erreur de dereferencement de null
     * @param compiler
     */
    public static void deferencementNull(DecacCompiler compiler) {
        compiler.addLabel(dereferencement_null);
        compiler.addInstruction(new WSTR("Erreur: dereferencement de null"));
        compiler.addInstruction(new WNL());
        compiler.addInstruction(new ERROR());                
    }

    public static Label heapOverflow = new Label("Heap_Overflow");

    /**
     * Genere le code assembleur pour l'erreur d'un tas plein
     * @param compiler
     */
    public static void HeapOverflow(DecacCompiler compiler) {
        compiler.addLabel(heapOverflow);
        compiler.addInstruction(new WSTR("Erreur : Heap Overflow"));
        compiler.addInstruction(new WNL());
        compiler.addInstruction(new ERROR());                
    }

}
