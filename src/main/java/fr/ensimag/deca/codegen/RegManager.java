package fr.ensimag.deca.codegen;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.tree.AbstractDeclClass;
import fr.ensimag.deca.tree.ListDeclClass;
import fr.ensimag.deca.tree.Program;


/**
 * class permettant de gérer les registres et le piles
 * 
 * @author gl19
 * @date 10/01/2022
 */
public class RegManager {
    // Tableau contenant true si le registre Ri est occupé et 
    // false sinon
    private boolean[] regOccup;
    private int GB;
    private int LB;
    private HashMap<Symbol,Integer> offsetGB;
    private HashMap<Integer, Set<Integer>> lienClass;
    private int needPop;


    /**
     * retourne la valeur actuelle de GB
     * @return GB
     */
    public int getGB() {
        return GB;
    }

    /**
     * retourne la valeur actuelle de LB
     * @return LB
     */
    public int getLB() {
        return LB;
    }

    /**
     * retourne le nombre d'instruction POP nécessaires
     * @return needPop
     */
    public int getNeedPop() {
        return this.needPop;
    }

    /**
     * incremente le nombre de POP nécessaires cette méthode est appelé après un PUSH
     */
    public void IncrementNeedPop() {
        this.needPop++;
    }

    /**
     * décremente le nombre de POP nécessaires cette méthode est appelé après un POP
     */
    public void decrementNeedPop() {
        this.needPop -= 1;
    }

    /**
     * Renvoie l'Offset correspondant au symbol
     * @param symbol le symbol d'une variable
     * @return l'offset correspondant dans la pile
     */
    public int getGBOffset(Symbol symbol) {
        return offsetGB.get(symbol);
    }

    /**
     * Renvoie l'Offset correspondant au symbol, 
     * si aucun offset ne correspond elle crée un 
     * et elle le renvoi tout en incrementant la pile
     * @param symbol le symbol d'une variable
     * @return l'offset correspondant dans la pile
     */
    public int setGBOffset(Symbol symbol) {
        if (!offsetGB.containsKey(symbol)) {
            offsetGB.put(symbol, GB);
            incrementeGB();
        }
        return this.getGBOffset(symbol);
    }
    
    /**
     * renvoie un register Offset de type i(GB) pour un symbol donné,
     * on utilisant la méthod setGBoffset 
     * @param name le symbol d'une variable
     * @return le registre offset correspondant au symbol
     */
    public RegisterOffset getRegisterOffset(Symbol name)
    {
        return new RegisterOffset(setGBOffset(name), Register.GB);
    }

    /**
     * constructeur du Registre Manager il est appelé une fois au début du compilateur
     * @param nbRegistre nombre maximum de registre disponible
     */
    public RegManager(int nbRegistre) {
        regOccup = new boolean[nbRegistre];
        for (int i = 0; i < regOccup.length; i++) {
            this.regOccup[i] = false;
        }
        this.GB = 2;
        this.LB = 2;
        
        offsetGB = new HashMap<Symbol, Integer>();
        lienClass = new HashMap<Integer,Set<Integer>>();
        needPop = 0;
    }

    
    /**
     * Remet à false tous les cases de la table de registres. 
     * Tous les registres deviennent libre à l'utilisation. 
     * On ne réinitialize pas les registres scratch R0 et R1.  
     */    
    public void ReinitRegOccup() {
        for (int i = 2; i < regOccup.length; i++) {
            this.regOccup[i] = false;
        }       
    }    

    /**
     * Retourne la taille de la table d'occupation regOccup.
     */
    public int getRegOccupLength() {
        return this.regOccup.length;
    }


    /**
     * Incrémente le nombre de case mémoire utilisées dans le registre 
     * GB par 1.
     */
    public void incrementeGB() {
        this.GB += 1;
    }

    /**
     * Setter pour GB. 
     */
    public void setGB(int offset) {
        this.GB = offset;
    }
     
    /**
     * Incrémente LB par 1;
     */
    public void incrementeLB() {
        this.LB += 1;
    }
    /**
     * Retourne true si le registre dont l'indice est donné en 
     * paramètre est occupé et false sinon.
     * @param i: indice du registre, obtenue à partir du getNumber 
     * de GPRegistre.
     */
    public boolean isOccupied(int i) {
        return regOccup[i];
    }

    /**
     * Permet la récupération d'un registre pour une nouvelle instruction.
     * et il le marque comme utilise (occupé)
     * Retourne l'indice du premier registre libre.
     * Dans le cas où tous les registres sont occupés retourne -1.
     */
    public int getIndexRegistre() {
        for (int i = 2; i < regOccup.length; i++) {
            if (!this.isOccupied(i)) {
                makeOccupied(i);
                return i;
            }
        }
        return -1;
    }

    /**
     * Retourne l'indice du dernier registre utilisé de la table
     * regOccup.
     */
    public int getIndexLastRegister() {
        int index = 2;
        while (index < regOccup.length && this.isOccupied(index)) {
            index += 1;
        }
        if (index == 2){
            return 2;
        }
        return index-1;
    }

    /**
     * initialise la valeur du dernier registre utilise
     *  pour l'instant sa utilite est se voit apres chaque STORE
     */
    public void freeLastOccupiedReg(){
        int index = getIndexLastRegister();
        regOccup[index] = false;
    }


    /**
     * Remet a true la valeur de registre utilise
     * @param i: indice du registre utilisé
     */
    public void makeOccupied(int i){
        regOccup[i] = true;
    }


    /**
     * calculer le nombre de SP que le program utlitse
     * @return le nombre de SP
     */
    public int getSP(Program program)
    {   
        int SP = 2; // A verifer: je crois ici c'est sp = 0
        ListDeclClass listDeclClass = program.getClasses();
        for(AbstractDeclClass tmp : listDeclClass.getList())
        {
            SP += tmp.getNbSP();
        }
        SP += program.getMain().getNbSP(); //Variable global
        
        return SP;
    }

    /**
     * chercher tous les sous class de class current
     * @param key le index offset de parent
     * @return tous les fils de parent(ket) et lui meme;
     */
    public Iterator<Integer> getClassFilsEtKey(int key)
    {
        Set<Integer> ret = this.lienClass.get(key);
        if(ret != null){
            boolean change = false;
            Set<Integer> tmp  = new HashSet<Integer>();
            do{
                change = false;
                Iterator<Integer> ints = ret.iterator();
                while(ints.hasNext())
                {
                    Integer indexParent = ints.next();
                    if(this.lienClass.containsKey(indexParent)){
                        int lengthDebut = tmp.size();
                        tmp.addAll(this.lienClass.get(indexParent));
                        int lengthfin = tmp.size();
                        if(lengthDebut!= lengthfin)
                            change = true;
                    }
                }
                ret.addAll(tmp);
                
            }while(change);
        }else{
            ret = new HashSet<Integer>();
        }
       
        ret.add(key);
        return ret.iterator();
    }

    /**
     * creer un lien entre supClass et sous-class
     * @param key le index offset de parent
     * @param value le index offset de sous-class
     */
    public void setClassParent(int key, int value)
    {
        if(!this.lienClass.containsKey(key))
        {
            this.lienClass.put(key, new HashSet<Integer>());
        }
        this.lienClass.get(key).add(value);
        
    }

}
