package fr.ensimag.ima.pseudocode;

import java.io.PrintStream;

/**
 * IMA instruction.
 *
 * @author Ensimag
 * @date 01/01/2022
 */
public abstract class Instruction {
    // Lorsqu'on ne veut pas afficher une instruction dans le code 
    // assembleur on met cette variable à False
    // Cette variable est utilisée principalement pour ne pas afficher 
    // l'intruction BOV
    protected boolean displayInst = true;
    
    String getName() {
        return this.getClass().getSimpleName();
    }
    abstract void displayOperands(PrintStream s);
    void display(PrintStream s) {
        if (displayInst) {
            s.print(getName());
            displayOperands(s);
        }
    }
}
