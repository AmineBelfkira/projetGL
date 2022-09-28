package fr.ensimag.ima.pseudocode.instructions;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.BranchInstruction;
import fr.ensimag.ima.pseudocode.Label;

/**
 *
 * @author Ensimag
 * @date 01/01/2022
 */
public class BOV extends BranchInstruction {

    public BOV(Label op) {
        super(op);
    }

    // Constructeur prenant en paramètre le compilateur
    // pour afficher ou non l'intsruction BOV
    public BOV(Label op, DecacCompiler compiler) {
        super(op);

        if (!compiler.getCompilerOptions().getCheck()) {
            super.displayInst = false;
        }
    }

}
