#! /bin/sh

# Auteur : gl19
# Version initiale : 01/01/2022

# Test minimaliste de la syntaxe.
# On lance test_lex sur un fichier valide, et les tests invalides.

# dans le cas du fichier valide, on teste seulement qu'il n'y a pas eu
# d'erreur. Il faudrait tester que l'arbre donné est bien le bon. Par
# exemple, en stoquant la valeur attendue quelque part, et en
# utilisant la commande unix "diff".
#
# Il faudrait aussi lancer ces tests sur tous les fichiers deca
# automatiquement. Un exemple d'automatisation est donné avec une
# boucle for sur les tests invalides, il faut aller encore plus loin.
cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/launchers:"$PATH"

GREEN="\e[1;32m"
RED="\e[31m"


reussi=0
echec=0
rm -r ./src/test/deca/TestsLexroResultat
mkdir ./src/test/deca/TestsLexroResultat
test_lex_invalide () {
    # $1 = premier argument.
    if test_lex "$1" 2>&1 | grep -q -e "$1:[0-9][0-9]*:"
    then
        filename=$(echo ${cas_de_test} | xargs basename)
        echo '\e[1;37m'['\e[1;34m'TEST'\e[1;37m']${GREEN} "Echec attendu pour test_lex sur $filename"
        reussi=$(($reussi + 1))
    else
        filename=$(echo ${cas_de_test} | xargs basename)
        echo '\e[1;37m'['\e[1;34m'TEST'\e[1;37m']${RED} "Succes inattendu de test_lex sur $filename"
        echec=$(($echec + 1))
        
    fi
}     
 
test_lex_valide () {
    # $1 = premier argument.
    if test_lex "$1" 2<&1 | grep -q -e "$1:[0-9][0-9]*:"
    then
        filename=$(echo ${cas_de_test} | xargs basename)
        resultat=$(test_lex "$1")
        echo "$resultat">./src/test/deca/TestsLexroResultat/$filename.lis
        echo '\e[1;37m'['\e[1;34m'TEST'\e[1;37m']${RED} "Echec inattendu de test_lex sur $filename"
        echec=$(($echec + 1))
    else
        filename=$(echo ${cas_de_test} | xargs basename)
        resultat=$(test_lex "$1")
        echo "$resultat">./src/test/deca/TestsLexroResultat/$filename.lis
        echo '\e[1;37m'['\e[1;34m'TEST'\e[1;37m']${GREEN} "Succes attendu pour test_lex sur $filename" 
        reussi=$(($reussi + 1))
        
        
    fi
} 


echo '\e[1;37m'['\e[1;34m'INFO'\e[1;37m'] '\e[1;37m'"Tests invalides:"
for cas_de_test in ./src/test/deca/syntax/invalid/lex/*.deca
do
    test_lex_invalide "$cas_de_test"
done

echo     

echo '\e[1;37m'['\e[1;34m'INFO'\e[1;37m'] '\e[1;37m'"Tests valides:"
for cas_de_test in ./src/test/deca/syntax/valid/lex/*.deca
do
    test_lex_valide "$cas_de_test"
done

echo
echo 
echo '\e[1;37m'['\e[1;34m'INFO'\e[1;37m']'\e[1;35m'"  Totale tests réussis : $reussi  "
echo '\e[1;37m'['\e[1;34m'INFO'\e[1;37m']'\e[1;35m'"  Totale tests échoués : $echec  "

